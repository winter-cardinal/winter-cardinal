/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isEmptyArray } from "../util/lang/is-empty";
import { Event } from "./event";

/**
 * Represents a connections.
 */
export class Connection {
	/** Event handler */
	handler: Function;

	/** Event type followed by scopes */
	type: string[];

	/** Event limit (Null means no limit.) */
	limit: number | null;

	/** Event target */
	target: unknown;

	constructor( handler: Function, type: string[], limit: number | null, target: unknown ) {
		this.handler = handler;
		this.type = type;
		this.limit = limit;
		this.target = target;
	}

	/**
	 * Invokes the newly-registered handler.
	 *
	 * @param object object firing the specified event
	 * @param target target of the specified event
	 * @param parameters array of parameters of the specified event
	 * @returns the returned values of the invoked handlers
	 */
	trigger( object: unknown, target: unknown, parameters?: unknown[] ): unknown[] {
		const newParameters = ( parameters == null ? [ null ] : [ null as unknown ].concat( parameters ) );
		return this.triggerDirect( object, target, newParameters );
	}

	triggerDirect( object: unknown, target: unknown, args?: unknown[] ): unknown[] {
		const name = this.type[ 0 ];
		if( isEmptyArray( this.type ) || isEmptyArray( name ) ) {
			return [];
		}
		const e = new Event(name, object, target, target, null);
		e.currentTarget = object;

		args = args || [ null ];
		args[ 0 ] = e;

		if( this.limit != null ) {
			if( 0 < this.limit ) {
				this.limit -= 1;
			} else {
				return [];
			}
		}

		return [this.handler.apply( e.currentTarget, args )];
	}
}
