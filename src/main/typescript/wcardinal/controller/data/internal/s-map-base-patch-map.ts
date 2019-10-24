/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "../../../util/lang/has-own";
import { PlainObject } from "../../../util/lang/plain-object";
import { SMapBasePatch } from "./s-map-base-patch";

export class SMapBasePatchMap<V> implements SMapBasePatch<V> {
	private _added: PlainObject<V | null>;
	private _removed: PlainObject<0>;
	private _addedLength: number;

	constructor() {
		this._added = {};
		this._removed = {};
		this._addedLength = 0;
	}

	put_( key: string, value: V | null ): void {
		delete this._removed[ key ];
		if( ! (key in this._added) ) {
			this._addedLength += 1;
		}
		this._added[ key ] = value;
	}

	putAll_( values: PlainObject<V | null> ): void {
		if( values != null ) {
			const added = this._added;
			let addedLength = 0;
			const removed = this._removed;
			for( const key in values ) {
				if( hasOwn( values, key ) ) {
					const value = values[ key ];
					delete removed[ key ];
					if( ! ( key in added ) ) {
						addedLength += 1;
					}
					added[ key ] = value;
				}
			}
			this._addedLength += addedLength;
		}
	}

	remove_( key: string ): void {
		if( key in this._added ) {
			delete this._added[ key ];
			this._addedLength -= 1;
		}
		this._removed[ key ] = 0;
	}

	removeAll_( values: PlainObject<V | null> ): void {
		if( values != null ) {
			const added = this._added;
			let addedLength = 0;
			const removed = this._removed;
			for( const key in values ) {
				if( hasOwn( values, key ) ) {
					if( key in added ) {
						delete added[ key ];
						addedLength -= 1;
					}
					removed[ key ] = 0;
				}
			}
			this._addedLength += addedLength;
		}
	}

	getWeight_(): number {
		return this._addedLength;
	}

	isReset_(): boolean {
		return false;
	}

	serialize_( result: unknown[] ): void {
		result.push( this._added, Object.keys( this._removed ) );
	}
}
