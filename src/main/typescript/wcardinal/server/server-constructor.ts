/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Settings } from "../controller/internal/settings";
import { Server } from "./server";

export interface ServerConstructor {
	// tslint:disable-next-line: callable-types
	new ( settings: Settings ): Server;
}
