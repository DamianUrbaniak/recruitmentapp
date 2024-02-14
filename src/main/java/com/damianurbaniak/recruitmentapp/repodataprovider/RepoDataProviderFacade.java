package com.damianurbaniak.recruitmentapp.repodataprovider;

import org.springframework.http.ResponseEntity;

public interface RepoDataProviderFacade {

  ResponseEntity<String> elo(final String userName, final String acceptHeader);

  }
