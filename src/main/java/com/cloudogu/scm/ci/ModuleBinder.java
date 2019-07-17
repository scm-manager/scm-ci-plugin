package com.cloudogu.scm.ci;

import com.cloudogu.scm.ci.cistatus.api.CIStatusMapper;
import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;
import sonia.scm.plugin.Extension;

@Extension
public class ModuleBinder extends AbstractModule {

  @Override
  protected void configure() {
    bind(CIStatusMapper.class).to(Mappers.getMapper(CIStatusMapper.class).getClass());
  }
}
