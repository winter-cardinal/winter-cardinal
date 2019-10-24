/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../util/lang/plain-object";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SObjectNodeMemory } from "./internal/s-object-node-memory";
import { SScalar } from "./internal/s-scalar";
import { SScalarParentMemory } from "./internal/s-scalar-parent-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * A synchronized object node.
 */
export class SObjectNode extends SScalar<PlainObject, SObjectNodeMemory> {
	constructor( memory: SObjectNodeMemory ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_(
		parent: SScalarParentMemory, name: string, properties: Properties, lock: Lock
	) {
		return new SObjectNodeMemory( parent, name, properties, lock, SObjectNode );
	},

	getConstructor_() {
		return SObjectNode;
	},

	getType_() {
		return SType.OBJECT_NODE;
	}
});
