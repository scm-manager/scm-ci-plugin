package com.cloudogu.scm.ci.cistatus.api;

import com.google.inject.Provider;
import com.google.inject.util.Providers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import static java.net.URI.create;

class CIStatusPathBuilderTest {

  private Provider<ScmPathInfoStore> pathInfoStore;
  private CIStatusPathBuilder ciStatusPathBuilder;


  @BeforeEach
  void init() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() ->
      create("/api/"));
    pathInfoStore = Providers.of(scmPathInfoStore);
    ciStatusPathBuilder = new CIStatusPathBuilder(pathInfoStore);
  }

  @Test
  void shouldCreateSelfLink() {
    String commentSelfUri = ciStatusPathBuilder.createCiStatusSelfUri("space", "name", "changesetId", "jenkins", "build1");
    Assertions.assertThat(commentSelfUri).isEqualTo("/api/v2/ci/space/name/changesets/changesetId/jenkins/build1");
  }
}
