/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

// tslint:disable: only-arrow-functions

export const bind = function( targetFunction: Function, thisArg: unknown ): Function {
	if( 2 < arguments.length ) {
		const args = Array.prototype.slice.call( arguments, 2 );
		return function() {
			const newArgs = args.slice( 0 );
			for( let i = 0, imax = arguments.length; i < imax; ++i ) {
				newArgs.push( arguments[ i ] );
			}
			return targetFunction.apply( thisArg, newArgs );
		};
	} else {
		return function() {
			return targetFunction.apply( thisArg, arguments );
		};
	}
};
