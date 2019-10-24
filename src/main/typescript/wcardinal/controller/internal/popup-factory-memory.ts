/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { Lock } from "../lock";
import { Popup } from "../popup";
import { PopupFactory } from "../popup-factory";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { FactoryMemory, NewCtrlr } from "./factory-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class PopupFactoryMemory extends FactoryMemory<PopupFactory, Popup> {
	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock,
		wrapperConstructor: WrapperConstructor<PopupFactory>,
		newCtrlr: NewCtrlr<PopupFactory, Popup>
	) {
		super(
			name, parent, staticInfo, staticInstanceInfo, lock, wrapperConstructor,
			newCtrlr, ControllerType.POPUP_FACTORY
		);
	}
}
