/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.data;

import java.util.Locale;

import org.wcardinal.util.doc.ThreadSafe;

/**
 * Represents the string data.
 */
public interface SString extends SScalar<String>, Comparable<String> {
	/**
	 * Compares this string and the specified string lexicographically.
	 *
	 * @param string string to be compared
	 * @return a negative integer, zero, or a positive integer as this string is less than, equal to, or greater than the specified string
	 * @see String#compareTo(String)
	 */
	@ThreadSafe
	int compareTo( final String string );

	/**
	 * Compares this string and the specified string lexicographically, ignoring case differences.
	 *
	 * @param string string to be compared
	 * @return a negative integer, zero, or a positive integer as this string is less than, equal to, or greater than the specified string
	 * @see String#compareToIgnoreCase(String)
	 */
	@ThreadSafe
	int compareToIgnoreCase( final String string );

	/**
	 * Concatenates the specified string to the end of this string.
	 * Returns null if this string is null.
	 *
	 * @param string string to be concatenated
	 * @return concatenated string
	 * @see String#concat(String)
	 */
	@ThreadSafe
	String concat( final String string );

	/**
	 * Returns true if this string contains the specified sequence of char values.
	 *
	 * @param sequence sequence to search for
	 * @return true if this string contains the specified sequence of char values
	 * @see String#contains(CharSequence)
	 */
	@ThreadSafe
	boolean	contains( final CharSequence sequence );

	/**
	 * Returns true if this string ends with the specified suffix.
	 *
	 * @param suffix suffix to search for
	 * @return true if this string ends with the specified suffix
	 * @see String#endsWith(String)
	 */
	@ThreadSafe
	boolean	endsWith( final String suffix );

	/**
	 * Returns true if this string is equal to the specified string, ignoring case considerations.
	 *
	 * @param string string to be compared
	 * @return true if this string is equal to the specified string ignoring case considerations
	 * @see String#equalsIgnoreCase(String)
	 */
	@ThreadSafe
	boolean	equalsIgnoreCase( final String string );

	/**
	 * Returns the index within this string of the first occurrence of the specified character.
	 * Returns -1 if no such character occurs in this string.
	 *
	 * @param ch character to search for
	 * @return the index within this string of the first occurence of the specified character
	 * @see String#indexOf(int)
	 */
	@ThreadSafe
	int	indexOf( final int ch );

	/**
	 * Returns the index within this string of the first occurrence of the specified character, starting the search at the specified index.
	 * Returns -1 if no such character occurs in this string.
	 *
	 * @param ch character to search for
	 * @param fromIndex index to start the search from
	 * @return the index within this string of the first occurence of the specified character
	 * @see String#indexOf(int, int)
	 */
	@ThreadSafe
	int	indexOf( final int ch, final int fromIndex );

	/**
	 * Returns the index within this string of the first occurrence of the specified substring.
	 * Returns -1 if no such substring occurs in this string.
	 *
	 * @param substring substring to search for
	 * @return index within this string of the first occurence of the specified substring
	 * @see String#indexOf(String)
	 */
	@ThreadSafe
	int	indexOf( final String substring );

	/**
	 * Returns the index within this string of the first occurrence of the specified substring, starting at the specified index.
	 * Returns -1 if no such substring occurs in this string.
	 *
	 * @param substring substring to searhc for
	 * @param fromIndex where to begin searching the specified substring
	 * @return index within this string of the first occurrence of the specified substring
	 * @see String#indexOf(String, int)
	 */
	@ThreadSafe
	int	indexOf( final String substring, final int fromIndex );

	/**
	 * Returns true if length() is less than or equal to 0.
	 *
	 * @return true if length() is less than or equal to 0
	 * @see String#isEmpty()
	 */
	@ThreadSafe
	boolean	isEmpty();

	/**
	 * Returns the index within this string of the last occurrence of the specified character.
	 * Returns -1 if no such character occurs in this string.
	 *
	 * @param ch character to searhc for
	 * @return index within this string of the last occurrence of the specified character
	 * @see String#lastIndexOf(int)
	 */
	@ThreadSafe
	int	lastIndexOf( final int ch );

	/**
	 * Returns the index within this string of the last occurrence of the specified character, searching backward starting at the specified index.
	 * Returns -1 if no such character occurs in this string.
	 *
	 * @param ch character to search for
	 * @param fromIndex where to begin searching the specified character
	 * @return index within this string of the last occurrence of the specified character
	 * @see String#lastIndexOf(int, int)
	 */
	@ThreadSafe
	int	lastIndexOf( final int ch, final int fromIndex );

