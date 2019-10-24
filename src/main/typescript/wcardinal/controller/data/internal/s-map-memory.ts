/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../../util/lang/each";
import { hasOwn } from "../../../util/lang/has-own";
import { isEmptyObject } from "../../../util/lang/is-empty";
import { isEqual } from "../../../util/lang/is-equal";
import { PlainObject } from "../../../util/lang/plain-object";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { AddedMapItems, RemovedMapItems, UpdatedMapItems } from "../s-map";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SMapBaseMemory } from "./s-map-base-memory";
import { SMapPatch } from "./s-map-patch";
import { SMapPatches } from "./s-map-patches";
import { WrapperConstructor } from "./wrapper-constructor";

const patchMap = <V>(
	cadded: AddedMapItems<V>, cremoved: string[], map: PlainObject<V | null>,
	padded: AddedMapItems<V>, premoved: RemovedMapItems<V>, pupdated: UpdatedMapItems<V>
): void => {
	// Removed
	for( let i = 0, imax = cremoved.length; i < imax; ++i ) {
		const key = cremoved[ i ];
		if( key in map ) {
			const value = map[ key ];
			delete map[ key ];
			delete pupdated[ key ];
			if( key in padded ) {
				delete padded[ key ];
			} else {
				premoved[ key ] = value;
			}
		}
	}

	// Added
	for( const key in cadded ) {
		const newValue = cadded[ key ];
		if( key in map ) {
			const oldValue = map[ key ];
			const update = pupdated[ key ];
			if( update != null ) {
				update.newValue = newValue;
			} else {
				pupdated[ key ] = { newValue, oldValue };
			}
		} else {
			padded[ key ] = newValue;
		}
		map[ key ] = newValue;
	}
};

const patchReset = <V>(
	values: PlainObject<V | null>,
	map: PlainObject<V | null>,
	added: AddedMapItems<V>, removed: RemovedMapItems<V>, updated: UpdatedMapItems<V>
): void => {
	for( const key in map ) {
		const oldValue = map[ key ];
		if( key in values ) {
			const newValue = values[ key ];
			if( isEqual( newValue, oldValue ) !== true ) {
				updated[ key ] = { newValue, oldValue };
				map[ key ] = newValue;
			}
		} else {
			removed[ key ] = oldValue;
			delete map[ key ];
		}
	}

	for( const key in values ) {
		if( ! ( key in map ) ) {
			map[ key ] = added[ key ] = values[ key ];
		}
	}
};

