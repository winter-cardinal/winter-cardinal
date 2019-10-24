/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { EventInfo } from "../data/internal/event-info";
import { EVENT_CHANGE } from "../data/internal/event-name";
import { SBooleanMemory } from "../data/internal/s-boolean-memory";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { SBoolean } from "../data/s-boolean";
import { Lock } from "../lock";
import { Popup } from "../popup";
import { checkSupported } from "./check-supported";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { newMemory } from "./new-memory";
import { Property } from "./property";
import { StaticInfo, StaticInstanceInfo } from "./settings";
import { VisibilityControllerMemory } from "./visibility-controller-memory";

export class PopupMemory extends VisibilityControllerMemory<Popup> {
	private _isShown: SBooleanMemory;

	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock, wrapperConstructor: WrapperConstructor<Popup>
	) {
		super( name, parent, staticInfo, staticInstanceInfo, lock, wrapperConstructor, ControllerType.POPUP );

		const cp = this._properties.clone_().retain_( Property.LOCAL );
		this._isShown = newMemory( SBooleanMemory, SBoolean, this, "$is", cp, lock );
		this._isShown.getWrapper_().on( EVENT_CHANGE, () => {
			this.checkVisibility_();
		});
	}

	show_(): void {
		checkSupported( this );
		this._isShown.set_( true, false );
	}

	hide_(): void {
		checkSupported( this );
		this._isShown.set_( false, false );
	}

	isShown_(): boolean {
		const parent = this.getParent_();
		return ( this._isShown.get_() === true && parent != null && parent.isShown_() );
	}

	getHistoryTitle_( separator: string ): string {
		return ( this._isShown.get_() ? super.getHistoryTitle_( separator ) : "" );
	}

	isHistoryChanged_( eventInfo: EventInfo ) {
		if( eventInfo.data != null && this._isShown.getName_() in eventInfo.data ) {
			return true;
		}
		return super.isHistoryChanged_( eventInfo );
	}
}
