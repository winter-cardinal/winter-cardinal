/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { EventInfo } from "../data/internal/event-info";
import { SStringMemory } from "../data/internal/s-string-memory";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { SString } from "../data/s-string";
import { Lock } from "../lock";
import { VisibilityController } from "../visibility-controller";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { mergeHistoryTitle } from "./merge-history-title";
import { newMemory } from "./new-memory";
import { Property } from "./property";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class VisibilityControllerMemory<W extends VisibilityController> extends ControllerMemory<W> {
	private _displayName: SStringMemory;

	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock,
		wrapperConstructor: WrapperConstructor<W>, type: ControllerType
	) {
		super( name, parent, staticInfo, staticInstanceInfo, lock, wrapperConstructor, type );

		this._visibility = false;
		const cp = this._properties.clone_().retain_( Property.LOCAL );
		this._displayName = newMemory( SStringMemory, SString, this, "$dn", cp, this._lock );
	}

	getDisplayName_(): string | null {
		return this._displayName.get_();
	}

	setDisplayName_( displayName: string ): void {
		this._displayName.set_( displayName, false );
	}

	getHistoryTitle_( separator: string ): string {
		let result = "";
		result = mergeHistoryTitle( result, this.getDisplayName_(), separator );
		result = mergeHistoryTitle( result, super.getHistoryTitle_( separator ), separator );
		return result;
	}

	isHistoryChanged_( eventInfo: EventInfo ) {
		if( eventInfo.data != null && this._displayName.getName_() in eventInfo.data ) {
			return true;
		}
		return super.isHistoryChanged_( eventInfo );
	}
}
