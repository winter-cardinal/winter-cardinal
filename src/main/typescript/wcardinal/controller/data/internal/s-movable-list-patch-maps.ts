/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isEmptyArray, isNotEmptyArray } from "../../../util/lang/is-empty";
import { AddedListItems, RemovedListItems, UpdatedListItems } from "../s-list";
import { MovedListItem, MovedListItems } from "../s-movable-list";
import {
	addAllToAdded1, AddedListItemArrays,
	addToAdded1, addToAdded2, addToUpdated1, addToUpdatedAll, moveKey1,
	RemovedListItemArrays, removeToAddedToRemoved1, removeToAddedToRemoved2,
	removeToUpdated1, removeToUpdatedAll, UpdatedListItemArrays,
	updateToUpdated1, vindexOf1, visNotEmpty1, vput1, vremoveByIndex1
} from "./s-list-base-patch-maps";

export type MovedListItemArray<V> = number;
export type MovedListItemArrays<V> = Array<MovedListItemArray<V>>;

const vlength1 = <V>( list: MovedListItemArrays<V> ): number => {
	return (list.length >> 1);
};

const vlength2 = <V>( list: MovedListItems<V> ): number => {
	return list.length;
};

const visEmpty1 = <V>( list: MovedListItemArrays<V> ): boolean => {
	return isEmptyArray( list );
};

const visEmpty2 = <V>( list: MovedListItems<V> ): boolean => {
	return isEmptyArray( list );
};

const visNotEmpty2 = <V>( list: MovedListItems<V> ): boolean => {
	return isNotEmptyArray( list );
};

const toOldIndexToAdd1 = <V>( newIndex: number, newMoved: MovedListItemArrays<V> ): number => {
	for( let i = newIndex; 0 <= i; i -= 1 ) {
		const oldIndex = toOldIndex1<V>( i, newMoved, false );
		if( 0 <= oldIndex ) { return oldIndex + (i === newIndex ? 0 : 1); }
	}
	return 0;
};

const toOldIndexToAdd2 = <V>( newIndex: number, newMoved: MovedListItems<V> ): number => {
	for( let i = newIndex; 0 <= i; i -= 1 ) {
		const oldIndex = toOldIndex2<V>( i, newMoved, false );
		if( 0 <= oldIndex ) {
			return oldIndex + (i === newIndex ? 0 : 1);
		}
	}
	return 0;
};

const countNewIndex1 = <V>( newIndex: number, moveds: MovedListItemArrays<V> ): number => {
	let result = 0;

	for( let i = moveds.length - 2; 0 <= i; i -= 2 ) {
		const movedNewIndex = moveds[ i ];

		if( newIndex === movedNewIndex ) {
			return -i - 1;
		} else if( movedNewIndex < newIndex ) {
			result += 1;
		}
	}

	return result;
};

const countNewIndex2 = <V>( newIndex: number, moveds: MovedListItems<V> ): number => {
	let result = 0;

	for( let i = moveds.length - 1; 0 <= i; i -= 1 ) {
		const moved = moveds[ i ];
		const movedNewIndex = moved.newIndex;

		if( newIndex === movedNewIndex ) {
			return -i - 1;
		} else if( movedNewIndex < newIndex ) {
			result += 1;
		}
	}

	return result;
};

const countOldIndex1 = <V>( oldIndex: number, moveds: MovedListItemArrays<V> ): number => {
	let result = 0;

	for( let i = moveds.length - 2; 0 <= i; i -= 2 ) {
		const movedOldIndex = moveds[ i + 1 ];

		if( oldIndex === movedOldIndex ) {
			return -i - 1;
		} else if( movedOldIndex < oldIndex ) {
			result += 1;
		}
	}

	return result;
};

const countOldIndex2 = <V>( oldIndex: number, moveds: MovedListItems<V> ): number => {
	let result = 0;

	for( let i = moveds.length - 1; 0 <= i; i -= 1 ) {
		const moved = moveds[ i ];
		const movedOldIndex = moved.oldIndex;

		if( oldIndex === movedOldIndex ) {
			return -i - 1;
		} else if( movedOldIndex < oldIndex ) {
			result += 1;
		}
	}

	return result;
};

