/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SIntegerMemory } from "./internal/s-integer-memory";
import { SIntegralNumber } from "./internal/s-integral-number";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized integer.
 */
export class SInteger extends SIntegralNumber {
	constructor( memory: SIntegerMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SIntegerMemory( parent, name, properties, lock, SInteger );
	},

	getConstructor_() {
		return SInteger;
	},

	getType_() {
		return SType.INTEGER;
	}
});
