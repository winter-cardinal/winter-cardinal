/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SNavigableMapMemory } from "./s-navigable-map-memory";
import { SType } from "./s-type";
import { WrapperConstructor } from "./wrapper-constructor";

export class SAscendingMapMemory<V> extends SNavigableMapMemory<V> {
	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor
	) {
		super( parent, name, properties, lock, wrapperConstructor, SType.ASCENDING_MAP );
	}
}
