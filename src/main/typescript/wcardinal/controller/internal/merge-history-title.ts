/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { isNotEmptyArray } from "../../util/lang/is-empty";

export const mergeHistoryTitle = ( title: string, piece: string | null | undefined, separator: string ): string => {
	if( piece != null && isNotEmptyArray( piece ) ) {
		if( isNotEmptyArray( title ) ) {
			return title + separator + piece;
		} else {
			return piece;
		}
	}
	return title;
};
