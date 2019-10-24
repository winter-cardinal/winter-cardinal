/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const inherit = ( Object.create != null ?
	( target: any, superClass: Function ): void => {
		target.superClass = superClass;
		target.prototype = Object.create(superClass.prototype, {
			constructor: {
				value: target,
				enumerable: false,
				writable: true,
				configurable: true
			}
		});
	} :
	( target: any, superClass: Function ): void => {
		target.superClass = superClass;
		const TempClass: any = () => { /* DO NOTHING */ };
		TempClass.prototype = superClass.prototype;
		target.prototype = new TempClass();
		target.prototype.constructor = target;
	}
);
