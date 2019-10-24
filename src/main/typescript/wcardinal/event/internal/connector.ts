/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isEmptyArray, isEmptyObject, isNotEmptyArray } from "../../util/lang/is-empty";
import { Connection } from "../connection";
import { Event, OriginalEvent } from "../event";
import { OnoffHandler, OnonHandler } from "../handler";
import { ConnectionsMap } from "./connections-map";

const SPLIT_REGEX = /\s+/;
const NAMESPACE_PREFIX = ".";
const ASTERISK = "*";
const FAKE_TYPE = [""];
const parse = ( typesString: string ): string[][] => {
	const result: string[][] = [];
	const types: string[] = typesString.trim().split( SPLIT_REGEX );
	for( let i = 0, imax = types.length; i < imax; ++i ) {
		const type = types[ i ];
		const nameAndNamespaces: string[] = type.split( NAMESPACE_PREFIX );
		if( nameAndNamespaces[ 0 ] === NAMESPACE_PREFIX ) {
			nameAndNamespaces[ 0 ] = ASTERISK;
		}
		result.push( nameAndNamespaces );
	}
	return result;
};

const addOnon = ( context: Connector, type: string[], handler: OnonHandler ): void => {
	const name = type[0];
	if( isNotEmptyArray( type ) && isNotEmptyArray( name ) ) {
		const nameToOnons = context.getOnons();
		nameToOnons[name] = nameToOnons[name] || [];
		nameToOnons[name].push( new Connection( handler, type, null, null ) );
	}
};

const checkOnonType = ( onon: Connection, type: string[] ): boolean => {
	for( let i = 1, imax = onon.type.length; i < imax; ++i ) {
		if( type.indexOf( onon.type[i] ) <= 0 ) {
			return false;
		}
	}
	return true;
};

const callOnon = ( context: Connector, type: string[], connection: Connection, isFirst: boolean ): void => {
	const name = connection.type[0];

	const onons = context.getOnons()[name];
	if( onons != null ) {
		for( let i = 0, imax = onons.length; i < imax; ++i ) {
			const onon = onons[i];
			if( checkOnonType( onon, type ) !== true ) {
				continue;
			}
			onon.handler.call( connection.target, connection, isFirst );
		}
	}
};

const addOnoff = ( context: Connector, type: string[], handler: OnoffHandler ): void => {
	const name = type[0];
	if( isNotEmptyArray( type ) && isNotEmptyArray( name ) ) {
		const nameToOnoffs = context.getOnoffs();
		nameToOnoffs[name] = nameToOnoffs[name] || [];
		nameToOnoffs[name].push( new Connection( handler, type, null, null ) );
	}
};

const checkOnoffType = ( onoff: Connection, type: string[] ): boolean => {
	for( let i = 1, imax = onoff.type.length; i < imax; ++i ) {
		if( type.indexOf( onoff.type[i] ) <= 0 ) {
			return false;
		}
	}
	return true;
};

const callOnoff = ( context: Connector, type: string[], connection: Connection, isLast: boolean ): void => {
	const name = connection.type[0];

	const onoffs = context.getOnoffs()[name];
	if( onoffs != null ) {
		for( let i = 0, imax = onoffs.length; i < imax; ++i ) {
			const onoff = onoffs[i];
			if( checkOnoffType( onoff, type ) === true ) {
				onoff.handler.call( connection.target, connection, isLast );
			}
		}
	}
};

const addConnection = (
	context: Connector, target: unknown, type: string[], handler: Function, limit: number | null
): void => {
	const name = type[0];
	if( isNotEmptyArray( type ) && isNotEmptyArray( name ) ) {
		const connection = new Connection( handler, type, limit, target );
		const nameToConnections = context.getConnections();
		nameToConnections[ name ] = nameToConnections[ name ] || [];

		let connections = nameToConnections[ name ];
		connections.push( connection );

		callOnon( context, type, connection, connections.length === 1 );

		if( connection.limit != null && connection.limit <= 0 ) {
			// Here, we need to refresh the `sconnections`.
			// This is because event handlers invoked via the `callOnon`
			// may change the `connections`.
			connections = nameToConnections[ name ];
			if( connections != null ) {
				const index = connections.indexOf( connection );
				if( 0 <= index ) {
					connections.splice( index, 1 );
					callOnoff( context, type, connection, isEmptyArray( connections ) );
				}
			}
		}
	}
};

const match = ( connection: Connection, type: string[], target: unknown ): boolean => {
	for( let i = 1, imax = type.length; i < imax; ++i ) {
		if( connection.type.indexOf( type[i] ) <= 0 ) {
			return false;
		}
	}
	return connection.target === target;
};

