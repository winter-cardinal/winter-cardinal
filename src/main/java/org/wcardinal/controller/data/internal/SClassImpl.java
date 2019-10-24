/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data.internal;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.data.SClass;
import org.wcardinal.util.json.Json;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SClassImpl<T> extends SScalarImpl<T> implements SClass<T>, SGeneric {
	final AtomicReference<JavaType> valueType = new AtomicReference<JavaType>( null );
	boolean isEmbeddable = false;

	public SClassImpl() {
		super( SType.CLASS.ordinal() );
	}

	@Override
	T cast(final JsonNode valueNode) throws Exception {
		if( valueNode == null || valueNode.isNull() ) return null;
		return Json.mapper.convertValue(valueNode, valueType.get());
	}

	@Override
	public void setGenericType( final ResolvableType type ){
		valueType.set( Json.mapper.constructType(type.getType()) );
	}

	@Override
	T makeNonNullValue() {
		return null;
	}
}
