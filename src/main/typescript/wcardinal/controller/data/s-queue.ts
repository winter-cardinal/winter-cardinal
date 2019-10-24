/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isNumber } from "../../util/lang/is-number";
import { Properties } from "../internal/properties";
import { Lock } from "../lock";
import { SContainerParentMemory } from "./internal/s-container-parent-memory";
import { SQueueMemory } from "./internal/s-queue-memory";
import { SType } from "./internal/s-type";
import { STypeToClass } from "./internal/s-type-to-class";
import { SROQueue } from "./s-ro-queue";

/**
 * A synchronized queue.
 *
 * @param V a value type
 */
export class SQueue<V> extends SROQueue<V> {
	constructor( memory: SQueueMemory<V> ) {
		super( memory );
	}

	/**
	 * Adds the specified element to this queue.
	 *
	 * @param element the element to be added
	 * @throws Error if this is read-only
	 * @throws Error if the specified element is null and this does not support a null element
	 * @returns true
	 */
	add( element: V | null ): boolean {
		return this.__mem__.add_( element );
	}

	/**
	 * Adds all the elements of the specified array to this queue.
	 *
	 * @param elements the array of elements to be inserted
	 * @throws Error if this is read-only
	 * @throws Error if the specified array is null
	 * @throws Error if the specified array contains null and this does not support a null value
	 * @returns true if this queue is changed as a result of the call
	 */
	addAll( elements: ArrayLike<V | null> ): boolean {
		return this.__mem__.addAll_( elements );
	}

	/**
	 * Adds the specified element to this queue.
	 *
	 * @param element the element to be added
	 * @throws Error if this is read-only
	 * @throws Error if the specified element is null and this does not support a null element
	 * @returns true
	 */
	offer( element: V | null ): boolean {
		return this.__mem__.add_( element );
	}

	/**
	 * Removes and returns the first value.
	 *
	 * @returns the removed first element
	 * @throws Error if this is read-only
	 * @throws Error if this queue is empty
	 */
	remove(): V | null {
		return this.__mem__.remove_();
	}

	/**
	 * Removes and returns the first value.
	 *
	 * @returns the removed first element, or null if this queue is empty
	 * @throws Error if this is read-only
	 */
	poll(): V | null {
		return this.__mem__.poll_();
	}

	/**
	 * Removes all of the elements in this queue.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	clear(): this {
		this.__mem__.clear_();
		return this;
	}

	/**
	 * Clear this queue and then appends the specified element at the end of this queue.
	 *
	 * @param element element to be pushed
	 * @throws Error if this is read-only
	 * @throws Error if the specified element is null and this does not support a null
	 * @returns true
	 */
	clearAndAdd( element: V | null ): boolean {
		return this.__mem__.clearAndAdd_( element );
	}

	/**
	 * Clear this queue and then appends all of the elements in the specified array to the end of this queue.
	 *
	 * @param elements the array of elements to be appended
	 * @throws Error if this is read-only
	 * @throws Error if the specified array is null
	 * @throws Error if the specified array contains null and this does not support a null
	 * @returns true if this list is changed as a result of the call
	 */
	clearAndAddAll( elements: ArrayLike<V | null> ): boolean {
		return this.__mem__.clearAndAddAll_( elements );
	}

	/**
	 * Clear this queue and then appends the specified element to this queue without raising an exception.
	 *
	 * @param element element to be appended to this queue
	 * @returns true if the specified element is successfully appended
	 * @throws Error if this queue does not permit null, and the specified element is null
	 */
	clearAndOffer( element: V | null ): boolean {
		return this.__mem__.clearAndAdd_( element );
	}

	/**
	 * Clear this queue and then appends the specified elements to this queue.
	 *
	 * @param elements elements to be appended to this queue
	 * @returns true if this queue is changed as a result of the call
	 * @throws Error  if the specified collection is null, or if this queue does not permit null,
	 * and the specified collection contains null elements
	 */
	clearAndOfferAll( elements: ArrayLike<V | null> ): boolean {
		return this.__mem__.clearAndAddAll_( elements );
	}

	/**
	 * Sets to the specified capacity.
	 * Older elements are removed to fit to the specified capacity
	 * if the number of elements this queue has is larger than the specified capacity.
	 *
	 * @param capacity the new capacity
	 * @returns the previous capacity
	 * @throws Error if this is read-only
	 * @throws Error if the specified capacity is null or negative
	 */
	setCapacity( capacity: number ): number {
		return this.__mem__.setCapacity_( capacity );
	}

	/** @hidden */
	capacity(): number;

	/** @hidden */
	capacity( capacity: number ): number;

	/** @hidden */
	capacity( capacity?: number ): number {
		if( isNumber( capacity ) ) {
			return this.__mem__.setCapacity_( capacity );
		} else {
			return this.__mem__.getCapacity_();
		}
	}

	/**
	 * Sets to the specified JSON array.
	 *
	 * @param json JSON array to be set
	 * @returns this
	 */
	fromJson( json: unknown ): this {
		this.__mem__.fromJson_( json );
		return this;
	}
}

STypeToClass.put_({
	create_( parent: SContainerParentMemory, name: string, properties: Properties, lock: Lock ) {
		return new SQueueMemory<unknown>( parent, name, properties, lock, SQueue );
	},

	getConstructor_() {
		return SQueue;
	},

	getType_() {
		return SType.QUEUE;
	}
});
