/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BasicsCallableArgumentClass(@JsonProperty(required = true) String field) {}
