/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../../util/lang/each";
import { isEmptyObject } from "../../../util/lang/is-empty";
import { PlainObject } from "../../../util/lang/plain-object";
import { sizeObject } from "../../../util/lang/size";
import { checkNonNull } from "../../internal/check-non-null";
import { checkNonNullValue } from "../../internal/check-non-null-value";
import { checkNonNullValues } from "../../internal/check-non-null-values";
import { checkSupported } from "../../internal/check-supported";
import { Comparator } from "../../internal/comparator";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { AddedMapItems, RemovedMapItems, UpdatedMapItems } from "../s-map";
import { SContainerMemory } from "./s-container-memory";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SMapBasePatch } from "./s-map-base-patch";
import { SMapBasePatches } from "./s-map-base-patches";
import { WrapperConstructor } from "./wrapper-constructor";

type SMapBaseEventData<V> = [ unknown, AddedMapItems<V>, RemovedMapItems<V>, UpdatedMapItems<V> ];

export abstract class SMapBaseMemory<V, P extends SMapBasePatch<V>, PS extends SMapBasePatches<V, P>>
	extends SContainerMemory<PlainObject<V | null>, P, PS> {
	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor, patches: PS
	) {
		super( parent, name, properties, lock, wrapperConstructor, patches );
	}

	protected makeInitValues_(): PlainObject<V | null> {
		return {};
	}

	onPut_( key: string, value: V | null ): boolean {
		this._patches.put_( this.getRevision_(), key, value );
		this.toUpdated_();
		return true;
	}

	onPutAll_( values: PlainObject<V | null> ): void {
		this._patches.putAll_( this.getRevision_(), values );
		this.toUpdated_();
	}

	onRemove_( key: string ): void {
		this._patches.remove_( this.getRevision_(), key );
		this.toUpdated_();
	}

	onRemoveAll_( values: PlainObject<V | null> ): void {
		this._patches.removeAll_( this.getRevision_(), values );
		this.toUpdated_();
	}

	onClear_(): void {
		this._patches.clear_( this.getRevision_() );
		this.toUpdated_();
	}

	protected newInitArgs_(): unknown[] {
		return [ null, this._values ];
	}

	protected newValueArgs_( initArgs: unknown[] ): unknown[] {
		return [ null, initArgs[ 1 ], {}, {} ];
	}

	toEventData_(
		added: AddedMapItems<V> | null, removed: RemovedMapItems<V> | null, updated: UpdatedMapItems<V> | null
	): [ null, AddedMapItems<V>, RemovedMapItems<V>, UpdatedMapItems<V> ] {
		return [
			null,
			added || {},
			removed || {},
			updated || {}
		];
	}

	mergeEvents_( current: SMapBaseEventData<V>, previous: SMapBaseEventData<V>, isFirst: boolean ): void {
		const cadded = current[ 1 ];
		const cremoved = current[ 2 ];
		const cupdated = current[ 3 ];
		const padded = previous[ 1 ];
		const premoved = previous[ 2 ];
		const pupdated = previous[ 3 ];

		// Operation order is as follows:
		//   Previous removed
		//   Previous added
		//   Previous updated
		//   Current removed
		//   Current added
		//   Current updated

		// Current removed -> previous updated -> previous added -> previous removed
		for( const key in cremoved ) {
			delete pupdated[ key ];
			if( key in padded ) {
				delete padded[ key ];
			} else {
				if( (key in premoved) !== true ) {
					premoved[ key ] = cremoved[ key ];
				}
			}
		}

		// Current added -> previous updated -> previous added
		for( const key in cadded ) {
			padded[ key ] = cadded[ key ];
		}

		// Current updated -> previous updated
		for( const key in cupdated ) {
			const update = pupdated[ key ];
			if( update != null ) {
				update.newValue = cupdated[ key ].newValue;
			} else {
				pupdated[ key ] = cupdated[ key ];
			}
		}
	}

	size_(): number {
		return sizeObject( this._values );
	}

	isEmpty_(): boolean {
		return isEmptyObject( this._values );
	}

	containsKey_( key: string ): boolean {
		return ( key in this._values );
	}

	containsValue_( value: unknown, comparator: Comparator, thisArg: unknown ): boolean {
		const values = this._values;
		for( const key in values ) {
			if( comparator.call( thisArg, values[ key ], value) ) {
				return true;
			}
		}
		return false;
	}

	each_( iteratee: Iteratee<string, V | null, any>, thisArg: unknown ): void {
		const values = this._values;
		const wrapper = this.getWrapper_();
		for( const key in values ) {
			if( iteratee.call( thisArg, values[ key ], key, wrapper ) === false ) {
				return;
			}
		}
	}

	filter_( iteratee: Iteratee<string, V | null, any>, thisArg: unknown ): this {
		checkSupported( this );

		this.lock_();
		try {
			this.filter__( iteratee, thisArg );
		} finally {
			this.unlock_();
		}
		return this;
	}

	find_( predicate: Iteratee<string, V | null, any>, thisArg: unknown ): unknown {
		const values = this._values;
		const wrapper = this.getWrapper_();
		for( const key in values ) {
			if( predicate.call( thisArg, values[ key ], key, wrapper ) === true ) {
				return values[ key ];
			}
		}
		return null;
	}

	values_(): Array<V | null> {
		const result = [];
		const values = this._values;
		for( const key in values ) {
			result.push( values[ key ] );
		}
		return result;
	}

	get_( key: string ): V | null {
		const values = this._values;
		if( key in values ) {
			return values[ key ];
		} else {
			return null;
		}
	}

	put_( key: string, value: V | null ): V | null {
		checkSupported( this );
		checkNonNull( key );
		checkNonNullValue( this, value );

		this.lock_();
		try {
			return this.put__( key, value );
		} finally {
			this.unlock_();
		}
	}

	reput_( key: string ): V | null {
		checkSupported( this );
		checkNonNull( key );

		this.lock_();
		try {
			return this.reput__( key );
		} finally {
			this.unlock_();
		}
	}

	putAll_( mappings: PlainObject<V | null> ): this {
		checkSupported( this );
		checkNonNullValues( this, mappings );

		this.lock_();
		try {
			this.putAll__( mappings );
		} finally {
			this.unlock_();
		}
		return this;
	}

	clearAndPut_( key: string, value: V | null ): null {
		checkSupported( this );
		checkNonNull( key );
		checkNonNullValue( this, value );

		this.lock_();
		try {
			return this.clearAndPut__( key, value );
		} finally {
			this.unlock_();
		}
	}

	clearAndPutAll_( mappings: PlainObject<V | null> ): this {
		checkSupported( this );
		checkNonNullValues( this, mappings );

		this.lock_();
		try {
			this.clearAndPutAll__( mappings );
		} finally {
			this.unlock_();
		}
		return this;
	}

	remove_( key: string ): V | null {
		checkSupported( this );

		this.lock_();
		try {
			return this.remove__( key );
		} finally {
			this.unlock_();
		}
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

	toJson_(): PlainObject<V | null> {
		return this._values;
	}

	fromJson_( json: unknown ): void {
		this.lock_();
		try {
			this.clear__();
			this.putAll__( json as PlainObject<V | null> );
		} finally {
			this.unlock_();
		}
	}

	toObject_(): PlainObject<V | null> {
		return this._values;
	}

	fromString_( str: string ) {
		this.fromJson_( JSON.parse( str ) );
	}

	protected abstract filter__( iteratee: Iteratee<string, V | null, any>, thisArg: unknown ): void;
	protected abstract put__( key: string, value: V | null ): V | null;
	protected abstract reput__( key: string ): V | null;
	protected abstract putAll__( mappings: PlainObject<V | null> ): void;
	protected abstract clearAndPut__( key: string, value: V | null ): null;
	protected abstract clearAndPutAll__( mappings: PlainObject<V | null> ): void;
	protected abstract remove__( key: string ): V | null;
	protected abstract clear__(): void;
}
