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
