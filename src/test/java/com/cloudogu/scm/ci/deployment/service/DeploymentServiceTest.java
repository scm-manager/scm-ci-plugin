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

package com.cloudogu.scm.ci.deployment.service;

import org.apache.shiro.authz.UnauthorizedException;
import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.NotFoundException;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.InMemoryByteDataStoreFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static com.cloudogu.scm.ci.deployment.DeploymentTestData.buildDefaultDeployment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith({ShiroExtension.class, MockitoExtension.class})
@SubjectAware("Trainer Red")
class DeploymentServiceTest {

  private final Repository repository = RepositoryTestData.createHeartOfGold();
  private final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
  private InMemoryByteDataStoreFactory storeFactory;

  @Mock
  private RepositoryServiceFactory repositoryServiceFactory;
  @Mock
  private RepositoryService repositoryService;
  @Mock
  private LogCommandBuilder logCommandBuilder;

  private DeploymentService deploymentService;

  @BeforeEach
  void setup() {
    storeFactory = new InMemoryByteDataStoreFactory();
    deploymentService = new DeploymentService(storeFactory, clock, repositoryServiceFactory);
    repository.setId("1337");
  }

  private DataStore<DeploymentCollection> buildStore(DeploymentType type) {
    return storeFactory.withType(DeploymentCollection.class).withName(type.storeName).forRepository(repository).build();
  }

  @Nested
  class GetAllBranchDeployments {

    @BeforeEach
    void setup() {
      lenient().when(repositoryServiceFactory.create(repository)).thenReturn(repositoryService);
      lenient().when(repositoryService.getLogCommand()).thenReturn(logCommandBuilder);
      lenient().when(logCommandBuilder.setBranch("main")).thenReturn(logCommandBuilder);
      lenient().when(logCommandBuilder.setPagingLimit(1)).thenReturn(logCommandBuilder);
    }

