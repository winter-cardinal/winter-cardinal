/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "../util/lang/has-own";
import { isString } from "../util/lang/is-string";
import { PlainObject } from "../util/lang/plain-object";
import { OriginalEvent } from "./event";
import { OnoffHandler, OnonHandler } from "./handler";
import { Connector } from "./internal/connector";

/**
 * Provides event handling methods.
 */
export class Connectable {
	private __con__!: Connector;

	constructor() {
		this.connector();
	}

	connector(): Connector {
		if( this.__con__ == null ) {
			Object.defineProperties(this, {
				__con__: {
					value: new Connector()
				}
			});
		}
		return this.__con__;
	}

	/**
	 * Registers the event handler for the registration events of the specified events.
	 * The handler is invoked with [[Connection]] and a boolean when
	 * the event handlers of the specified events are registered.
	 * The latter boolean gets true if this is the first handler of the event identified
	 * by [[Connection.type]].
	 *
	 *     onon("eventname", function( connection, isFirst ){
	 *         if( some-condition ) {
	 *             connection.trigger( this, null, [ first-event-parameter, second-event-parameter, ... ] );
	 *         }
	 *     });
	 *
	 * @param types space-separated event types
	 * @param handler an event handler
	 * @returns this
	 */
	onon( types: string, handler: OnonHandler ): this {
		this.connector().onon( types, handler );
		return this;
	}

	/**
	 * Registers the event handler for the unregistration events of the specified events.
	 * The handler is invoked with [[Connection]] and a boolean when
	 * the event handlers of the specified events are unregistered.
	 * The latter boolean gets true if this is the last handler of the event identified
	 * by [[Connection.type]].
	 *
	 *     onoff("eventname", function( connection, isLast ){
	 *         // DO SOMETHING HERE
	 *     });
	 *
	 * @param types space-separated event names
	 * @param handler an event handler
	 * @returns this
	 */
	onoff( types: string, handler: OnoffHandler ): this {
		this.connector().onoff( types, handler );
		return this;
	}

	/**
	 * Unregisters the event handlers for the registration events of the specified events.
	 *
	 *     offon("eventname", handler);
	 *
	 * @param types space-separated event types
	 * @param handler an event handler
	 * @returns this
	 */
	offon( types: string, handler?: OnonHandler ): this {
		this.connector().offon( types, handler );
		return this;
	}

	/**
	 * Unregisters the event handler for the unregistration events of the specified events.
	 *
	 *     onoff("eventname", handler);
	 *
	 * @param types space-separated event names
	 * @param handler an event handler
	 * @returns this
	 */
	offoff( types: string, handler?: OnoffHandler ): this {
		this.connector().offoff( types, handler );
		return this;
	}

	/**
	 * Registers the specified event handler for the specified events.
	 * When the handler is invoked, the this keyword is a reference to the this class.
	 * This method supports space-separated event types and namespces.
	 *
	 *     on( "success", function(){} );
	 *
	 *     // `success` event with a namespace `sample`
	 *     on( "success.sample", function(){} );
	 *
	 *     // Multiple namespaces
	 *     on( "success.ns1.ns2.ns3", function(){} );
	 *
	 *     // Multiple event types
	 *     on( "success fail", function(){} );
	 *
	 *     // Event target
	 *     on( eventTarget, "success", function(){} );
	 *
	 * @param types space-separated event types
	 * @param handler the event handler to be registered
	 * @returns this
	 */
	on( types: string, handler: Function ): this;

	/**
	 * Registers the specified event handler for the specified events.
	 * When the handler is invoked, the this keyword is a reference to the this class.
	 * This method supports space-separated event types and namespces.
	 *
	 *     on( "success", function(){} );
	 *
	 *     // `success` event with a namespace `sample`
	 *     on( "success.sample", function(){} );
	 *
	 *     // Multiple namespaces
	 *     on( "success.ns1.ns2.ns3", function(){} );
	 *
	 *     // Multiple event types
	 *     on( "success fail", function(){} );
	 *
	 *     // Event target
	 *     on( eventTarget, "success", function(){} );
	 *
	 * @param target event target or event types
	 * @param types space-separated event types
	 * @param handler the event handler to be registered
	 * @returns this
	 */
	on( target: unknown, types: string, handler: Function ): this;

	on(	targetOrTypes: unknown, typesOrHandler: string | Function, handler?: Function ): this {
		const c = this.connector();
		if( isString( typesOrHandler ) ) {
			c.on( targetOrTypes, typesOrHandler, handler! );
		} else {
			c.on( this, targetOrTypes as string, typesOrHandler );
		}
		return this;
	}

