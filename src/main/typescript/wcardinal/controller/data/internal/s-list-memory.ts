/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isEmptyArray } from "../../../util/lang/is-empty";
import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { AddedListItems, RemovedListItems, UpdatedListItems } from "../s-list";
import { SContainerParentMemory } from "./s-container-parent-memory";
import { SListBaseMemory } from "./s-list-base-memory";
import {
	AddedListItemArrays, RemovedListItemArrays, SListBasePatchMaps, UpdatedListItemArrays
} from "./s-list-base-patch-maps";
import { mergeAndSortUpdated } from "./s-list-base-patch-maps-merge-and-sort-updated";
import { SListPatch } from "./s-list-patch";
import { SListPatches } from "./s-list-patches";
import { WrapperConstructor } from "./wrapper-constructor";

type SListBaseEventData<V> = [
	unknown,
	AddedListItems<V>,
	RemovedListItems<V>,
	UpdatedListItems<V>
];

export class SListMemory<V> extends SListBaseMemory<V, SListPatch<V>, SListPatches<V>> {
	constructor(
		parent: SContainerParentMemory, name: string, properties: Properties,
		lock: Lock, newWrapper: WrapperConstructor
	) {
		super( parent, name, properties, lock, newWrapper, new SListPatches( properties ) );
	}

	mergeEvents_( current: SListBaseEventData<V>, previous: SListBaseEventData<V>, isFirst: boolean ): void {
		SListBasePatchMaps.merge( current[ 1 ], current[ 2 ], current[ 3 ],
			previous[ 1 ], previous[ 2 ], previous[ 3 ] );
	}

	onMergedEvent_( mergedEvent: SListBaseEventData<V> ): void {
		mergedEvent[ 3 ] = mergeAndSortUpdated( mergedEvent[ 3 ] );
	}

	patch_( patches: unknown[] ): unknown[] | null {
		const revision = this.getRevision_();
		const list = this._values;
		const added: AddedListItems<V> = [];
		const removed: RemovedListItems<V> = [];
		let updated: UpdatedListItems<V> = [];

		const type = patches[ 2 ];
		if( type === 0 ) {
			this.patchReset_( patches[ 3 ] as Array<V | null>, list, added, removed, updated );
		} else {
			const startRevision = patches[ 0 ] as number;
			const ceilingRevision = Math.max( revision - startRevision, 0 );
			for( let i = 3, imax = patches.length; i < imax; i += 4 ) {
				if( ceilingRevision <= (patches[ i + 0 ] as number) ) {
					SListBasePatchMaps.patch(
						patches[ i + 1 ] as AddedListItemArrays<V>,
						patches[ i + 2 ] as RemovedListItemArrays<V>,
						patches[ i + 3 ] as UpdatedListItemArrays<V>,
						list, added, removed, updated
					);
				}
			}
			if( 7 < patches.length ) {
				updated = mergeAndSortUpdated( updated );
			}
		}

		if( isEmptyArray( added ) && isEmptyArray( removed ) && isEmptyArray( updated ) ) {
			return null;
		} else {
			return [ null, added, removed, updated ];
		}
	}
}
