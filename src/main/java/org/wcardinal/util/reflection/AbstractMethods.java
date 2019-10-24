/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.util.reflection;

import java.lang.reflect.Method;
import java.util.EnumSet;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.databind.JsonNode;

public class AbstractMethods<T extends AbstractMethodWrapper<?>> {
	protected boolean hasLockRequired = false;
	protected boolean hasLockNotRequired = false;
	protected boolean hasLockUnspecified = false;
	protected boolean hasTrackable = false;

	public boolean add( final T wrapper ){
		// Lock
		if( wrapper.lockRequirement == LockRequirement.REQUIRED ){
			hasLockRequired = true;
		} else if( wrapper.lockRequirement == LockRequirement.NOT_REQUIRED ){
			hasLockNotRequired = true;
		} else {
			hasLockUnspecified = true;
		}

		// Trackable
		if( wrapper.isTrackable ){
			hasTrackable = true;
		}

		return true;
	}

	public boolean contains(){
		return hasLockRequired || hasLockNotRequired || hasLockUnspecified;
	}

	public boolean containsLockUnspecified(){
		return hasLockUnspecified;
	}

	public boolean containsLockRequired(){
		return hasLockRequired;
	}

	public boolean containsLockNotRequired(){
		return hasLockNotRequired;
	}

	public boolean contains( final EnumSet<LockRequirement> lockRequirements ) {
		return (
			( lockRequirements.contains( LockRequirement.UNSPECIFIED ) && this.hasLockUnspecified ) ||
			( lockRequirements.contains( LockRequirement.REQUIRED ) && this.hasLockRequired ) ||
			( lockRequirements.contains( LockRequirement.NOT_REQUIRED ) && this.hasLockNotRequired )
		);
	}

	public boolean containsTrackable(){
		return hasTrackable;
	}

	public static int[] getParameterOrder( final Method method, final ResolvableType implementationType, final ResolvableType[] callerTypes ){
		final int calleeSize = method.getParameterTypes().length;
		final int callerSize = callerTypes.length;

		final int[] result = new int[ calleeSize ];
		final ResolvableType[] calleeTypes = new ResolvableType[ calleeSize ];
		for( int i=0; i<calleeSize; ++i ){
			result[i] = -1;
			calleeTypes[i] = ResolvableType.forMethodParameter(new MethodParameter(method, i), implementationType);
		}

		for( int j=0; j<callerSize; ++j ){
			for( int i=0; i<calleeSize; ++i ){
				if( 0 <= result[i] ) continue;
				if( calleeTypes[i].isAssignableFrom(callerTypes[j]) != true ) continue;
				result[i] = j;
				break;
			}
		}

		for( int i=0; i<calleeSize; ++i ){
			if( result[i] < 0 ) return null;
		}

		return result;
	}

	public static Object[] toParameters( final JsonNode arguments, final int offset ){
		final Object[] parameters = new Object[ arguments.size() - offset ];
		for( int i=0; i<arguments.size() - offset; ++i ){
			final JsonNode argument = arguments.get(i + offset);
			if( argument.isValueNode() ){
				if( argument.isBoolean() ){
					parameters[i] = argument.asBoolean();
				} else if( argument.isTextual() ){
					parameters[i] = argument.asText();
				} else if( argument.isNumber() ){
					parameters[i] = argument.numberValue();
				} else {
					parameters[i] = null;
				}
			} else if( argument.isArray() ){
				parameters[i] = argument;
			} else {
				parameters[i] = argument;
			}
		}
		return parameters;
	}
}
