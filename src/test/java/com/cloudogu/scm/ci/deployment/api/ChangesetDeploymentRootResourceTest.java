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

import com.cloudogu.scm.ci.RepositoryResolver;
import com.cloudogu.scm.ci.deployment.service.DeploymentCollection;
import com.cloudogu.scm.ci.deployment.service.DeploymentService;
import com.cloudogu.scm.ci.deployment.service.DeploymentStatus;
import com.cloudogu.scm.ci.deployment.service.DeploymentType;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.util.Providers;
import jakarta.inject.Provider;
import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.web.JsonMockHttpResponse;
import sonia.scm.web.RestDispatcher;

import java.net.URI;
import java.net.URISyntaxException;

import static com.cloudogu.scm.ci.deployment.Constants.MEDIA_TYPE;
import static com.cloudogu.scm.ci.deployment.DeploymentTestData.buildDefaultDeployment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, ShiroExtension.class})
@SubjectAware("Trainer Red")
class ChangesetDeploymentRootResourceTest {

  private final Repository repository = RepositoryTestData.createHeartOfGold();
  private final String domainForLinks = "https://test-domain.de/scm/api/";

  @Mock
  private RepositoryManager repositoryManager;
  @Mock
  private DeploymentService deploymentService;

  private RestDispatcher dispatcher;

