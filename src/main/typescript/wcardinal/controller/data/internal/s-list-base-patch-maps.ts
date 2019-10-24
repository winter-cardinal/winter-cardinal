/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isNotEmptyArray } from "../../../util/lang/is-empty";
import { AddedListItems, RemovedListItem, RemovedListItems, UpdatedListItems } from "../s-list";

export type AddedListItemArray<V> = (number|V|null);
export type RemovedListItemArray<V> = number;
export type UpdatedListItemArray<V> = (number|V|null);

export type AddedListItemArrays<V> = Array<AddedListItemArray<V>>;
export type RemovedListItemArrays<V> = Array<RemovedListItemArray<V>>;
export type UpdatedListItemArrays<V> = Array<UpdatedListItemArray<V>>;
export type AddedOrUpdatedItemArrays<V> = AddedListItemArrays<V> | UpdatedListItemArrays<V>;
export type Items1<V> = AddedListItemArrays<V> | RemovedListItemArrays<V> | UpdatedListItemArrays<V>;

export type AddedOrRemovedListItems<V> = AddedListItems<V> | RemovedListItems<V>;
export type ListItems<V> = AddedListItems<V> | RemovedListItems<V> | UpdatedListItems<V>;

export const moveKey1 = <V>(
	list: AddedOrUpdatedItemArrays<V>, from: number | null, to: number | null, delta: number
): number => {
	for( let i = list.length - 2; 0 <= i; i -= 2 ) {
		const index = list[ i ] as number;
		if( from == null || from <= index ) {
			if( to == null || index < to ) {
				list[ i ] = (list[ i ] as number) + delta;
			}
		} else {
			return i;
		}
	}
	return -1;
};

const moveKey2 = <V>( list: ListItems<V>, from: number | null, to: number | null, delta: number ): number => {
	for( let i = list.length - 1; 0 <= i; i -= 1 ) {
		const e = list[ i ];
		if( from == null || from <= e.index ) {
			if( to == null || e.index < to ) {
				e.index += delta;
			}
		} else {
			return i;
		}
	}
	return -1;
};

export const vindexOf1 = <V>( array: AddedOrUpdatedItemArrays<V>, value: number ): number => {
	let i0 = 0;
	let i1 = ( array.length >> 1 ) - 1;

	while( i0 <= i1 ) {
		const i2 = i0 + ((i1 - i0) >> 1);
		const v2 = array[ i2 << 1 ] as number;
		if( value < v2 ) {
			i1 = i2 - 1;
		} else if( v2 < value ) {
			i0 = i2 + 1;
		} else {
			return ( i2 << 1 );
		}
	}

	return -1;
};

const vindexOf2 = <V>( array: ListItems<V>, value: number ): number => {
	let i0 = 0;
	let i1 = array.length - 1;

	while( i0 <= i1 ) {
		const i2 = i0 + ((i1 - i0) >> 1);
		const v2 = array[ i2 ].index;
		if( value < v2 ) {
			i1 = i2 - 1;
		} else if( v2 < value ) {
			i0 = i2 + 1;
		} else {
			return i2;
		}
	}

	return -1;
};

const vceilingIndex1 = <V>( array: AddedOrUpdatedItemArrays<V>, value: number ): number => {
	let i0 = 0;
	let i1 = ( array.length >> 1 ) - 1;

	while( i0 <= i1 ) {
		const i2 = i0 + ((i1 - i0) >> 1);
		const v2 = array[ i2 << 1 ] as number;
		if( value < v2 ) {
			i1 = i2 - 1;
		} else if( v2 < value ) {
			i0 = i2 + 1;
		} else {
			return ( i2 << 1 );
		}
	}

	return (Math.max( i0, i1 ) << 1);
};

const vceilingIndex2 = <V>( array: ListItems<V>, value: number ): number => {
	let i0 = 0;
	let i1 = array.length - 1;

	while( i0 <= i1 ) {
		const i2 = i0 + ((i1 - i0) >> 1);
		const v2 = array[ i2 ].index;
		if( value < v2 ) {
			i1 = i2 - 1;
		} else if( v2 < value ) {
			i0 = i2 + 1;
		} else {
			return i2;
		}
	}

	return Math.max( i0, i1 );
};

export const vput1 = <V>( list: AddedOrUpdatedItemArrays<V>, index: number, value: V ): void => {
	for( let i = list.length - 2; 0 <= i; i -= 2 ) {
		const ikey = list[ i ] as number;
		if( ikey === index ) {
			list[ i + 1 ] = value;
			return;
		} else if( ikey < index ) {
			list.splice( i + 2, 0, index, value );
			return;
		}
	}
	list.splice( 0, 0, index, value );
};

