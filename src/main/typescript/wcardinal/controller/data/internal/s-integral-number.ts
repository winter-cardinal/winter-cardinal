/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SIntegralNumberMemory } from "./s-integral-number-memory";
import { SNumber } from "./s-number";

export class SIntegralNumber extends SNumber<SIntegralNumberMemory> {
	constructor( memory: SIntegralNumberMemory ) {
		super( memory );
	}
}
