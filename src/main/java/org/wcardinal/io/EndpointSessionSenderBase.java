/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.io;

import org.wcardinal.io.message.MessageWriterSender;

public abstract class EndpointSessionSenderBase implements MessageWriterSender {
	final EndpointSessionLockResult lockResult;

	EndpointSessionSenderBase( final EndpointSessionLockResult lockResult ) {
		this.lockResult = lockResult;
	}
}
