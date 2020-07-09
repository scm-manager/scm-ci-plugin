/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.ci.cistatus.api;

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static com.cloudogu.scm.ci.PermissionCheck.mayRead;

@Extension
@Enrich(Changeset.class)
public class ChangesetStatusEnricher implements HalEnricher {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final CIStatusPathBuilder pathBuilder;

  @Inject
  public ChangesetStatusEnricher(CIStatusService ciStatusService, CIStatusMapper mapper, CIStatusPathBuilder pathBuilder) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.pathBuilder = pathBuilder;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    Changeset changeset = context.oneRequireByType(Changeset.class);

    if (mayRead(repository)) {
      appender.appendLink("ciStatus", pathBuilder.createChangesetCiStatusCollectionUri(repository.getNamespace(), repository.getName(), changeset.getId()));
      appender.appendEmbedded("ciStatus",
        ciStatusService.get(CIStatusStore.CHANGESET_STORE, repository, changeset.getId())
          .stream()
          .map(ciStatus -> mapper.map(repository, changeset.getId(), ciStatus))
          .collect(Collectors.toList()));
    }
  }
}

