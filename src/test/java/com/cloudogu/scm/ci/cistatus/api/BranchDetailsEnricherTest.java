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

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import de.otto.edison.hal.HalRepresentation;
import org.github.sdorra.jse.ShiroExtension;
import org.github.sdorra.jse.SubjectAware;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.repository.BranchDetails;
import sonia.scm.repository.Repository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(ShiroExtension.class)
@ExtendWith(MockitoExtension.class)
class BranchDetailsEnricherTest {

  private static final Repository REPOSITORY = new Repository("42", "git", "hitchhiker", "hog");

  @Mock
  private CIStatusService service;
  @Mock
  private CIStatusMapper mapper;

  @Mock
  private HalAppender appender;

  @InjectMocks
  private BranchDetailsEnricher enricher;

  @Test
  @SubjectAware(value = "tricia", permissions = "repository:read:42")
  void shouldEnrichWithStatus() {
    HalEnricherContext context = mockContext();
    CIStatusCollection ciStatus = createStatusCollection("jenkins", "sonar");

    when(mapper.map(any(), any(), any())).thenAnswer(invocation -> {
      final CIStatusDto dto = new CIStatusDto();
      dto.setType(invocation.getArgument(2, CIStatus.class).getType());
      return dto;
    });
    when(service.getByBranch(REPOSITORY, "master")).thenReturn(ciStatus);

    enricher.enrich(context, appender);

    verify(appender).appendEmbedded(
      eq("ciStatus"),
      argThat(
        (List<HalRepresentation> status) -> {
          assertThat(status)
            .extracting("type")
            .contains("jenkins", "sonar");
          return true;
        }
      ));
  }

  @Test
  @SubjectAware(value = "tricia", permissions = "repository:read:23")
  void shouldSkipStatusWithoutPermissionForRepository() {
    HalEnricherContext context = mockContext();

    enricher.enrich(context, appender);

    verify(appender, never()).appendEmbedded(
      any(),
      any(List.class));
  }

  private HalEnricherContext mockContext() {
    return new HalEnricherContext.Builder()
      .put(Repository.class, REPOSITORY)
      .put(BranchDetails.class, new BranchDetails("master"))
      .build();
  }

  private CIStatusCollection createStatusCollection(String... types) {
    CIStatusCollection ciStatus = new CIStatusCollection();
    Arrays.stream(types).forEach(t -> ciStatus.put(new CIStatus(t, null, null, null, null)));
    return ciStatus;
  }
}
