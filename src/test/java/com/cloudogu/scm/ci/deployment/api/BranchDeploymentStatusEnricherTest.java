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

package com.cloudogu.scm.ci.deployment.api;

import com.google.inject.util.Providers;
import jakarta.inject.Provider;
import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.BranchDetails;
import sonia.scm.repository.Repository;

import java.net.URI;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, ShiroExtension.class})
@SubjectAware("Trainer Red")
class BranchDeploymentStatusEnricherTest {

  private final Repository repository = new Repository("1337", "git", "pokemon", "gold");
  private final BranchDetails branchDetails = new BranchDetails("main");
  private final String domainForLinks = "https://scm.com/api/";

  @Mock
  private HalEnricherContext context;
  @Mock
  private HalAppender appender;

  private BranchDeploymentStatusEnricher enricher;

  @BeforeEach
  void setup() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create(domainForLinks));
    Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    DeploymentStatusPathBuilder pathBuilder = new DeploymentStatusPathBuilder(scmPathInfoStoreProvider);

    enricher = new BranchDeploymentStatusEnricher(pathBuilder);
  }

  @Test
  void shouldNotEnrichWithoutPermissions() {
    when(context.oneRequireByType(Repository.class)).thenReturn(repository);
    enricher.enrich(context, appender);
    verifyNoInteractions(appender);
  }

  @Test
  @SubjectAware(permissions = "repository:readCIStatus:1337")
  void shouldAppendLink() {
    when(context.oneRequireByType(Repository.class)).thenReturn(repository);
    when(context.oneRequireByType(BranchDetails.class)).thenReturn(branchDetails);

    enricher.enrich(context, appender);

    verify(appender).appendLink(
      "deploymentStatus",
      "https://scm.com/api/v2/deployments/pokemon/gold/branches/main"
    );
  }
}
