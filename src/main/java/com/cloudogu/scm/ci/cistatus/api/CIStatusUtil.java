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

import sonia.scm.ContextEntry;
import sonia.scm.IllegalIdentifierChangeException;

final class CIStatusUtil {

  private CIStatusUtil() {}

  static boolean validateCIStatus(String type, String ciName, CIStatusDto statusDto) {
    if (!type.equals(statusDto.getType()) || !ciName.equals(statusDto.getName())) {
      throw new IllegalIdentifierChangeException(ContextEntry.ContextBuilder.entity(CIStatusDto.class,
        statusDto.getName() + ":" + statusDto.getType()), String.format("changing identifier attributes is not allowed (type '%s' != '%s' or name '%s' != '%s'", type, statusDto.getType(), ciName, statusDto.getName()));
    }
    return true;
  }
}
