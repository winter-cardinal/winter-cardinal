/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SDoubleMemory } from "./internal/s-double-memory";
import { SRealNumber } from "./internal/s-real-number";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized double-precision floating-point number.
 */
export class SDouble extends SRealNumber {
	constructor( memory: SDoubleMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SDoubleMemory( parent, name, properties, lock, SDouble );
	},

	getConstructor_() {
		return SDouble;
	},

	getType_() {
		return SType.DOUBLE;
	}
});