const filter = (
	context: Connector, connections: Connection[], type: string[],
	handler: Function | null | undefined, target: unknown, callOnOff: boolean
): Connection[] => {
	const result: Connection[] = [];
	for( let i = 0, imax = connections.length; i < imax; ++i ) {
		const connection = connections[ i ];
		if( match( connection, type, target ) ) {
			if( handler == null || connection.handler === handler ) {
				if( callOnOff ) {
					callOnoff( context, type, connection, i === imax - 1 && isEmptyArray( result ) );
				}
				continue;
			}
		}
		result.push( connection );
	}
	return result;
};

const removeConnection = (
	context: Connector, target: unknown, type: string[], handler: Function | null | undefined
): void => {
	const name = type[0];
	if( isNotEmptyArray( type ) && isNotEmptyArray( name ) ) {
		filterConnections( context, target, context.getConnections(), name, type, handler, true );
	}
};

const filterConnection = (
	context: Connector, target: unknown, nameToConnections: ConnectionsMap,
	name: string, type: string[], handler: Function | null | undefined, callOnOff: boolean
): void => {
	const filteredConnections = filter( context, nameToConnections[ name ], type, handler, target, callOnOff );
	if( isNotEmptyArray( filteredConnections ) ) {
		nameToConnections[ name ] = filteredConnections;
	} else {
		delete nameToConnections[ name ];
	}
};

const filterConnections = (
	context: Connector, target: unknown, nameToConnections: ConnectionsMap,
	name: string, type: string[], handler: Function | null | undefined, callOnOff: boolean
): void => {
	if( name !== ASTERISK ) {
		if( nameToConnections[ name ] == null ) {
			return;
		}
		filterConnection( context, target, nameToConnections, name, type, handler, callOnOff );
	} else {
		for( name in nameToConnections ) {
			filterConnection( context, target, nameToConnections, name, type, handler, callOnOff );
		}
	}
};

const removeOnon = ( context: Connector, type: string[], handler: OnonHandler | null | undefined ): void => {
	const name = type[0];
	if( isNotEmptyArray( type ) && isNotEmptyArray( name ) ) {
		filterConnections( context, null, context.getOnons(), name, type, handler, false );
	}
};

const removeOnoff = ( context: Connector, type: string[], handler: OnoffHandler | null | undefined ): void => {
	const name = type[0];
	if( isNotEmptyArray( type ) && isNotEmptyArray( name ) ) {
		filterConnections( context, null, context.getOnoffs(), name, type, handler, false );
	}
};

const triggerConnections = (
	context: Connector, connections: Connection[], type: string[],
	e: Event, args: unknown[], results: unknown[] | null | undefined
): void => {
	if( connections != null ) {
		for( let i = 0, imax = connections.length; i < imax; ++i ) {
			if( e.isImmediatePropagationStopped() ) {
				return;
			}
			const connection = connections[ i ];
			if( match( connection, type, e.currentTarget ) ) {
				let removed = false;
				if( connection.limit != null ) {
					if( connection.limit === 1 ) {
						connection.limit = 0;
						connections.splice(i, 1);
						i -= 1;
						imax -= 1;
						removed = true;
					} else if( connection.limit <= 0 ) {
						continue;
					} else {
						connection.limit -= 1;
					}
				}

				const result = connection.handler.apply( e.currentTarget, args );
				if( results != null ) {
					results.push( result );
				}
				if( removed ) {
					callOnoff( context, type, connection, isEmptyArray( connections ) );
				}
			}
		}
	}
};

const triggerChain = (
	context: Connector, chain: unknown[], connections: Connection[],
	type: string[], e: Event, args: unknown[], results: unknown[]
): void => {
	for( let i = 0, imax = chain.length; i < imax; ++i ) {
		if( ! e.isPropagationStopped() && ! e.isImmediatePropagationStopped() ) {
			e.currentTarget = chain[ i ];
			triggerConnections( context, connections, type, e, args, results );
		}
	}
};

const getChain = ( object: unknown, target: Node | null ): unknown[] => {
	const result = [];
	let currentTarget: Node | null = target;
	while( currentTarget != null ) {
		result.push( currentTarget );
		currentTarget = currentTarget.parentNode;
	}
	result.push( object );
	return result;
};

const getArgs = ( eventObject: unknown, params: unknown[] | null | undefined ): unknown[] => {
	return [ eventObject ].concat( params || [] );
};

const triggerNamed = (
	context: Connector, object: unknown, target: Node | null, name: string,
	type: string[], originalEvent: OriginalEvent | null, params: unknown[] | null | undefined, results: unknown[]
): void => {
	const nameToConnections = context.getConnections();
	const connectionsName = nameToConnections[ name ];
	const hasConnectionName = ( connectionsName != null && isNotEmptyArray( connectionsName ) );
	const connectionsStar = nameToConnections[ ASTERISK ];
	const hasConnectionStar = ( connectionsStar != null && isNotEmptyArray( connectionsStar ) );
	if( hasConnectionName || hasConnectionStar ) {
		const eventObject = new Event(name, object, target, target, originalEvent);
		const args = getArgs( eventObject, params );
		const chain = getChain( object, target );

		if( hasConnectionName ) {
			triggerChain( context, chain, connectionsName, type, eventObject, args, results );
		}

		if( hasConnectionStar ) {
			triggerChain( context, chain, connectionsStar, type, eventObject, args, results );
		}
	}
};

