package com.damianurbaniak.recruitmentapp.repodataprovider.hub;

import com.damianurbaniak.recruitmentapp.repodataprovider.dto.BranchData;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoData;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@UtilityClass
class RepoDataMapper {

  List<RepoDto.BranchDto> mapBranchDataToDtos(final List<BranchData> branchData) {
    return branchData.stream()
      .map(branch -> new RepoDto.BranchDto(branch.name(), branch.commit().sha()))
      .toList();
  }

  List<RepoData> mapJsonToRepoData(final String jsonString) {
    try {
      return new ObjectMapper().readValue(jsonString, new TypeReference<>() {
      });
    } catch (IOException exception) {
      log.error(exception.getMessage());
    }
    return Collections.emptyList();
  }

  List<BranchData> mapJsonToBranchData(final String jsonString) {
    try {
      return new ObjectMapper().readValue(jsonString, new TypeReference<>() {
      });
    } catch (IOException exception) {
      log.error(exception.getMessage());
    }
    return Collections.emptyList();
  }
}