const toOldIndex1 = <V>( newIndex: number, newMoved: MovedListItemArrays<V>, allowDirect: boolean ): number => {
	const newCount = countNewIndex1<V>( newIndex, newMoved );
	if( newCount < 0 ) {
		return (allowDirect ? newMoved[ -newCount ] : newCount);
	}
	const newPosition = newIndex - newCount;

	for( let i = newIndex; 0 <= i; i -= 1 ) {
		const oldCount = countOldIndex1<V>( i, newMoved );
		if( oldCount < 0 ) {
			continue;
		}
		const oldPosition = i - oldCount;
		if( oldPosition === newPosition ) {
			return i;
		} else if( oldPosition < newPosition ) {
			break;
		} else {
			continue;
		}
	}

	for( let i = newIndex + 1; ; ++i ) {
		const oldCount = countOldIndex1<V>( i, newMoved );
		if( oldCount < 0 ) {
			continue;
		}
		const oldPosition = i - oldCount;
		if( oldPosition === newPosition ) {
			return i;
		} else if( oldPosition < newPosition ) {
			continue;
		} else {
			break;
		}
	}

	return newIndex;
};

const toOldIndex2 = <V>( newIndex: number, newMoved: MovedListItems<V>, allowDirect: boolean ): number => {
	const newCount = countNewIndex2<V>( newIndex, newMoved );
	if( newCount < 0 ) {
		return (allowDirect ? newMoved[ -newCount - 1 ].oldIndex : newCount);
	}
	const newPosition = newIndex - newCount;

	for( let i = newIndex; 0 <= i; i -= 1 ) {
		const oldCount = countOldIndex2<V>( i, newMoved );
		if( oldCount < 0 ) {
			continue;
		}
		const oldPosition = i - oldCount;
		if( oldPosition === newPosition ) {
			return i;
		} else if( oldPosition < newPosition ) {
			break;
		} else {
			continue;
		}
	}

	for( let i = newIndex + 1; ; ++i ) {
		const oldCount = countOldIndex2<V>( i, newMoved );
		if( oldCount < 0 ) {
			continue;
		}
		const oldPosition = i - oldCount;
		if( oldPosition === newPosition ) {
			return i;
		} else if( oldPosition < newPosition ) {
			continue;
		} else {
			break;
		}
	}

	return newIndex;
};

const addToNewMoved1 = <V>( index: number, size: number, newMoved: MovedListItemArrays<V> ): number => {
	if( visNotEmpty1<V>( newMoved ) ) {
		const newIndex = index;
		const oldIndex = toOldIndexToAdd1<V>( newIndex, newMoved );

		for( let i = newMoved.length - 2; 0 <= i; i -= 2 ) {
			const moveNewIndex = newMoved[ i + 0 ];
			const moveOldIndex = newMoved[ i + 1 ];
			if( newIndex <= moveNewIndex ) {
				newMoved[ i + 0 ] += size;
			}
			if( oldIndex <= moveOldIndex ) {
				newMoved[ i + 1 ] += size;
			}
		}

		return oldIndex;
	} else {
		return index;
	}
};

const addToNewMoved2 = <V>( index: number, size: number, newMoved: MovedListItems<V> ): number => {
	if( visNotEmpty2<V>( newMoved ) ) {
		const newIndex = index;
		const oldIndex = toOldIndexToAdd2<V>( newIndex, newMoved );

		for( let i = newMoved.length - 1; 0 <= i; i -= 1 ) {
			const move = newMoved[ i ];
			if( newIndex <= move.newIndex ) {
				move.newIndex += size;
			}
			if( oldIndex <= move.oldIndex ) {
				move.oldIndex += size;
			}
		}

		return oldIndex;
	} else {
		return index;
	}
};

const removeToNewMoved2 = <V>( index: number, newMoved: MovedListItems<V> ): number => {
	if( visNotEmpty2<V>( newMoved ) ) {
		const newIndex = index;
		const oldIndex = toOldIndex2<V>( newIndex, newMoved, true );

		for( let i = newMoved.length - 1; 0 <= i; i -= 1 ) {
			const move = newMoved[ i ];

			if( newIndex === move.newIndex ) {
				newMoved.splice( i, 1 );
				continue;
			}

			if( newIndex < move.newIndex ) {
				move.newIndex -= 1;
			}
			if( oldIndex < move.oldIndex ) {
				move.oldIndex -= 1;
			}
		}

		return oldIndex;
	} else {
		return index;
	}
};

