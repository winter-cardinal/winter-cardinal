/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Lock } from "../lock";
import { Popup } from "../popup";
import { PopupFactory } from "../popup-factory";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { FactoryImpl } from "./factory-impl";
import { NewCtrlr } from "./factory-memory";
import { PopupFactoryMemory } from "./popup-factory-memory";
import { PopupImpl } from "./popup-impl";
import { PopupMemory } from "./popup-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class PopupFactoryImpl extends FactoryImpl<Popup> implements PopupFactory {
	constructor( memory: PopupFactoryMemory ) {
		super( memory );
	}
}

const newPopup: NewCtrlr<PopupFactory, Popup> =
( name, parent, staticInfo, staticInstanceInfo, lock ) => {
	return new PopupMemory( name, parent, staticInfo, staticInstanceInfo, lock, PopupImpl );
};

ControllerTypeToClass.put_({
	create_(
		parent: ControllerMemory, name: string, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
	) {
		return new PopupFactoryMemory(
			name, parent, staticInfo, staticInstanceInfo, lock, PopupFactoryImpl, newPopup );
	},

	getConstructor_() {
		return PopupFactoryImpl;
	},

	getType_() {
		return ControllerType.POPUP_FACTORY;
	}
});
