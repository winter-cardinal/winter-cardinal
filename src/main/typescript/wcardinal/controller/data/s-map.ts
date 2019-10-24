/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../util/lang/plain-object";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SContainerParentMemory } from "./internal/s-container-parent-memory";
import { SMapBase } from "./internal/s-map-base";
import { SMapMemory } from "./internal/s-map-memory";
import { SMapPatch } from "./internal/s-map-patch";
import { SMapPatches } from "./internal/s-map-patches";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * Represents an added map item.
 *
 * @param V a value type
 */
export interface AddedMapItems<V> extends PlainObject<V | null> {}

/**
 * A removed map item.
 *
 * @param V a value type
 */
export interface RemovedMapItems<V> extends PlainObject<V | null> {}

/**
 * An update of an map item.
 *
 * @param V a value type
 */
export interface UpdatedMapItem<V> {
	/** a new value */
	newValue: V | null;

	/** an old value */
	oldValue: V | null;
}

/**
 * An updated map item.
 *
 * @param V a value type
 */
export interface UpdatedMapItems<V> extends PlainObject<UpdatedMapItem<V>> {}

/**
 * A synchronized map.
 *
 * @param V a value type
 */
export class SMap<V> extends SMapBase<V, SMapPatch<V>, SMapPatches<V>, SMapMemory<V>> {
	constructor( memory: SMapMemory<V> ) {
		super( memory );
	}
}

STypeToClass.put_({
	create_( parent: SContainerParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SMapMemory( parent, name, properties, lock, SMap );
	},

	getConstructor_() {
		return SMap;
	},

	getType_() {
		return SType.MAP;
	}
});
