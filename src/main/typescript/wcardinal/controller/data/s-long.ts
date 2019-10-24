/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SIntegralNumber } from "./internal/s-integral-number";
import { SLongMemory } from "./internal/s-long-memory";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized long integer.
 */
export class SLong extends SIntegralNumber {
	constructor( memory: SLongMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SLongMemory( parent, name, properties, lock, SLong );
	},

	getConstructor_() {
		return SLong;
	},

	getType_() {
		return SType.LONG;
	}
});
