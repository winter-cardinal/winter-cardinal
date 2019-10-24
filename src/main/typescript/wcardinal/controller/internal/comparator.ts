/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export type Comparator<V = unknown> = ( a: V | null, b: V | null ) => boolean;

export function defaultComparator<V>( a: V | null, b: V | null ): boolean {
	return a === b;
}
