/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../../util/lang/plain-object";
import { SMapBasePatch } from "./s-map-base-patch";
import { SPatches } from "./s-patches";

export abstract class SMapBasePatches<V, P extends SMapBasePatch<V>> extends SPatches<PlainObject<V | null>, P> {
	put_( revision: number, key: string, value: V | null ): void {
		this.getOrCreate_( revision ).put_( key, value );
	}

	putAll_( revision: number, values: PlainObject<V | null> ): void {
		this.getOrCreate_( revision ).putAll_( values );
	}

	remove_( revision: number, key: string ): void {
		this.getOrCreate_( revision ).remove_( key );
	}

	removeAll_( revision: number, values: PlainObject<V | null> ): void {
		this.getOrCreate_( revision ).removeAll_( values );
	}
}
