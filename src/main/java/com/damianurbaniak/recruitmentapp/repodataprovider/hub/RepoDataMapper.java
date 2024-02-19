package com.damianurbaniak.recruitmentapp.repodataprovider.hub;

import com.damianurbaniak.recruitmentapp.repodataprovider.dto.BranchData;
import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@UtilityClass
class RepoDataMapper {

  List<RepoDto.BranchDto> mapBranchDataToDtos(final List<BranchData> branchData) {
    return branchData.stream()
      .map(branch -> new RepoDto.BranchDto(branch.name(), branch.commit().sha()))
      .toList();
  }
}
