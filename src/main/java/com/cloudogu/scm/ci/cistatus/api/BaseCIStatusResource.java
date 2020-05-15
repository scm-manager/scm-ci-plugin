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
import com.cloudogu.scm.ci.cistatus.service.CIStatus;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import com.cloudogu.scm.ci.cistatus.service.CIStatusService;
import sonia.scm.repository.Repository;

import static com.cloudogu.scm.ci.cistatus.api.CIStatusUtil.validateCIStatus;

class BaseCIStatusResource {

  private final CIStatusService ciStatusService;
  private final CIStatusMapper mapper;
  private final Repository repository;
  private final String id;
  private final CIStatusStore store;

  public BaseCIStatusResource(CIStatusService ciStatusService, CIStatusMapper mapper, Repository repository, String id, CIStatusStore store) {
    this.ciStatusService = ciStatusService;
    this.mapper = mapper;
    this.repository = repository;
    this.id = id;
    this.store = store;
  }

  CIStatusDto get(String type, String ciName) {
    CIStatusCollection ciStatusCollection = ciStatusService.get(store, repository, id);
    return mapper.map(repository, id, ciStatusCollection.get(type, ciName));
  }

  void put(String type, String ciName, CIStatusDto ciStatusDto) {
    validateCIStatus(type, ciName, ciStatusDto);
    CIStatus ciStatus = mapper.map(ciStatusDto);
    ciStatusService.put(store, repository, id, ciStatus);
  }
}
