/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../../util/lang/each";
import { isArray } from "../../../util/lang/is-array";
import { isEmptyArray, isNotEmptyArray } from "../../../util/lang/is-empty";
import { isEqual } from "../../../util/lang/is-equal";
import { checkNonNull } from "../../internal/check-non-null";
import { checkNonNullValue } from "../../internal/check-non-null-value";
import { checkNonNullValues } from "../../internal/check-non-null-values";
import { checkRange } from "../../internal/check-range";
import { checkSupported } from "../../internal/check-supported";
import { Comparator } from "../../internal/comparator";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { AddedListItems, RemovedListItems, UpdatedListItems } from "../s-list";
import { MovedListItems } from "../s-movable-list";
import { SContainerMemory } from "./s-container-memory";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SListBasePatch } from "./s-list-base-patch";
import { SListBasePatches } from "./s-list-base-patches";
import { WrapperConstructor } from "./wrapper-constructor";

const patchReset = <V>(
	index: number, values: Array<V | null>, list: Array<V | null>, added: AddedListItems<V>,
	removed: RemovedListItems<V>, updated: UpdatedListItems<V>
): number => {
	const llen = list.length;
	if( index < llen ) {
		const value = values[ index ];
		const lvalue = list[ index ];
		if( isEqual( value, lvalue ) ) {
			return index + 1;
		}

		let mode = 0;
		let ilast = index + 1;
		const vlen = values.length;
		for( let i = index + 1, imax = Math.min( index + 10, Math.max( llen, vlen ) ); i < imax; ++i ) {
			if( i < vlen && isEqual( lvalue, values[ i ] ) ) {
				mode = 1;
				ilast = i;
				break;
			}

			if( i < llen && isEqual( value, list[ i ] ) ) {
				mode = 2;
				ilast = i;
				break;
			}
		}

		if( mode !== 0 ) {
			for( let i = index + 1, imax = Math.min(ilast, Math.min( llen, vlen )); i < imax; ++i ) {
				if( isEqual( list[ i ], values[ i ] ) ) {
					mode = 3;
					ilast = i;
					break;
				}
			}
		}

		switch( mode ) {
		case 1:
			for( let i = index; i < ilast; ++i ) {
				const newValue = values[ i ];
				list.splice( i, 0, newValue );
				added.push({ index: i, value: newValue });
			}
			return ilast + 1;
		case 2:
			for( let i = index; i < ilast; ++i ) {
				const oldValue = list.splice( index, 1 )[ 0 ];
				removed.push({ index: index + removed.length - added.length, value: oldValue });
			}
			return index + 1;
		default:
			for( let i = index; i < ilast; ++i ) {
				const newValue = values[ i ];
				const oldValue = list.splice( i, 1, newValue )[ 0 ];
				updated.push({ index: i, newValue, oldValue });
			}
			return (mode === 3 ? ilast + 1 : ilast);
		}
	} else {
		const value = values[ index ];
		added.push({ index, value });
		list.push( value );
		return index + 1;
	}
};

