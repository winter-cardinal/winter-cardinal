/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize( using=AckInfoSerializer.class )
@JsonDeserialize( using=AckInfoDeserializer.class )
public class AckInfo {
	Set<Long> senderIds;

	public AckInfo( final long senderId ) {
		senderIds = new HashSet<>();
		senderIds.add( senderId );
	}

	public AckInfo( final long[] senderIds ) {
		this.senderIds = new HashSet<>();
		for( final long senderId: senderIds ) {
			this.senderIds.add( senderId );
		}
	}

	public AckInfo( final AckInfo other ) {
		senderIds = new HashSet<>();
		senderIds.addAll( other.senderIds );
	}

	public void merge( final AckInfo other ) {
		senderIds.addAll( other.senderIds );
	}

	public boolean contains( final long senderId ) {
		return senderIds.contains( senderId );
	}
}
