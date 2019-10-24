/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

/**
 * Represents the movable list data.
 * The class {@code V} can be any class.
 * However, for synchronization, the class {@code V} must be serializable
 * to, and deserializable from JSON by the Jackson.
 *
 * @param <V> value type
 */
public abstract class SMovableList<V> extends SList<V> {
	public static class Move<V> {
		final int newIndex;
		final int oldIndex;
		final V value;

		public Move(){
			this.newIndex = 0;
			this.oldIndex = 0;
			this.value = null;
		}

		public Move( final int newIndex, final int oldIndex, final V value ) {
			this.newIndex = newIndex;
			this.oldIndex = oldIndex;
			this.value = value;
		}

		public int getNewIndex(){
			return newIndex;
		}

		public int getOldIndex(){
			return oldIndex;
		}

		public V getValue(){
			return value;
		}

		@Override
		public String toString() {
			return "{newIndex: " + newIndex + ", oldIndex: " + oldIndex + ", value: " + value + "}";
		}
	}

	/**
	 * Moves the element at the index {@code oldIndex} to the index {@code newIndex}.
	 *
	 * @param oldIndex index of the element to be moved
	 * @param newIndex index where the element is moved to
	 * @throws IndexOutOfBoundsException if the one of the indices is out of range (index {@literal <} 0 || size() {@literal <=} index)
	 */
	public abstract void move( int oldIndex, int newIndex );
}
