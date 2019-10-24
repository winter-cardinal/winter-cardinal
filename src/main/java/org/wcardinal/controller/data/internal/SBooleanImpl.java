/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;

import org.wcardinal.controller.data.SBoolean;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SBooleanImpl extends SScalarImpl<Boolean> implements SBoolean {
	public SBooleanImpl() {
		super( SType.BOOLEAN.ordinal() );
	}

	public SBooleanImpl( final Boolean initialValue ) {
		super( SType.BOOLEAN.ordinal(), initialValue );
	}

	@Override
	Boolean cast(final JsonNode valueElement) throws Exception {
		if( valueElement == null || valueElement.isNull() || valueElement.isBoolean() != true ) return null;
		return valueElement.asBoolean();
	}

	@Override
	Boolean makeNonNullValue() {
		return false;
	}

	@Override
	public boolean equals( final Boolean target ) {
		return Objects.equal( value, target );
	}

	@Override
	public boolean equals( final boolean target ) {
		return Objects.equal( value, target );
	}
}
