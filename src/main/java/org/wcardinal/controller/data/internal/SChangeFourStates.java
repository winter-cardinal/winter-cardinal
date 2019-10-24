/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

public class SChangeFourStates implements SChange {
	final Object[] parameters;

	public SChangeFourStates(final Object addedItems, final Object removedItems, final Object updatedItems, final Object movedItemsOrderedByNewIndices, final Object movedItemsOrderedByOldIndices ) {
		this.parameters = new Object[]{ addedItems, removedItems, updatedItems, movedItemsOrderedByNewIndices, movedItemsOrderedByOldIndices };
	}

	@Override
	public Object[] toParameters() {
		return parameters;
	}
}
