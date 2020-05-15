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
package com.cloudogu.scm.ci.cistatus.update;

import com.cloudogu.scm.ci.cistatus.CIStatusStore;
import com.cloudogu.scm.ci.cistatus.service.CIStatusCollection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.RepositoryLocationResolver;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.function.BiConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreUpdateStepTest {

  @Mock
  RepositoryLocationResolver repositoryLocationResolver;
  @Mock
  RepositoryLocationResolver.RepositoryLocationResolverInstance<Path> resolverInstance;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  DataStoreFactory dataStoreFactory;
  @Mock
  DataStore<CIStatusCollection> newDataStore;
  @Mock
  DataStore<CIStatusCollection> oldDataStore;

  @InjectMocks
  StoreUpdateStep updateStep;

  @Test
  void shouldCopyCollectionFromOldStoreToNewStore() {
    when(dataStoreFactory.withType(CIStatusCollection.class).withName(CIStatusStore.CHANGESET_STORE.name).forRepository("42").build())
      .thenReturn(newDataStore);
    when(dataStoreFactory.withType(CIStatusCollection.class).withName("ciStatus").forRepository("42").build())
      .thenReturn(oldDataStore);
    CIStatusCollection ciStatusCollection = new CIStatusCollection();
    when(oldDataStore.getAll()).thenReturn(Collections.singletonMap("abc", ciStatusCollection));
    when(repositoryLocationResolver.forClass(Path.class)).thenReturn(resolverInstance);
    doAnswer(invocationOnMock -> {
      invocationOnMock.getArgument(0, BiConsumer.class).accept("42", null);
      return null;
    }).when(resolverInstance).forAllLocations(any());

    updateStep.doUpdate();

    verify(newDataStore).put("abc", ciStatusCollection);
  }
}
