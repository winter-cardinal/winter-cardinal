/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SClassMemory } from "./internal/s-class-memory";
import { SScalar } from "./internal/s-scalar";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized class.
 *
 * @param V a value type
 */
export class SClass<V> extends SScalar<V, SClassMemory<V>> {
	constructor( memory: SClassMemory<V> ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SClassMemory<unknown>( parent, name, properties, lock, SClass );
	},

	getConstructor_() {
		return SClass;
	},

	getType_() {
		return SType.CLASS;
	}
});
