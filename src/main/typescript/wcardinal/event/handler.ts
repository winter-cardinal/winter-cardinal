/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Connection } from "./connection";

/**
 * A handler for registration events.
 */
export type OnonHandler =
/**
 * @param connection a connection object containing information of registered handler
 * @param isFirst true if this is a first connection of an event identified by [[Connection.type]]
 */
( connection: Connection, isFirst: boolean ) => void;

/**
 * A handler for unregistration events.
 */
export type OnoffHandler =
/**
 * @param connection a connection object containing information of registered handler
 * @param isLast true if this is a last connection of an event identified by [[Connection.type]]
 */
( connection: Connection, isLast: boolean ) => void;