    @Test
    void shouldNotBeAllowedToGetAllBranchDeploymentsBecauseOfMissingPermission() {
      assertThatThrownBy(
        () -> deploymentService.getAllBranchDeployments(repository, "main")
      ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:readCIStatus:1337")
    void shouldThrowNotFoundBecauseNoChangesetsWereFoundForBranch() throws IOException {
      when(logCommandBuilder.getChangesets())
        .thenReturn(new ChangesetPagingResult(1, List.of()));


      assertThatThrownBy(
        () -> deploymentService.getAllBranchDeployments(repository, "main")
      ).isInstanceOf(NotFoundException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:readCIStatus:1337")
    void shouldGetAllDeploymentsOfBranch() throws IOException {
      when(logCommandBuilder.getChangesets())
        .thenReturn(
          new ChangesetPagingResult(
            1,
            List.of(new Changeset("2", null, null, null))
          )
        );

      DeploymentCollection deploymentsOfOtherCommit = new DeploymentCollection();
      deploymentsOfOtherCommit.put(buildDefaultDeployment(DeploymentType.COMMIT, "1"));

      DeploymentCollection deploymentsOfLatestBranchCommit = new DeploymentCollection();
      deploymentsOfLatestBranchCommit.put(buildDefaultDeployment(DeploymentType.COMMIT, "2"));

      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.COMMIT);
      deploymentStore.put("1", deploymentsOfOtherCommit);
      deploymentStore.put("2", deploymentsOfLatestBranchCommit);

      DeploymentCollection result = deploymentService.getAllBranchDeployments(repository, "main");

      assertThat(result).usingRecursiveComparison().isEqualTo(deploymentsOfLatestBranchCommit);
    }
  }

  @Nested
  class GetAllCommitDeployments {

    @Test
    void shouldNotBeAllowedToGetAllCommitDeploymentsBecauseOfMissingPermission() {
      assertThatThrownBy(
        () -> deploymentService.getAllCommitDeployments(repository, "1337")
      ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:readCIStatus:1337")
    void shouldGetAllDeploymentsOfCommit() {
      DeploymentCollection deployments = new DeploymentCollection();
      deployments.put(buildDefaultDeployment(DeploymentType.COMMIT, "1"));

      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.COMMIT);
      deploymentStore.put("1", deployments);

      DeploymentCollection result = deploymentService.getAllCommitDeployments(repository, "1");

      assertThat(result).usingRecursiveComparison().isEqualTo(deployments);
    }
  }

  @Nested
  class GetAllPullRequestDeployments {

    @Test
    void shouldNotBeAllowedToGetAllPullRequestDeploymentsBecauseOfMissingPermission() {
      assertThatThrownBy(
        () -> deploymentService.getAllPullRequestDeployments(repository, "1337")
      ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:readCIStatus:1337")
    void shouldGetAllDeploymentsOfPullRequest() {
      DeploymentCollection deployments = new DeploymentCollection();
      deployments.put(buildDefaultDeployment(DeploymentType.PULL_REQUEST, "1"));

      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.PULL_REQUEST);
      deploymentStore.put("1", deployments);

      DeploymentCollection result = deploymentService.getAllPullRequestDeployments(repository, "1");

      assertThat(result).usingRecursiveComparison().isEqualTo(deployments);
    }
  }

  @Nested
  class PutCommitDeployment {

    @Test
    void shouldNotAllowUpdateBecauseOfMissingPermission() {
      assertThatThrownBy(
        () -> deploymentService.putCommitDeployment(new Deployment(), repository, "1337")
      ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:writeCIStatus:1337")
    void shouldUpdateExistingDeploymentStatus() {
      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.COMMIT);
      DeploymentCollection oldDeployment = new DeploymentCollection();
      oldDeployment.put(buildDefaultDeployment(DeploymentType.COMMIT));
      deploymentStore.put("1", oldDeployment);

      Deployment newDeployment = buildDefaultDeployment(DeploymentType.COMMIT);
      newDeployment.setDisplayName("Updated DisplayName");
      newDeployment.setUrl("https://scm-manager.org");
      newDeployment.setStatus(DeploymentStatus.FAILURE);

      deploymentService.putCommitDeployment(newDeployment, repository, "1");

      DeploymentCollection expectedCollection = new DeploymentCollection();
      expectedCollection.put(newDeployment);
      assertThat(deploymentStore.get("1")).usingRecursiveComparison().isEqualTo(expectedCollection);
    }

    @Test
    @SubjectAware(permissions = "repository:writeCIStatus:1337")
    void shouldCreateExistingDeploymentStatus() {
      Deployment newDeployment = buildDefaultDeployment(DeploymentType.COMMIT);

      deploymentService.putCommitDeployment(newDeployment, repository, "1");

      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.COMMIT);
      DeploymentCollection expectedCollection = new DeploymentCollection();
      expectedCollection.put(newDeployment);
      assertThat(deploymentStore.get("1")).usingRecursiveComparison().isEqualTo(expectedCollection);
    }
  }

  @Nested
  class PutPullRequestDeployment {

    @Test
    void shouldNotAllowUpdateBecauseOfMissingPermission() {
      assertThatThrownBy(
        () -> deploymentService.putPullRequestDeployment(new Deployment(), repository, "1337")
      ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:writeCIStatus:1337")
    void shouldUpdateExistingDeploymentStatus() {
      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.PULL_REQUEST);
      DeploymentCollection oldDeployment = new DeploymentCollection();
      oldDeployment.put(buildDefaultDeployment(DeploymentType.PULL_REQUEST));
      deploymentStore.put("1", oldDeployment);

      Deployment newDeployment = buildDefaultDeployment(DeploymentType.PULL_REQUEST);
      newDeployment.setDisplayName("Updated DisplayName");
      newDeployment.setUrl("https://scm-manager.org");
      newDeployment.setStatus(DeploymentStatus.FAILURE);

      deploymentService.putPullRequestDeployment(newDeployment, repository, "1");

      DeploymentCollection expectedCollection = new DeploymentCollection();
      expectedCollection.put(newDeployment);
      assertThat(deploymentStore.get("1")).usingRecursiveComparison().isEqualTo(expectedCollection);
    }

    @Test
    @SubjectAware(permissions = "repository:writeCIStatus:1337")
    void shouldCreateExistingDeploymentStatus() {
      Deployment newDeployment = buildDefaultDeployment(DeploymentType.PULL_REQUEST);

      deploymentService.putPullRequestDeployment(newDeployment, repository, "1");

      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.PULL_REQUEST);
      DeploymentCollection expectedCollection = new DeploymentCollection();
      expectedCollection.put(newDeployment);
      assertThat(deploymentStore.get("1")).usingRecursiveComparison().isEqualTo(expectedCollection);
    }
  }

  @Nested
  class DeleteCommitDeployment {

    @Test
    void shouldNotAllowDeleteBecauseOfMissingPermission() {
      assertThatThrownBy(
        () -> deploymentService.deleteCommitDeployment(
          new DeploymentCollection.Key("source", "environment"),
          repository,
          "1337"
        )
      ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:writeCIStatus:1337")
    void shouldDeleteDeployment() {
      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.COMMIT);

      Deployment linuxDeployment = buildDefaultDeployment(DeploymentType.COMMIT);
      linuxDeployment.setSource("ArgoCD");
      linuxDeployment.setEnvironment("Linux");

      DeploymentCollection allDeployments = new DeploymentCollection();
      allDeployments.put(linuxDeployment);
      deploymentStore.put("1", allDeployments);

      deploymentService.deleteCommitDeployment(
        new DeploymentCollection.Key(linuxDeployment.getSource(), linuxDeployment.getEnvironment()),
        repository,
        "1"
      );

      DeploymentCollection actualDeployments = deploymentStore.get("1");
      assertThat(actualDeployments).usingRecursiveComparison().isEqualTo(new DeploymentCollection());
    }
  }

  @Nested
  class DeletePullRequestDeployment {

    @Test
    void shouldNotAllowDeleteBecauseOfMissingPermission() {
      assertThatThrownBy(
        () -> deploymentService.deletePullRequestDeployment(
          new DeploymentCollection.Key("source", "environment"),
          repository,
          "1337"
        )
      ).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @SubjectAware(permissions = "repository:writeCIStatus:1337")
    void shouldDeleteDeployment() {
      DataStore<DeploymentCollection> deploymentStore = buildStore(DeploymentType.PULL_REQUEST);

      Deployment linuxDeployment = buildDefaultDeployment(DeploymentType.PULL_REQUEST);
      linuxDeployment.setSource("ArgoCD");
      linuxDeployment.setEnvironment("Linux");

      DeploymentCollection allDeployments = new DeploymentCollection();
      allDeployments.put(linuxDeployment);
      deploymentStore.put("1", allDeployments);

      deploymentService.deletePullRequestDeployment(
        new DeploymentCollection.Key(linuxDeployment.getSource(), linuxDeployment.getEnvironment()),
        repository,
        "1"
      );

      DeploymentCollection actualDeployments = deploymentStore.get("1");
      assertThat(actualDeployments).usingRecursiveComparison().isEqualTo(new DeploymentCollection());
    }
  }
}