const triggerUnnamed = (
	context: Connector, object: unknown, target: Node | null, type: string[],
	originalEvent: OriginalEvent | null, params: unknown[] | null | undefined, results: unknown[]
): void => {
	const eventObject = new Event( "", object, target, target, originalEvent );
	const args = getArgs( eventObject, params );
	const chain = getChain( object, target );

	const nameToConnections = context.getConnections();
	for( const name in nameToConnections ) {
		triggerChain( context, chain, nameToConnections[ name ], type, eventObject, args, results );
	}
};

const trigger = (
	context: Connector, object: unknown, target: Node | null, type: string[],
	originalEvent: OriginalEvent | null, params: unknown[] | null | undefined, results: unknown[]
): void => {
	const name = type[ 0 ];
	if( isNotEmptyArray( type ) && isNotEmptyArray( name ) ) {
		if( name !== ASTERISK ) {
			triggerNamed( context, object, target, name, type, originalEvent, params, results );
		} else {
			triggerUnnamed( context, object, target, type, originalEvent, params, results );
		}
	}
};

/**
 * Provides event handling functions.
 */
export class Connector {
	private _nameToConnections: ConnectionsMap;
	private _nameToOnons: ConnectionsMap;
	private _nameToOnoffs: ConnectionsMap;

	constructor() {
		this._nameToConnections = {};
		this._nameToOnons = {};
		this._nameToOnoffs = {};
	}

	getConnections(): ConnectionsMap {
		return this._nameToConnections;
	}

	containsConnections(): boolean {
		return ! isEmptyObject( this._nameToConnections );
	}

	getOnons(): ConnectionsMap {
		return this._nameToOnons;
	}

	containsOnons(): boolean {
		return ! isEmptyObject( this._nameToOnons );
	}

	getOnoffs(): ConnectionsMap {
		return this._nameToOnoffs;
	}

	containsOnoffs(): boolean {
		return ! isEmptyObject( this._nameToOnoffs );
	}

	onon( types: string, handler: OnonHandler ): void {
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			addOnon( this, typesList[i], handler );
		}
	}

	onoff( types: string, handler: OnoffHandler ): void {
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			addOnoff( this, typesList[i], handler );
		}
	}

	on( target: unknown, types: string, handler: Function ): void {
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			addConnection( this, target, typesList[i], handler, null );
		}
	}

	one( target: unknown, types: string, handler: Function ): void {
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			addConnection( this, target, typesList[i], handler, 1 );
		}
	}

	off( target: unknown, types: string, handler: Function | null | undefined ): void {
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			removeConnection( this, target, typesList[i], handler );
		}
	}

	offon( types: string, handler: OnonHandler | null | undefined ): void {
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			removeOnon( this, typesList[i], handler );
		}
	}

	offoff( types: string, handler: OnoffHandler | null | undefined ): void {
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			removeOnoff( this, typesList[i], handler );
		}
	}

	trigger(
		object: unknown, target: Node | null, types: string, params: unknown[] | null | undefined
	): unknown[] {
		const results: unknown[] = [];
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			trigger( this, object, target, typesList[i], null, params, results );
		}
		return results;
	}

	triggerDirect(
		object: unknown, name: string, type: string[] | null, args: unknown[], results: unknown[] | null
	): unknown[] | null {
		let eventObject = null;

		const nameToConnections = this._nameToConnections;
		const connectionsName = nameToConnections[ name ];
		if( connectionsName != null && isNotEmptyArray( connectionsName ) ) {
			eventObject = new Event(name, object, object, null, null);
			args[ 0 ] = eventObject;
			triggerConnections( this, connectionsName, type || FAKE_TYPE, eventObject, args, results );
		}

		const connectionsStar = nameToConnections[ ASTERISK ];
		if( connectionsStar != null && isNotEmptyArray( connectionsStar ) ) {
			if( eventObject == null ) {
				eventObject = new Event(name, object, null, null, null);
				args[ 0 ] = eventObject;
			}
			triggerConnections( this, connectionsStar, type || FAKE_TYPE, eventObject, args, results );
		}

		return results;
	}

	retrigger(
		object: unknown, target: Node | null, originalEvent: OriginalEvent,
		types: string, params: unknown[] | null | undefined
	): unknown[] {
		const results: unknown[] = [];
		const typesList = parse( types );
		for( let i = 0, imax = typesList.length; i < imax; ++i ) {
			trigger( this, object, target, typesList[i], originalEvent, params, results );
		}
		return results;
	}
}
