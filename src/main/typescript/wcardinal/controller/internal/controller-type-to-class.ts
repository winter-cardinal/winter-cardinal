/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Lock } from "../lock";
import { ControllerMemory } from "./controller-memory";
import { ControllerType } from "./controller-type";
import { StaticInfo, StaticInstanceInfo } from "./settings";

export interface ControllerTypeFactory {
	create_(
		parent: ControllerMemory, name: string, staticInfo: StaticInfo,
		staticInstanceInfo: StaticInstanceInfo | null, lock: Lock
	): ControllerMemory;
	getConstructor_(): Function;
	getType_(): ControllerType;
}

const mapping: { [type: number ]: ControllerTypeFactory } = {};

export class ControllerTypeToClass {
	static put_( factory: ControllerTypeFactory ) {
		mapping[ factory.getType_() ] = factory;
	}

	static getFactory_( type: ControllerType ): ControllerTypeFactory {
		return mapping[ type ];
	}

	static getType_( constructor: Function ): ControllerType | null {
		for( const type in mapping ) {
			const factory = mapping[ type ];
			if( factory.getConstructor_() === constructor ) {
				return factory.getType_();
			}
		}
		return null;
	}
}
