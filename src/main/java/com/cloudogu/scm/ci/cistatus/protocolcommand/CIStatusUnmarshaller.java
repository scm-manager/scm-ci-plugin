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
package com.cloudogu.scm.ci.cistatus.protocolcommand;

import com.cloudogu.scm.ci.cistatus.service.CIStatus;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

@Singleton
public class CIStatusUnmarshaller {

  private final JAXBContext context;

  public CIStatusUnmarshaller() {
    try {
      context = JAXBContext.newInstance(CIStatus.class);
    } catch (JAXBException e) {
      throw new IllegalStateException("failed to create jaxb context for ci status", e);
    }
  }

  public CIStatus unmarshal(InputStream input) {
    try {
      Unmarshaller unmarshaller = context.createUnmarshaller();
      return (CIStatus) unmarshaller.unmarshal(input);
    } catch (JAXBException ex) {
      throw new IllegalArgumentException("failed to unmarshal input stream", ex);
    }
  }
}
