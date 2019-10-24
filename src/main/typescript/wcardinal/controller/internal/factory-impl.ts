/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Iteratee } from "../../util/lang/each";
import { Controller } from "../controller";
import { Factory } from "../factory";
import { ComponentImpl } from "./component-impl";
import { FactoryMemory } from "./factory-memory";

export class FactoryImpl<V extends Controller>
	extends ComponentImpl<Factory<V>, FactoryMemory<Factory<V>, V>>
	implements Factory<V> {
	constructor( __mem__: FactoryMemory<Factory<V>, V> ) {
		super( __mem__ );
	}

	create( ...parameters: unknown[] ): V {
		return this.__mem__.create_( parameters );
	}

	each( iteratee: Iteratee<number, V, this> | Iteratee<string, V, this>, thisArg: unknown= this ): this {
		this.__mem__.eachWrapper_( iteratee as Iteratee<number, V, this>, thisArg );
		return this;
	}

	toArray(): V[] {
		return this.__mem__.toWrapperArray_();
	}

	size(): number {
		return this.__mem__.size_();
	}

	get( index: number ): V | null {
		return this.__mem__.getWrapperAt_( index );
	}

	isEmpty(): boolean {
		return this.__mem__.isEmpty_();
	}

	indexOf( instance: V ): number {
		return this.__mem__.indexOf_( instance );
	}

	contains( instance: V ): boolean {
		return this.__mem__.containsOf_( instance );
	}

	remove( index: number ): V | null {
		return this.__mem__.removeWrapperAt_( index );
	}

	filter( iteratee: Iteratee<number, V, this>, thisArg: unknown= this ): this {
		this.__mem__.filter_( iteratee, thisArg );
		return this;
	}

	destroy( instance: V ): boolean {
		return this.__mem__.destroyWrapper_( instance );
	}

	clear(): this {
		this.__mem__.clearCtrlrs_();
		return this;
	}

	toJson(): unknown {
		return this.__mem__.toJson_();
	}

	toString(): string {
		return this.__mem__.toString_();
	}
}