const vput2 = <V>( list: AddedOrRemovedListItems<V>, index: number, value: V ): void => {
	for( let i = list.length - 1; 0 <= i; i -= 1 ) {
		const e = list[ i ];
		if( e.index === index ) {
			e.value = value;
			return;
		} else if( e.index < index ) {
			list.splice( i + 1, 0, { index, value } );
			return;
		}
	}
	list.splice( 0, 0, { index, value } );
};

export const visNotEmpty1 = <V>( list: Items1<V> ): boolean => {
	return isNotEmptyArray( list );
};

export const vremoveByIndex1 = <V>( list: AddedOrUpdatedItemArrays<V>, index: number ): V => {
	return list.splice( index, 2 )[ 1 ] as V;
};

const vremoveByIndex2 = <V>( list: RemovedListItems<V>, index: number ): RemovedListItem<V> => {
	return list.splice( index, 1 )[ 0 ];
};

const vadd1 = <V>( list: RemovedListItemArrays<V>, value: number ): void => {
	for( let i = list.length - 1; 0 <= i; i -= 1 ) {
		if( list[ i ] < value ) {
			list.splice( i + 1, 0, value );
			return;
		}
	}
	list.splice( 0, 0, value );
};

export const addToAdded1 = <V>( index: number, value: V, added: AddedListItemArrays<V> ): void => {
	moveKey1( added, index, null, 1 );
	vput1( added, index, value );
};

export const addToAdded2 = <V>( index: number, value: V, added: AddedListItems<V> ): void => {
	moveKey2( added, index, null, 1 );
	vput2( added, index, value );
};

export const addAllToAdded1 = <V>( index: number, values: ArrayLike<V>, added: AddedListItemArrays<V> ): void => {
	if( values != null && isNotEmptyArray( values ) ) {
		moveKey1( added, index, null, values.length );

		for( let i = 0, imax = values.length; i < imax; ++i ) {
			vput1( added, index + i, values[ i ] );
		}
	}
};

export const removeToAddedToRemoved1 = <V>(
	index: number, added: AddedListItemArrays<V>, removed: RemovedListItemArrays<V>
): void => {
	const i = vindexOf1( added, index );
	if( 0 <= i ) {
		vremoveByIndex1( added, i );
		moveKey1( added, index, null, -1 );
	} else {
		moveKey1( added, index, null, -1 );
		index -= (vceilingIndex1( added, index ) >> 1);
		removeToRemoved1( index, removed );
	}
};

export const removeToAddedToRemoved2 = <V>(
	index: number, value: V, added: AddedListItems<V>, removed: RemovedListItems<V>
): void => {
	const i = vindexOf2( added, index );
	if( 0 <= i ) {
		vremoveByIndex2( added, i );
		moveKey2( added, index, null, -1 );
	} else {
		moveKey2( added, index, null, -1 );
		index -= vceilingIndex2( added, index );
		removeToRemoved2( index, value, removed );
	}
};

const removeToRemoved1 = <V>( index: number, removed: RemovedListItemArrays<V> ): void => {
	vadd1( removed, findRemoveIndex1( index, removed ) );
};

const removeToRemoved2 = <V>( index: number, value: V, removed: RemovedListItems<V> ): void => {
	vput2( removed, findRemoveIndex2( index, removed ), value );
};

const findRemoveIndex1 = <V>( index: number, removed: RemovedListItemArrays<V> ): number => {
	let result = index;
	for( let i = 0, imax = removed.length; i < imax; ++i ) {
		if( result < removed[ i ] ) {
			break;
		}
		result += 1;
	}
	return result;
};

const findRemoveIndex2 = <V>( index: number, removed: RemovedListItems<V> ): number => {
	let result = index;
	for( let i = 0, imax = removed.length; i < imax; ++i ) {
		if( result < removed[ i ].index ) {
			break;
		}
		result += 1;
	}
	return result;
};

export const removeToUpdated1 = <V>( index: number, updated: UpdatedListItemArrays<V> ): void => {
	if( visNotEmpty1( updated ) ) {
		const i = moveKey1( updated, index + 1, null, -1 );
		if( 0 <= i && updated[ i ] === index ) {
			vremoveByIndex1( updated, i );
		}
	}
};

