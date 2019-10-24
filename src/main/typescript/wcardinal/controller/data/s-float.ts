/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SFloatMemory } from "./internal/s-float-memory";
import { SRealNumber } from "./internal/s-real-number";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized floating-point number.
 */
export class SFloat extends SRealNumber {
	constructor( memory: SFloatMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SFloatMemory( parent, name, properties, lock, SFloat );
	},

	getConstructor_() {
		return SFloat;
	},

	getType_() {
		return SType.FLOAT;
	}
});
