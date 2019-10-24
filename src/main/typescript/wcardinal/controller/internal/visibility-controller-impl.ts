/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { VisibilityController } from "../visibility-controller";
import { ControllerImpl } from "./controller-impl";
import { VisibilityControllerMemory } from "./visibility-controller-memory";

export class VisibilityControllerImpl
	extends ControllerImpl<VisibilityController, VisibilityControllerMemory<VisibilityController>>
	implements VisibilityController {

	getDisplayName(): string | null {
		return this.__mem__.getDisplayName_();
	}

	setDisplayName( displayName: string ): this {
		this.__mem__.setDisplayName_( displayName );
		return this;
	}
}
