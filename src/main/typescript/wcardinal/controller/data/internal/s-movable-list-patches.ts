/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SListBasePatches } from "./s-list-base-patches";
import { SMovableListPatch } from "./s-movable-list-patch";
import { SMovableListPatchMap } from "./s-movable-list-patch-map";
import { SMovableListPatchReset } from "./s-movable-list-patch-reset";

const SMOVABLELIST_PATCH_RESET_INSTANCE = new SMovableListPatchReset<any>();
const SMOVABLELIST_PATCH_MAP_DUMMY = new SMovableListPatchMap<any>();

export class SMovableListPatches<V> extends SListBasePatches<V, SMovableListPatch<V>> {
	newPatchMap_() {
		return new SMovableListPatchMap<V>();
	}

	newPatchReset_() {
		return SMOVABLELIST_PATCH_RESET_INSTANCE;
	}

	getPatchMapDummy_() {
		return SMOVABLELIST_PATCH_MAP_DUMMY;
	}

	move_( revision: number, oldIndex: number, newIndex: number ): void {
		this.getOrCreate_( revision ).move_( oldIndex, newIndex );
	}
}
