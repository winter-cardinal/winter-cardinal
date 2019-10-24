/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isNumber } from "../../../util/lang/is-number";
import { SNumberMemory } from "./s-number-memory";

export class SRealNumberMemory extends SNumberMemory {
	cast_( value: unknown ): number | null {
		if( isNumber(value) ) {
			return value;
		}

		return null;
	}
}
