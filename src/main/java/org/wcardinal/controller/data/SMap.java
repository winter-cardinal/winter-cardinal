/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.Map;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the map data.
 * The key type is the string and must not be null.
 * The class {@code V} can be any class.
 * However, for synchronization, the class {@code V} must be serializable
 * to, and deserializable from JSON by the Jackson.
 *
 * @param <V> value type
 */
public abstract class SMap<V> implements SLockable, SVariable, Map<String, V> {
	public static class Update<V> {
		final V newValue;
		final V oldValue;

		public Update(){
			this.newValue = null;
			this.oldValue = null;
		}

		public Update( final V newValue, final V oldValue ) {
			this.newValue = newValue;
			this.oldValue = oldValue;
		}

		public V getNewValue(){
			return newValue;
		}

		public V getOldValue(){
			return oldValue;
		}

		@Override
		public String toString() {
			return "{newValue: " + newValue + ", oldValue: " + oldValue + "}";
		}
	}

	/**
	 * Constructs a map from the specified iterable and replaces the existing mappings with the specified mappings
	 * if the existing mappings are not equal to the specified mappings.
	 * For testing the equality of mappings, V#equals(Object) is used.
	 * As a key, a field or a method annotated with javax.persistence.Id is used.
	 *
	 * @param values new values
	 * @throws NullPointerException if the specified iterable is null
	 * @throws IllegalStateException if this navigable map is not initialized properly
	 */
	public abstract void replace( Iterable<? extends V> values );

	/**
	 * Constructs a map from the specified iterable and replaces the existing mappings with the specified mappings
	 * if the existing mappings are not equal to the specified mappings.
	 * For testing the equality of mappings, V#equals(Object) is used.
	 * As a key, the return value of {@link SKeyOf#keyOf(Object)} is used.
	 *
	 * @param values new values
	 * @param keyOf function object for retrieving keys from values
	 * @throws NullPointerException if the specified iterable or keyOf is null
	 */
	public abstract void replace( Iterable<? extends V> values, SKeyOf<V> keyOf );

	/**
	 * Replaces the existing mappings with the specified mappings
	 * if the existing mappings are not equal to the specified mappings.
	 * For testing the equality of mappings, V#equals(Object) is used.
	 *
	 * @param mappings new mappings
	 * @throws NullPointerException if the specified map is null
	 */
	public abstract void replace( Map<String, ? extends V> mappings );

	/**
	 * Marks the value of the specified key as updated.
	 *
	 * @param key key of the value to be marked as updated
	 */
	@ThreadSafe
	public abstract void toDirty( String key );

	/**
	 * Marks all the values as updated.
	 */
	@ThreadSafe
	public abstract void toDirty();

	/**
	 * Clear this map and then associates the specified value with the specified key in this map.
	 *
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return null
	 * @throws NullPointerException if this map does not permit null keys or value, and the specified key or value is null
	 */
	public abstract V clearAndPut( String key, V value );

	/**
	 * Clear this map and then copies all of the mappings from the specified map to this map.
	 *
	 * @param mappings mappings to be copied to this map
	 * @throws NullPointerException if the specified map is null, or if this map does not permit null keys or value, and the specified map contains null keys or values
	 */
	public abstract void clearAndPutAll( Map<? extends String, ? extends V> mappings );

	/**
	 * Reassociate the value of the specified key.
	 *
	 * @param key the key which is to be reassociated.
	 * @return the value of the specified key.
	 * @throws NullPointerException if this map does not permit null keys, and the specified key is null
	 */
	public abstract V reput( String key );
}
