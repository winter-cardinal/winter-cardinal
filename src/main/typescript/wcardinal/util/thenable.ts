/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { default as PromisePolyfill } from "promise-polyfill";
import { isFunction } from "./lang/is-function";

/** @hidden */
const PromiseClass = (typeof Promise !== "undefined" ? Promise : PromisePolyfill);

Object.defineProperties( PromiseClass.prototype, {
	timeout: {
		value<T>( this: Promise<T>, timeout: number, onTimeoutOrReason: any ) {
			return new Promise<T>(( resolve, reject ) => {
				const timeoutId = self.setTimeout(() => {
					if( isFunction( onTimeoutOrReason ) ) {
						reject( onTimeoutOrReason() );
					} else {
						reject( onTimeoutOrReason || "timeout" );
					}
				}, timeout || 0);

				this.then(( result ) => {
					self.clearTimeout( timeoutId );
					resolve( result );
				}, ( reason ) => {
					self.clearTimeout( timeoutId );
					reject( reason );
				});
			});
		}
	}
});

export { PromiseClass as Thenable };