	/**
	 * Returns the index within this string of the last occurrence of the specified substring.
	 * Returns -1 if no such substring occurs in this string.
	 *
	 * @param substring substring to search for
	 * @return index within this string of the last occurrence of the specified substring
	 * @see String#lastIndexOf(String)
	 */
	@ThreadSafe
	int	lastIndexOf( final String substring );

	/**
	 * Returns the index within this string of the last occurrence of the specified substring, searching backward starting at the specified index.
	 * Returns -1 if no such substring occurs in this string.
	 *
	 * @param substring substring to search for
	 * @param fromIndex where to begin searching the specified substring
	 * @return index within this string of the last occurrence of the specified substring
	 * @see String#lastIndexOf(String, int)
	 */
	@ThreadSafe
	int	lastIndexOf( final String substring, final int fromIndex );

	/**
	 * Returns the length of this string.
	 * Returns -1 if this string is null.
	 *
	 * @return length of this string
	 * @see String#length()
	 */
	@ThreadSafe
	int	length();

	/**
	 * Returns true if this string matches the specified regular expression.
	 * Returns false if this string is null.
	 *
	 * @param regex regular expression to which this string is to be matched
	 * @return true if this string matches the specified regular expression
	 * @see String#matches(String)
	 */
	@ThreadSafe
	boolean	matches( final String regex );

	/**
	 * Returns true if this string starts with the specified prefix.
	 * Returns false if this string is null.
	 *
	 * @param prefix prefix to search for
	 * @return true if the substring of this string starts with the specified prefix
	 * @see String#startsWith(String)
	 */
	@ThreadSafe
	boolean startsWith( final String prefix );

	/**
	 * Returns true if the substring of this string beginning at the specified index starts with the specified prefix.
	 * Returns false if this string is null.
	 *
	 * @param prefix prefix to search for
	 * @param toffset where to begin looking in this string
	 * @return true if the substring of this string beginning at the specified index starts with the specified prefix
	 * @see String#startsWith(String, int)
	 */
	@ThreadSafe
	boolean startsWith( final String prefix, final int toffset );

	/**
	 * Returns a new string that is a substring of this string.
	 *
	 * @param beginIndex begining index (inclusive)
	 * @return substring of this string
	 * @throws IndexOutOfBoundsException if beginIndex is negative or larger than the length of this String object
	 * @see String#substring(int)
	 */
	@ThreadSafe
	String substring( final int beginIndex );

	/**
	 * Returns a new string that is a substring of this string.
	 *
	 * @param beginIndex beginning index (inclusive)
	 * @param endIndex  ending index (exclusive)
	 * @return substring of this string
	 * @throws IndexOutOfBoundsException if beginIndex is negative or larger than the length of this String object
	 * @see String#substring(int, int)
	 */
	@ThreadSafe
	String substring( final int beginIndex, final int endIndex );

	/**
	 * Converts all of the characters in this String to lower case using the rules of the default locale and returns it.
	 * Returns null if this string is null.
	 *
	 * @return string converted to lowercase
	 * @see String#toLowerCase()
	 */
	@ThreadSafe
	String toLowerCase();

	/**
	 * Converts all of the characters in this String to lower case using the rules of the given Locale and returns it.
	 * Returns null if this string is null.
	 *
	 * @param locale use the case transformation rules for this locale
	 * @return string converted to lowercase
	 * @see String#toLowerCase(Locale)
	 */
	@ThreadSafe
	String toLowerCase( final Locale locale );

	/**
	 * Converts all of the characters in this String to upper case using the rules of the default locale and returns it.
	 * Returns null if this string is null.
	 *
	 * @return string converted to uppercase
	 * @see String#toUpperCase()
	 */
	@ThreadSafe
	String toUpperCase();

	/**
	 * Converts all of the characters in this String to upper case using the rules of the given locale and returns it.
	 * Returns null if this string is null.
	 *
	 * @param locale use the case transformation rules for this locale
	 * @return string converted to uppercase
	 * @see String#toUpperCase(Locale)
	 */
	@ThreadSafe
	String toUpperCase( final Locale locale );

	/**
	 * Returns a copy of this string, with leading and trailing whitespace omitted.
	 * Returns null if this string is null.
	 *
	 * @return a copy of this string without leading and trailing whitespace
	 * @see String#trim()
	 */
	@ThreadSafe
	String trim();

	/**
	 * Returns true if the specified target is equal to this.
	 *
	 * @param target the target to be compared
	 * @return true if the specified target is equal to this
	 */
	@ThreadSafe
	boolean equals( String target );
}
