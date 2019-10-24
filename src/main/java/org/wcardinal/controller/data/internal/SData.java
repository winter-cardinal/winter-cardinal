/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

public class SData{
	SBase<?> base;
	long authorizedRevision;
	long senderId;
	long sentSenderId;
	long sentRevision;
	long sendingRevision;
	long lastSendTime;

	public SData( final SBase<?> base ){
		this.base = base;
		this.authorizedRevision = -1;
		this.senderId = -1;
		this.sendingRevision = -1;
		this.lastSendTime = -1;
		this.sentSenderId = -1;
		this.sentRevision = -1;
	}

	public SBase<?> get(){
		return base;
	}

	public long getAuthorizedRevision(){
		return authorizedRevision;
	}

	public long getSenderId(){
		return senderId;
	}

	public long getSendingRevision() {
		return sendingRevision;
	}

	public long getLastSendTime() {
		return lastSendTime;
	}

	public long getSentRevision() {
		return sentRevision;
	}

	public long getSentSenderId() {
		return sentSenderId;
	}

	public void resetSentSenderId() {
		sentSenderId = -1;
	}

	public void lock( final long senderId, final long sendingRevision ) {
		this.senderId = senderId;
		this.sendingRevision = sendingRevision;
	}

	public void cancel(){
		senderId = -1;
		sendingRevision = -1;
	}

	public void unlock( final long now ){
		sentSenderId = senderId;
		sentRevision = sendingRevision;
		senderId = -1;
		sendingRevision = -1;
		lastSendTime = now;
	}

	public void resetAuthorizedRevision(){
		authorizedRevision = -1;
		senderId = -1;
		sendingRevision = -1;
		lastSendTime = -1;
		sentSenderId = -1;
		sentRevision = -1;
	}

	public void setAuthorizedRevision( final long authorizedRevision ){
		this.authorizedRevision = Math.max(this.authorizedRevision, authorizedRevision);
	}
}
