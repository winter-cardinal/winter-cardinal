/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SListBasePatch } from "./s-list-base-patch";
import { SListBasePatches } from "./s-list-base-patches";
import { SListPatch } from "./s-list-patch";
import { SListPatchMap } from "./s-list-patch-map";
import { SListPatchReset } from "./s-list-patch-reset";

const SLIST_PATCH_RESET_INSTANCE = new SListPatchReset<any>();
const SLIST_PATCH_MAP_DUMMY = new SListPatchMap<any>();

export class SListPatches<V> extends SListBasePatches<V, SListPatch<V>> {
	newPatchMap_(): SListBasePatch<V> {
		return new SListPatchMap<V>();
	}

	newPatchReset_(): SListBasePatch<V> {
		return SLIST_PATCH_RESET_INSTANCE;
	}

	getPatchMapDummy_(): SListBasePatch<V> {
		return SLIST_PATCH_MAP_DUMMY;
	}
}
