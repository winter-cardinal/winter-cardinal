/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { UnsupportedOperationException } from "../../exception/unsupported-operation-exception";

export const checkSupported = ( target: { isReadOnly_(): boolean } ) => {
	if( target.isReadOnly_() ) {
		throw UnsupportedOperationException.create();
	}
};
