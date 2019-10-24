/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Lock } from "../lock";
import { SArrayNodeMemory } from "./internal/s-array-node-memory";
import { SScalar } from "./internal/s-scalar";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized array node.
 */
export class SArrayNode extends SScalar<unknown[], SArrayNodeMemory> {
	constructor( memory: SArrayNodeMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SScalarParentMemory, name: string, properties, lock: Lock ) {
		return new SArrayNodeMemory( parent, name, properties, lock, SArrayNode );
	},

	getConstructor_() {
		return SArrayNode;
	},

	getType_() {
		return SType.ARRAY_NODE;
	}
});
