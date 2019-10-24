/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Lock } from "../lock";
import { Page } from "../page";
import { PageFactory } from "../page-factory";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { FactoryImpl } from "./factory-impl";
import { NewCtrlr } from "./factory-memory";
import { PageFactoryMemory } from "./page-factory-memory";
import { PageImpl } from "./page-impl";
import { PageMemory } from "./page-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class PageFactoryImpl extends FactoryImpl<Page> implements PageFactory {
	constructor( memory: PageFactoryMemory ) {
		super( memory );
	}
}

const newPage: NewCtrlr<PageFactory, Page> =
( name, parent, staticInfo, staticInstanceInfo, lock ) => {
	return new PageMemory( name, parent, staticInfo, staticInstanceInfo, lock, PageImpl );
};

ControllerTypeToClass.put_({
	create_(
		parent: ControllerMemory, name: string, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
	) {
		return new PageFactoryMemory(
			name, parent, staticInfo, staticInstanceInfo, lock, PageFactoryImpl, newPage );
	},

	getConstructor_() {
		return PageFactoryImpl;
	},

	getType_() {
		return ControllerType.PAGE_FACTORY;
	}
});
