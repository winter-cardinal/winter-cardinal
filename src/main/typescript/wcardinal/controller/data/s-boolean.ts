/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SBooleanMemory } from "./internal/s-boolean-memory";
import { SScalar } from "./internal/s-scalar";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized boolean.
 *
 * @param V a value type
 */
export class SBoolean extends SScalar<boolean, SBooleanMemory> {
	constructor( memory: SBooleanMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SBooleanMemory( parent, name, properties, lock, SBoolean );
	},

	getConstructor_() {
		return SBoolean;
	},

	getType_() {
		return SType.BOOLEAN;
	}
});
