package com.damianurbaniak.recruitmentapp.repodataprovider.dto;

import java.util.List;

public record RepoDto(String repoName,
                      String owner,
                      List<BranchDto> branches) {

  public record BranchDto(String name,
                          String lastSha) {
  }
}
