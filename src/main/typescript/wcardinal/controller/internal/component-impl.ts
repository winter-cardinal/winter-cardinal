/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component } from "../component";
import { Lock } from "../lock";
import { ComponentMemory } from "./component-memory";
import { ControllerImpl } from "./controller-impl";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class ComponentImpl<
	C extends Component= Component,
	M extends ComponentMemory<C> = ComponentMemory<C>
> extends ControllerImpl<C, M> implements Component {
	constructor( __mem__: M ) {
		super( __mem__ );
	}
}

ControllerTypeToClass.put_({
	create_(
		parent: ControllerMemory, name: string, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
	) {
		return new ComponentMemory(
			name, parent, staticInfo, staticInstanceInfo, lock, ComponentImpl
		);
	},

	getConstructor_() {
		return ComponentImpl;
	},

	getType_() {
		return ControllerType.COMPONENT;
	}
});

ControllerTypeToClass.put_({
	create_(
		parent: ControllerMemory, name: string, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
	) {
		return new ComponentMemory(
			name, parent, staticInfo, staticInstanceInfo, lock, ComponentImpl
		);
	},

	getConstructor_() {
		return ComponentImpl;
	},

	getType_() {
		return ControllerType.SHARED_COMPONENT;
	}
});
