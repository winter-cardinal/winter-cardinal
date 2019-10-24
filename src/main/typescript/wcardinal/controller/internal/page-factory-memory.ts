/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { Lock } from "../lock";
import { Page } from "../page";
import { PageFactory } from "../page-factory";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { FactoryMemory, NewCtrlr } from "./factory-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class PageFactoryMemory extends FactoryMemory<PageFactory, Page> {
	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock,
		wrapperConstructor: WrapperConstructor<PageFactory>,
		newCtrlr: NewCtrlr<PageFactory, Page>
	) {
		super(
			name, parent, staticInfo, staticInstanceInfo, lock,
			wrapperConstructor, newCtrlr, ControllerType.PAGE_FACTORY
		);
	}
}
