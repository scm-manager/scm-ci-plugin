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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import sonia.scm.IllegalIdentifierChangeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CIStatusUtilTest {

  @ParameterizedTest
  @MethodSource("validCiStatusProvider")
  void shouldBeValid(String statusFromParameter, String statusFromDto) {
    String type = "Jenkins";
    String ciName = "SomeName";
    CIStatusDto ciStatusDto = new CIStatusDto();
    ciStatusDto.setType(type);
    ciStatusDto.setName(ciName);

    CIStatusUtil.validateCIStatus(type, ciName, ciStatusDto);

    // should not throw an exception
  }

  static Object[][] validCiStatusProvider() {
    return new Object[][]{
      {"SomeName", "SomeName"},
      {"Some+Name", "Some Name"}, // plus char in a path parameter can either be a '+' or a space
      {"Some+Name", "Some+Name"},
      {"Some(Name)", "Some(Name)"}
    };
  }

  @Test
  void shouldThrowIllegalIdentifierChangeException() {
    String type = "Jenkins";
    String ciName = "SomeName";
    CIStatusDto ciStatusDto = new CIStatusDto();
    ciStatusDto.setType("type");
    ciStatusDto.setName("ciName");

    assertThrows(IllegalIdentifierChangeException.class, () -> CIStatusUtil.validateCIStatus(type, ciName, ciStatusDto));
  }
}
