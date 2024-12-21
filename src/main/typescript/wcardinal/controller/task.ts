/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../event/connectable";
import { TaskMemory } from "./internal/task-memory";
import { TaskCall } from "./task-call";
import { TaskMethod } from "./task-method";

/**
 * A central class for task methods.
 *
 * @param RESULT a result type
 * @param ARGUMENTS argument types
 */
export class Task<RESULT, ARGUMENTS extends unknown[]> extends Connectable
	implements TaskMethod<RESULT, ARGUMENTS> {
	constructor( protected readonly __mem__: TaskMemory<RESULT, ARGUMENTS> ) {
		super();
	}

	getArguments(): ARGUMENTS {
		return this.__mem__.getArguments_();
	}

	getArgument( index: number ): unknown {
		return this.__mem__.getArgument_( index );
	}

	getResult(): RESULT | null {
		return this.__mem__.getResult_();
	}

	getReason(): string | null {
		return this.__mem__.getReason_();
	}

	create( args: ARGUMENTS ): this {
		this.__mem__.create_( args );
		return this;
	}

	isCanceled(): boolean {
		return this.__mem__.isCanceled_();
	}

	isSucceeded(): boolean {
		return this.__mem__.isSucceeded_();
	}

	isFailed(): boolean {
		return this.__mem__.isFailed_();
	}

	isDone(): boolean {
		return this.__mem__.isDone_();
	}

	cancel(): this {
		this.__mem__.cancel_();
		return this;
	}

	isReadOnly(): boolean {
		return this.__mem__.isReadOnly_();
	}

	isNonNull(): boolean {
		return this.__mem__.isNonNull_();
	}

	uninitialize(): this {
		this.__mem__.uninitialize_();
		return this;
	}

	isInitialized(): boolean {
		return this.__mem__.isInitialized_();
	}

	initialize(): this {
		this.__mem__.initialize_();
		return this;
	}

	lock(): this {
		this.__mem__.lock_();
		return this;
	}

	isLocked(): boolean {
		return this.__mem__.isLocked_();
	}

	unlock(): this {
		this.__mem__.unlock_();
		return this;
	}

	toJson(): unknown {
		return this.__mem__.toJson_();
	}

	toString(): string {
		return this.__mem__.toString_();
	}
}

export interface Task<RESULT, ARGUMENTS extends unknown[]>
	extends Connectable, TaskMethod<RESULT, ARGUMENTS>, TaskCall<ARGUMENTS, RESULT> {}
