/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const stringToBase64 = ( string: string ): string => {
	return btoa( unescape(encodeURIComponent( string )) );
};

export const base64ToString = ( base64: string ): string => {
	return decodeURIComponent( escape(atob( base64 )) );
};
