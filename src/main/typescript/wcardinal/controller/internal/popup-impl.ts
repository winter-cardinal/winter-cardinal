/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Lock } from "../lock";
import { Popup } from "../popup";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { PopupMemory } from "./popup-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";
import { VisibilityControllerImpl } from "./visibility-controller-impl";

export class PopupImpl extends VisibilityControllerImpl implements Popup {
	constructor( __mem__: PopupMemory ) {
		super( __mem__ );
	}

	show() {
		this.__mem__.show_();
		return this;
	}

	hide() {
		this.__mem__.hide_();
		return this;
	}

	isShown() {
		return this.__mem__.isShown_();
	}
}

ControllerTypeToClass.put_({
	create_(
		parent: ControllerMemory, name: string, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
	) {
		return new PopupMemory(	name, parent, staticInfo, staticInstanceInfo, lock, PopupImpl );
	},

	getConstructor_() {
		return PopupImpl;
	},

	getType_() {
		return ControllerType.POPUP;
	}
});
