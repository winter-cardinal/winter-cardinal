/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { UpdatedListItem, UpdatedListItems } from "../s-list";

const N_BITS = 8;
const N_BINS = 256;
const HAS_TYPED_ARRAY = ( typeof Uint32Array !== "undefined" );
const COUNT = ( HAS_TYPED_ARRAY ? new Uint32Array( N_BINS ) : new Array( N_BINS ) );
const BIN_START = ( HAS_TYPED_ARRAY ? new Uint32Array( N_BINS ) : new Array( N_BINS ) );
const BIN_END = ( HAS_TYPED_ARRAY ? new Uint32Array( N_BINS ) : new Array( N_BINS ) );
const DUMMY_UPDATED_ITEM2: UpdatedListItem<any> = { index: 0, newValue: null, oldValue: null };
let WORKING_ARRAY: Array<UpdatedListItem<any>> = [];

const toBinIndex = ( index: number, mask: number, shift: number ): number => {
	return (index & mask) >>> shift;
};

export const mergeAndSortUpdated = <V>( array: UpdatedListItems<V> ): UpdatedListItems<V> => {
	const length = array.length;
	if( length <= 1 ) {
		return array;
	}

	let workingArray = WORKING_ARRAY;
	workingArray.length = length;
	let mask = N_BINS - 1;
	let shift = 0;
	const count = COUNT;
	const start = BIN_START;
	const end = BIN_END;

	let max = array[ 0 ].index;
	for( let i = 0; i < length; ++i ) {
		max = Math.max( max, array[ i ].index );
	}

	while( 0 < max ) {
		for( let i = 0; i < N_BINS; ++i ) {
			count[ i ] = 0;
		}

		for( let i = 0; i < length; ++i ) {
			++count[ toBinIndex( array[ i ].index, mask, shift ) ];
		}

		start[ 0 ] = end[ 0 ] = 0;
		for( let i = 1; i < N_BINS; ++i ) {
			start[ i ] = end[ i ] = start[ i - 1 ] + count[ i - 1 ];
		}

		for( let i = 0; i < length; ++i ) {
			workingArray[ end[ toBinIndex( array[ i ].index, mask, shift ) ]++ ] = array[ i ];
		}

		// Swap
		const tmp1 = array;
		array = workingArray;
		workingArray = tmp1;

		// Next
		mask <<= N_BITS;
		shift += N_BITS;
		max = max >>> N_BITS;
	}

	const tmp2 = array;
	array = workingArray;
	WORKING_ARRAY = workingArray = tmp2;

	// Merge and copy
	let previous = workingArray[ 0 ];
	workingArray[ 0 ] = DUMMY_UPDATED_ITEM2;
	array[ 0 ] = previous;
	let inext = 1;
	for( let i = 1; i < length; ++i ) {
		const next = workingArray[ i ];
		workingArray[ i ] = DUMMY_UPDATED_ITEM2;
		if( previous.index === next.index ) {
			previous.newValue = next.newValue;
		} else {
			array[ inext++ ] = next;
			previous = next;
		}
	}
	array.length = inext;

	return array;
};
