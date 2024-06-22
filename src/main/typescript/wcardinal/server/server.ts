/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../controller/internal/settings";
import { Event } from "../event/event";
import { IoSettings } from "../io/io-settings";
import { MessageEmitter } from "../io/message-emitter";
import { AjaxErrorHandler, AjaxSuccessHandler } from "../util/ajax";
import { PlainObject } from "../util/lang/plain-object";

/**
 * Represents a server, managing a connection to and messages from a remote server.
 */
export interface Server extends MessageEmitter<string[]> {
	/**
	 * Connects to a remote server.
	 *
	 *     connect();
	 *
	 * @returns this
	 */
	connect(): this;

	/**
	 * Disconnects from a remote server.
	 *
	 * @returns this
	 */
	disconnect(): this;

	/**
	 * Checks if a connection to a remote server is established.
	 *
	 * @returns true if the connection is established
	 */
	isConnected(): boolean;

	/**
	 * Sets a communication protocol for a remote server.
	 * The following string is allowed:
	 *
	 * - polling: Long-polling based communication protocol
	 * - websocket: WebSocket communication protocol
	 *
	 * Must be called before wcardinal.server.Server#connect.
	 *
	 *     server.setProtocol("websocket");
	 *
	 * @param protocol The communication protocol name.
	 * @returns this
	 */
	setProtocol( protocol: string ): this;

	/**
	 * Returns the current communication protocol.
	 *
	 *     server.getProtocol();
	 *
	 * @param allowedProtocols an array of allowed protocols
	 * @returns The name of the current communication protocol.
	 */
	getProtocol( allowedProtocols?: PlainObject<IoSettings> ): string | null;

	/**
	 * Returns settings.
	 *
	 * @since 1.1.0
	 */
	getSettings(): Settings;

	/**
	 * Sends a message to a remote server.
	 *
	 * @param type a message type
	 * @param content a message content
	 * @returns this
	 */
	send( type: string, content: any ): this;

	ajax(
		mode: string, data: any, timeout: number, onSuccess: AjaxSuccessHandler,
		onError: AjaxErrorHandler, context?: any
	): void;

	/**
	 * Triggered when a connection to a remote server is established.
	 *
	 * @event connected
	 * @param event Event object.
	 * @internal
	 */
	onconnected?( event: Event ): void;

	/**
	 *  Triggered when a connection to a remote server is lost.
	 *
	 * @event disconnected
	 * @param event Event object.
	 * @internal
	 */
	ondisconnected?( event: Event ): void;

	/**
	 * Triggered when an attempt to connect to a remote server is finished unsuccessfully.
	 * Exits from the loop to connect to a remote server by explicitly returning false in event handlers.
	 *
	 * @event connecting
	 * @param event Event object
	 * @param reason Reason
	 * @param status Status code
	 * @internal
	 */
	onconnecting?( event: Event, reason: string, status: number ): void;
}
