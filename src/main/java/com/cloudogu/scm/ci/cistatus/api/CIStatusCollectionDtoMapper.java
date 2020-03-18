/**
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

import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import de.otto.edison.hal.Embedded;
import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CIStatusCollectionDtoMapper {

  private final CIStatusMapper mapper;
  private final CIStatusPathBuilder ciStatusPathBuilder;

  @Inject
  public CIStatusCollectionDtoMapper(CIStatusMapper mapper, CIStatusPathBuilder ciStatusPathBuilder) {
    this.mapper = mapper;
    this.ciStatusPathBuilder = ciStatusPathBuilder;
  }

  HalRepresentation map(Stream<CIStatus> ciStatus, Repository repository, String changesetId) {
    return new HalRepresentation(
      new Links.Builder().self(ciStatusPathBuilder.createCollectionUri(repository.getNamespace(), repository.getName(), changesetId)).build(),
      Embedded.embedded("ciStatus", ciStatus
        .map(s -> mapper.map(repository, changesetId, s))
        .collect(Collectors.toList())));
  }
}
