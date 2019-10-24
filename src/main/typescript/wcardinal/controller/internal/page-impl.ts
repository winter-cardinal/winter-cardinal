/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Lock } from "../lock";
import { Page } from "../page";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { PageMemory } from "./page-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";
import { VisibilityControllerImpl } from "./visibility-controller-impl";

export class PageImpl extends VisibilityControllerImpl implements Page {
	constructor( __mem__: PageMemory ) {
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
		return new PageMemory( name, parent, staticInfo, staticInstanceInfo, lock, PageImpl );
	},

	getConstructor_() {
		return PageImpl;
	},

	getType_() {
		return ControllerType.PAGE;
	}
});
