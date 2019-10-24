/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SListBasePatch } from "./s-list-base-patch";
import {
	AddedListItemArrays, RemovedListItemArrays, SListBasePatchMaps, UpdatedListItemArrays
} from "./s-list-base-patch-maps";

export class SListBasePatchMap<V> implements SListBasePatch<V> {
	protected _added: AddedListItemArrays<V>;
	protected _removed: RemovedListItemArrays<V>;
	protected _updated: UpdatedListItemArrays<V>;

	constructor() {
		this._added = [];
		this._removed = [];
		this._updated = [];
	}

	add_( index: number, value: V | null ): void {
		SListBasePatchMaps.add( index, value, this._added, this._updated );
	}

	addAll_( index: number, values: Array<V | null> ): void {
		SListBasePatchMaps.addAll( index, values, this._added, this._updated );
	}

	remove_( index: number ): void {
		SListBasePatchMaps.remove( index, this._added, this._removed, this._updated );
	}

	set_( index: number, value: V | null ): void {
		SListBasePatchMaps.set( index, value, this._updated );
	}

	getWeight_(): number {
		return (this._added.length >> 1) + (this._updated.length >> 1);
	}

	isReset_(): boolean {
		return false;
	}

	serialize_( result: unknown[] ): void {
		result.push( this._added, this._removed, this._updated );
	}
}
