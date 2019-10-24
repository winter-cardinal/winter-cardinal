/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { Server } from "../../server/server";
import { RootController } from "../root-controller";
import { ControllerImpl } from "./controller-impl";
import { LocalRootControllerMemory } from "./local-root-controller-memory";
import { RootControllerMemory } from "./root-controller-memory";

export class RootControllerImpl extends ControllerImpl implements RootController {
	constructor( __mem__: RootControllerMemory | LocalRootControllerMemory ) {
		super( __mem__ );
	}

	getServer(): Server {
		return this.__mem__.getServer_();
	}
}
