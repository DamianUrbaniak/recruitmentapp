package com.damianurbaniak.recruitmentapp.repodataprovider.controller;

import com.damianurbaniak.recruitmentapp.repodataprovider.RepoDataProviderFacade;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
class RepoDataProviderController {

  private final RepoDataProviderFacade repoDataProviderFacade;

  private static final class Routes {
    private static final String ROOT = "/api/github";
    private static final String FETCH_REPO_DATA = ROOT + "/fetch-repo-data/{userName}";
  }

  @GetMapping(Routes.FETCH_REPO_DATA)
  List<RepoDto> getGitHubRepositories(@PathVariable final String userName,
                                      @RequestHeader(HttpHeaders.ACCEPT) final String acceptHeader) {
    return repoDataProviderFacade.fetchRepoData(userName, acceptHeader);
  }
}
