/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { SIntegralNumberMemory } from "./s-integral-number-memory";
import { SScalarParentMemory } from "./s-scalar-parent-memory";
import { SType } from "./s-type";
import { WrapperConstructor } from "./wrapper-constructor";

export class SIntegerMemory extends SIntegralNumberMemory {
	constructor(
		parent: SScalarParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor
	) {
		super( parent, name, properties, lock, wrapperConstructor, SType.INTEGER );
	}
}
