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

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.repository.Repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryStatusEnricherTest {

  static final Repository REPOSITORY = new Repository("1", "git", "space", "X");

  @Mock
  CIStatusPathBuilder pathBuilder;

  @InjectMocks
  RepositoryStatusEnricher enricher;

  @Mock
  HalEnricherContext context;
  @Mock
  HalAppender appender;

  @Mock
  Subject subject;

  @BeforeEach
  void setUpDefaults() {
    ThreadContext.bind(subject);
    lenient().when(context.oneRequireByType(Repository.class)).thenReturn(REPOSITORY);
  }

  @Test
  void shouldNotEnrichWithoutPermissions() {
    when(subject.isPermitted(any(String.class))).thenReturn(false);

    enricher.enrich(context, appender);

    verify(appender, never()).appendLink(any(), any());
  }

  @Test
  void shouldAppendLink() {
    when(subject.isPermitted("repository:readCIStatus:1")).thenReturn(true);
    when(pathBuilder.createChangesetCiStatusCollectionUri("space", "X", "REVISION")).thenReturn("http://scm.com/REVISION");

    enricher.enrich(context, appender);

    verify(appender).appendLink("ciStatus", "http://scm.com/{revision}");
  }
}
