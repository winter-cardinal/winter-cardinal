/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { IllegalArgumentException } from "../../../exception/illegal-argument-exception";
import { NoSuchElementException } from "../../../exception/no-such-element-exception";
import { Iteratee } from "../../../util/lang/each";
import { isArray } from "../../../util/lang/is-array";
import { isEmptyArray, isNotEmptyArray } from "../../../util/lang/is-empty";
import { isEqual } from "../../../util/lang/is-equal";
import { isNumber } from "../../../util/lang/is-number";
import { checkNonNull } from "../../internal/check-non-null";
import { checkNonNullValue } from "../../internal/check-non-null-value";
import { checkNonNullValues } from "../../internal/check-non-null-values";
import { checkRange } from "../../internal/check-range";
import { checkSupported } from "../../internal/check-supported";
import { Comparator } from "../../internal/comparator";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { AddedQueueItems, RemovedQueueItems } from "../s-ro-queue";
import { SContainerMemory } from "./s-container-memory";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SQueueData } from "./s-queue-data";
import { SQueuePatch } from "./s-queue-patch";
import { SQueuePatches } from "./s-queue-patches";
import { WrapperConstructor } from "./wrapper-constructor";

type SQueueEventData<V> = [
	unknown,
	AddedQueueItems<V>,
	RemovedQueueItems<V>
];

const checkCapacity = ( capacity: number ): void => {
	if( isNumber( capacity ) !== true || capacity < 0 ) {
		throw IllegalArgumentException.create();
	}
};

const removeHead = <V>( queue: Array<V | null>, size: number, removed: RemovedQueueItems<V> ): void => {
	for( let i = 0; i < size; ++i ) {
		removed.push( queue[ i ] );
	}
	queue.splice( 0, size );
};

const removeTail = <V>( queue: Array<V | null>, from: number, removed: RemovedQueueItems<V> ): void => {
	for( let i = from, imax = queue.length; i < imax; ++i ) {
		removed.push( queue[ i ] );
	}
	queue.splice( from, queue.length - from );
};

const addTail = <V>(
	queue: Array<V | null>, from: number, values: Array<V | null>, added: AddedQueueItems<V>
): void => {
	for( let i = from, imax = values.length; i < imax; ++i ) {
		const value = values[ i ];
		queue.push( value );
		added.push( value );
	}
};

const patchReset = <V>(
	values: Array<V | null>, capacity: number,
	data: SQueueData<V>, added: AddedQueueItems<V>, removed: RemovedQueueItems<V>
): void => {
	removed.length = 0;
	added.length = 0;

	if( 0 <= capacity ) {
		data._capacity = capacity;
	}

	const queue = data._queue;
	if( isEmptyArray( values ) ) {
		removeHead( queue, queue.length, removed );
	} else {
		const first = values[ 0 ];
		for( let i = 0, imax = queue.length; i < imax; ++i ) {
			const value = queue[ i ];
			if( isEqual( first, value ) !== true ) {
				continue;
			}

			// REMOVE THE HEAD
			removeHead( queue, i, removed );

			// SEARCH AND REMOVE THE TAIL
			i = 1;
			imax = queue.length;
			for( ; i < imax; ++i ) {
				if( i < values.length && isEqual( values[ i ], queue[ i ] ) ) {
					continue;
				}
				removeTail( queue, i, removed );
				break;
			}

			// ADD THE REST
			if( i < values.length ) {
				addTail( queue, i, values, added );
			}

			return;
		}

		removeHead( queue, queue.length, removed );
		addTail( queue, 0, values, added );
	}
};

const patchMap = <V>(
	cadded: Array<V | null>, cremoved: number, ccapacity: number,
	data: SQueueData<V>, padded: AddedQueueItems<V>, premoved: RemovedQueueItems<V>
): void => {
	const queue = data._queue;
	for( let i = 0, imax = cadded.length; i < imax; ++i ) {
		const value = cadded[ i ];
		queue.push( value );
		padded.push( value );
	}

	for( let i = 0; i < cremoved; ++i ) {
		premoved.push( queue[ i ] );
	}
	queue.splice( 0, cremoved );

	if( 0 <= ccapacity ) {
		data._capacity = ccapacity;
	}
};

