/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

public class RequestMessageConnect extends RequestMessageArgument {
	public RequestMessageConnect( final MessageArgument argument ) {
		super( "c", argument );
	}
}
