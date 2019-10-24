/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

export const escapeHandler = ( character: string ): string => {
	switch( character ) {
	case "&": return "&amp;";
	case "<": return "&lt;";
	case ">": return "&gt;";
	case '"': return "&quot;";
	case "'": return "&#39;";
	default: return "";
	}
};

export const escapeRegEx = /[&<>"']/g;
