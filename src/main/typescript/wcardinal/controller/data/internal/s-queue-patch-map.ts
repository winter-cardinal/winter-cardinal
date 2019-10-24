/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SQueuePatch } from "./s-queue-patch";

export class SQueuePatchMap<V> implements SQueuePatch<V> {
	private _added: Array<V | null>;
	private _removed: number;
	private _capacity: number;

	constructor() {
		this._added = [];
		this._removed = 0;
		this._capacity = -1;
	}

	add_( value: V | null ): void {
		this._added.push( value );
	}

	addAll_( values: ArrayLike<V | null> ): void {
		for( let i = 0, imax = values.length; i < imax; ++i ) {
			this._added.push( values[ i ] );
		}
	}

	remove_(): void {
		this._removed += 1;
	}

	capacity_( capacity: number ): void {
		this._capacity = capacity;
	}

	getWeight_(): number {
		return this._added.length;
	}

	isReset_(): boolean {
		return false;
	}

	serialize_( result: unknown[] ): void {
		result.push( this._added, this._removed, this._capacity );
	}
}
