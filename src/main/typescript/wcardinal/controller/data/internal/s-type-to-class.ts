/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Properties } from "../../internal/properties";
import { Lock } from "../../lock";
import { SContainerMemory } from "./s-container-memory";
import { SScalarMemory } from "./s-scalar-memory";
import { SType } from "./s-type";

interface STypeFactory {
	create_( parent: unknown, name: string, properties: Properties, lock: Lock ): SScalarMemory | SContainerMemory;
	getConstructor_(): Function;
	getType_(): SType;
}

const mapping: {[ type: number ]: STypeFactory } = {};

export class STypeToClass {
	static put_( factory: STypeFactory ): void {
		mapping[ factory.getType_() ] = factory;
	}

	static getFactory_( type: SType ): STypeFactory {
		return mapping[ type ];
	}

	static getType_( constructor: Function ): SType | null {
		for( const key in mapping ) {
			const factory = mapping[ key ];
			if( factory.getConstructor_() === constructor ) {
				return factory.getType_();
			}
		}
		return null;
	}
}
