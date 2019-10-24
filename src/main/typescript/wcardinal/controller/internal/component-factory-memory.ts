/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component } from "../component";
import { ComponentFactory } from "../component-factory";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { Lock } from "../lock";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { FactoryMemory, NewCtrlr } from "./factory-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class ComponentFactoryMemory extends FactoryMemory<ComponentFactory, Component> {
	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock,
		wrapperConstructor: WrapperConstructor<ComponentFactory>,
		newCtrlr: NewCtrlr<ComponentFactory, Component>
	) {
		super(
			name, parent, staticInfo, staticInstanceInfo, lock, wrapperConstructor,
			newCtrlr, ControllerType.COMPONENT_FACTORY
		);
	}
}