export abstract class SListBaseMemory<V, P extends SListBasePatch<V>, PS extends SListBasePatches<V, P>>
	extends SContainerMemory<Array<V | null>, P, PS> {
	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor, patches: PS
	) {
		super( parent, name, properties, lock, wrapperConstructor, patches );
	}

	protected makeInitValues_(): Array<V | null> {
		return [];
	}

	onAdd_( index: number, value: V | null ): boolean {
		this._patches.add( this.getRevision_(), index, value );
		this.toUpdated_();
		return true;
	}

	onAddAll_( index: number, values: ArrayLike<V | null> ): boolean {
		this._patches.addAll( this.getRevision_(), index, values );
		this.toUpdated_();
		return isNotEmptyArray( values );
	}

	onRemove_( index: number ): void {
		this._patches.remove( this.getRevision_(), index );
		this.toUpdated_();
	}

	onClear_(): void {
		this._patches.clear_( this.getRevision_() );
		this.toUpdated_();
	}

	onSet_( index: number, newValue: V | null ): void {
		this._patches.set( this.getRevision_(), index, newValue );
		this.toUpdated_();
	}

	toEventData_(
		added?: AddedListItems<V> | null,
		removed?: RemovedListItems<V> | null,
		updated?: UpdatedListItems<V> | null
	): [
		null,
		AddedListItems<V>,
		RemovedListItems<V>,
		UpdatedListItems<V>,
		MovedListItems<V>?,
		MovedListItems<V>?
	] {
		return [
			null,
			added || [],
			removed || [],
			updated || []
		];
	}

	protected newInitArgs_(): unknown[] {
		return [ null, this.toArrayObject_() ];
	}

	protected newValueArgs_( initArgs: [ unknown, AddedListItems<V> ] ): unknown[] {
		return this.toEventData_( initArgs[ 1 ] );
	}

	patchReset_(
		values: Array<V | null>, list: Array<V | null>, added: AddedListItems<V>,
		removed: RemovedListItems<V>, updated: UpdatedListItems<V>
	): void {
		removed.length = 0;
		added.length = 0;
		updated.length = 0;

		for( let i = 0, imax = values.length; i < imax; ) {
			i = patchReset( i, values, list, added, removed, updated );
		}

		for( let imin = values.length, i = imin, imax = list.length; i < imax; ++i ) {
			removed.push({ index: imin + removed.length - added.length, value: list.splice( imin, 1 )[ 0 ] });
		}
	}

	size_(): number {
		return this._values.length;
	}

	isEmpty_(): boolean {
		return isEmptyArray( this._values );
	}

	indexOf_( value: V | null, comparator: Comparator, thisArg: unknown ): number {
		const values = this._values;
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			if( comparator.call( thisArg, values[ i ], value) ) {
				return i;
			}
		}
		return -1;
	}

	lastIndexOf_( value: V | null, comparator: Comparator, thisArg: unknown ): number {
		const values = this._values;
		for( let i = values.length - 1; 0 <= i; --i ) {
			if( comparator.call( thisArg, values[ i ], value) ) {
				return i;
			}
		}
		return -1;
	}

	contains_( value: V | null, comparator: Comparator, thisArg: unknown ): boolean {
		return 0 <= this.indexOf_( value, comparator, thisArg );
	}

	containsAll_( values: ArrayLike<V | null>, comparator: Comparator, thisArg: unknown ): boolean {
		if( values != null && isNotEmptyArray( values ) ) {
			for( let i = 0, imax = values.length; i < imax; ++i ) {
				if( this.indexOf_( values[ i ], comparator, thisArg ) < 0 ) {
					return false;
				}
			}
		}
		return true;
	}

	each_( iteratee: Iteratee<number, V | null, any>, thisArg: unknown, reverse: boolean ): this {
		const values = this._values;
		const wrapper = this.getWrapper_();
		if( reverse ) {
			for( let i = values.length - 1; 0 <= i; --i ) {
				if( iteratee.call( thisArg, values[ i ], i, wrapper ) === false ) {
					return this;
				}
			}
		} else {
			for( let i = 0, imax = values.length; i < imax; ++i ) {
				if( iteratee.call( thisArg, values[ i ], i, wrapper ) === false ) {
					return this;
				}
			}
		}
		return this;
	}

	add_( index: number, value: V | null ): boolean {
		checkSupported( this );
		checkNonNullValue( this, value );
		checkNonNull( index );
		checkRange( index, 0, this.size_() + 1 );

		this.lock_();
		try {
			return this.add__( index, value );
		} finally {
			this.unlock_();
		}
	}

	private add__( index: number, value: V | null ): boolean {
		const added: AddedListItems<V> = [{ index, value }];
		this._values.splice( index, 0, value );

		this.onAdd_( index, value );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( added, this.toEventData_( added ) );

		return true;
	}

	filter_( iteratee: Iteratee<number, V | null, any>, thisArg: unknown ): this {
		checkSupported( this );

		this.lock_();
		try {
			this.filter__( iteratee, thisArg );
		} finally {
			this.unlock_();
		}
		return this;
	}

	private filter__( iteratee: Iteratee<number, V | null, any>, thisArg: unknown ): void {
		const values = this._values;
		const wrapper = this.getWrapper_();

		const removed: RemovedListItems<V> = [];
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			const value = values[ i ];
			if( iteratee.call( thisArg, value, i, wrapper ) === true ) {
				continue;
			}
			removed.push({ index: i, value });
		}

		if( isNotEmptyArray( removed ) ) {
			for( let i = removed.length - 1; 0 <= i; --i ) {
				const index = removed[ i ].index;
				values.splice( index, 1 );
				this.onRemove_( index );
			}

			this.getAndIncrementRevision_();
			this.pushChangeEvent_( [], this.toEventData_( null, removed ) );
		}
	}

	find_( predicate: Iteratee<number, V | null, any>, thisArg: unknown, reverse: boolean ): V | null {
		const values = this._values;
		const wrapper = this.getWrapper_();
		if( reverse ) {
			for( let i = values.length - 1; 0 <= i; --i ) {
				if( predicate.call( thisArg, values[ i ], i, wrapper ) === true ) {
					return values[ i ];
				}
			}
		} else {
			for( let i = 0, imax = values.length; i < imax; ++i ) {
				if( predicate.call( thisArg, values[ i ], i, wrapper ) === true ) {
					return values[ i ];
				}
			}
		}
		return null;
	}

	get_( index: number ): V | null {
		return this._values[ index ];
	}

	set_( index: number, value: V | null ): V | null {
		checkSupported( this );
		checkNonNullValue( this, value );
		checkNonNull( index );
		checkRange( index, 0, this.size_() );

		this.lock_();
		try {
			return this.set__( index, value );
		} finally {
			this.unlock_();
		}
	}

	private set__( index: number, value: V | null ): V | null {
		const oldValue = this._values[ index ];
		this._values[ index ] = value;

		this.onSet_( index, value );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( null, this.toEventData_( null, null, [{index, newValue: value, oldValue}] ) );
		return oldValue;
	}

	reset_( index: number ): V | null {
		checkSupported( this );
		checkNonNull( index );
		checkRange( index, 0, this.size_() );

		this.lock_();
		try {
			return this.reset__( index );
		} finally {
			this.unlock_();
		}
	}

	private reset__( index: number ): V | null {
		const value = this._values[ index ];
		this.onSet_( index, value );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( null, this.toEventData_( null, null, [{index, newValue: value, oldValue: value}] ) );
		return value;
	}

	addAll_( index: number, values: ArrayLike<V | null> ): boolean {
		checkSupported( this );
		checkNonNullValues( this, values );
		checkNonNull( index );
		checkRange( index, 0, this.size_() + 1 );

		this.lock_();
		try {
			return this.addAll__( index, values );
		} finally {
			this.unlock_();
		}
	}

	private addAll__( index: number, values: ArrayLike<V | null> ): boolean {
		const added: AddedListItems<V> = [];
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			const newValue = values[ i ];
			added.push({ index: index + i, value: newValue });
			this._values.splice( index + i, 0, newValue );
		}

		if( isNotEmptyArray( added ) ) {
			this.onAddAll_( index, values );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( added, this.toEventData_( added ) );
			return true;
		}
		return false;
	}

	clearAndAdd_( value: V | null ): boolean {
		checkSupported( this );
		checkNonNullValue( this, value );

		this.lock_();
		try {
			return this.clearAndAdd__( value );
		} finally {
			this.unlock_();
		}
	}

	private clearAndAdd__( newValue: V | null ): boolean {
		const values = this._values;

		const added: AddedListItems<V> = [{ index: 0, value: newValue }];
		const removed: RemovedListItems<V> = [];
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			removed.push({ index: i, value: values[ i ] });
		}
		values.splice( 0, values.length, newValue );

		this.onClear_();
		this.onAdd_( 0, newValue );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( added, this.toEventData_( added, removed ) );

		return true;
	}

	clearAndAddAll_( values: ArrayLike<V | null> ): boolean {
		checkSupported( this );
		checkNonNullValues( this, values );

		this.lock_();
		try {
			return this.clearAndAddAll__( values );
		} finally {
			this.unlock_();
		}
	}

	private clearAndAddAll__( newValues: ArrayLike<V | null> ): boolean {
		const values = this._values;

		const removed: RemovedListItems<V> = [];
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			removed.push({ index: i, value: values[ i ] });
		}
		values.splice( 0, values.length );

		const added: AddedListItems<V> = [];
		for( let i = 0, imax = newValues.length; i < imax; ++i ) {
			const newValue = newValues[ i ];
			added.push({ index: values.length, value: newValue });
			values.push( newValue );
		}

		if( isNotEmptyArray( added ) || isNotEmptyArray( removed ) ) {
			this.onClear_();
			this.onAddAll_( 0, newValues );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( added, this.toEventData_( added, removed ) );
			return true;
		}
		return false;
	}

	remove_( index: number ): V | null {
		checkSupported( this );
		checkRange( index, 0, this.size_() );

		this.lock_();
		try {
			return this.remove__( index );
		} finally {
			this.unlock_();
		}
	}

	private remove__( index: number ): V | null {
		const oldValue = this._values.splice( index, 1 )[ 0 ];
		const removed: RemovedListItems<V> = [{ index, value: oldValue }];

		this.onRemove_( index );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( [], this.toEventData_( null, removed ) );
		return oldValue;
	}

	clear_(): this {
		checkSupported( this );

		this.lock_();
		try {
			this.clear__();
		} finally {
			this.unlock_();
		}

		return this;
	}

	private clear__(): void {
		const removed: RemovedListItems<V> = [];
		const values = this._values;
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			removed.push({ index: i, value: values[ i ] });
		}
		values.splice( 0, values.length );

		if( isNotEmptyArray( removed ) ) {
			this.onClear_();
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( [], this.toEventData_( null, removed ) );
		}
	}

	toArrayObject_(): AddedListItems<V> {
		const result: AddedListItems<V> = [];
		const values = this._values;
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			result.push({ index: i, value: values[ i ] });
		}
		return result;
	}

	toArray_(): Array<V | null> {
		return this._values;
	}

	toJson_(): Array<V | null> {
		return this._values;
	}

	fromJson_( json: unknown ): void {
		if( isArray( json ) ) {
			this.clearAndAddAll_( json );
		}
	}
}
