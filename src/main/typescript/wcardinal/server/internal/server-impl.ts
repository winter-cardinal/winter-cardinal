/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../../controller/internal/settings";
import { IoSettings } from "../../io/io-settings";
import { MessageEmitter } from "../../io/message-emitter";
import { AjaxErrorHandler, AjaxSuccessHandler } from "../../util/ajax";
import { PlainObject } from "../../util/lang/plain-object";
import { Server } from "../server";
import { isBFCached } from "./is-bfcached";
import { ServerBase } from "./server-base";

export class ServerImpl extends MessageEmitter<string[]> implements Server {
	private readonly _serverBase: ServerBase;

	constructor( settings: Settings ) {
		super();
		settings.bfcached = isBFCached( settings );
		this._serverBase = new ServerBase( settings, {
			_trigger: ( name: string ): void => {
				this.triggerDirect( name, null, null, null );
			},

			_triggerAndGet: ( name: string, type: string[] | null, data: unknown[] | null ): unknown[] => {
				return this.triggerDirect( name, type, data, [] );
			},

			_triggerHandlers: ( messages: string[] ): void => {
				this.triggerHandlers( messages );
			}
		});
	}

	getIo() {
		return this._serverBase.getIo_();
	}

	getSettings() {
		return this._serverBase.getSettings_();
	}

	connect(): this {
		this._serverBase.connect_();
		return this;
	}

	disconnect(): this {
		this._serverBase.disconnect_();
		return this;
	}

	isConnected(): boolean {
		return this._serverBase.isConnected_();
	}

	setProtocol( protocol: string ): this {
		this._serverBase.setProtocol_( protocol );
		return this;
	}

	getProtocol( allowedProtocols?: PlainObject<IoSettings> ): string | null {
		return this._serverBase.getProtocol_( allowedProtocols );
	}

	send( type: string, content: unknown ): this {
		this._serverBase.send_( type, content );
		return this;
	}

	ajax(
		mode: string, data: BodyInit | null, timeout: number, onSuccess: AjaxSuccessHandler,
		onError: AjaxErrorHandler, context?: unknown
	): void {
		this._serverBase.ajax_( mode, data, timeout, onSuccess, onError, context );
	}
}
