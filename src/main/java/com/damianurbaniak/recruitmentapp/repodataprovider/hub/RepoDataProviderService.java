package com.damianurbaniak.recruitmentapp.repodataprovider.hub;

import com.damianurbaniak.recruitmentapp.common.exception.DataReceiveException;
import com.damianurbaniak.recruitmentapp.repodataprovider.RepoDataProviderFacade;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.BranchData;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoData;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
class RepoDataProviderService implements RepoDataProviderFacade {

  private static final class ErrorMessages {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String REPO_NOT_FOUND = "Repository not found";
    private static final String SERVER_NOT_RESPONDING = "Server is not responding";
  }

  private static final String GITHUB_REPOS_API_URL = "https://api.github.com/users/%s/repos";
  private static final String GITHUB_BRANCHES_API_URL = "https://api.github.com/repos/%s/%s/branches";

  @Override
  @SneakyThrows
  public List<RepoDto> fetchRepoData(final String userName, final String acceptHeader) {
    final List<RepoData> userRepos =
      Arrays.stream(fetchDataFromUrl(buildUrlForRepos(userName), RepoData[].class, ErrorMessages.USER_NOT_FOUND))
        .filter(repoData -> isFalse(repoData.fork()))
        .toList();

    final List<Callable<BranchData[]>> callables = userRepos.stream()
      .map(repoData ->
        (Callable<BranchData[]>) () ->
          fetchDataFromUrl(buildUrlForBranches(userName, repoData.name()), BranchData[].class, ErrorMessages.REPO_NOT_FOUND))
      .toList();

    try (final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      final List<Future<BranchData[]>> futures = executor.invokeAll(callables);
      awaitTerminationAfterShutdown(executor);
      final List<BranchData[]> branchData = extractDataFromFuture(futures);

      return RepoDataMapper.mapResultsToDtos(userRepos, branchData);
    }
    //todo create custom exception
  }

  private <T> List<T> extractDataFromFuture(final List<Future<T>> futures) {
    return futures.stream()
      .map(future -> {
        try {
          return future.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      })
      .toList();
  }

  private <T> T fetchDataFromUrl(final String url, final Class<T> clazz, final String errorMessage) {
    final Mono<ResponseEntity<T>> entity = WebClient.create()
      .get()
      .uri(url)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError,
        error -> throwDataReceiveException(errorMessage, HttpStatus.NOT_FOUND.value()))
      .onStatus(HttpStatusCode::is5xxServerError,
        error -> throwDataReceiveException(ErrorMessages.SERVER_NOT_RESPONDING, HttpStatus.INTERNAL_SERVER_ERROR.value()))
      .toEntity(clazz);

    return entity.block().getBody();
  }

  private void awaitTerminationAfterShutdown(final ExecutorService threadPool) {
    threadPool.shutdown();
    try {
      if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
        threadPool.shutdownNow();
      }
    } catch (InterruptedException e) {
      threadPool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  private String buildUrlForRepos(final String userName) {
    return String.format(GITHUB_REPOS_API_URL, userName);
  }

  private String buildUrlForBranches(final String userName, final String repoName) {
    return String.format(GITHUB_BRANCHES_API_URL, userName, repoName);
  }

  private Mono<Throwable> throwDataReceiveException(final String errorMessage, final int status) {
    return Mono.error(new DataReceiveException(errorMessage, status));
  }
}
