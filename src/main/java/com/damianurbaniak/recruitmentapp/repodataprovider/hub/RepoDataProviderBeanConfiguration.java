package com.damianurbaniak.recruitmentapp.repodataprovider.hub;

import com.damianurbaniak.recruitmentapp.repodataprovider.RepoDataProviderFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RepoDataProviderBeanConfiguration {

  @Bean
  RepoDataProviderFacade repoDataProviderFacade() {
    return new RepoDataProviderService();
  }
}
