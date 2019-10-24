/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { checkConnectingResults } from "./check-connecting-results";
import { ServerBase } from "./server-base";
import { MessageType } from "./server-constructor-message-type";
import { ServerTriggers } from "./server-triggers";

const serverTriggers: ServerTriggers = {
	_trigger: ( name: string ): void => {
		// @ts-ignore
		// The error at the following line is caused by the lack of the WebWorker library.
		// Namely, `compilerOption.libs: [ "webworker" ]` in the tsconfig.json fixes the error.
		// However, that library also introduces the duplicated declaration error of some global
		// variables including the `self`.
		postMessage([
			MessageType.TRIGGER,
			name
		]);
	},

	_triggerAndGet: ( name: string, type: string[] | null, data: unknown[] | null ): unknown[] => {
		// @ts-ignore
		postMessage([
			MessageType.TRIGGER_AND_GET,
			name, type, data
		]);
		return [];
	},

	_triggerHandlers: ( messages: string[] ): void => {
		// @ts-ignore
		postMessage([
			MessageType.MESSAGE,
			messages
		]);
	}
};

export class ServerInWorker {
	static run(): void {
		let serverBase: ServerBase | null = null;
		self.addEventListener( "message", ( e ): void => {
			const data = e.data;
			const type = data[ 0 ];
			switch( type ) {
			case MessageType.SEND:
				if( serverBase != null ) {
					serverBase.send__( data[ 1 ] );
				}
				break;
			case MessageType.CREATE:
				if( serverBase == null ) {
					serverBase = new ServerBase( data[ 1 ], serverTriggers );
				}
				break;
			case MessageType.CONNECT:
				if( serverBase != null ) {
					serverBase.connect_();
				}
				break;
			case MessageType.DISCONNECT:
				if( serverBase != null ) {
					serverBase.disconnect_();
				}
				break;
			case MessageType.SET_PROTOCOL:
				if( serverBase != null ) {
					serverBase.setProtocol_( data[ 1 ] );
					// @ts-ignore
					postMessage([
						MessageType.SET_PROTOCOL,
						serverBase.getProtocol_()
					]);
				}
				break;
			case MessageType.TRIGGER_AND_GET:
				if( serverBase != null ) {
					switch( data[ 1 ] ) {
					case "connecting":
						if( ! checkConnectingResults( data[ 2 ] ) ) {
							serverBase.stopServerCheck_();
						}
						break;
					}
				}
				break;
			}
		});
	}
}
