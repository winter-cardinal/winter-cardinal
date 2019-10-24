/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { PlainObject } from "../../util/lang/plain-object";

export const copyEvent = (
	Object.keys != null ?
	( target: PlainObject, source: PlainObject ): void => {
		const keys = Object.keys(source);
		for( let i = 0, imax = keys.length; i < imax; ++i ) {
			const key = keys[i];
			if( key in target ) {
				continue;
			}
			target[ key ] = source[ key ];
		}
	} :
	( target: PlainObject, source: PlainObject ): void => {
		for( const key in source ) {
			if( key in target ) {
				continue;
			}
			target[ key ] = source[ key ];
		}
	}
);
