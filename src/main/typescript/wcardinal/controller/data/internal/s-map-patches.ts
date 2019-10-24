/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SMapBasePatches } from "./s-map-base-patches";
import { SMapPatch } from "./s-map-patch";
import { SMapPatchMap } from "./s-map-patch-map";
import { SMapPatchReset } from "./s-map-patch-reset";

const SMAP_PATCH_RESET_INSTANCE = new SMapPatchReset<unknown>();
const SMAP_PATCH_MAP_DUMMY = new SMapPatchMap<unknown>();

export class SMapPatches<V> extends SMapBasePatches<V, SMapPatch<V>> {
	newPatchMap_(): SMapPatch<V> {
		return new SMapPatchMap();
	}

	newPatchReset_(): SMapPatch<V> {
		return SMAP_PATCH_RESET_INSTANCE;
	}

	getPatchMapDummy_(): SMapPatch<V> {
		return SMAP_PATCH_MAP_DUMMY;
	}
}
