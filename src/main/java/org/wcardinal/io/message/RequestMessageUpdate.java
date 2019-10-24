/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io.message;

public class RequestMessageUpdate extends RequestMessageArgument {
	public RequestMessageUpdate( final MessageArgument argument ) {
		super( "u", argument );
	}
}
