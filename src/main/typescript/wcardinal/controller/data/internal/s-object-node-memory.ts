/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isPlainObject } from "../../../util/lang/is-plain-object";
import { PlainObject } from "../../../util/lang/plain-object";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { SScalarMemory } from "./s-scalar-memory";
import { SScalarParentMemory } from "./s-scalar-parent-memory";
import { SType } from "./s-type";
import { WrapperConstructor } from "./wrapper-constructor";

export class SObjectNodeMemory extends SScalarMemory<PlainObject> {
	constructor(
		parent: SScalarParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor
	) {
		super( parent, name, properties, lock, wrapperConstructor, SType.OBJECT_NODE );
	}

	cast_( value: unknown ): PlainObject | null {
		if( isPlainObject(value) ) {
			return value;
		}

		return null;
	}
}
