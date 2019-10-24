/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { copyEvent } from "./internal/copy-event";

export interface OriginalEvent {
	cancelBubble?: boolean;
	returnValue?: unknown;
	stopPropagation?(): void;
	stopImmediatePropagation?(): void;
	preventDefault?(): void;
}

/**
 * Provides event data and event operations
 */
export class Event {
	/** Event name */
	readonly type: string;

	/** Object who triggered this event */
	readonly object: unknown;

	/** Current target of this event */
	currentTarget: unknown;

	/** Origin of this event */
	readonly target: unknown;

	/** Original event object */
	readonly originalEvent: OriginalEvent | null;

	private _isPropagationStopped: boolean;
	private _isImmediatePropagationStopped: boolean;
	private _isDefaultPrevented: boolean;

	/**
	 * @param type the event type
	 * @param object the object who triggered this event
	 * @param currentTarget the current target of this event
	 * @param target the origin of this event
	 * @param originalEvent the original event object
	 */
	constructor(
		type: string, object: unknown, currentTarget: unknown,
		target: unknown, originalEvent: OriginalEvent | null
	) {
		// Copy info
		if( originalEvent != null ) {
			copyEvent( this as any, originalEvent as any );
		}

		this.type = type;
		this.object = object;
		this.currentTarget = currentTarget;
		this.target = target;
		this.originalEvent = originalEvent;

		this._isPropagationStopped = false;
		this._isImmediatePropagationStopped = false;
		this._isDefaultPrevented = false;
	}

	/**
	 * Prevents this event from bubbling up the DOM tree, and any parent handlers from being notified of this event.
	 *
	 * @returns this
	 */
	stopPropagation(): Event {
		this._isPropagationStopped = true;

		const oe = this.originalEvent;
		if( oe ) {
			if( oe.stopPropagation ) {
				oe.stopPropagation();
			}
			oe.cancelBubble = true;
		}
		return this;
	}

	/**
	 * Prevents the rest of event handlers from being notified of this event.
	 *
	 * @returns this
	 */
	stopImmediatePropagation(): Event {
		this._isImmediatePropagationStopped = true;

		const oe = this.originalEvent;
		if( oe ) {
			if( oe.stopImmediatePropagation ) {
				oe.stopImmediatePropagation();
			} else if( oe.stopPropagation ) {
				oe.stopPropagation();
			}
			oe.cancelBubble = true;
		}
		return this;
	}

	/**
	 * Prevents the default action of this event from being executed.
	 *
	 * @returns this
	 */
	preventDefault(): Event {
		this._isDefaultPrevented = true;

		const oe = this.originalEvent;
		if( oe ) {
			if( oe.preventDefault ) {
				oe.preventDefault();
			}
			oe.returnValue = false;
		}
		return this;
	}

	/**
	 * Returns true if wcardinal.event.Event#stopPropagation was called previously.
	 *
	 * @returns true if wcardinal.event.Event#stopPropagation was called previously
	 */
	isPropagationStopped(): boolean {
		return this._isPropagationStopped;
	}

	/**
	 * Returns true if wcardinal.event.Event#stopImmediatePropagation was called previously.
	 *
	 * @returns true if wcardinal.event.Event#stopImmediatePropagation was called previously
	 */
	isImmediatePropagationStopped(): boolean {
		return this._isImmediatePropagationStopped;
	}

	/**
	 * Returns true if wcardinal.event.Event#preventDefault was called previously.
	 *
	 * @returns true if wcardinal.event.Event#preventDefault was called previously
	 */
	isDefaultPrevented(): boolean {
		return this._isDefaultPrevented;
	}
}
