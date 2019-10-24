/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connectable } from "../event/connectable";

export type MessageHandler<V> = ( message: V ) => void;

export class MessageEmitter<V> extends Connectable {
	private _handlers: Array<MessageHandler<V>>;

	constructor() {
		super();
		this._handlers = [];
	}

	addHandler( handler: MessageHandler<V> ): this {
		this._handlers.push( handler );
		return this;
	}

	removeHandler( handler: MessageHandler<V> ): this {
		const handlers = this._handlers;
		const index = handlers.indexOf( handler );
		if( 0 < index ) {
			handlers.splice( index, 1 );
		}
		return this;
	}

	triggerHandlers( message: V ): this {
		const handlers = this._handlers;
		for( let i = 0, imax = handlers.length; i < imax; ++i ) {
			handlers[ i ]( message );
		}
		return this;
	}
}