const removeToNewMoved1 = <V>( index: number, newMoved: MovedListItemArrays<V> ): number => {
	if( visNotEmpty1<V>( newMoved ) ) {
		const newIndex = index;
		const oldIndex = toOldIndex1<V>( newIndex, newMoved, true );

		for( let i = newMoved.length - 2; 0 <= i; i -= 2 ) {
			const moveNewIndex = newMoved[ i + 0 ];
			const moveOldIndex = newMoved[ i + 1 ];

			if( newIndex === moveNewIndex ) {
				newMoved.splice( i, 2 );
				continue;
			}

			if( newIndex < moveNewIndex ) {
				newMoved[ i + 0 ] -= 1;
			}
			if( oldIndex < moveOldIndex ) {
				newMoved[ i + 1 ] -= 1;
			}
		}

		return oldIndex;
	} else {
		return index;
	}
};

const moveToUpdatedAll = <V>(
	oldIndex: number, newIndex: number, value: V | null, updated: UpdatedListItems<V>
): V | null => {
	if( oldIndex <= newIndex ) {
		for( let i = updated.length - 1; 0 <= i; --i ) {
			const update = updated[ i ];
			const index = update.index;
			if( index === oldIndex ) {
				update.index = newIndex;
				value = update.oldValue;
			} else if( oldIndex < index && index <= newIndex ) {
				update.index -= 1;
			}
		}
	} else {
		for( let i = updated.length - 1; 0 <= i; --i ) {
			const update = updated[ i ];
			const index = update.index;
			if( index === oldIndex ) {
				update.index = newIndex;
				value = update.oldValue;
			} else if( newIndex <= index && index < oldIndex ) {
				update.index += 1;
			}
		}
	}
	return value;
};

const moveToUpdated1_ = <V>( oldIndex: number, newIndex: number, updated: UpdatedListItemArrays<V> ): void => {
	if( oldIndex <= newIndex ) {
		moveKey1<V>( updated, oldIndex + 1, newIndex + 1, -1 );
	} else {
		moveKey1<V>( updated, newIndex, oldIndex, +1 );
	}
};

const moveToUpdated1 = <V>( oldIndex: number, newIndex: number, updated: UpdatedListItemArrays<V> ): void => {
	if( visNotEmpty1<V>( updated ) ) {
		const index = vindexOf1<V>( updated, oldIndex );
		if( 0 <= index ) {
			const newValue = vremoveByIndex1<V>( updated, index );
			moveToUpdated1_( oldIndex, newIndex, updated );
			vput1<V>( updated, newIndex, newValue );
		} else {
			moveToUpdated1_( oldIndex, newIndex, updated );
		}
	}
};

const moveToNewMoved2 = <V>( oldIndex: number, newIndex: number, value: V, newMoved: MovedListItems<V> ): void => {
	if( visEmpty2<V>( newMoved ) ) {
		newMoved.splice( 0, 0, { newIndex, oldIndex, value } );
	} else {
		let found = null;
		const oldOldIndex = toOldIndex2<V>( oldIndex, newMoved, false );
		if( oldOldIndex < 0 ) {
			const foundIndex = -(oldOldIndex + 1);
			found = newMoved.splice( foundIndex, 1 )[ 0 ];
			found.newIndex = newIndex;
		} else {
			found = { newIndex, oldIndex: oldOldIndex, value };
		}

		if( oldIndex <= newIndex ) {
			for( let i = vlength2<V>( newMoved ) - 1; 0 <= i; i -= 1 ) {
				const move = newMoved[ i ];
				if( move.newIndex <= oldIndex ) {
					break;
				}
				if( move.newIndex <= newIndex ) {
					move.newIndex -= 1;
				}
			}
		} else {
			for( let i = vlength2<V>( newMoved ) - 1; 0 <= i; i -= 1 ) {
				const move = newMoved[ i ];
				if( move.newIndex < newIndex ) {
					break;
				}
				if( move.newIndex < oldIndex ) {
					move.newIndex += 1;
				}
			}
		}

		for( let i = vlength2<V>( newMoved ) - 1; 0 <= i; i -= 1 ) {
			const move = newMoved[ i ];
			if( newIndex < move.newIndex ) {
				continue;
			}
			newMoved.splice( i + 1, 0, found );
			return;
		}
		newMoved.splice( 0, 0, found );
	}
};

