/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Event } from "../../event/event";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SContainerParentMemory } from "./internal/s-container-parent-memory";
import { SListBase } from "./internal/s-list-base";
import { SListMemory } from "./internal/s-list-memory";
import { SListPatch } from "./internal/s-list-patch";
import { SListPatches } from "./internal/s-list-patches";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * Represents an added list item.
 *
 * @param V a value type
 */
export interface AddedListItem<V> {
	/** an item index */
	index: number;

	/** an item value */
	value: V | null;
}

/**
 * Represents a removed list item.
 *
 * @param V a value type
 */
export interface RemovedListItem<V> {
	/** an item index */
	index: number;

	/** an item value */
	value: V | null;
}

/**
 * Represents an updated list item.
 *
 * @param V a value type
 */
export interface UpdatedListItem<V> {
	/** an item index */
	index: number;

	/** a new value */
	newValue: V | null;

	/** an old value */
	oldValue: V | null;
}

/**
 * An array of added list items.
 *
 * @param V a value type
 */
export type AddedListItems<V> = Array<AddedListItem<V>>;

/**
 * An array of removed list items.
 *
 * @param V a value type
 */
export type RemovedListItems<V> = Array<RemovedListItem<V>>;

/**
 * An array of updated list items.
 *
 * @param V a value type
 */
export type UpdatedListItems<V> = Array<UpdatedListItem<V>>;

/**
 * A synchronized list.
 *
 * @param V a value type
 */
export class SList<V> extends SListBase<V, SListPatch<V>, SListPatches<V>, SListMemory<V>> {
	constructor( memory: SListMemory<V> ) {
		super( memory );
	}

	/**
	 * Triggered when a list is initialized or changed.
	 * If a list is initialized when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 * @event value
	 * @param event an event object
	 * @param addedItems inserted items sorted by their indices in ascending order
	 * @param removedItems removed items sorted by their indices in ascending order
	 * @param updatedItems updated items sorted by their indices in ascending order
	 * @internal
	 */
	onvalue?(
		event: Event, addedItems: AddedListItems<V>,
		removedItems: RemovedListItems<V>, updatedItems: UpdatedListItems<V>
	): void;

	/**
	 * Triggered when items of a list are changed.
	 *
	 * @event change
	 * @param event an event object
	 * @param addedItems inserted items sorted by their indices in ascending order
	 * @param removedItems removed items sorted by their indices in ascending order
	 * @param updatedItems updated items sorted by their indices in ascending order
	 * @internal
	 */
	onchange?(
		event: Event, addedItems: AddedListItems<V>,
		removedItems: RemovedListItems<V>, updatedItems: UpdatedListItems<V>
	): void;
}

STypeToClass.put_({
	create_(
		parent: SContainerParentMemory, name: string, properties: Properties, lock: Lock
	) {
		return new SListMemory( parent, name, properties, lock, SList );
	},

	getConstructor_() {
		return SList;
	},

	getType_() {
		return SType.LIST;
	}
});
