/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Controller } from "../controller";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { Lock } from "../lock";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { RejectedDynamicInfo } from "./info/rejected-set-dynamic-info";
import { DynamicInfo, LocalSettings } from "./settings";

export class LocalRootControllerMemory<W extends Controller = Controller> extends ControllerMemory<W> {
	protected _hasUpdate = false;
	protected _rejectedDynamicInfos: RejectedDynamicInfo[] = [];

	constructor( name: string, settings: LocalSettings, wrapperConstructor: WrapperConstructor<W> ) {
		super(
			name, null, settings.info.static, settings.info.instance, new Lock(),
			wrapperConstructor, ControllerType.CONTROLLER
		);
	}

	init_( settings: LocalSettings ): this {
		if( settings.info.dynamic != null ) {
			this.lock_();
			try {
				this.setDynamicInfo_( settings.info.dynamic, false, false );
			} finally {
				this.unlock_();
			}
		}
		return this;
	}

	unlock_() {
		const count = this._lock.getHoldCount();
		if( count <= 1 ) {
			// The last unlock
			this.onUnlock_();
		} else {
			// Otherwise
			this._lock.unlock();
		}
	}

	update_() {
		this._isChanged = true;
	}

	onUnlock_() {
		const events = this._events;
		this._events = null;

		if( this._isChanged ) {
			this._isChanged = false;
			this._lock.unlock();
			if( ! this.isLocal_() ) {
				this._hasUpdate = true;
			}
		} else {
			this._lock.unlock();
		}

		if( events != null ) {
			this.handleEvents_( events );
			this.triggerEvents_( events );
		}
	}

	setDynamicInfo_( receivedDynamicInfo: DynamicInfo, sendUpdate?: boolean, processRejectedInfos?: boolean ): void {
		const rejectedDynamicInfos = this._rejectedDynamicInfos;

		const hasNonSContainer = [ false ];
		const rejectionInfo = super.setSContainerDynamicInfo_( receivedDynamicInfo, hasNonSContainer );
		if( rejectionInfo != null && hasNonSContainer[ 0 ] ) {
			if( sendUpdate ) {
				this._hasUpdate = true;
			}

			rejectedDynamicInfos.push( new RejectedDynamicInfo( rejectionInfo, receivedDynamicInfo ) );
		} else {
			if( sendUpdate ) {
				this._hasUpdate = true;
			}

			if( processRejectedInfos ) {
				for( let i = 0, imax = rejectedDynamicInfos.length, ioffset = 0; i < imax; ++i ) {
					const rejectedDynamicInfo = rejectedDynamicInfos[ i - ioffset ];
					if( this.isApplicable_( rejectedDynamicInfo._rejectionInfo ) ) {
						super.setDynamicInfo_( rejectedDynamicInfo._dynamicInfo );
						rejectedDynamicInfos.splice( i - ioffset, 1 );
						ioffset += 1;
					}
				}
			}

			super.setDynamicInfo_( receivedDynamicInfo );
		}
	}
}
