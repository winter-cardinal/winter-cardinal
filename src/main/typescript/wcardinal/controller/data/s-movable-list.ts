/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Event } from "../../event/event";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SContainerParentMemory } from "./internal/s-container-parent-memory";
import { SListBase } from "./internal/s-list-base";
import { SMovableListMemory } from "./internal/s-movable-list-memory";
import { SMovableListPatch } from "./internal/s-movable-list-patch";
import { SMovableListPatches } from "./internal/s-movable-list-patches";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";
import { AddedListItems, RemovedListItems, UpdatedListItems } from "./s-list";

/**
 * A moved item.
 *
 * @param V a value type
 */
export interface MovedListItem<V> {
	/** a new index */
	newIndex: number;

	/** an old index */
	oldIndex: number;

	/** an item value */
	value: V | null;
}

/**
 * An array of moved items.
 *
 * @param V a value type
 */
export type MovedListItems<V> = Array<MovedListItem<V>>;

/**
 * A synchronized movable list.
 *
 * @param V a value type
 */
export class SMovableList<V> extends SListBase<V, SMovableListPatch<V>, SMovableListPatches<V>, SMovableListMemory<V>> {
	constructor( memory: SMovableListMemory<V> ) {
		super( memory );
	}

	/**
	 * Moves the element at the index `oldIndex` to the index `newIndex`.
	 *
	 * @param oldIndex index of the element to be moved
	 * @param newIndex index where the element is moved to
	 * @throws Error if the one of the indices is out of range (index < 0 || size() <= index)
	 * @returns this
	 */
	move( oldIndex: number, newIndex: number ): this {
		this.__mem__.move_( oldIndex, newIndex );
		return this;
	}

	/**
	 * Triggered when a list is initialized or changed.
	 * If a list is initialized when event handlers are set,
	 * event handlers are invoked with items at that time.
	 *
	 *     on( "value", ( event, addedItems, removedItems, updatedItems,
	 *         movedItemsSortedByNewIndices, movedItemsSortedByOldIndices ) => {
	 *         // DO SOMETHING HERE
	 *     })
	 *
	 * @event value
	 * @param event an event object
	 * @param addedItems inserted items sorted by their indices in ascending order
	 * @param removedItems removed items sorted by their indices in ascending order
	 * @param updatedItems updated items sorted by their indices in ascending order
	 * @param movedItemsSortedByNewIndices moved items sorted by their new indices in ascending order
	 * @param movedItemsSortedByOldIndices moved items sorted by their old indices in ascending order
	 */
	onvalue?(
		event: Event, addedItems: AddedListItems<V>, removedItems: RemovedListItems<V>, updatedItems: UpdatedListItems<V>,
		movedItemsSortedByNewIndices: MovedListItems<V>, movedItemsSortedByOldIndices: MovedListItems<V>
	): void;

	/**
	 * Triggered when a list is changed.
	 *
	 *     on( "change", ( event, addedItems, removedItems, updatedItems,
	 *         movedItemsSortedByNewIndices, movedItemsSortedByOldIndices ) => {
	 *         // DO SOMETHING HERE
	 *     })
	 *
	 * @event change
	 * @param event an event object
	 * @param addedItems inserted items sorted by their indices in ascending order
	 * @param removedItems removed items sorted by their indices in ascending order
	 * @param updatedItems updated items sorted by their indices in ascending order
	 * @param movedItemsSortedByNewIndices moved items sorted by their new indices in ascending order
	 * @param movedItemsSortedByOldIndices moved items sorted by their old indices in ascending order
	 */
	onchange?(
		event: Event, addedItems: AddedListItems<V>, removedItems: RemovedListItems<V>, updatedItems: UpdatedListItems<V>,
		movedItemsSortedByNewIndices: MovedListItems<V>, movedItemsSortedByOldIndices: MovedListItems<V>
	): void;
}

STypeToClass.put_({
	create_( parent: SContainerParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SMovableListMemory( parent, name, properties, lock, SMovableList );
	},

	getConstructor_() {
		return SMovableList;
	},

	getType_() {
		return SType.MOVABLE_LIST;
	}
});
