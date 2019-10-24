/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SKeyOf;
import org.wcardinal.controller.data.SMap;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Uninitialized;
import test.PuppeteerTest;
import test.annotation.Test;

@Controller
public class BasicsMapController {
	@Autowired
	SMap<String> field_map;

	@OnCreate
	void init(){
		field_map.clear();
		field_map.put("a", "a");
		field_map.put("c", "c");
		field_map.put("b", "b");
		field_map.put(null, null);
	}

	class StringComparator implements Comparator<String> {
		@Override
		public int compare(final String a, final String b) {
			if( a != null ) {
				if( b != null ){
					return a.compareTo(b);
				} else {
					return -1;
				}
			} else {
				if( b != null ){
					return +1;
				} else {
					return 0;
				}
			}
		}
	}

	String[] values( final Map<String, String> map ){
		final String[] result = map.values().toArray(new String[]{});
		Arrays.sort(result, new StringComparator());
		return result;
	}

	Object[] values2( final Map<String, String> map ){
		final Object[] result = map.values().toArray();
		Arrays.sort(result);
		return result;
	}

	String[] values( final Set<String> set ){
		final String[] result = set.toArray(new String[]{});
		Arrays.sort(result, new StringComparator());
		return result;
	}

	Object[] values2( final Set<String> set ){
		final Object[] result = set.toArray();
		Arrays.sort(result);
		return result;
	}

	boolean checkValues( final Map<String, String> map, String... values ){
		return Objects.deepEquals( values(map), values);
	}

	boolean checkValues2( final Map<String, String> map, final String... values ){
		return Objects.deepEquals( values(map), values );
	}

	boolean checkValues( final Set<String> set, final String... values ){
		return Objects.deepEquals( values(set), values );
	}

	boolean checkValues2( final Set<String> set, final String... values ){
		return Objects.deepEquals( values2(set), values );
	}

	boolean checkValues( final Map.Entry<String, String> entry, final String key, final String value ){
		return Objects.equals(entry.getKey(), key) && Objects.equals(entry.getValue(), value);
	}

	boolean checkEntries( final Set<Map.Entry<String, String>> entrySet, final String... values ){
		final Map<String, String> map = new HashMap<String, String>();
		for( final Map.Entry<String, String> entry: entrySet ){
			map.put(entry.getKey(), entry.getValue());
		}
		return checkValues( map, values );
	}

	boolean checkValues( final Collection<String> collection, final String... values ){
		final String[] array = collection.toArray(new String[]{});
		Arrays.sort(array);
		return Objects.deepEquals( array, values );
	}

	Map<String, String> mapOf( final String... entries ){
		final Map<String, String> result = new HashMap<>();
		for( int i=0; 2*i+1 < entries.length; ++i ){
			result.put(entries[ 2*i+0 ], entries[ 2*i+1 ]);
		}
		return result;
	}

	Set<String> setOf( final String... entries ){
		final Set<String> result = new HashSet<>();
		for( int i=0; i < entries.length; ++i ){
			result.add(entries[ i ]);
		}
		return result;
	}

	Collection<Map.Entry<String, String>> entrySetOf( final String... entries ){
		final List<Map.Entry<String, String>> result = new ArrayList<>();
		for( int i=0; 2*i+1 < entries.length; ++i ){
			result.add(entryOf(entries[ 2*i+0 ], entries[ 2*i+1 ]));
		}
		return result;
	}

	Map.Entry<String, String> entryOf( final String key, final String value ){
		return new AbstractMap.SimpleImmutableEntry<String, String>(key, value);
	}

	//--------------------------------------------------------------------
	// SERVER-SIDE TESTS
	//--------------------------------------------------------------------
	@Autowired
	SMap<String> field_map2;

	@Autowired
	SClass<List<String>> test_methods;

	@OnCreate
	void init2(){
		field_map2.clear();
		field_map2.put("a", "a");
		field_map2.put("c", "c");
		field_map2.put("b", "b");
		field_map2.put(null, null);

		final List<String> methods = PuppeteerTest.findTestMethods( this.getClass() );
		Collections.sort( methods );
		test_methods.set( methods );
	}

	@Test
	@Callable
	boolean a001_map_isNonNull_check(){
		return ! field_map2.isNonNull();
	}

