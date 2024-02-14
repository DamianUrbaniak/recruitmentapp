package com.damianurbaniak.recruitmentapp.repodataprovider.hub;

import com.damianurbaniak.recruitmentapp.repodataprovider.RepoDataProviderFacade;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.BranchData;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
class RepoDataProviderService implements RepoDataProviderFacade {

  private final String GITHUB_REPOS_API_URL = "https://api.github.com/users/";
  private final String GITHUB_BRANCHES_API_URL = "https://api.github.com/repos/";
  private final String REPOS_SUFFIX = "/repos";
  private final String BRANCHES_SUFFIX = "/branches";

  @Override
  public ResponseEntity<String> elo(final String userName, final String acceptHeader) {

    if (isFalse(acceptHeader.equals("application/json"))) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Accept header");
    }

    final List<RepoData> maps = mapJsonToDtos(getResponseEntity(buildUrlForRepos(userName)).getBody());
    final List<BranchData> mapss = mapJsonToDtoss(getResponseEntity(buildUrlForBranches(userName, "Hello-World")).getBody());

    return null;
  }

  private ResponseEntity<String> getResponseEntity(final String apiUrl) {
    try {
      final ResponseEntity<String> response = new RestTemplate().getForEntity(apiUrl, String.class);
      return ResponseEntity.ok(response.getBody());
    } catch (HttpClientErrorException exception) {
      if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data");
      }
    }
  }

  private List<RepoData> mapJsonToDtos(final String jsonString) {
    try {
      return new ObjectMapper().readValue(jsonString, new TypeReference<>() {
      });
    } catch (IOException exception) {
      log.error(exception.getMessage());
    }
    return Collections.emptyList();
  }

  private List<BranchData> mapJsonToDtoss(final String jsonString) {
    try {
      return new ObjectMapper().readValue(jsonString, new TypeReference<>() {
      });
    } catch (IOException exception) {
      log.error(exception.getMessage());
    }
    return Collections.emptyList();
  }

  private String buildUrlForRepos(final String userName) {
    return GITHUB_REPOS_API_URL + userName + REPOS_SUFFIX;
  }

  private String buildUrlForBranches(final String userName, final String repoName) {
    return GITHUB_BRANCHES_API_URL + userName.concat("/") + repoName + BRANCHES_SUFFIX;
  }

}
