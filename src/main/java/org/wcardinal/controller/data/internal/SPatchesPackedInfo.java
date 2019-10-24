/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat( shape=JsonFormat.Shape.ARRAY )
public class SPatchesPackedInfo {
	public long startRevision;
	public long revisionRange;
	public int type;

	public SPatchesPackedInfo( final long startRevision, final long endRevision, final int type ) {
		this.startRevision = startRevision;
		this.revisionRange = endRevision - startRevision;
		this.type = type;
	}
}
