/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component } from "../component";
import { ComponentFactory } from "../component-factory";
import { Lock } from "../lock";
import { ComponentFactoryMemory } from "./component-factory-memory";
import { ComponentImpl } from "./component-impl";
import { ComponentMemory } from "./component-memory";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { ControllerTypeToClass } from "./controller-type-to-class";
import { FactoryImpl } from "./factory-impl";
import { NewCtrlr } from "./factory-memory";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class ComponentFactoryImpl extends FactoryImpl<Component> implements ComponentFactory {
	constructor( memory: ComponentFactoryMemory ) {
		super( memory );
	}
}

const newComponent: NewCtrlr<ComponentFactory, Component> =
( name, parent, staticInfo, staticInstanceInfo, lock ) => {
	return new ComponentMemory( name, parent, staticInfo, staticInstanceInfo, lock, ComponentImpl );
};

ControllerTypeToClass.put_({
	create_(
		parent: ControllerMemory, name: string, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
	) {
		return new ComponentFactoryMemory(
			name, parent, staticInfo, staticInstanceInfo, lock, ComponentFactoryImpl, newComponent );
	},

	getConstructor_() {
		return ComponentFactoryImpl;
	},

	getType_() {
		return ControllerType.COMPONENT_FACTORY;
	}
});