	/**
	 * Registers the specified event handler for the specified events.
	 * The specified event handler is invoked at most once.
	 * When the handler is invoked, the this keyword is a reference to the this class.
	 * This method supports space-separated event types and namespces.
	 *
	 *     one( "success", function(){} );
	 *
	 *     // `success` event with a namespace `sample`
	 *     one( "success.sample", function(){} );
	 *
	 *     // Multiple namespaces
	 *     one( "success.ns1.ns2.ns3", function(){} );
	 *
	 *     // Multiple event types
	 *     one( "success fail", function(){} );
	 *
	 * @param types space-separated event types
	 * @param handler The event handler to be registered
	 * @returns this
	 */
	one( types: string, handler: Function ): this;

	/**
	 * Registers the specified event handler for the specified events.
	 * The specified event handler is invoked at most once.
	 * When the handler is invoked, the this keyword is a reference to the this class.
	 * This method supports space-separated event types and namespces.
	 *
	 *     one( "success", function(){} );
	 *
	 *     // `success` event with a namespace `sample`
	 *     one( "success.sample", function(){} );
	 *
	 *     // Multiple namespaces
	 *     one( "success.ns1.ns2.ns3", function(){} );
	 *
	 *     // Multiple event types
	 *     one( "success fail", function(){} );
	 *
	 * @param target event target
	 * @param types space-separated event types
	 * @param handler The event handler to be registered
	 * @returns this
	 */
	one( target: unknown, types: string, handler: Function ): this;

	one( targetOrTypes: unknown, typesOrHandler: string | Function, handler?: Function ): this {
		const c = this.connector();
		if( isString( typesOrHandler ) ) {
			c.one( targetOrTypes, typesOrHandler, handler! );
		} else {
			c.one( this, targetOrTypes as string, typesOrHandler );
		}
		return this;
	}

	/**
	 * Unregister the event handlers of the specified events.
	 *
	 *     off( "success" );
	 *
	 *     // Unregister all the handlers of the events with the namespace `sample`.
	 *     off( ".sample" );
	 *
	 *     // Unregister all the handlers of the `success` event with the namespace `sample`.
	 *     off( "success.sample" );
	 *
	 *     // Multiple event types
	 *     off( "success fail" );
	 *
	 * @param types space-separated event types
	 * @param handler the event handler to be unregistered
	 * @returns this
	 */
	off( types: string, handler?: Function ): this;

	/**
	 * Unregister the event handlers of the specified events.
	 *
	 *     off( "success" );
	 *
	 *     // Unregister all the handlers of the events with the namespace `sample`.
	 *     off( ".sample" );
	 *
	 *     // Unregister all the handlers of the `success` event with the namespace `sample`.
	 *     off( "success.sample" );
	 *
	 *     // Multiple event types
	 *     off( "success fail" );
	 *
	 * @param target event target
	 * @param types space-separated event types
	 * @param handler the event handler to be unregistered
	 * @returns this
	 */
	off( target: unknown, types: string, handler?: Function ): this;

	off( targetOrTypes: unknown, typesOrHandler?: string | Function, handler?: Function ): this {
		const a = arguments;
		const c = this.connector();
		if( a.length === 1 ) {
			c.off( this, targetOrTypes as string, null );
		} else if( a.length === 2 ) {
			if( isString(a[0]) ) {
				c.off( this, targetOrTypes as string, typesOrHandler as Function );
			} else {
				c.off( targetOrTypes, typesOrHandler as string, null );
			}
		} else {
			c.off( targetOrTypes, typesOrHandler as string, handler );
		}
		return this;
	}

	/**
	 * Invokes all the handlers of the specified event.
	 *
	 *     trigger( "success" );
	 *
	 *     // With two parameters true and 156
	 *     trigger( "success", [true, 156] );
	 *
	 *     // Namespaced event type
	 *     trigger( "success.sample" );
	 *
	 *     // Multiple event types
	 *     trigger( "success fail" );
	 *
	 * @param types space-separated event types
	 * @param parameters event parameters
	 * @returns the returned values of the invoked handlers
	 */
	trigger( types: string, parameters?: unknown[] ): unknown[];

	/**
	 * Invokes all the handlers of the specified event.
	 *
	 *     trigger( "success" );
	 *
	 *     // With two parameters true and 156
	 *     trigger( "success", [true, 156] );
	 *
	 *     // Namespaced event type
	 *     trigger( "success.sample" );
	 *
	 *     // Multiple event types
	 *     trigger( "success fail" );
	 *
	 * @param target Event target
	 * @param types space-separated event types
	 * @param parameters event parameters
	 * @returns the returned values of the invoked handlers
	 */
	trigger( target: Node | null, types: string, parameters?: unknown[] ): unknown[];

