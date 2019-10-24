/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Server } from "../server/server";
import { Controller } from "./controller";

/**
 * Represents a root controller.
 */
export interface RootController extends Controller {
	/**
	 * Returns a server.
	 *
	 * @returns The server this class has.
	 */
	getServer(): Server;
}
