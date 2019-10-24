/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isEmptyArray, isNotEmptyArray } from "../../../util/lang/is-empty";
import { size } from "../../../util/lang/size";
import { Properties } from "../../internal/properties";
import { SPatch } from "./s-patch";

const lastIndexOf = ( revisions: number[], target: number ): number => {
	for( let i = revisions.length - 1; 0 <= i; --i ) {
		const revision = revisions[ i ];
		if( target < revision ) {
			continue;
		}
		return ( revision === target ? i : -1 );
	}
	return -1;
};

export abstract class SPatches<V, P extends SPatch> {
	private _isLocal: boolean;
	private _revisions: number[];
	private _patches: Array<P | null>;

	constructor( properties: Properties ) {
		this._isLocal = properties.isLocal_();
		this._revisions = [];
		this._patches = [];
	}

	getOrCreate_( revision: number ): P {
		const patches = this._patches;
		const revisions = this._revisions;

		const index = lastIndexOf( revisions, revision );
		if( 0 <= index ) {
			return patches[ index ] as P;
		} else {
			if( this._isLocal ) {
				if( revisions.length === 0 ) {
					const result = this.newPatchReset_();
					revisions.push( revision );
					patches.push( result );
					return result;
				} else {
					revisions[ 0 ] = revision;
					return patches[ 0 ] as P;
				}
			} else {
				if( isNotEmptyArray( patches ) ) {
					const length = patches.length;
					const dummy = this.getPatchMapDummy_();
					const lastPatch = patches[ length - 1 ];
					if( lastPatch != null && lastPatch !== dummy ) {
						if( 1 < length ) {
							const secondLastPatch = patches[ length - 2 ];
							if( secondLastPatch != null ) {
								revisions[ length - 1 ] = revision;
							} else {
								patches[ length - 1 ] = dummy;
								revisions.push( revision );
								patches.push( lastPatch );
							}
						} else {
							patches[ length - 1 ] = dummy;
							revisions.push( revision );
							patches.push( lastPatch );
						}
						return lastPatch;
					}
				}

				const result = this.newPatchMap_();
				revisions.push( revision );
				patches.push( result );
				return result;
			}
		}
	}

	abstract newPatchMap_(): P;

	abstract newPatchReset_(): P;

	abstract getPatchMapDummy_(): P;

	clear_( revision?: number ): void {
		this._revisions.length = 0;
		this._patches.length = 0;
		if( revision != null ) {
			this._revisions.push( revision );
			this._patches.push( this.newPatchReset_() );
		}
	}

	compact_( authorizedRevision: number ): void {
		for( let i = 0, imax = this._revisions.length; i < imax; ++i ) {
			const revision = this._revisions[ 0 ];
			if( authorizedRevision <= revision ) {
				break;
			}
			this._revisions.splice( 0, 1 );
			this._patches.splice( 0, 1 );
		}
	}

	resetIfTooHeavy_( weightCriteria: number ): void {
		const revisions = this._revisions;
		if( isEmptyArray( revisions ) ) {
			return;
		}

		let weight = 0;
		const patches = this._patches;
		for( let i = 0, imax = patches.length; i < imax; ++i ) {
			const patch = patches[ i ];
			if( patch != null ) {
				weight += patch.getWeight_();
			}
		}
		if( weight <= weightCriteria ) {
			return;
		}

		this.clear_( revisions[ revisions.length - 1 ] );
	}

	containsReset_(): boolean {
		for( let i = 0, imax = this._patches.length; i < imax; ++i ) {
			const patch = this._patches[ i ];
			if( patch != null && patch.isReset_() ) {
				return true;
			}
		}
		return false;
	}

	pack_(
		authorizedRevision: number, forceReset: boolean, revision: number,
		data: V, isSoft: boolean
	): unknown[] {
		const dataSize = size( data );
		this.resetIfTooHeavy_( dataSize );

		const revisions = this._revisions;
		const patches = this._patches;
		if( isSoft || forceReset || dataSize <= 0 ||
			(isNotEmptyArray( revisions ) && authorizedRevision < revisions[ 0 ]) || this.containsReset_() ) {
			for( let i = 0, imax = patches.length; i < imax; ++i ) {
				if( revision <= revisions[ i ] ) {
					break;
				}
				patches[ i ] = null;
			}
			return this.packReset_( authorizedRevision, revision, data );
		} else {
			let isEmpty = true;
			let firstRevision = 0;
			const dummy = this.getPatchMapDummy_();
			const result = [ 0, 0, 1 ];
			for( let i = 0, imax = patches.length; i < imax; ++i ) {
				const r = revisions[ i ];
				if( revision <= r ) {
					break;
				}
				const patch = patches[ i ];
				if( patch != null ) {
					patches[ i ] = null;
					if( isEmpty ) {
						isEmpty = false;
						firstRevision = r;
					}
					if( patch !== dummy ) {
						result.push( r - firstRevision );
						patch.serialize_( result );
					}
				}
			}
			if( isEmpty ) {
				result[ 0 ] = revision;
				result[ 1 ] = 0;
			} else {
				result[ 0 ] = firstRevision;
				result[ 1 ] = revision - firstRevision;
			}
			return result;
		}
	}

	packReset_( authorizedRevision: number, revision: number, data: V ): unknown[] {
		return [ authorizedRevision, revision - authorizedRevision, 0, data ];
	}

	isEmpty_(): boolean {
		return isEmptyArray( this._revisions );
	}
}