  @BeforeEach
  void setup() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create(domainForLinks));
    Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    DeploymentStatusPathBuilder pathBuilder = new DeploymentStatusPathBuilder(scmPathInfoStoreProvider);
    DeploymentMapper mapper = new DeploymentMapperImpl();
    mapper.setPathBuilder(pathBuilder);

    ChangesetDeploymentRootResource resource = new ChangesetDeploymentRootResource(
      new RepositoryResolver(repositoryManager),
      mapper,
      new DeploymentCollectionMapper(mapper),
      deploymentService
    );

    dispatcher = new RestDispatcher();
    dispatcher.addSingletonResource(resource);
    repository.setId("1337");
  }

  @Nested
  class GetCommitDeployments {

    @BeforeEach
    void setup() {
      lenient().when(repositoryManager.get(any(NamespaceAndName.class))).thenReturn(repository);

      DeploymentCollection deployments = new DeploymentCollection();
      deployments.put(buildDefaultDeployment(DeploymentType.COMMIT));
      lenient().when(deploymentService.getAllCommitDeployments(repository, "1337")).thenReturn(deployments);
    }

    @Test
    void shouldThrowNotFoundBecauseRepositoryIsUnknown() throws URISyntaxException {
      when(repositoryManager.get(any(NamespaceAndName.class))).thenReturn(null);

      MockHttpRequest request = MockHttpRequest.get("/v2/deployments/unknown-namespace/unknown-name/changesets/1337");
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(404);
      verifyNoInteractions(deploymentService);
    }

    @Test
    void shouldReturnDeploymentDtos() throws URISyntaxException {
      MockHttpRequest request = MockHttpRequest.get(
        String.format("/v2/deployments/%s/%s/changesets/1337", repository.getNamespace(), repository.getName())
      );
      JsonMockHttpResponse response = new JsonMockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(200);

      JsonNode responseBody = response.getContentAsJson();
      assertThat(responseBody.isArray()).isTrue();
      assertThat(responseBody.size()).isEqualTo(1);
      assertThat(responseBody.get(0).get("source").asText()).isEqualTo("ArgoCD");
      assertThat(responseBody.get(0).get("environment").asText()).isEqualTo("ARM64+Linux");
      assertThat(responseBody.get(0).get("displayName").asText()).isEqualTo("Test Deployment");
      assertThat(responseBody.get(0).get("url").asText()).isEqualTo("https://next-scm.cloudogu.com");
      assertThat(responseBody.get(0).get("status").asText()).isEqualTo(DeploymentStatus.SUCCESS.name());
    }

    @Test
    void shouldNotAddAnyWriteLinksBecausePermissionIsMissing() throws URISyntaxException {
      MockHttpRequest request = MockHttpRequest.get(
        String.format("/v2/deployments/%s/%s/changesets/1337", repository.getNamespace(), repository.getName())
      );
      JsonMockHttpResponse response = new JsonMockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(200);
      JsonNode responseBody = response.getContentAsJson();
      assertThat(responseBody.get(0).get("_links")).isNull();
    }

    @Test
    @SubjectAware(permissions = "repository:read:1337")
    void shouldNotAddAnyWriteLinksBecauseWritePermissionIsMissing() throws URISyntaxException {
      MockHttpRequest request = MockHttpRequest.get(
        String.format("/v2/deployments/%s/%s/changesets/1337", repository.getNamespace(), repository.getName())
      );
      JsonMockHttpResponse response = new JsonMockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(200);
      JsonNode responseBody = response.getContentAsJson();
      assertThat(responseBody.get(0).get("_links")).isNull();
    }

    @Test
    @SubjectAware(permissions = "repository:writeCIStatus:1337")
    void shouldAddWriteLinks() throws URISyntaxException {
      MockHttpRequest request = MockHttpRequest.get(
        String.format("/v2/deployments/%s/%s/changesets/1337", repository.getNamespace(), repository.getName())
      );
      JsonMockHttpResponse response = new JsonMockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(200);
      JsonNode responseBody = response.getContentAsJson();
      assertThat(responseBody.get(0).get("_links").get("delete").get("href").asText()).isEqualTo(
        String.format(
          "%sv2/deployments/%s/%s/changesets/1337/ArgoCD/ARM64+Linux",
          domainForLinks,
          repository.getNamespace(),
          repository.getName())
      );
      assertThat(responseBody.get(0).get("_links").get("update").get("href").asText()).isEqualTo(
        String.format(
          "%sv2/deployments/%s/%s/changesets/1337",
          domainForLinks,
          repository.getNamespace(),
          repository.getName())
      );
    }
  }

  @Nested
  class DeleteDeployment {

    @Test
    void shouldThrowNotFoundBecauseRepositoryIsUnknown() throws URISyntaxException {
      MockHttpRequest request = MockHttpRequest.delete(
          "/v2/deployments/unknown-namespace/unknown-name/changesets/1337/ArgoCD/ARM64+Linux"
        );
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(404);
      verifyNoInteractions(deploymentService);
    }

    @Test
    void shouldDeleteDeployment() throws URISyntaxException {
      when(repositoryManager.get(any(NamespaceAndName.class))).thenReturn(repository);

      MockHttpRequest request = MockHttpRequest.delete(
        String.format(
          "/v2/deployments/%s/%s/changesets/1337/ArgoCD/ARM64+Linux",
          repository.getNamespace(),
          repository.getName()
        )
      );
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(204);
      verify(deploymentService).deleteCommitDeployment(
        new DeploymentCollection.Key("ArgoCD", "ARM64+Linux"),
        repository,
        "1337"
      );
    }
  }

  @Nested
  class PutDeployment {

    @Test
    void shouldThrowNotFoundBecauseRepositoryIsUnknown() throws URISyntaxException {
      MockHttpRequest request = MockHttpRequest.put(
          "/v2/deployments/unknown-namespace/unknown-name/changesets/1337/42"
        ).contentType(MEDIA_TYPE)
        .content("""
          {
            "source": "ArgoCD",
            "environment": "ARM64+Linux",
            "displayName": "Test Deployment",
            "url": "https://next-scm.cloudogu.com",
            "status": "SUCCESS"
          }""".getBytes());
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(404);
      verifyNoInteractions(deploymentService);
    }

    @Test
    void shouldPutDeployment() throws URISyntaxException {
      when(repositoryManager.get(any(NamespaceAndName.class))).thenReturn(repository);

      MockHttpRequest request = MockHttpRequest.put(
          String.format("/v2/deployments/%s/%s/changesets/1337", repository.getNamespace(), repository.getName())
        ).contentType(MEDIA_TYPE)
        .content("""
          {
            "source": "ArgoCD",
            "environment": "ARM64+Linux",
            "displayName": "Test Deployment",
            "url": "https://next-scm.cloudogu.com",
            "status": "SUCCESS"
          }""".getBytes());
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(request, response);

      assertThat(response.getStatus()).isEqualTo(204);
      verify(deploymentService).putCommitDeployment(
        buildDefaultDeployment(DeploymentType.COMMIT),
        repository,
        "1337"
      );
    }
  }
}
