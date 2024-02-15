package com.damianurbaniak.recruitmentapp.repodataprovider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepoData(String name,
                       Owner owner,
                       boolean fork) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Owner(String login) {
  }
}
