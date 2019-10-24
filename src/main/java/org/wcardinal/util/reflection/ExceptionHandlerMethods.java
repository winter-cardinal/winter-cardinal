/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wcardinal.exception.AmbiguousExceptionHandlerException;

public class ExceptionHandlerMethods<T> extends AbstractMethodWrappers<ExceptionHandlerMethod<T>, T> {
	final List<ExceptionHandlerMethod<T>> handlers = new ArrayList<>();

	@Override
	public boolean add( final ExceptionHandlerMethod<T> target ){
		if( target.type != null ){
			for( final ExceptionHandlerMethod<T> handler: handlers ) {
				if( handler.type != null ) {
					if( target.type.equals( handler.type ) ) {
						throw new AmbiguousExceptionHandlerException( target.method, handler.method, target.type );
					} else if( target.type.isAssignableFrom( handler.type ) ) {
						handler.order += 1;
					} else if( handler.type.isAssignableFrom( target.type ) ) {
						target.order += 1;
					}
				}
			}
			target.order += 1;
		} else {
			for( final ExceptionHandlerMethod<T> handler: handlers ) {
				if( handler.type == null ) {
					throw new AmbiguousExceptionHandlerException( target.method, handler.method );
				}
			}
		}

		handlers.add( target );
		Collections.sort( handlers );
		return super.add( target );
	}

	public ExceptionHandlerMethod<T> find( final Class<? extends Throwable> throwableClass ){
		for( final ExceptionHandlerMethod<T> handler: handlers ) {
			if( handler.type != null ) {
				if( handler.type.isAssignableFrom( throwableClass ) ){
					return handler;
				}
			} else {
				return handler;
			}
		}
		return null;
	}
}