export class SMapMemory<V> extends SMapBaseMemory<V, SMapPatch<V>, SMapPatches<V>> {
	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor
	) {
		super( parent, name, properties, lock, wrapperConstructor, new SMapPatches( properties ) );
	}

	protected filter__( iteratee: Iteratee<string, V | null, any>, thisArg: unknown ): void {
		let isChanged = false;
		const values = this._values;
		const wrapper = this.getWrapper_();

		const removed: RemovedMapItems<V> = {};
		for( const key in values ) {
			const value = values[ key ];
			if( iteratee.call( thisArg, value, key, wrapper ) !== true ) {
				removed[ key ] = value;
				isChanged = true;
			}
		}

		if( isChanged ) {
			for( const key in removed ) {
				delete values[ key ];
			}

			const eventData = this.toEventData_( null, removed, null );
			this.onRemoveAll_( removed );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	protected put__( key: string, value: V | null ): V | null {
		const values = this._values;

		let eventData = null;
		let oldValue = null;
		if( key in values ) {
			oldValue = values[ key ];
			const updated: UpdatedMapItems<V> = {};
			updated[ key ] = { newValue: value, oldValue };
			eventData = this.toEventData_( null, null, updated );
		} else {
			const added: AddedMapItems<V> = {};
			added[ key ] = value;
			eventData = this.toEventData_( added, null, null );
		}
		values[ key ] = value;

		this.onPut_( key, value );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( eventData[ 1 ], eventData );

		return oldValue;
	}

	protected reput__( key: string ): V | null {
		let value = null;
		const values = this._values;
		if( key in values ) {
			value = values[ key ];
			const updated: UpdatedMapItems<V> = {};
			updated[ key ] = { newValue: value, oldValue: value };
			const eventData = this.toEventData_( null, null, updated );
			this.onPut_( key, value );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
		return value;
	}

	protected putAll__( mappings: PlainObject<V | null> ): void {
		let isChanged = false;
		const values = this._values;

		const added: AddedMapItems<V> = {};
		const updated: UpdatedMapItems<V> = {};
		for( const key in mappings ) {
			if( hasOwn(mappings, key) ) {
				const newValue = mappings[ key ];
				if( key in values ) {
					updated[ key ] = { newValue, oldValue: values[ key ] };
				} else {
					added[ key ] = newValue;
				}
				values[ key ] = newValue;

				isChanged = true;
			}
		}

		if( isChanged ) {
			const eventData = this.toEventData_( added, null, updated );
			this.onPutAll_( mappings );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	protected clearAndPut__( key: string, value: V | null ): null {
		const values = this._values;

		const removed: RemovedMapItems<V> = {};
		for( const rkey in values ) {
			if( rkey !== key ) {
				removed[ rkey ] = values[ rkey ];
				delete values[ rkey ];
			}
		}

		let eventData = null;
		if( key in values ) {
			const updated: UpdatedMapItems<V> = {};
			updated[ key ] = { newValue: value, oldValue: values[ key ] };
			eventData = this.toEventData_( null, removed, updated );
		} else {
			const added: AddedMapItems<V> = {};
			added[ key ] = value;
			eventData = this.toEventData_( added, removed, null );
		}
		values[ key ] = value;

		this.onClear_();
		this.onPut_( key, value );
		this.getAndIncrementRevision_();
		this.pushChangeEvent_( eventData[ 1 ], eventData );
		return null;
	}

	protected clearAndPutAll__( mappings: PlainObject<V | null> ): void {
		let isChanged = false;
		const values = this._values;

		const removed: RemovedMapItems<V> = {};
		for( const key in values ) {
			if( !(key in mappings) ) {
				removed[ key ] = values[ key ];
				delete values[ key ];
				isChanged = true;
			}
		}

		const added: AddedMapItems<V> = {};
		const updated: UpdatedMapItems<V> = {};
		for( const key in mappings ) {
			if( hasOwn(mappings, key) ) {
				const newValue = mappings[ key ];
				if( key in values ) {
					updated[ key ] = { newValue, oldValue: values[ key ] };
				} else {
					added[ key ] = newValue;
				}
				values[ key ] = newValue;
				isChanged = true;
			}
		}

		if( isChanged ) {
			const eventData = this.toEventData_( added, removed, updated );
			this.onClear_();
			this.onPutAll_( mappings );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	protected remove__( key: string ): V | null {
		const values = this._values;
		let result = null;

		if( key in values ) {
			const removed: RemovedMapItems<V> = {};
			result = removed[ key ] = values[ key ];
			delete values[ key ];
			const eventData = this.toEventData_( null, removed, null );
			this.onRemove_( key );
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}

		return result;
	}

	protected clear__(): void {
		const values = this._values;

		let isChanged = false;
		const removed: RemovedMapItems<V> = {};
		for( const key in values ) {
			removed[ key ] = values[ key ];
			delete values[ key ];
			isChanged = true;
		}

		if( isChanged ) {
			const eventData = this.toEventData_( null, removed, null );
			this.onClear_();
			this.getAndIncrementRevision_();
			this.pushChangeEvent_( eventData[ 1 ], eventData );
		}
	}

	patch_( patches: unknown[] ): unknown[] | null {
		const revision = this.getRevision_();
		const map = this._values;

		const added: AddedMapItems<V> = {};
		const removed: RemovedMapItems<V> = {};
		const updated: UpdatedMapItems<V> = {};

		const type = patches[ 2 ];
		if( type === 0 ) {
			patchReset(
				patches[ 3 ] as PlainObject<V | null>,
				map, added, removed, updated
			);
		} else {
			const startRevision = patches[ 0 ] as number;
			const ceilingRevision = Math.max( revision - startRevision, 0 );
			for( let i = 3, imax = patches.length; i < imax; i += 3 ) {
				if( ceilingRevision <= (patches[ i + 0 ] as number) ) {
					patchMap(
						patches[ i + 1 ] as AddedMapItems<V>,
						patches[ i + 2 ] as string[],
						map, added, removed, updated
					);
				}
			}
		}

		if( isEmptyObject( added ) && isEmptyObject( removed ) && isEmptyObject( updated ) ) {
			return null;
		} else {
			return [ null, added, removed, updated ];
		}
	}
}
