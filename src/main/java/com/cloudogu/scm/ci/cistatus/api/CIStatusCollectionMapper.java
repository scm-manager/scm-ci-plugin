package com.cloudogu.scm.ci.cistatus.api;

import com.google.inject.Inject;

class CIStatusCollectionMapper {

  private final CIStatusMapper ciStatusMapper;

  @Inject
  CIStatusCollectionMapper(CIStatusMapper ciStatusMapper) {
    this.ciStatusMapper = ciStatusMapper;
  }

}
