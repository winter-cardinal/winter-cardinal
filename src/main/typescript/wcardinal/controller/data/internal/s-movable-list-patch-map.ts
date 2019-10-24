/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SListBasePatchMap } from "./s-list-base-patch-map";
import { MovedListItemArrays, SMovableListPatchMaps } from "./s-movable-list-patch-maps";

export class SMovableListPatchMap<V> extends SListBasePatchMap<V> {
	private _newMoved: MovedListItemArrays<V> = [];

	add_( index: number, value: V | null ): void {
		SMovableListPatchMaps.add( index, value, this._added, this._updated, this._newMoved );
	}

	addAll_( index: number, values: Array<V | null> ): void {
		SMovableListPatchMaps.addAll( index, values, this._added, this._updated, this._newMoved );
	}

	remove_( index: number ): void {
		SMovableListPatchMaps.remove( index, this._added, this._removed, this._updated, this._newMoved );
	}

	set_( index: number, value: V | null ): void {
		SMovableListPatchMaps.set( index, value, this._updated );
	}

	move_( oldIndex: number, newIndex: number ): void {
		SMovableListPatchMaps.move( oldIndex, newIndex, this._updated, this._newMoved );
	}

	serialize_( result: unknown[] ): void {
		result.push( this._added, this._removed, this._updated, this._newMoved );
	}
}