const moveToNewMoved1 = <V>( oldIndex: number, newIndex: number, value: V, newMoved: MovedListItemArrays<V> ): void => {
	if( visEmpty1<V>( newMoved ) ) {
		newMoved.splice( 0, 0, newIndex, oldIndex );
	} else {
		let found: number[] | null = null;
		const oldOldIndex = toOldIndex1<V>( oldIndex, newMoved, false );
		if( oldOldIndex < 0 ) {
			const foundIndex = (-(oldOldIndex + 1)) << 1;
			found = newMoved.splice( foundIndex, 2 );
			found[ 0 ] = newIndex;
		} else {
			found = [newIndex, oldOldIndex];
		}

		if( oldIndex <= newIndex ) {
			for( let i = newMoved.length - 2; 0 <= i; i -= 2 ) {
				const moveNewIndex = newMoved[ i ];
				if( moveNewIndex <= oldIndex ) {
					break;
				}
				if( moveNewIndex <= newIndex ) {
					newMoved[ i ] -= 1;
				}
			}
		} else {
			for( let i = newMoved.length - 2; 0 <= i; i -= 2 ) {
				const moveNewIndex = newMoved[ i ];
				if( moveNewIndex < newIndex ) {
					break;
				}
				if( moveNewIndex < oldIndex ) {
					newMoved[ i ] += 1;
				}
			}
		}

		for( let i = newMoved.length - 2; 0 <= i; i -= 2 ) {
			const moveNewIndex = newMoved[ i ];
			if( newIndex < moveNewIndex ) {
				continue;
			}
			newMoved.splice( i + 2, 0, found[ 0 ], found[ 1 ] );
			return;
		}
		newMoved.splice( 0, 0, found[ 0 ], found[ 1 ] );
	}
};

const findNewIndex = ( targetCount: number, sortedOldIndices: number[] ): number => {
	let count = 0;
	let previous = -1;
	for( let i = 0, imax = sortedOldIndices.length; i < imax; ++i ) {
		const oldIndex = sortedOldIndices[ i ];
		const diff = oldIndex - previous - 1;
		if( targetCount <= count + diff ) {
			return previous + 1 + (targetCount - count);
		} else {
			count += diff;
			previous = oldIndex;
		}
	}
	return previous + 1 + (targetCount - count);
};

