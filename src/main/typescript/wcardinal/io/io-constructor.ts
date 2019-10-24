/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Io } from "./io";

export interface IoConstructor {
	// tslint:disable-next-line: callable-types
	new ( parameters: {} ): Io;
}
