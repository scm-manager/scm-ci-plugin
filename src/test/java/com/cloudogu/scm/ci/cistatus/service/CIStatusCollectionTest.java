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
package com.cloudogu.scm.ci.cistatus.service;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CIStatusCollectionTest {

  @Test
  void shouldPutAndGet() {
    CIStatus ciStatus = createCiStatus(Status.PENDING);
    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    ciStatusCollection.put(ciStatus);

    CIStatus ciStatusReceived = ciStatusCollection.get("jenkins", "build1");

    assertThat(ciStatusReceived).isSameAs(ciStatus);
  }

  @Test
  void shouldUpdate() {
    CIStatus ciStatusPending = createCiStatus(Status.PENDING);
    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    ciStatusCollection.put(ciStatusPending);

    CIStatus ciStatusSuccess = createCiStatus(Status.SUCCESS);
    ciStatusCollection.put(ciStatusSuccess);

    CIStatus ciStatusReceived = ciStatusCollection.get("jenkins", "build1");

    assertThat(Iterables.size(ciStatusCollection)).isEqualTo(1);

    assertThat(ciStatusReceived).isSameAs(ciStatusSuccess);
  }

  @Test
  void shouldBeIterable() {
    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    ciStatusCollection.put(createCiStatus("build1", Status.PENDING));
    ciStatusCollection.put(createCiStatus("build2", Status.PENDING));

    assertThat(Iterables.size(ciStatusCollection)).isEqualTo(2);
  }

  @Test
  void shouldBeMarshallable() {
    CIStatusCollection ciStatusCollection = new CIStatusCollection();

    CIStatus build1 = createCiStatus("build1", Status.PENDING);
    CIStatus build2 = createCiStatus("build2", Status.PENDING);
    ciStatusCollection.put(build1);
    ciStatusCollection.put(build2);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    JAXB.marshal(ciStatusCollection, baos);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CIStatusCollection unmarshalled = JAXB.unmarshal(bais, CIStatusCollection.class);

    assertThat(unmarshalled.get("jenkins", "build1")).isEqualTo(build1);
    assertThat(unmarshalled.get("jenkins", "build2")).isEqualTo(build2);
  }

  @Test
  void shouldStream() {
    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    ciStatusCollection.put(createCiStatus("build1", Status.PENDING));
    ciStatusCollection.put(createCiStatus("build2", Status.PENDING));

    List<String> results = ciStatusCollection
      .stream()
      .map(CIStatus::getName)
      .collect(Collectors.toList());

    assertThat(results).contains("build1", "build2");
  }


  private CIStatus createCiStatus(String name, Status success) {
    return new CIStatus("jenkins", name, null, success, "https://test.de");
  }

  private CIStatus createCiStatus(Status success) {
    return createCiStatus("build1", success);
  }
}
