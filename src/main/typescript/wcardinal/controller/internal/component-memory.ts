/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component } from "../component";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { Lock } from "../lock";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export class ComponentMemory<W extends Component = Component> extends ControllerMemory<W> {
	constructor(
		name: string, parent: ControllerMemory, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock,
		wrapperConstructor: WrapperConstructor<W>, type= ControllerType.COMPONENT
	) {
		super( name, parent, staticInfo, staticInstanceInfo, lock, wrapperConstructor, type );
	}
}
