/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { Lock } from "../lock";
import { Page } from "../page";
import { checkSupported } from "./check-supported";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { StaticInfo, StaticInstanceInfo } from "./settings";
import { VisibilityControllerMemory } from "./visibility-controller-memory";

export class PageMemory extends VisibilityControllerMemory<Page> {
	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock, wrapperConstructor: WrapperConstructor<Page>
	) {
		super( name, parent, staticInfo, staticInstanceInfo, lock, wrapperConstructor, ControllerType.PAGE );
	}

	show_(): void {
		checkSupported( this );
		const parent = this.getParent_();
		if( parent != null ) {
			parent.showChild_( this );
		}
	}

	hide_(): void {
		checkSupported( this );
		const parent = this.getParent_();
		if( parent != null ) {
			parent.hideChild_( this );
		}
	}

	isShown_(): boolean {
		const parent = this.getParent_();
		return ( parent != null && parent.getActivePage_() === this && parent.isShown_() );
	}
}
