/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

public class SetDynamicInfo {
	public final SChangeInfo schange;
	public final RejectionInfo rejection;

	public SetDynamicInfo( final SChangeInfo schange, final RejectionInfo rejection ) {
		this.schange = schange;
		this.rejection = rejection;
	}

	public static SetDynamicInfo create( final SetDynamicDataMap data, final SetDynamicInfoMap info ) {
		final boolean hasSChanges = ( (data != null && data.nameToSChange != null) || (info != null && info.nameToSChange != null) );
		final SChangeInfo schange = ( hasSChanges ? new SChangeInfo( (data != null ? data.nameToSChange : null ), ( info != null ? info.nameToSChange : null ) ) : null );

		final boolean hasRejection = ( (data != null && data.nameToRejection != null) || (info != null && info.nameToRejection != null) );
		final RejectionInfo rejection = ( hasRejection ? new RejectionInfo( (data != null ? data.nameToRejection : null ), (info != null ? info.nameToRejection : null) ) : null );

		if( hasSChanges || hasRejection ) {
			return new SetDynamicInfo( schange, rejection );
		}
		return null;
	}
}
