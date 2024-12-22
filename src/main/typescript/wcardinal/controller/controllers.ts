/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../util/lang/plain-object";
import { Controller } from "./controller";
import { ControllersImpl } from "./internal/controllers-impl";

/**
 * An utility class for controllers.
 */
export class Controllers {
	/**
	 * Returns a controller of the specified structure.
	 *
	 *     var controller = Controllers.create({
	 *         foo: "SString"
	 *         component: {
	 *             bar: "@NonNull SClass",
	 *         }
	 *     });
	 *
	 *     console.log( controller.foo.get() ); // prints "null"
	 *
	 *     controller.component.bar.set({ name: "Cardinal" });
	 *     console.log( controller.component.bar.get().name ); // prints "Cardinal"
	 *
	 * @param {Object} structure a controller structure
	 * @returns a created controller
	 * @throws Error if the specified structure contains unsupported type.
	 */
	static create( structure: PlainObject ): Controller {
		return ControllersImpl.create( structure );
	}
}