export const removeToUpdatedAll = <V>( index: number, value: V | null, updated: UpdatedListItems<V> ): V | null => {
	for( let i = updated.length - 1; 0 <= i; --i ) {
		const update = updated[ i ];
		if( index + 1 <= update.index ) {
			update.index -= 1;
		} else if( update.index === index ) {
			updated.splice( i, 1 );
			value = update.oldValue;
		}
	}
	return value;
};

export const addToUpdated1 = <V>( index: number, size: number, updated: UpdatedListItemArrays<V> ): void => {
	if( visNotEmpty1( updated ) ) {
		moveKey1( updated, index, null, size );
	}
};

export const addToUpdatedAll = <V>( index: number, size: number, updated: UpdatedListItems<V> ): void => {
	for( let i = 0, imax = updated.length; i < imax; ++i ) {
		const update = updated[ i ];
		if( index <= update.index ) {
			update.index += size;
		}
	}
};

export const updateToUpdated1 = <V>( index: number, newValue: V, updated: UpdatedListItemArrays<V> ): void => {
	vput1( updated, index, newValue );
};

export class SListBasePatchMaps {
	static add<V>( index: number, value: V, padded: AddedListItemArrays<V>, pupdated: UpdatedListItemArrays<V> ): void {
		addToUpdated1( index, 1, pupdated );
		addToAdded1( index, value, padded );
	}

	static addAll<V>(
		index: number, values: ArrayLike<V>, padded: AddedListItemArrays<V>, pupdated: UpdatedListItemArrays<V>
	): void {
		if( values != null && isNotEmptyArray( values ) ) {
			addToUpdated1( index, values.length, pupdated );
			addAllToAdded1( index, values, padded );
		}
	}

	static remove<V>(
		index: number, padded: AddedListItemArrays<V>, premoved: RemovedListItemArrays<V>, pupdated: UpdatedListItemArrays<V>
	): void {
		removeToUpdated1( index, pupdated );
		removeToAddedToRemoved1( index, padded, premoved );
	}

	static set<V>( index: number, value: V, pupdated: UpdatedListItemArrays<V> ): void {
		updateToUpdated1( index, value, pupdated );
	}

	static merge<V>(
		cadded: AddedListItems<V>, cremoved: RemovedListItems<V>, cupdated: UpdatedListItems<V>,
		padded: AddedListItems<V>, premoved: RemovedListItems<V>, pupdated: UpdatedListItems<V>
	): void {
		// Operation order is as follows:
		//   Previous Removed
		//   Previous Added
		//   Previous updated
		//   Current removed
		//   Current added
		//   Current updated

		// Remove
		for( let i = cremoved.length - 1; 0 <= i; --i ) {
			const cr = cremoved[ i ];
			const value = removeToUpdatedAll( cr.index, cr.value, pupdated );
			removeToAddedToRemoved2( cr.index, value, padded, premoved );
		}

		// Add
		for( let i = 0, imax = cadded.length; i < imax; ++i ) {
			const ca = cadded[ i ];
			addToUpdatedAll( ca.index, 1, pupdated );
			addToAdded2( ca.index, ca.value, padded );
		}

		// Update
		for( let i = 0, imax = cupdated.length; i < imax; ++i ) {
			pupdated.push( cupdated[ i ] );
		}
	}

	static patch<V>(
		cadded: AddedListItemArrays<V>, cremoved: RemovedListItemArrays<V>, cupdated: UpdatedListItemArrays<V>,
		list: Array<V | null>, padded: AddedListItems<V>, premoved: RemovedListItems<V>, pupdated: UpdatedListItems<V>
	): void {
		// Remove
		for( let i = cremoved.length - 1; 0 <= i; --i ) {
			const index = cremoved[ i ];
			let value = list.splice( index, 1 )[ 0 ];
			value = removeToUpdatedAll( index, value, pupdated );
			removeToAddedToRemoved2( index, value, padded, premoved );
		}

		// Add
		for( let i = 0, imax = cadded.length; i < imax; i += 2 ) {
			const index = cadded[ i + 0 ] as number;
			const value = cadded[ i + 1 ] as V;
			list.splice( index, 0, value );
			addToUpdatedAll( index, 1, pupdated );
			addToAdded2( index, value, padded );
		}

		// Update
		for( let i = 0, imax = cupdated.length; i < imax; i += 2 ) {
			const index = cupdated[ i + 0 ] as number;
			const newValue = cupdated[ i + 1 ] as V;
			const oldValue = list[ index ];
			list[ index ] = newValue;
			pupdated.push({ index, newValue, oldValue });
		}
	}
}
