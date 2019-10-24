/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const checkConnectingResults = ( results: unknown[] | null | undefined ): boolean => {
	if( results != null ) {
		for( let i = 0, imax = results.length; i < imax; ++i ) {
			const result = results[ i ];
			if( result === false ) {
				return false;
			}
		}
	}
	return true;
};
