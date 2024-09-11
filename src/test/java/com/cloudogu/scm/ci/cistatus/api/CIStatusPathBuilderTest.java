/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

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
    String commentSelfUri = ciStatusPathBuilder.createChangesetCiStatusSelfUri("space", "name", "changesetId", "jenkins", "build1");
    Assertions.assertThat(commentSelfUri).isEqualTo("/api/v2/ci/space/name/changesets/changesetId/jenkins/build1");
  }
}
