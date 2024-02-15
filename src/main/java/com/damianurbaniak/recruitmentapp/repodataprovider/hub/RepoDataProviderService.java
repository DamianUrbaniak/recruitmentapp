package com.damianurbaniak.recruitmentapp.repodataprovider.hub;

import com.damianurbaniak.recruitmentapp.common.exception.DataReceiveException;
import com.damianurbaniak.recruitmentapp.repodataprovider.RepoDataProviderFacade;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.BranchData;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
class RepoDataProviderService implements RepoDataProviderFacade {

  private static final class ErrorMessages {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String SERVER_NOT_RESPONDING = "Server is not responding";
  }

  private static final String GITHUB_REPOS_API_URL = "https://api.github.com/users/%s/repos";
  private static final String GITHUB_BRANCHES_API_URL = "https://api.github.com/repos/%s/%s/branches";

  @Override
  public List<RepoDto> fetchRepoData(final String userName, final String acceptHeader) {
    return RepoDataMapper.mapJsonToRepoData(fetchDataFromUrl(buildUrlForRepos(userName))).stream()
      .filter(repoData -> isFalse(repoData.fork()))
      .map(repoData -> {
        final List<BranchData> branchData = RepoDataMapper.mapJsonToBranchData(fetchDataFromUrl(buildUrlForBranches(userName, repoData.name())));
        return new RepoDto(repoData.name(), repoData.owner().login(), RepoDataMapper.mapBranchDataToDtos(branchData));
      })
      .toList();
  }

  private String fetchDataFromUrl(final String url) {
    final Mono<String> repoDataMono = WebClient.create()
      .get()
      .uri(url)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError,
        error -> throwDataReceiveException(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value()))
      .onStatus(HttpStatusCode::is5xxServerError,
        error -> throwDataReceiveException(ErrorMessages.SERVER_NOT_RESPONDING, HttpStatus.INTERNAL_SERVER_ERROR.value()))
      .bodyToMono(String.class);

    return repoDataMono.block();
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