const vindexOf3 = ( array: number[], value: number ): number => {
	let i0 = 0;
	let i1 = ( array.length ) - 1;

	while( i0 <= i1 ) {
		const i2 = i0 + ((i1 - i0) >> 1);
		const v2 = array[ i2 ];
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

const findNewIndex1 = <V>(
	newIndex: number, oldIndex: number, index: number, sortedOldIndices: number[], moved: MovedListItemArrays<V>
): number => {
	let result = findNewIndex( newIndex - index, sortedOldIndices );

	for( let i = result; ; ++i ) {
		if( vindexOf3( sortedOldIndices, i ) < 0 ) {
			result = i;
			break;
		}
	}

	for( let i = 0, imax = moved.length; i < imax; i += 2 ) {
		const otherNewIndex = moved[ i + 0 ];
		const otherOldIndex = moved[ i + 1 ];
		if( otherNewIndex <= otherOldIndex ) {
			if( otherNewIndex <= result && result < otherOldIndex ) {
				result += 1;
			}
		} else {
			if( otherOldIndex < result && result <= otherNewIndex ) {
				result -= 1;
			}
		}
	}

	if( oldIndex < result ) {
		result -= 1;
	}

	return result;
};

const findNewIndex2 = <V>(
	newIndex: number, oldIndex: number, index: number, sortedOldIndices: number[], moved: MovedListItems<V>
): number => {
	let result = findNewIndex( newIndex - index, sortedOldIndices );

	for( let i = result; ; ++i ) {
		if( vindexOf3( sortedOldIndices, i ) < 0 ) {
			result = i;
			break;
		}
	}

	for( let i = 0, imax = moved.length; i < imax; ++i ) {
		const move = moved[ i ];
		const otherNewIndex = move.newIndex;
		const otherOldIndex = move.oldIndex;
		if( otherNewIndex <= otherOldIndex ) {
			if( otherNewIndex <= result && result < otherOldIndex ) {
				result += 1;
			}
		} else {
			if( otherOldIndex < result && result <= otherNewIndex ) {
				result -= 1;
			}
		}
	}

	if( oldIndex < result ) {
		result -= 1;
	}

	return result;
};

const findOldIndex1 = <V>( targetOldIndex: number, moved: MovedListItemArrays<V> ): number => {
	let result = targetOldIndex;
	for( let i = 0, imax = moved.length; i < imax; i += 2 ) {
		const newIndex = moved[ i + 0 ];
		const oldIndex = moved[ i + 1 ];

		if( newIndex <= oldIndex ) {
			if( newIndex <= result && result < oldIndex ) {
				result += 1;
			}
		} else {
			if( oldIndex < result && result <= newIndex ) {
				result -= 1;
			}
		}
	}
	return result;
};

const findOldIndex2 = <V>( targetOldIndex: number, moved: MovedListItems<V> ): number => {
	let result = targetOldIndex;
	for( let i = 0, imax = moved.length; i < imax; ++i ) {
		const move = moved[ i ];
		const newIndex = move.newIndex;
		const oldIndex = move.oldIndex;

		if( newIndex <= oldIndex ) {
			if( newIndex <= result && result < oldIndex ) {
				result += 1;
			}
		} else {
			if( oldIndex < result && result <= newIndex ) {
				result -= 1;
			}
		}
	}
	return result;
};

const oldIndexComparator = ( a: number, b: number ): number => {
	return a - b;
};

const toMoved1 = <V>( newMoved: MovedListItemArrays<V> ): MovedListItemArrays<V> => {
	if( vlength1<V>( newMoved ) <= 1 ) {
		return newMoved;
	}

	const result: MovedListItemArrays<V> = [];

	const sortedOldIndices = [];
	for( let i = 0, imax = newMoved.length; i < imax; i += 2 ) {
		sortedOldIndices.push( newMoved[ i + 1 ] );
	}
	sortedOldIndices.sort( oldIndexComparator );

	for( let i = 0, imax = newMoved.length; i < imax; i += 2 ) {
		const oldIndex = findOldIndex1<V>( newMoved[ i + 1 ], result );
		const newIndex = findNewIndex1<V>( newMoved[ i + 0 ], oldIndex, (i >> 1), sortedOldIndices, result );
		if( newIndex !== oldIndex ) {
			result.push( newIndex, oldIndex );
		}
	}

	return result;
};

const toMoved2 = <V>( newMoved: MovedListItems<V> ): MovedListItems<V> => {
	if( vlength2<V>( newMoved ) <= 1 ) {
		return newMoved;
	}

	const result: MovedListItems<V> = [];

	const sortedOldIndices = [];
	for( let i = 0, imax = newMoved.length; i < imax; ++i ) {
		sortedOldIndices.push( newMoved[ i ].oldIndex );
	}
	sortedOldIndices.sort( oldIndexComparator );

	for( let i = 0, imax = newMoved.length; i < imax; ++i ) {
		const newMove = newMoved[ i ];
		const oldIndex = findOldIndex2( newMove.oldIndex, result );
		const newIndex = findNewIndex2( newMove.newIndex, oldIndex, i, sortedOldIndices, result );
		if( newIndex !== oldIndex ) {
			result.push({ newIndex, oldIndex, value: newMove.value });
		}
	}

	return result;
};

const oldMovedComparator = <V>( a: MovedListItem<V>, b: MovedListItem<V> ): number => {
	return a.oldIndex - b.oldIndex;
};

export const makeOldMoved = <V>( newMoved: MovedListItems<V> ): MovedListItems<V> => {
	return newMoved.slice( 0 ).sort( oldMovedComparator );
};

export class SMovableListPatchMaps {
	static add<V>(
		index: number, value: V, padded: AddedListItemArrays<V>,
		pupdated: UpdatedListItemArrays<V>, pnewMoved: MovedListItemArrays<V>
	): void {
		addToUpdated1( index, 1, pupdated );
		index = addToNewMoved1( index, 1, pnewMoved );
		addToAdded1( index, value, padded );
	}

	static addAll<V>(
		index: number, values: ArrayLike<V>,
		padded: AddedListItemArrays<V>, pupdated: UpdatedListItemArrays<V>,
		pnewMoved: MovedListItemArrays<V>
	): void {
		if( values != null && isNotEmptyArray( values ) ) {
			addToUpdated1( index, values.length, pupdated );
			index = addToNewMoved1( index, values.length, pnewMoved );
			addAllToAdded1( index, values, padded );
		}
	}

	static remove<V>(
		index: number, padded: AddedListItemArrays<V>, premoved: RemovedListItemArrays<V>,
		pupdated: UpdatedListItemArrays<V>, pnewMoved: MovedListItemArrays<V>
	): void {
		removeToUpdated1( index, pupdated );
		index = removeToNewMoved1( index, pnewMoved );
		removeToAddedToRemoved1( index, padded, premoved );
	}

	static set<V>( index: number, value: V, pupdated: UpdatedListItemArrays<V> ) {
		updateToUpdated1( index, value, pupdated );
	}

	static move<V>(
		oldIndex: number, newIndex: number, pupdated: UpdatedListItemArrays<V>,
		pnewMoved: MovedListItemArrays<V>
	): void {
		moveToUpdated1( oldIndex, newIndex, pupdated );
		moveToNewMoved1( oldIndex, newIndex, null, pnewMoved );
	}

	static merge<V>(
		cadded: AddedListItems<V>, cremoved: RemovedListItems<V>, cupdated: UpdatedListItems<V>, cmoved: MovedListItems<V>,
		padded: AddedListItems<V>, premoved: RemovedListItems<V>, pupdated: UpdatedListItems<V>, pmoved: MovedListItems<V> ) {
		// Operation order is as follows:
		//   Previous Removed
		//   Previous Added
		//   Previous moved
		//   Previous updated
		//   Current removed
		//   Current added
		//   Current moved
		//   Current updated

		// Remove
		for( let i = cremoved.length - 1; 0 <= i; --i ) {
			const cr = cremoved[ i ];
			const value = removeToUpdatedAll( cr.index, cr.value, pupdated );
			const index = removeToNewMoved2( cr.index, pmoved );
			removeToAddedToRemoved2( index, value, padded, premoved );
		}

		// Add
		for( let i = 0, imax = cadded.length; i < imax; ++i ) {
			const ca = cadded[ i ];
			addToUpdatedAll( ca.index, 1, pupdated );
			const index = addToNewMoved2( ca.index, 1, pmoved );
			addToAdded2( index, ca.value, padded );
		}

		// New move
		const moved = toMoved2<V>( cmoved );
		for( let i = 0, imax = moved.length; i < imax; ++i ) {
			const move = moved[ i ];
			const newIndex = move.newIndex;
			const oldIndex = move.oldIndex;
			const value = moveToUpdatedAll( oldIndex, newIndex, move.value, pupdated );
			moveToNewMoved2( oldIndex, newIndex, value, pmoved );
		}

		// Update
		for( let i = 0, imax = cupdated.length; i < imax; ++i ) {
			pupdated.push( cupdated[ i ] );
		}
	}

	static patch<V>(
		cadded: AddedListItemArrays<V>, cremoved: RemovedListItemArrays<V>,
		cupdated: UpdatedListItemArrays<V>, cmoved: MovedListItemArrays<V>,
		list: Array<V | null>,
		padded: AddedListItems<V>, premoved: RemovedListItems<V>,
		pupdated: UpdatedListItems<V>, pmoved: MovedListItems<V>
	) {
		// Remove
		for( let i = cremoved.length - 1; 0 <= i; --i ) {
			let index = cremoved[ i ];
			let value = list.splice( index, 1 )[ 0 ];
			value = removeToUpdatedAll( index, value, pupdated );
			index = removeToNewMoved2( index, pmoved );
			removeToAddedToRemoved2( index, value, padded, premoved );
		}

		// Add
		for( let i = 0, imax = cadded.length; i < imax; i += 2 ) {
			let index = cadded[ i + 0 ] as number;
			const value = cadded[ i + 1 ] as V;
			list.splice( index, 0, value );
			addToUpdatedAll( index, 1, pupdated );
			index = addToNewMoved2( index, 1, pmoved );
			addToAdded2( index, value, padded );
		}

		// New move
		const moved = toMoved1( cmoved );
		for( let i = 0, imax = moved.length; i < imax; i += 2 ) {
			const newIndex = moved[ i + 0 ];
			const oldIndex = moved[ i + 1 ];
			let value = list.splice( oldIndex, 1 )[ 0 ];
			list.splice( newIndex, 0, value );
			value = moveToUpdatedAll( oldIndex, newIndex, value, pupdated );
			moveToNewMoved2( oldIndex, newIndex, value, pmoved );
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
