package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sonia.scm.repository.Repository;
import java.util.List;

@Mapper
public interface CIStatusMapper {

    @Mapping(target = "attributes", ignore = true) // We do not map HAL attributes
    CIStatusDto map(@Context Repository repository, @Context String changeSetId, List<CIStatus> ciStatus);

    CIStatus map(CIStatusDto ciStatusDto);
}
