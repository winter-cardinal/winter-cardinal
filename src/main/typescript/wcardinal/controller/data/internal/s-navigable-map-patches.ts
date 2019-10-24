/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SMapBasePatches } from "./s-map-base-patches";
import { SNavigableMapPatch } from "./s-navigable-map-patch";
import { SNavigableMapPatchMap } from "./s-navigable-map-patch-map";
import { SNavigableMapPatchReset } from "./s-navigable-map-patch-reset";

const SNAVIGABLEMAP_PATCH_RESET_INSTANCE = new SNavigableMapPatchReset<unknown>();
const SNAVIGABLEMAP_PATCH_MAP_DUMMY = new SNavigableMapPatchMap<unknown>();

export class SNavigableMapPatches<V> extends SMapBasePatches<V, SNavigableMapPatch<V>> {
	newPatchMap_() {
		return new SNavigableMapPatchMap<V>();
	}

	newPatchReset_() {
		return SNAVIGABLEMAP_PATCH_RESET_INSTANCE;
	}

	getPatchMapDummy_() {
		return SNAVIGABLEMAP_PATCH_MAP_DUMMY;
	}
}
