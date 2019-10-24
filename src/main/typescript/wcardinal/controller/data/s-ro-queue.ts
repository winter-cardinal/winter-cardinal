/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Event } from "../../event/event";
import { Iteratee } from "../../util/lang/each";
import { Comparator, defaultComparator } from "../internal/comparator";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SContainer } from "./internal/s-container";
import { SContainerParentMemory } from "./internal/s-container-parent-memory";
import { SQueueData } from "./internal/s-queue-data";
import { SQueueMemory } from "./internal/s-queue-memory";
import { SQueuePatch } from "./internal/s-queue-patch";
import { SQueuePatches } from "./internal/s-queue-patches";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";

/**
 * Added queue items
 *
 * @param V a value type
 */
export interface AddedQueueItems<V> extends Array<V | null> {}

/**
 * Removed queue items
 *
 * @param V a value type
 */
export interface RemovedQueueItems<V> extends Array<V | null> {}

/**
 * A synchronized read-only queue.
 *
 * @param V a value type
 */
export class SROQueue<V> extends SContainer<SQueueData<V>, SQueuePatch<V>, SQueuePatches<V>, SQueueMemory<V>> {
	constructor( memory: SQueueMemory<V> ) {
		super( memory );
	}

	/**
	 * Returns the capacity.
	 *
	 * @returns {number} the capacity
	 */
	getCapacity(): number {
		return this.__mem__.getCapacity_();
	}

	/** @hidden */
	capacity(): number {
		return this.__mem__.getCapacity_();
	}

	/**
	 * Returns an element at the specified index.
	 *
	 * @param {number} index an index of the element
	 * @returns {*} the element at the specified index
	 * @throws Error if the index is out of bounds (index < 0 || size() <= index)
	 */
	get( index: number ): V | null {
		return this.__mem__.get_( index );
	}

	/**
	 * Returns the head of this queue, or returns null if this queue is empty.
	 *
	 * @returns the head of this queue, or returns null if this queue is empty
	 */
	element(): V | null {
		return this.__mem__.getFirst_( true );
	}

	/**
	 * Returns the head of this queue, or returns null if this queue is empty.
	 *
	 * @returns the head of this queue, or returns null if this queue is empty
	 */
	peek(): V | null {
		return this.__mem__.getFirst_( false );
	}

	/**
	 * Returns the size.
	 *
	 * @returns the size
	 */
	size(): number {
		return this.__mem__.size_();
	}

	/**
	 * Returns true if this queue is empty.
	 *
	 * @returns {boolean} true if this queue is empty
	 */
	isEmpty(): boolean {
		return this.__mem__.isEmpty_();
	}

	/**
	 * Iterates over elements of this queue, calling the iteratee for each element.
	 * The iteratee is bound to the thisArg and invoked with three arguments: value, key, this queue.
	 * The iteratee may exit iteration early by explicitly returning false.
	 *
	 * @param iteratee the function called per iteration
	 * @param thisArg the this binding of the iteratee
	 * @param reverse true to iterate in the reverse order
	 * @returns this
	 */
	each( iteratee: Iteratee<number, V | null, this>, thisArg: unknown= this, reverse= false ): this {
		this.__mem__.each_( iteratee, thisArg, reverse );
		return this;
	}

	/**
	 * Returns the first element which the specified predicate returns true, or null if there is no such element.
	 * The predicate is bound to the thisArg and invoked with three arguments: element, index and this instance.
	 *
	 * @param predicate the function called per iteration
	 * @param thisArg the this binding of the predicate
	 * @param reverse true to iterate in the reverse order
	 * @returns the first element which the predicate returns true, or null if there is no such element
	 */
	find( predicate: Iteratee<number, V | null, this>, thisArg: unknown= this, reverse= false ): unknown {
		return this.__mem__.find_( predicate, thisArg, reverse );
	}

	/**
	 * Returns the index of the specified value.
	 *
	 * @param element value to search for
	 * @param comparator comparator
	 * @param thisArg context of the comparator
	 * @returns the index of the specified value or -1 if this queue does not contain the specified value
	 */
	indexOf( element: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): number {
		return this.__mem__.indexOf_( element, comparator, thisArg );
	}

	/**
	 * Returns the last index of the specified value.
	 *
	 * @param element value to search for
	 * @param comparator comparator
	 * @param thisArg context of the comparator
	 * @returns the last index of the specified value or -1 if this queue does not contain the specified value
	 */
	lastIndexOf( element: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): number {
		return this.__mem__.lastIndexOf_( element, comparator, thisArg );
	}

	/**
	 * Returns true if this queue contains the specified value.
	 *
	 * @param element value to search for
	 * @param comparator comparator
	 * @param thisArg context of the comparator
	 * @returns true if this queue contains the specified value
	 */
	contains( element: unknown, comparator: Comparator= defaultComparator, thisArg: unknown= this ): boolean {
		return this.__mem__.contains_( element, comparator, thisArg );
	}

	/**
	 * Returns true if this queue contains all the specified values.
	 *
	 * @param elements values to search for
	 * @param comparator comparator
	 * @param thisArg context of the comparator
	 * @returns true if this queue contains all the specified values
	 */
	containsAll(
		elements: ArrayLike<unknown>, comparator: Comparator= defaultComparator,
		thisArg: unknown= this
	): boolean {
		return this.__mem__.containsAll_( elements, comparator, thisArg );
	}

	/**
	 * Returns the array representing this queue.
	 * Must not change the returned array.
	 *
	 * @returns the array representing this queue
	 */
	toArray(): Array<V | null> {
		return this.__mem__.toArray_();
	}

	toJson(): Array<V | null> {
		return this.__mem__.toJson_();
	}

	toString(): string {
		return this.__mem__.toString_();
	}

	/**
	 * Triggered when a queue is initialized or changed.
	 * If a queue is initialized when event handlers are set,
	 * event handlers are invoked immediately.
	 *
	 *     on( "value", ( event, addedItems, removedItems ) => {
	 *         // DO SOMETHING HERE
	 *     })
	 *
	 * @event value
	 * @param event an event object
	 * @param addedItems enqueued items sorted by their enqueue timing in ascending order
	 * @param removedItems dequeued items sorted by their dequeue timing in ascending order
	 */
	onvalue?( event: Event, addedItems: AddedQueueItems<V>, removedItems: RemovedQueueItems<V> ): void;

	/**
	 * Triggered when a queue is changed.
	 *
	 *     on( "change", ( event, addedItems, removedItems ) => {
	 *         // DO SOMETHING HERE
	 *     })
	 *
	 * @event change
	 * @param event event object.
	 * @param addedItems enqueued items sorted by their enqueue timing in ascending order
	 * @param removedItems dequeued items sorted by their dequeue timing in ascending order
	 */
	onchange?( event: Event, addedItems: AddedQueueItems<V>, removedItems: RemovedQueueItems<V> ): void;
}

STypeToClass.put_({
	create_( parent: SContainerParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SQueueMemory<unknown>( parent, name, properties, lock, SROQueue );
	},

	getConstructor_() {
		return SROQueue;
	},

	getType_() {
		return SType.RO_QUEUE;
	}
});
