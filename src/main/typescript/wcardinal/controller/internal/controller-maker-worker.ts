/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { ServerConstructorWorker as ServerConstructor } from "../../server/internal/server-constructor-worker";
import { inherit } from "../../util/lang/inherit";
import { register } from "../../util/lang/register";
import { WrapperConstructor } from "../data/internal/wrapper-constructor";
import { RootController } from "../root-controller";
import { RootControllerImpl } from "./root-controller-impl";
import { RootControllerMemory } from "./root-controller-memory";
import { Settings } from "./settings";

export class ControllerMakerWorker {
	constructor( wrapperConstructor: WrapperConstructor<RootController>, name: string, settings: Settings ) {
		inherit( wrapperConstructor, RootControllerImpl );

		const server = new ServerConstructor( settings );
		const instance = new RootControllerMemory( name, server, settings, wrapperConstructor ).getWrapper_();

		register( name, wrapperConstructor );
		register( name, instance, true );

		server.connect();
	}

	static init( instance: RootControllerImpl, memory: RootControllerMemory ) {
		RootControllerImpl.call( instance, memory );
	}
}