	@Test
	@Callable
	boolean a001_map_isLocked_check(){
		return field_map2.isLocked();
	}

	@Test
	@Callable
	boolean a001_map_tryLock_check(){
		if( field_map2.tryLock() ){
			try {
				return true;
			} finally {
				field_map2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a001_map_tryLock_timeout_check(){
		if( field_map2.tryLock( 100, TimeUnit.MILLISECONDS ) ){
			try {
				return true;
			} finally {
				field_map2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a001_map_equals_check(){
		return ! field_map2.equals( null );
	}

	@Test
	@Callable
	boolean a002_map_replace_map_1_check(){
		field_map2.replace(mapOf("a","b", "d", "e"));
		boolean result = checkValues( field_map2, "b", "e" );
		field_map2.clear();
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_map_replace_map_2_check(){
		field_map2.replace(mapOf("a","a", "d", "d"));
		boolean result = checkValues( field_map2, "a", "d" );
		field_map2.clear();
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_map_replace_map_null_check(){
		try {
			field_map2.replace((Map<String, String>)null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_replace_iterable_keyOf_1_check(){
		field_map2.replace(setOf("a", "d"), new SKeyOf<String>(){
			@Override
			public String keyOf(final String value) {
				return value;
			}
		});
		boolean result = checkValues( field_map2, "a", "d" );
		field_map2.clear();
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_map_replace_iterable_keyOf_2_check(){
		field_map2.replace(setOf("a", "d"), new SKeyOf<String>(){
			@Override
			public String keyOf(final String value) {
				return "a";
			}
		});
		boolean result = checkValues( field_map2, "a" ) || checkValues( field_map2, "d" );
		field_map2.clear();
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_map_replace_iterable_keyOf_3_check(){
		field_map2.replace(setOf("d"), new SKeyOf<String>(){
			@Override
			public String keyOf(final String value) {
				return "a";
			}
		});
		boolean result = checkValues( field_map2, "d" );
		field_map2.clear();
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_map_replace_iterable_keyOf_null_1_check(){
		try {
			field_map2.replace(setOf("a", "d"), null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_replace_iterable_keyOf_null_2_check(){
		try {
			field_map2.replace((Iterable<? extends String>)null, (SKeyOf<String>)null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_replace_iterable_fail_check(){
		field_map2.replace(setOf("a", "d"));
		final boolean result = field_map2.isEmpty();
		field_map2.clear();
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_map_replace_iterable_null_check(){
		try {
			field_map2.replace((Iterable<String>)null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_toDirty_check(){
		try {
			field_map2.toDirty();
			return true;
		} catch ( final Exception e ) {
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_toDirty_element_check(){
		try {
			field_map2.toDirty( "a" );
			return true;
		} catch ( final Exception e ) {
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_toDirty_element_fail_check(){
		try {
			field_map2.toDirty( "x" );
			return true;
		} catch ( final Exception e ) {
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_isEmpty_check(){
		final Set<String> set = field_map2.keySet();
		return !set.isEmpty();
	}

	@Test
	@Callable
	boolean a001_map_keySet_size_check(){
		final Set<String> set = field_map2.keySet();
		return set.size() == 3;
	}

	@Test
	@Callable
	boolean a001_map_keySet_contains_check(){
		final Set<String> set = field_map2.keySet();
		return set.contains( "a" ) && !set.contains( "d" );
	}

	@Test
	@Callable
	boolean a001_map_keySet_containsAll_check(){
		final Set<String> set = field_map2.keySet();
		return !set.containsAll(ImmutableSet.of("x")) && set.containsAll(ImmutableSet.of("a", "b"));
	}

	@Test
	@Callable
	boolean a001_map_keySet_containsAll_null_1_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.containsAll(setOf((String)null));
			return false;
		} catch( final Exception e ){
			return true;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_containsAll_null_2_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.containsAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_equals_check(){
		final Set<String> set = field_map2.keySet();
		return !set.equals(null);
	}

	@Test
	@Callable
	boolean a001_map_keySet_toArray_check(){
		final Set<String> set = field_map2.keySet();
		return checkValues( set, "a", "b", "c" ) && checkValues2( set, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_keySet_toString_check(){
		final Set<String> set = field_map2.keySet();
		final String result = set.toString();
		return "[a, b, c]".equals( result ) ||
			   "[b, c, a]".equals( result ) ||
			   "[c, a, b]".equals( result ) ||
			   "[a, c, b]".equals( result ) ||
			   "[c, b, a]".equals( result ) ||
			   "[b, a, c]".equals( result );
	}

	@Test
	@Callable
	boolean a001_map_keySet_add_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.add( "x" );
			return false;
		} catch( final UnsupportedOperationException e ) {
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_addAll_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.addAll(ImmutableList.of( "x" ));
			return false;
		} catch( final UnsupportedOperationException e ) {
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_iterator_check(){
		final Set<String> expected = setOf("a", "b", "c");
		int count = 0;
		for( final String value: field_map2.keySet() ){
			if( expected.size() <= count || expected.contains( value ) != true ) return false;
			count += 1;
		}

		final Iterator<String> keys = field_map2.keySet().iterator();
		keys.remove();
		if( checkValues( field_map2, "a", "b", "c") != true ) return false;
		final String value = keys.next();
		keys.remove();
		switch( value ) {
		case "a":
			if( checkValues( field_map2, "b", "c") != true ) return false;
			break;
		case "b":
			if( checkValues( field_map2, "a", "c") != true ) return false;
			break;
		case "c":
			if( checkValues( field_map2, "a", "b") != true ) return false;
			break;
		}
		field_map2.put(value, value);

		return true;
	}

	@Test
	@Callable
	boolean a001_map_keySet_remove_check(){
		final Set<String> set = field_map2.keySet();
		final boolean result = set.remove("b") &&
			checkValues( field_map2, "a", "c");
		field_map2.put("b", "b");
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_keySet_remove_fail_check(){
		final Set<String> set = field_map2.keySet();
		final boolean result = !set.remove( "d" );
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_keySet_remove_null_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.remove( null );
			return false;
		} catch( final Exception e ){
			return true;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_removeAll_check(){
		final Set<String> set = field_map2.keySet();
		final boolean result = set.removeAll(ImmutableSet.of("b", "d")) &&
			checkValues( field_map2, "a", "c");
		field_map2.put("b", "b");
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_keySet_removeAll_null_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.removeAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_removeAll_null_element_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.removeAll(setOf((String)null));
			return false;
		} catch( final Exception e ){
			return true;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_retainAll_check(){
		final Set<String> set = field_map2.keySet();
		final boolean result = set.retainAll(ImmutableSet.of("b", "c", "d")) &&
				checkValues( field_map2, "b", "c");
		field_map2.put("a", "a");
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_keySet_retainAll_null_check(){
		final Set<String> set = field_map2.keySet();
		try {
			set.retainAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_keySet_clear_check(){
		final Set<String> set = field_map2.keySet();
		set.clear();
		final boolean result = checkValues( set );
		field_map2.putAll(ImmutableMap.of("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		for( final Map.Entry<String, String> entry: set ) {
			if( entry.equals( null ) != false ||
				entry.equals( (Object)"a" ) != false ||
				entry.equals( entryOf( entry.getKey(), entry.getValue() ) ) != true ||
				entry.equals( entryOf( "x", entry.getValue() ) ) != false ||
				entry.equals( entryOf( entry.getKey(), "x" ) ) != false ||
				entry.toString() == null
			) {
				return false;
			}

			final String value = entry.getValue();
			final String newValue = "y";
			if( value.equals( entry.setValue( newValue ) ) != true ) return false;
			if( newValue.equals( field_map2.get( value ) ) != true ) return false;
			entry.setValue( value );
		}
		return checkEntries( set, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_remove_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final boolean result = set.remove(entryOf( "a", "a" )) && !set.remove(entryOf( "d", "d" )) && !set.remove(entryOf( "x", "x" ))
			&& checkEntries( set, "b", "c" ) && checkValues( field_map2, "b", "c" );
		field_map2.put("a", "a");
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_remove_fail_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return !set.remove(entryOf( "a", "x" )) && checkEntries( set, "a", "b", "c" ) && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_remove_null_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		try {
			set.remove(null);
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_entrySet_retainAll_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final boolean result = set.retainAll(entrySetOf( "a", "a", "b", "x" )) && checkEntries( set, "a" ) && checkValues( field_map2, "a" );
		field_map2.put("b", "b");
		field_map2.put("c", "c");
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_retainAll_fail_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		try {
			set.retainAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_entrySet_removeAll_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final boolean result = set.removeAll(entrySetOf( "a", "x", "b", "b" )) && checkEntries( set, "a", "c" ) && checkValues( field_map2, "a", "c" );
		field_map2.put("b", "b");
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_removeAll_fail_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		try {
			set.removeAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_entrySet_add_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final boolean result = set.add(entryOf( "d", "d" )) && set.add(entryOf( "a", "x" )) &&
			checkEntries( set, "b", "c", "d", "x" ) && checkValues( field_map2, "b", "c", "d", "x" );
		field_map2.put( "a", "a" );
		field_map2.remove( "d" );
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_add_fail_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return !set.add(entryOf( "a", "a" )) && checkEntries( set, "a", "b", "c" ) && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_add_null_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		try {
			set.add(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_entrySet_iterator_remove_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final Iterator<Map.Entry<String, String>> iterator = set.iterator();
		String removed = null;
		while( iterator.hasNext() ){
			removed = iterator.next().getKey();
			iterator.remove();
			break;
		}

		boolean result = false;
		switch( removed ){
		case "a":
			result = checkEntries( set, "b", "c" ) && checkValues( field_map2, "b", "c" );
			break;
		case "b":
			result = checkEntries( set, "a", "c" ) && checkValues( field_map2, "a", "c" );
			break;
		case "c":
			result = checkEntries( set, "a", "b" ) && checkValues( field_map2, "a", "b" );
			break;
		}
		field_map2.put(removed, removed);
		return result;
	}

	@Test
	@Callable
	boolean a001_map_entrySet_clear_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		set.clear();
		final boolean result = checkEntries( set ) && checkValues( field_map2 );
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_contains_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return set.contains(entryOf("a", "a")) && !set.contains(entryOf("a", "x")) && !set.contains(entryOf("z", "z"));
	}

	@Test
	@Callable
	boolean a001_map_entrySet_contains_null_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		try {
			set.contains(null);
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_entrySet_addAll_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final boolean result = set.addAll(entrySetOf("d", "d", "e", "e")) && checkEntries( set, "a", "b", "c", "d", "e" ) && checkValues( field_map2, "a", "b", "c", "d", "e" );
		field_map2.remove("d");
		field_map2.remove("e");
		return result;
	}

	@Test
	@Callable
	boolean a001_map_entrySet_addAll_fail_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return !set.addAll(entrySetOf("a", "a")) && checkEntries( set, "a", "b", "c" ) && checkValues( field_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_addAll_null_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		try {
			set.addAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_map_entrySet_containsAll_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return set.containsAll(entrySetOf("a", "a")) && !set.containsAll(entrySetOf("a", "x"));
	}

	@Test
	@Callable
	boolean a001_map_entrySet_containsAll_null_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		try {
			set.containsAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	@Callable
	boolean a001_map_entrySet_toArray_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final Object[] array = set.toArray();
		final Map<String, String> map = new HashMap<>();
		for( final Object entryObject: array ){
			final Map.Entry<String, String> entry = (Map.Entry<String, String>) entryObject;
			map.put(entry.getKey(), entry.getValue());
		}
		return array.length == 3 && checkValues( map, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_toArray_element_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		@SuppressWarnings("unchecked")
		final Map.Entry<String, String>[] array = set.toArray(new Map.Entry[]{});
		final Map<String, String> map = new HashMap<>();
		for( final Map.Entry<String, String> entry: array ){
			map.put(entry.getKey(), entry.getValue());
		}
		return array.length == 3 && checkValues( map, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_map_entrySet_toString_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		final String result = set.toString();
		return "[a=a, b=b, c=c]".equals(result) ||
			   "[a=a, c=c, b=b]".equals(result) ||
			   "[b=b, a=a, c=c]".equals(result) ||
			   "[b=b, c=c, a=a]".equals(result) ||
			   "[c=c, a=a, b=b]".equals(result) ||
			   "[c=c, b=b, a=a]".equals(result);
	}

	@Test
	@Callable
	boolean a001_map_entrySet_isEmpty_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return !set.isEmpty();
	}

	@Test
	@Callable
	boolean a001_map_entrySet_size_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return set.size() == 3;
	}

	@Test
	@Callable
	boolean a001_map_entrySet_equals_check(){
		final Set<Map.Entry<String, String>> set = field_map2.entrySet();
		return !set.equals(null);
	}

	@Test
	@Callable
	boolean a002_map_values_remove_check(){
		final Collection<String> values = field_map2.values();
		final boolean result = values.remove("a") && !values.remove("x") && checkValues(values, "b", "c") && checkValues( field_map2, "b", "c" );
		field_map2.put("a", "a");
		return result;
	}

	@Test
	@Callable
	boolean a002_map_values_remove_null_check(){
		final Collection<String> values = field_map2.values();
		try {
			values.remove(null);
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_values_retainAll_check(){
		final Collection<String> values = field_map2.values();
		final boolean result = values.retainAll(setOf("a", "x")) && checkValues( values, "a" ) && checkValues( field_map2, "a" );
		field_map2.put("b", "b");
		field_map2.put("c", "c");
		return result;
	}

	@Test
	@Callable
	boolean a002_map_values_retainAll_null_check(){
		final Collection<String> values = field_map2.values();
		try {
			values.retainAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_values_removeAll_check(){
		final Collection<String> values = field_map2.values();
		final boolean result = values.removeAll(setOf("a", "x")) && checkValues( values, "b", "c" ) && checkValues( field_map2, "b", "c" );
		field_map2.put("a", "a");
		return result;
	}

	@Test
	@Callable
	boolean a002_map_values_removeAll_null_check(){
		final Collection<String> values = field_map2.values();
		try {
			values.removeAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_values_clear_check(){
		final Collection<String> values = field_map2.values();
		values.clear();
		final boolean result = checkValues( values ) && checkValues( field_map2 );
		field_map2.put("a", "a");
		field_map2.put("b", "b");
		field_map2.put("c", "c");
		return result;
	}

	@Test
	@Callable
	boolean a002_map_values_containsAll_check(){
		final Collection<String> values = field_map2.values();
		return values.containsAll(setOf("a", "b")) && !values.containsAll(setOf("x"));
	}

	@Test
	@Callable
	boolean a002_map_values_containsAll_null_check(){
		final Collection<String> values = field_map2.values();
		try {
			values.containsAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_values_iterator_remove_check(){
		final Collection<String> values = field_map2.values();
		final Iterator<String> iterator = values.iterator();
		String removed = null;
		while( iterator.hasNext() ){
			removed = iterator.next();
			iterator.remove();
			break;
		}
		boolean result = false;
		switch( removed ){
		case "a":
			result = checkValues( values, "b", "c" ) && checkValues( field_map2, "b", "c" );
			break;
		case "b":
			result = checkValues( values, "a", "c" ) && checkValues( field_map2, "a", "c" );
			break;
		case "c":
			result = checkValues( values, "a", "b" ) && checkValues( field_map2, "a", "b" );
			break;
		}
		field_map2.put(removed, removed);
		return result;
	}

	@Test
	@Callable
	boolean a002_map_values_toArray_check(){
		final Collection<String> values = field_map2.values();
		final Object[] array = values.toArray();
		Arrays.sort(array);
		return Objects.deepEquals(array, new Object[]{ "a", "b", "c" });
	}

	@Test
	@Callable
	boolean a002_map_values_toArray_element_check(){
		final Collection<String> values = field_map2.values();
		final String[] array = values.toArray(new String[]{});
		Arrays.sort(array);
		return Objects.deepEquals(array, new String[]{ "a", "b", "c" });
	}

	@Test
	@Callable
	boolean a002_map_values_equals_check(){
		final Collection<String> values = field_map2.values();
		return !values.equals(null);
	}

	@Test
	@Callable
	boolean a002_map_values_contains_check(){
		final Collection<String> values = field_map2.values();
		return values.contains( "a" ) && !values.contains( "x" ) && !values.contains( null );
	}

	@Test
	@Callable
	boolean a002_map_values_toString_check(){
		final Collection<String> values = field_map2.values();
		final String result = values.toString();
		return "[a, b, c]".equals(result) ||
			   "[a, c, b]".equals(result) ||
			   "[b, a, c]".equals(result) ||
			   "[b, c, a]".equals(result) ||
			   "[c, a, b]".equals(result) ||
			   "[c, b, a]".equals(result);
	}

	@Test
	@Callable
	boolean a002_map_values_isEmpty_check(){
		final Collection<String> values = field_map2.values();
		return !values.isEmpty();
	}

	@Test
	@Callable
	boolean a002_map_values_size_check(){
		final Collection<String> values = field_map2.values();
		return values.size() == 3;
	}

	@Test
	@Callable
	boolean a002_map_values_add_check(){
		final Collection<String> values = field_map2.values();
		try {
			values.add(null);
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_map_values_addAll_check(){
		final Collection<String> values = field_map2.values();
		try {
			values.addAll(null);
			return false;
		} catch( final UnsupportedOperationException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a010_map_size_check(){
		return field_map2.size() == 3;
	}

	@Test
	@Callable
	boolean a030_map_each_check() {
		final String[] expected = { "a", "b", "c" };
		int count = 0;
		for( final Map.Entry<String, String> entry: field_map2.entrySet() ){
			if( Arrays.binarySearch(expected, entry.getKey()) < 0 ) return false;
			if( Arrays.binarySearch(expected, entry.getValue()) < 0 ) return false;
			count += 1;
		}
		return count == 3;
	}

	@Test
	@Callable
	boolean a070_map_values_check() {
		return checkValues(field_map2, "a", "b", "c");
	}

	@Test
	@Callable
	boolean a090_map_toString_check() {
		final String result = field_map2.toString();
		return "{a=a, b=b, c=c}".equals( result ) ||
			   "{b=b, c=c, a=a}".equals( result ) ||
			   "{c=c, a=a, b=b}".equals( result ) ||
			   "{a=a, c=c, b=b}".equals( result ) ||
			   "{c=c, b=b, a=a}".equals( result ) ||
			   "{b=b, a=a, c=c}".equals( result );
	}

	@Test
	@Callable
	boolean a110_map_get_check() {
		return (
			Objects.equals( field_map2.get( "a" ), "a" ) &&
			Objects.equals( field_map2.get( "b" ), "b" ) &&
			Objects.equals( field_map2.get( "c" ), "c" )
		);
	}

	@Test
	@Callable
	boolean a130_map_isEmpty_check() {
		return field_map2.isEmpty() == false;
	}

	@Test
	@Callable
	boolean a150_map_containsKey_check() {
		return (
			Objects.equals( field_map2.containsKey( "a" ), true ) &&
			Objects.equals( field_map2.containsKey( "b" ), true ) &&
			Objects.equals( field_map2.containsKey( "c" ), true ) &&
			Objects.equals( field_map2.containsKey( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a170_map_containsValue_check() {
		return (
			Objects.equals( field_map2.containsValue( "a" ), true ) &&
			Objects.equals( field_map2.containsValue( "b" ), true ) &&
			Objects.equals( field_map2.containsValue( "c" ), true ) &&
			Objects.equals( field_map2.containsValue( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a190_map_put_check() {
		return (
			Objects.equals( field_map2.put( "b", "b" ), "b" ) &&
			Objects.equals( field_map2.size(), 3 ) &&
			checkValues(field_map2, "a", "b", "c")
		);
	}

	@Test
	@Callable
	boolean a210_map_put_null_value_check() {
		return (
			Objects.equals( field_map2.put( "b", null ), "b" ) &&
			Objects.equals( field_map2.size(), 3 ) &&
			checkValues(field_map2, "a", "c", null )
		);
	}

	@Test
	@Callable
	boolean a230_map_put_case_sensitivity_check() {
		return (
			Objects.equals( field_map2.put( "A", "A" ), null ) &&
			Objects.equals( field_map2.size(), 4 ) &&
			checkValues(field_map2, "A", "a", "c", null )
		);
	}

	@Test
	@Callable
	boolean a250_map_remove_check() {
		return (
			Objects.equals( field_map2.remove( "A" ), "A" ) &&
			Objects.equals( field_map2.size(), 3 ) &&
			checkValues(field_map2, "a", "c", null)
		);
	}

	@Test
	@Callable
	boolean a251_map_remove_null_check() {
		return (
			Objects.equals( field_map2.remove( "x" ), null ) &&
			Objects.equals( field_map2.size(), 3 ) &&
			checkValues(field_map2, "a", "c", null)
		);
	}

	@Test
	@Callable
	boolean a270_map_clear_check() {
		field_map2.clear();
		return (
			Objects.equals( field_map2.size(), 0 ) &&
			checkValues(field_map2)
		);
	}

	@Test
	@Callable
	boolean a290_map_putAll_check() {
		field_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return (
			Objects.equals( field_map2.size(), 3 ) &&
			checkValues(field_map2, "a", "b", "c")
		);
	}

	@Test
	@Callable
	boolean a300_map_clearAndPut_check() {
		return (
			field_map2.clearAndPut( "a", "a" ) == null &&
			Objects.equals( field_map2.size(), 1 ) &&
			checkValues(field_map2, "a")
		);
	}

	@Test
	@Callable
	boolean a310_map_clearAndPutAll_check() {
		field_map2.clearAndPutAll(mapOf("a", "a", "b", "b", "c", "c"));
		return (
			Objects.equals( field_map2.size(), 3 ) &&
			checkValues(field_map2, "a", "b", "c")
		);
	}

	//----------------------------------------------------------------------
	// DATA SYNC
	//----------------------------------------------------------------------
	@Autowired
	SMap<String> sync_map;

	@Autowired
	SBoolean sync_map_result;

	@Callable
	boolean start_sync(){
		sync_map.clear();
		sync_map.put("a", "a");
		sync_map.put("c", "c");
		sync_map.put("b", "b");

		return true;
	}

	@OnChange("sync_map")
	void onSyncMapChange( final Map<String, String> added, final Map<String, String> removed ){
		sync_map_result.set(
			checkValues(added) &&
			checkValues(removed, "a", "b", "c")
		);
	}

	//----------------------------------------------------------------------
	// NON-NULL
	//----------------------------------------------------------------------
	@Autowired @NonNull
	SMap<String> field_map_nonnull;

	@Callable
	boolean nonnull_map_putAll_check() {
		field_map_nonnull.clear();
		field_map_nonnull.putAll(mapOf("a", "a", "b", "b", "c", "c"));

		return (
			field_map_nonnull.size() == 3 &&
			checkValues(field_map_nonnull, "a", "b", "c")
		);
	}

	@Callable
	boolean nonnull_map_putAll_null_check() {
		try {
			field_map_nonnull.putAll(mapOf( "d", (String)null ));
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_map_nonnull.size() == 3 &&
				checkValues(field_map_nonnull, "a", "b", "c")
			);
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_map_put_check() {
		return (
			field_map_nonnull.put( "d", "d" ) == null &&
			field_map_nonnull.size() == 4 &&
			checkValues( field_map_nonnull, "a", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_map_put_null_value_check() {
		try {
			field_map_nonnull.put( "e", null );
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_map_nonnull.size() == 4 &&
				checkValues( field_map_nonnull, "a", "b", "c", "d" )
			);
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_map_get_check() {
		return (
			field_map_nonnull.get( "a" ).equals( "a" ) &&
			field_map_nonnull.get( "b" ).equals( "b" ) &&
			field_map_nonnull.get( "c" ).equals( "c" ) &&
			field_map_nonnull.get( "d" ).equals( "d" ) &&
			field_map_nonnull.get( "e" ) == null
		);
	}

	//----------------------------------------------------------------------
	// UNINITIALIZED
	//----------------------------------------------------------------------
	@Autowired @Uninitialized
	SMap<String> field_map_uninitialized;

	@Callable
	void initialize( final String key, final String value ){
		field_map_uninitialized.put( key, value );
	}

	@Autowired @Uninitialized
	SMap<String> field_map_uninitialized2;

	@Callable
	void initialize(){
		field_map_uninitialized2.initialize();
	}

	@Callable
	boolean initialize_twice_check(){
		try {
			field_map_uninitialized2.initialize();
		} catch( final Exception e ){
			return false;
		}

		return field_map_uninitialized2.isInitialized();
	}
}
