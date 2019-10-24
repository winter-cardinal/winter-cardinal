/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SJsonNodeMemory } from "./internal/s-json-node-memory";
import { SScalar } from "./internal/s-scalar";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized JSON node.
 */
export class SJsonNode extends SScalar<unknown, SJsonNodeMemory> {
	constructor( memory: SJsonNodeMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SJsonNodeMemory( parent, name, properties, lock, SJsonNode );
	},

	getConstructor_() {
		return SJsonNode;
	},

	getType_() {
		return SType.JSON_NODE;
	}
});
