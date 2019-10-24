/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isEmptyArray } from "../../../util/lang/is-empty";
import { checkRange } from "../../internal/check-range";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { AddedListItems, RemovedListItems, UpdatedListItems } from "../s-list";
import { MovedListItems } from "../s-movable-list";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SListBaseMemory } from "./s-list-base-memory";
import { AddedListItemArrays, RemovedListItemArrays, UpdatedListItemArrays } from "./s-list-base-patch-maps";
import { mergeAndSortUpdated } from "./s-list-base-patch-maps-merge-and-sort-updated";
import { SMovableListEventData } from "./s-movable-list-event-data";
import { SMovableListPatch } from "./s-movable-list-patch";
import { makeOldMoved, MovedListItemArrays, SMovableListPatchMaps } from "./s-movable-list-patch-maps";
import { SMovableListPatches } from "./s-movable-list-patches";
import { WrapperConstructor } from "./wrapper-constructor";

export class SMovableListMemory<V> extends SListBaseMemory<V, SMovableListPatch<V>, SMovableListPatches<V>> {
	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, wrapperConstructor: WrapperConstructor
	) {
		super( parent, name, properties, lock, wrapperConstructor, new SMovableListPatches<V>( properties ) );
	}

	onMove_( oldIndex: number, newIndex: number ): void {
		this._patches.move_( this.getRevision_(), oldIndex, newIndex );
		this.toUpdated_();
	}

	toEventData_(
		added?: AddedListItems<V> | null, removed?: RemovedListItems<V> | null, updated?: UpdatedListItems<V> | null,
		newMoved?: MovedListItems<V> | null, oldMoved?: MovedListItems<V> | null
	): [ null, AddedListItems<V>, RemovedListItems<V>, UpdatedListItems<V>, MovedListItems<V>, MovedListItems<V> ] {
		return [
			null,
			added || [],
			removed || [],
			updated || [],
			newMoved || [],
			oldMoved || []
		];
	}

	mergeEvents_( current: SMovableListEventData<V>, previous: SMovableListEventData<V>, isFirst: boolean ): void {
		SMovableListPatchMaps.merge( current[ 1 ], current[ 2 ], current[ 3 ], current[ 4 ],
			previous[ 1 ], previous[ 2 ], previous[ 3 ], previous[ 4 ] );
	}

	onMergedEvent_( mergedEvent: SMovableListEventData<V> ): void {
		mergedEvent[ 3 ] = mergeAndSortUpdated( mergedEvent[ 3 ] );
		mergedEvent[ 5 ] = makeOldMoved( mergedEvent[ 4 ] );
	}

	patch_( patches: unknown[] ): unknown[] | null {
		const revision = this.getRevision_();
		const list = this._values;

		const added: AddedListItems<V> = [];
		const removed: RemovedListItems<V> = [];
		let updated: UpdatedListItems<V> = [];
		const newMoved: MovedListItems<V> = [];

		const type = patches[ 2 ];
		if( type === 0 ) {
			this.patchReset_(
				patches[ 3 ] as Array<V | null>,
				list, added, removed, updated
			);
		} else {
			const startRevision = patches[ 0 ] as number;
			const ceilingRevision = Math.max( revision - startRevision, 0 );
			for( let i = 3, imax = patches.length; i < imax; i += 5 ) {
				if( ceilingRevision <= (patches[ i + 0 ] as number) ) {
					SMovableListPatchMaps.patch(
						patches[ i + 1 ] as AddedListItemArrays<V>,
						patches[ i + 2 ] as RemovedListItemArrays<V>,
						patches[ i + 3 ] as UpdatedListItemArrays<V>,
						patches[ i + 4 ] as MovedListItemArrays<V>,
						list, added, removed, updated, newMoved
					);
				}
			}
			if( 8 < patches.length ) {
				updated = mergeAndSortUpdated( updated );
			}
		}

		if( isEmptyArray( added ) && isEmptyArray( removed ) && isEmptyArray( updated ) && isEmptyArray( newMoved ) ) {
			return null;
		} else {
			const oldMoved = makeOldMoved( newMoved );
			return [ null, added, removed, updated, newMoved, oldMoved ];
		}
	}

	move_( oldIndex: number, newIndex: number ): void {
		this.lock_();
		try {
			this.move__( oldIndex, newIndex );
		} finally {
			this.unlock_();
		}
	}

	private move__( oldIndex: number, newIndex: number ): void {
		const values = this._values;
		checkRange( oldIndex, 0, values.length );
		checkRange( newIndex, 0, values.length );

		if( oldIndex !== newIndex ) {
			const value = values.splice( oldIndex, 1 )[ 0 ];
			values.splice( newIndex, 0, value );
			this.onMove_( oldIndex, newIndex );
			this.getAndIncrementRevision_();
			const moved: MovedListItems<V> = [{newIndex, oldIndex, value}];
			this.pushChangeEvent_( null, this.toEventData_( null, null, null, moved, moved ) );
		}
	}
}
