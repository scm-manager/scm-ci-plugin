package com.cloudogu.scm.ci.cistatus.api;

import com.google.inject.Inject;
import sonia.scm.repository.Repository;

import java.util.List;


class CIStatusCollectionMapper {

  private final CIStatusMapper ciStatusMapper;

  @Inject
  CIStatusCollectionMapper(CIStatusMapper ciStatusMapper) {
    this.ciStatusMapper = ciStatusMapper;
  }

  List<CIStatusDto> map(Repository repository, String changeSetId) {

    //ciStatusIterable.

//    List<CIStatusDto> ciStatusDtoList = new ArrayList<>();
//    for (CIStatus ciStatus : ciStatusIterable) {
//      ciStatusDtoList.add(ciStatusMapper.map(repository, changeSetId, ciStatus));
//    }
    return null;
  }
}
