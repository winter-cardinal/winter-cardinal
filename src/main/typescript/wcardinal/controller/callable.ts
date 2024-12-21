/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../event/connectable";
import { CallableCall } from "./callable-call";
import { CallableMethod } from "./callable-method";
import { CallableMemory } from "./internal/callable-memory";

/**
 * A central class for callble methods.
 *
 * @param RESULT a return type
 * @param ARGUMENTS argument types
 */
export class Callable<RESULT, ARGUMENTS extends unknown[]> extends Connectable
	implements CallableMethod<RESULT, ARGUMENTS> {

	constructor( protected readonly __mem__: CallableMemory<RESULT, ARGUMENTS> ) {
		super();
	}

	timeout( timeout: number ): this {
		this.__mem__.timeout_( timeout );
		return this;
	}

	ajax(): this {
		this.__mem__.ajax_();
		return this;
	}

	unajax(): this {
		this.__mem__.unajax_();
		return this;
	}

	safe(): this {
		return this;
	}

	unsafe(): this {
		return this;
	}

	call( ...args: ARGUMENTS ): Promise<RESULT> {
		return this.invoke( args );
	}

	invoke( args: ARGUMENTS ): Promise<RESULT> {
		return this.__mem__.call_( args );
	}
}

export interface Callable<RESULT, ARGUMENTS extends unknown[]>
	extends Connectable, CallableMethod<RESULT, ARGUMENTS>, CallableCall<RESULT, ARGUMENTS> {}
