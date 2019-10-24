/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { SListBasePatch } from "./s-list-base-patch";
import { SPatches } from "./s-patches";

export abstract class SListBasePatches<V, P extends SListBasePatch<V>> extends SPatches<Array<V | null>, P> {
	add( revision: number, index: number, value: V | null ): void {
		this.getOrCreate_( revision ).add_( index, value );
	}

	addAll( revision: number, index: number, values: ArrayLike<V | null> ): void {
		this.getOrCreate_( revision ).addAll_( index, values );
	}

	remove( revision: number, index: number ): void {
		this.getOrCreate_( revision ).remove_( index );
	}

	set( revision: number, index: number, value: V | null ): void {
		this.getOrCreate_( revision ).set_( index, value );
	}
}