export class SQueueMemory<V> extends SContainerMemory<SQueueData<V>, SQueuePatch<V>, SQueuePatches<V>> {
	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor
	) {
		super( parent, name, properties, lock, wrapperConstructor, new SQueuePatches( properties ) );
	}

	protected makeInitValues_(): SQueueData<V> {
		return new SQueueData();
	}

	protected newInitArgs_(): unknown[] {
		return [ null, this._values._queue ];
	}

	protected newValueArgs_( initArgs: unknown[] ): unknown[] {
		return [ null, initArgs[ 1 ], [] ];
	}

	mergeEvents_( current: SQueueEventData<V>, previous: SQueueEventData<V> ): void {
		const cadded = current[ 1 ];
		const cremoved = current[ 2 ];
		const padded = previous[ 1 ];
		const premoved = previous[ 2 ];

		padded.push.apply( padded, cadded );
		premoved.push.apply( premoved, cremoved );
	}

	toEventData_(
		added: AddedQueueItems<V>, removed: RemovedQueueItems<V>
	): [ null, AddedQueueItems<V>, RemovedQueueItems<V> ] {
		return [ null, added, removed ];
	}

	getFirst_( throwWhenFailed: boolean ): V | null {
		if( isEmptyArray( this._values._queue ) ) {
			if( throwWhenFailed ) {
				throw NoSuchElementException.create();
			} else {
				return null;
			}
		}

		return this._values._queue[ 0 ];
	}

	private onAdd_( value: V | null ): boolean {
		this._patches.add_( this.getRevision_(), value );
		this.toUpdated_();
		return true;
	}

	private onAddAll_( values: ArrayLike<V | null> ): boolean {
		this._patches.addAll_( this.getRevision_(), values );
		this.toUpdated_();
		return isNotEmptyArray( values );
	}

	private onRemove_(): void {
		this._patches.remove_( this.getRevision_() );
		this.toUpdated_();
	}

	private onClear_(): void {
		this._patches.clear_( this.getRevision_() );
		this.toUpdated_();
	}

	private onCapacity_( capacity: number ): void {
		this._patches.capacity_( this.getRevision_(), capacity );
		this.toUpdated_();
	}

	private trimToCapacity_( removed: Array<V | null> ): void {
		const queue = this._values._queue;
		const size = queue.length - this._values._capacity;
		if( 0 < size ) {
			for( let i = 0; i < size; ++i ) {
				removed.push( queue[ i ] );
				this.onRemove_();
			}
			queue.splice( 0, size );
		}
	}

	add_( element: V | null ): boolean {
		checkSupported( this );
		checkNonNullValue( this, element );

		this.lock_();
		try {
			return this.add__( element );
		} finally {
			this.unlock_();
		}
	}

	addAll_( elements: ArrayLike<V | null> ): boolean {
		checkSupported( this );
		checkNonNullValues( this, elements );

		this.lock_();
		try {
			return this.addAll__( elements );
		} finally {
			this.unlock_();
		}
	}

	remove_(): V | null {
		checkSupported( this );

		this.lock_();
		try {
			return this.remove__( true );
		} finally {
			this.unlock_();
		}
	}

	poll_(): V | null {
		checkSupported( this );

		this.lock_();
		try {
			return this.remove__( false );
		} finally {
			this.unlock_();
		}
	}

	clear_(): void {
		checkSupported( this );

		this.lock_();
		try {
			this.clear__();
		} finally {
			this.unlock_();
		}
	}

	private add__( value: V | null ): boolean {
		const added: Array<V | null> = [ value ];
		const removed: Array<V | null> = [];
		this._values._queue.push( value );

		this.onAdd_( value );
		this.trimToCapacity_( removed );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( added, this.toEventData_( added, removed ) );

		return true;
	}

	private addAll__( values: ArrayLike<V | null> ): boolean {
		const added: Array<V | null> = [];
		const removed: Array<V | null> = [];
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			const newValue = values[ i ];
			added.push( newValue );
			this._values._queue.push( newValue );
		}

		if( isNotEmptyArray( added ) ) {
			this.onAddAll_( values );
			this.trimToCapacity_( removed );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( added, this.toEventData_( added, removed ) );
			return true;
		}
		return false;
	}

	private remove__( throwWhenFailed: boolean ): V | null {
		if( isEmptyArray( this._values._queue ) ) {
			if( throwWhenFailed ) {
				throw NoSuchElementException.create();
			} else {
				return null;
			}
		}

		const added: Array<V | null> = [];
		const removed: Array<V | null> = this._values._queue.splice( 0, 1 );
		this.onRemove_();
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( added, this.toEventData_( added, removed ) );
		return removed[ 0 ];
	}

	private clear__() {
		const added: Array<V | null> = [];
		const removed: Array<V | null> = [];
		const queue = this._values._queue;
		for( let i = 0, imax = queue.length; i < imax; ++i ) {
			removed.push( queue[ i ] );
		}
		queue.splice( 0, queue.length );

		if( isNotEmptyArray( removed ) ) {
			this.onClear_();
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( added, this.toEventData_( added, removed ) );
		}
	}

	clearAndAdd_( element: V | null ): boolean {
		checkSupported( this );
		checkNonNullValue( this, element );

		this.lock_();
		try {
			return this.clearAndAdd__( element );
		} finally {
			this.unlock_();
		}
	}

	private clearAndAdd__( newValue: V | null ): boolean {
		const queue = this._values._queue;

		const added = [ newValue ];
		const removed = [];
		for( let i = 0, imax = queue.length; i < imax; ++i ) {
			removed.push( queue[ i ] );
		}
		queue.splice( 0, queue.length );
		this.onClear_();

		queue.push( newValue );
		this.onAdd_( newValue );
		this.trimToCapacity_( removed );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( added, this.toEventData_( added, removed ) );

		return true;
	}

	clearAndAddAll_( elements: ArrayLike<V | null> ): boolean {
		checkSupported( this );
		checkNonNullValues( this, elements );

		this.lock_();
		try {
			return this.clearAndAddAll__( elements );
		} finally {
			this.unlock_();
		}
	}

	private clearAndAddAll__( newValues: ArrayLike<V | null> ): boolean {
		const values = this._values._queue;

		const removed = [];
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			removed.push( values[ i ] );
		}
		values.splice( 0, values.length );

		const added = [];
		for( let i = 0, imax = newValues.length; i < imax; ++i ) {
			const newValue = newValues[ i ];
			added.push( newValue );
			values.push( newValue );
		}

		if( isNotEmptyArray( added ) || isNotEmptyArray( removed ) ) {
			this.onClear_();
			this.onAddAll_( newValues );
			this.trimToCapacity_( removed );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( added, this.toEventData_( added, removed ) );
			return true;
		}
		return false;
	}

	getCapacity_(): number {
		return this._values._capacity;
	}

	setCapacity_( capacity: number ): number {
		checkSupported( this );
		checkNonNull( capacity );
		checkCapacity( capacity );

		this.lock_();
		try {
			return this.setCapacity__( capacity );
		} finally {
			this.unlock_();
		}
	}

	private setCapacity__( newCapacity: number ): number {
		const oldCapacity = this._values._capacity;
		if( newCapacity !== oldCapacity ) {
			this._values._capacity = newCapacity;
			this.onCapacity_( newCapacity );
			const removed: Array<V | null> = [];
			this.trimToCapacity_( removed );
			this.getAndIncrementRevision_();
			if( isNotEmptyArray( removed ) ) {
				const added: Array<V | null> = [];
				this.pushChangeEvent_( added, this.toEventData_( added, removed ) );
			}
		}
		return oldCapacity;
	}

	get_( index: number ): V | null {
		const queue = this._values._queue;
		checkRange( index, 0, queue.length );
		return queue[ index ];
	}

	size_(): number {
		return this._values._queue.length;
	}

	isEmpty_(): boolean {
		return isEmptyArray( this._values._queue );
	}

	each_( iteratee: Iteratee<number, V | null, any>, thisArg: unknown, reverse: boolean ): void {
		const queue = this._values._queue;
		const wrapper = this.getWrapper_();
		if( reverse ) {
			for( let i = queue.length - 1; 0 <= i; --i ) {
				if( iteratee.call( thisArg, queue[ i ], i, wrapper ) === false ) {
					return;
				}
			}
		} else {
			for( let i = 0, imax = queue.length; i < imax; ++i ) {
				if( iteratee.call( thisArg, queue[ i ], i, wrapper ) === false ) {
					return;
				}
			}
		}
	}

	find_( predicate: Iteratee<number, V | null, any>, thisArg: unknown, reverse: boolean ): unknown {
		const queue = this._values._queue;
		const wrapper = this.getWrapper_();
		if( reverse ) {
			for( let i = queue.length - 1; 0 <= i; --i ) {
				if( predicate.call( thisArg, queue[ i ], i, wrapper ) === true ) {
					return queue[ i ];
				}
			}
		} else {
			for( let i = 0, imax = queue.length; i < imax; ++i ) {
				if( predicate.call( thisArg, queue[ i ], i, wrapper ) === true ) {
					return queue[ i ];
				}
			}
		}
		return null;
	}

	indexOf_( value: unknown, comparator: Comparator, thisArg: unknown ): number {
		const queue = this._values._queue;
		for( let i = 0, imax = queue.length; i < imax; ++i ) {
			if( comparator.call( thisArg, queue[ i ], value) ) {
				return i;
			}
		}
		return -1;
	}

	lastIndexOf_( value: unknown, comparator: Comparator, thisArg: unknown ): number {
		const queue = this._values._queue;
		for( let i = queue.length; 0 <= i; --i ) {
			if( comparator.call( thisArg, queue[ i ], value) ) {
				return i;
			}
		}
		return -1;
	}

	contains_( value: unknown, comparator: Comparator, thisArg: unknown ): boolean {
		return 0 <= this.indexOf_( value, comparator, thisArg );
	}

	containsAll_( values: ArrayLike<unknown>, comparator: Comparator, thisArg: unknown ): boolean {
		if( values != null && isNotEmptyArray( values ) ) {
			for( let i = 0, imax = values.length; i < imax; ++i ) {
				if( this.indexOf_( values[ i ], comparator, thisArg ) < 0 ) {
					return false;
				}
			}
		}
		return true;
	}

	patch_( patches: unknown[] ): unknown[] | null {
		const revision = this.getRevision_();
		const data = this._values;
		const added: AddedQueueItems<V> = [];
		const removed: RemovedQueueItems<V> = [];

		const type = patches[ 2 ];
		if( type === 0 ) {
			patchReset(
				patches[ 3 ] as Array<V | null>,
				patches[ 4 ] as number,
				data, added, removed
			);
		} else {
			const startRevision = patches[ 0 ] as number;
			const ceilingRevision = Math.max( revision - startRevision, 0 );
			for( let i = 3, imax = patches.length; i < imax; i += 4 ) {
				if( ceilingRevision <= (patches[ i + 0 ] as number) ) {
					patchMap(
						patches[ i + 1 ] as Array<V | null>,
						patches[ i + 2 ] as number,
						patches[ i + 3 ] as number,
						data, added, removed
					);
				}
			}
		}

		if( isEmptyArray( added ) && isEmptyArray( removed ) ) {
			return null;
		} else {
			return [ null, added, removed ];
		}
	}

	toArray_(): Array<V | null> {
		return this._values._queue;
	}

	toJson_(): Array<V | null> {
		return this._values._queue;
	}

	toString_(): string {
		return JSON.stringify( this._values._queue );
	}

	fromJson_( json: unknown ): void {
		if( isArray( json ) ) {
			this.clearAndAddAll_( json );
		}
	}
}
