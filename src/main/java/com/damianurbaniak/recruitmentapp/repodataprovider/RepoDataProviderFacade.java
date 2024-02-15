package com.damianurbaniak.recruitmentapp.repodataprovider;

import com.damianurbaniak.recruitmentapp.repodataprovider.dto.RepoDto;

import java.util.List;

public interface RepoDataProviderFacade {

  List<RepoDto> fetchRepoData(final String userName, final String acceptHeader);
}
