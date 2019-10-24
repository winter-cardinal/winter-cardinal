/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../event/connectable";

export interface TaskMethod<RESULT, ARGUMENTS extends unknown[]> extends Connectable {
	/**
	 * Returns the current task arguments or null if it does not exist.
	 *
	 * @returns the current task arguments or null if it does not exist
	 */
	getArguments(): ARGUMENTS;

	/**
	 * Returns the current task argument at the specified position or null if it does not exist.
	 *
	 * @param index an index of an argument
	 * @returns the current task argument at the specified or null if it does not exist
	 */
	getArgument( index: number ): unknown;

	/**
	 * Returns the current task result or null if it does not exist.
	 *
	 * @returns the current task result or null if it does not exist
	 */
	getResult(): RESULT | null;

	/**
	 * Returns the reason why the current task is failed.
	 * Returns null when isFailed() is not true.
	 *
	 * @returns the reason why the current task is failed
	 */
	getReason(): string | null;

	/**
	 * Creates a new task with the specified arguments and cancels previous tasks if possible.
	 *
	 * @param args a task arguments
	 * @throws Error if this is read-only
	 * @throws Error if the `args` contains null and this does not allow null value
	 * @returns this
	 */
	create( args: ARGUMENTS ): this;

	/**
	 * Returns true if the current task is canceled.
	 *
	 * @returns true if the current task is canceled
	 */
	isCanceled(): boolean;

	/**
	 * Returns true if the current task is done successfully.
	 *
	 * @returns true if the current task is done successfully
	 */
	isSucceeded(): boolean;

	/**
	 * Returns true if the current task is failed.
	 *
	 * @returns true if the current task is failed
	 */
	isFailed(): boolean;

	/**
	 * Returns true If the current task is completed or if no task exists.
	 * Completion may be due to normal termination, cancellation, or anything else.
	 *
	 * @returns true if the current task is done
	 */
	isDone(): boolean;

	/**
	 * Tries to cancel the current task.
	 * This operation may fail if the task is already done.
	 *
	 * @throws Error if this is read-only
	 * @returns this
	 */
	cancel(): this;

	/**
	 * Returns true if this is read-only.
	 *
	 * @returns true if this is read-only
	 */
	isReadOnly(): boolean;

	/**
	 * Returns true if this is non-null.
	 *
	 * @returns true if this is non-null.
	 */
	isNonNull(): boolean;

	/**
	 * Returns true if this is initialized.
	 *
	 * @returns true if this is initialized.
	 */
	isInitialized(): boolean;

	/**
	 * Locks this task.
	 *
	 * @returns this
	 */
	lock(): this;

	/**
	 * Returns true if this task is locked.
	 *
	 * @returns true if this task is locked
	 */
	isLocked(): boolean;

	/**
	 * Unlock this task.
	 *
	 * @returns this
	 */
	unlock(): this;

	/**
	 * Returns the JSON object representing this task.
	 *
	 * @returns the JSON object representing this task
	 */
	toJson(): unknown;

	/**
	 * Returns the string representing this task.
	 *
	 * @returns the string representing this task
	 */
	toString(): string;

	/**
	 * Triggered when a task is created.
	 *
	 * @event create
	 * @param event an event object
	 * @param args a task arguments
	 * @param task a task itself
	 * @internal
	 */
	oncreate?( event: Event, args: unknown[], task: this ): void;

	/**
	 * Triggered when a task is done successfully.
	 *
	 * @event success
	 * @param event an event object
	 * @param result a task result
	 * @param task a task itself
	 */
	onsuccess?( event: Event, result: RESULT | null, task: this ): void;

	/**
	 * Triggered when a task is failed.
	 *
	 * @event fail
	 * @param event an event object
	 * @param reason a reason
	 * @param task a task itself
	 */
	onfail?( event: Event, reason: string | null, task: this ): void;
}
