/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SNumber } from "./s-number";
import { SRealNumberMemory } from "./s-real-number-memory";

export class SRealNumber extends SNumber<SRealNumberMemory> {
	constructor( memory: SRealNumberMemory ) {
		super( memory );
	}
}