	trigger(
		targetOrTypes: Node | string | null, typesOrParameters?: string | unknown[],
		parameters?: unknown[]
	): unknown[] {
		const a = arguments;
		const c = this.connector();
		if( a.length === 1 ) {
			return c.trigger( this, null, targetOrTypes as string, null );
		} else if( a.length === 2 ) {
			if( isString(a[0]) ) {
				return c.trigger(
					this, null, targetOrTypes as string,
					typesOrParameters as unknown[] | undefined
				);
			} else {
				return c.trigger(
					this, targetOrTypes as Node | null,
					typesOrParameters as string, null
				);
			}
		} else {
			return c.trigger(
				this, targetOrTypes as Node | null,
				typesOrParameters as string, parameters
			);
		}
	}

	triggerDirect(
		name: string, type: string[] | null, args: unknown[] | null, results: null
	): null;
	triggerDirect(
		name: string, type: string[] | null, args: unknown[] | null, results: unknown[]
	): unknown[];
	triggerDirect(
		name: string, type: string[] | null, args: unknown[] | null, results: unknown[] | null
	): unknown[] | null {
		return this.connector().triggerDirect( this, name, type, args || [ null ], results );
	}

	/**
	 * Invokes all the handlers of the specified event.
	 *
	 *     retrigger( originalEvent, "success" );
	 *
	 *     // With two parameters true and 156
	 *     retrigger( originalEvent, "success", [true, 156] );
	 *
	 *     // Namespaced event type
	 *     retrigger( originalEvent, "success.sample" );
	 *
	 *     // Multiple event types
	 *     retrigger( originalEvent, "success fail" );
	 *
	 * @param originalEvent the event object
	 * @param {string} types space-separated event types
	 * @param {Array<*>} extraParameters event parameters
	 * @returns {Array<*>} the returned values of the invoked handlers
	 */
	retrigger(
		originalEvent: OriginalEvent, types: string, parameters?: unknown[]
	): unknown[];

	/**
	 * Invokes all the handlers of the specified event.
	 *
	 *     retrigger( originalEvent, "success" );
	 *
	 *     // With two parameters true and 156
	 *     retrigger( originalEvent, "success", [true, 156] );
	 *
	 *     // Namespaced event type
	 *     retrigger( originalEvent, "success.sample" );
	 *
	 *     // Multiple event types
	 *     retrigger( originalEvent, "success fail" );
	 *
	 * @param target event target
	 * @param originalEvent the event object
	 * @param {string} types space-separated event types
	 * @param {Array<*>} parameters event parameters
	 * @returns {Array<*>} the returned values of the invoked handlers
	 */
	retrigger(
		target: Node | null, originalEvent: OriginalEvent, types: string, parameters?: unknown[]
	): unknown[];

	retrigger(
		targetOrOriginalEvent: unknown, originalEventOrTypes: OriginalEvent | string,
		typesOrParameters: string | unknown[] | null | undefined, parameters?: unknown[]
	): unknown[] {
		const a = arguments;
		const c = this.connector();
		if( a.length === 2 ) {
			return c.retrigger(
				this, null,
				targetOrOriginalEvent as OriginalEvent,
				originalEventOrTypes as string, null
			);
		} else if( a.length === 3 ) {
			if( isString(a[2]) ) {
				return c.retrigger(
					this, targetOrOriginalEvent as Node | null,
					originalEventOrTypes as OriginalEvent,
					typesOrParameters as string, null
				);
			} else {
				return c.retrigger(
					this, null,
					targetOrOriginalEvent as OriginalEvent,
					originalEventOrTypes as string,
					typesOrParameters as unknown[]
				);
			}
		} else {
			return c.retrigger(
				this, targetOrOriginalEvent as Node | null,
				originalEventOrTypes as OriginalEvent,
				typesOrParameters as string, parameters
			);
		}
	}

	/**
	 * Extends the specified target with the Connectable methods.
	 *
	 * @param target non-null target to be extended
	 * @returns the specified target
	 */
	static extend<T>( target: T ): T & Connectable {
		const parameters: PlainObject<{ value: unknown }> = {};
		const prototype = this.prototype as any;
		for( const key in prototype ) {
			if( hasOwn( prototype, key ) ) {
				parameters[ key ] = { value: prototype[ key ] };
			}
		}
		return Object.defineProperties( target, parameters ) as T & Connectable;
	}
}
