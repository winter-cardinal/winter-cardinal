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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
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
import org.wcardinal.controller.data.SNavigableMap;
import org.wcardinal.controller.data.annotation.Descending;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Uninitialized;
import test.PuppeteerTest;
import test.annotation.Test;

@Controller
public class BasicsNavigableMapController {
	@Autowired
	SNavigableMap<String> field_navigable_map;

	@Autowired
	@Descending
	SNavigableMap<String> field_descending_navigable_map;

	@Autowired
	SNavigableMap<String> sync_ascending_map;

	@Autowired
	@Descending
	SNavigableMap<String> sync_descending_map;

	@OnCreate
	void init(){
		field_navigable_map.clear();
		field_navigable_map.put("a", "a");
		field_navigable_map.put("c", "c");
		field_navigable_map.put("b", "b");
		field_navigable_map.put(null, null);

		field_descending_navigable_map.clear();
		field_descending_navigable_map.put("a", "a");
		field_descending_navigable_map.put("c", "c");
		field_descending_navigable_map.put("b", "b");
		field_descending_navigable_map.put(null, null);
	}

	boolean checkValues( final Map<String, String> map, final String... values ){
		return Objects.deepEquals( map.values().toArray(), values );
	}

	boolean checkValues2( final Map<String, String> map, final String... values ){
		return Objects.deepEquals( map.values().toArray(new String[]{}), values );
	}

	boolean checkValues( final Set<String> set, final String... values ){
		return Objects.deepEquals( set.toArray(), values );
	}

	boolean checkValues2( final Set<String> set, final String... values ){
		return Objects.deepEquals( set.toArray(new String[]{}), values );
	}

	boolean checkValues( final Map.Entry<String, String> entry, final String key, final String value ){
		return Objects.equals(entry.getKey(), key) && Objects.equals(entry.getValue(), value);
	}

	boolean checkEntries( final Set<Map.Entry<String, String>> entrySet, final String... values ){
		final List<String> set = new ArrayList<String>();
		for( final Map.Entry<String, String> entry: entrySet ){
			set.add(entry.getValue());
		}
		return Objects.deepEquals( set.toArray(new String[]{}), values );
	}

	boolean checkValues( final Collection<String> collection, final String... values ){
		return Objects.deepEquals( collection.toArray(new String[]{}), values );
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
	SNavigableMap<String> field_navigable_map2;

	@Autowired
	@Descending
	SNavigableMap<String> field_descending_navigable_map2;

	@Autowired
	SClass<List<String>> test_methods;

	@OnCreate
	void init2(){
		reset();

		final List<String> methods = PuppeteerTest.findTestMethods( this.getClass() );
		Collections.sort( methods );
		test_methods.set( methods );
	}

	boolean reset(){
		field_navigable_map2.clear();
		field_navigable_map2.put("a", "a");
		field_navigable_map2.put("c", "c");
		field_navigable_map2.put("b", "b");
		field_navigable_map2.put(null, null);

		field_descending_navigable_map2.clear();
		field_descending_navigable_map2.put("a", "a");
		field_descending_navigable_map2.put("c", "c");
		field_descending_navigable_map2.put("b", "b");
		field_descending_navigable_map2.put(null, null);

		return true;
	}

	@Test
	@Callable
	boolean a000_reset_for_server_side_tests(){
		return reset();
	}

	@Test
	@Callable
	boolean a001_ascending_map_isNonNull_check(){
		return ! field_navigable_map2.isNonNull();
	}

	@Test
	@Callable
	boolean a001_ascending_map_isLocked_check(){
		return field_navigable_map2.isLocked();
	}

	@Test
	@Callable
	boolean a001_ascending_map_tryLock_check(){
		if( field_navigable_map2.tryLock() ){
			try {
				return true;
			} finally {
				field_navigable_map2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a001_ascending_map_tryLock_timeout_check(){
		if( field_navigable_map2.tryLock( 100, TimeUnit.MILLISECONDS ) ){
			try {
				return true;
			} finally {
				field_navigable_map2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a001_ascending_map_equals_check(){
		return ! field_navigable_map2.equals( null );
	}

	@Test
	@Callable
	boolean a001_ascending_map_firstKey_check(){
		return field_navigable_map2.firstKey().equals( "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_lastKey_check(){
		return field_navigable_map2.lastKey().equals( "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_firstEntry_check(){
		final Map.Entry<String, String> entry = field_navigable_map2.firstEntry();
		return entry.getKey().equals( "a" ) && entry.getValue().equals( "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_lastEntry_check(){
		final Map.Entry<String, String> entry = field_navigable_map2.lastEntry();
		if( entry.toString() == null ) return false;
		if( entry.getKey().equals( "c" ) != true ) return false;
		if( entry.getValue().equals( "c" ) != true ) return false;
		if( "c".equals( entry.setValue( "d" ) ) != true ) return false;
		if( entry.getValue().equals( "d" ) != true ) return false;
		if( checkValues( field_navigable_map2, "a", "b", "d" ) != true ) return false;
		if( "d".equals( entry.setValue( "c" ) ) != true ) return false;
		if( entry.getValue().equals( "c" ) != true ) return false;
		if( checkValues( field_navigable_map2, "a", "b", "c" ) != true ) return false;
		return true;
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_isEmpty_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return !set.isEmpty();
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_size_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.size() == 3;
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_contains_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.contains( "a" ) && !set.contains( "d" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_containsAll_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return !set.containsAll(ImmutableSet.of("x")) && set.containsAll(ImmutableSet.of("a", "b"));
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_containsAll_null_1_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		try {
			set.containsAll(setOf((String)null));
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_containsAll_null_2_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
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
	boolean a001_ascending_map_keySet_equals_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return !set.equals(null);
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_toArray_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return checkValues( set, "a", "b", "c" ) && checkValues2( set, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_toString_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.toString().equals(ImmutableSet.of("a", "b", "c").toString());
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_comparator_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.comparator() != null;
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_first_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.first().equals("a");
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_last_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.last().equals("c");
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_lower_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.lower("b").equals("a");
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_higher_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.higher("b").equals("c");
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_lower_null_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		try {
			set.lower(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_higher_null_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		try {
			set.higher(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_floor_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.floor("b").equals("b");
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_floor_null_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		try {
			set.floor(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_ceiling_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return set.ceiling("b").equals("b");
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_ceiling_null_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		try {
			set.ceiling(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_descendingSet_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		return checkValues( set.descendingSet(), "c", "b", "a" ) && checkValues2( set.descendingSet(), "c", "b", "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_descendingIterator_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		final Iterator<String> iterator = set.descendingIterator();
		final String[] expected = new String[]{ "c", "b", "a" };
		int count = 0;
		while( iterator.hasNext() ){
			if( expected.length <= count || iterator.next().equals(expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_tailSet_inclusive_check(){
		final SortedSet<String> set = field_navigable_map2.navigableKeySet().tailSet("b");
		return checkValues( set, "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_tailSet_exclusive_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet().tailSet("b", false);
		return checkValues( set, "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_tailSet_inclusive_null_check(){
		try {
			field_navigable_map2.navigableKeySet().tailSet(null);
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_tailSet_exclusive_null_check(){
		try {
		field_navigable_map2.navigableKeySet().tailSet(null, false);
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_headSet_inclusive_check(){
		final SortedSet<String> set = field_navigable_map2.navigableKeySet().headSet("b", true);
		return checkValues( set, "a", "b" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_headSet_exclusive_check(){
		final SortedSet<String> set = field_navigable_map2.navigableKeySet().headSet("b");
		return checkValues( set, "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_headSet_inclusive_null_check(){
		try {
			field_navigable_map2.navigableKeySet().headSet(null, true);
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_headSet_exclusive_null_check(){
		try {
			field_navigable_map2.navigableKeySet().headSet(null);
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_subSet_inclusive_check(){
		final SortedSet<String> set = field_navigable_map2.navigableKeySet().subSet("b", "c");
		return checkValues( set, "b" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_subSet_exclusive_check(){
		final SortedSet<String> set = field_navigable_map2.navigableKeySet().subSet("b", false, "c", true);
		return checkValues( set, "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_subSet_inclusive_null_check(){
		try {
			field_navigable_map2.navigableKeySet().subSet(null, "c");
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_subSet_exclusive_null_check(){
		try {
			field_navigable_map2.navigableKeySet().subSet(null, false, "c", true);
			return false;
		} catch ( final NullPointerException e ){
			return true;
		} catch ( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_add_check(){
		final Set<String> set = field_navigable_map2.keySet();
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
	boolean a001_ascending_map_keySet_addAll_check(){
		final Set<String> set = field_navigable_map2.keySet();
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
	boolean a001_ascending_map_keySet_iterator_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		for( final String value: field_navigable_map2.keySet() ){
			if( expected.length <= count || Objects.equals(expected[ count ], value) != true ) return false;
			count += 1;
		}

		final Iterator<String> keys = field_navigable_map2.keySet().iterator();
		keys.remove();
		if( checkValues( field_navigable_map2, "a", "b", "c") != true ) return false;
		final String value = keys.next();
		keys.remove();
		switch( value ) {
		case "a":
			if( checkValues( field_navigable_map2, "b", "c") != true ) return false;
			break;
		case "b":
			if( checkValues( field_navigable_map2, "a", "c") != true ) return false;
			break;
		case "c":
			if( checkValues( field_navigable_map2, "a", "b") != true ) return false;
			break;
		}
		field_navigable_map2.put(value, value);

		return true;
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_pollFirst_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		final boolean result = set.pollFirst().equals("a") &&
			checkValues( field_navigable_map2, "b", "c");
		field_navigable_map2.put("a", "a");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_pollFirst_fail_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		set.clear();
		final boolean result = set.pollFirst() == null;
		field_navigable_map2.putAll(ImmutableMap.of("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_pollLast_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		final boolean result = set.pollLast().equals("c") &&
			checkValues( field_navigable_map2, "a", "b");
		field_navigable_map2.put("c", "c");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_pollLast_fail_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		set.clear();
		final boolean result = set.pollLast() == null;
		field_navigable_map2.putAll(ImmutableMap.of("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_remove_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		final boolean result = set.remove("b") &&
			checkValues( field_navigable_map2, "a", "c");
		field_navigable_map2.put("b", "b");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_remove_fail_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		final boolean result = !set.remove( "d" );
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_remove_null_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		try {
			set.remove( null );
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_removeAll_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		final boolean result = set.removeAll(ImmutableSet.of("b", "d")) &&
			checkValues( field_navigable_map2, "a", "c");
		field_navigable_map2.put("b", "b");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_removeAll_null_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
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
	boolean a001_ascending_map_keySet_removeAll_null_element_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		try {
			set.removeAll(setOf((String)null));
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_retainAll_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
		final boolean result = set.retainAll(ImmutableSet.of("b", "c", "d")) &&
				checkValues( field_navigable_map2, "b", "c");
		field_navigable_map2.put("a", "a");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_keySet_retainAll_null_check(){
		final NavigableSet<String> set = field_navigable_map2.navigableKeySet();
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
	boolean a001_ascending_map_keySet_clear_check(){
		final Set<String> set = field_navigable_map2.keySet();
		set.clear();
		final boolean result = checkValues( set );
		field_navigable_map2.putAll(ImmutableMap.of("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_check(){
		try {
			field_navigable_map2.subMap(null, "b");
			return false;
		} catch( final NullPointerException e ) {

		} catch( final Exception e ) {
			return false;
		}

		try {
			field_navigable_map2.subMap("a", null);
			return false;
		} catch( final NullPointerException e ) {

		} catch( final Exception e ) {
			return false;
		}

		final SortedMap<String, String> map = field_navigable_map2.subMap("a", "b");
		return checkValues( map, "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_inclusive_check(){
		try {
			field_navigable_map2.subMap(null, true, "b", true);
			return false;
		} catch( final NullPointerException e ) {

		} catch( final Exception e ) {
			return false;
		}

		try {
			field_navigable_map2.subMap("a", true, null, true);
			return false;
		} catch( final NullPointerException e ) {

		} catch( final Exception e ) {
			return false;
		}

		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "b", true);
		return checkValues( map, "a", "b" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_comparator_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return map.comparator() != null;
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_floorKey_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return map.floorKey("b").equals("b");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_ceilingKey_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return map.ceilingKey("b").equals("b");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_lastKey_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return map.lastKey().equals("c");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_firstKey_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return map.firstKey().equals("a");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_equals_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return !map.equals(null);
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_lowerKey_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return map.lowerKey("b").equals("a");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_higherKey_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return map.higherKey("b").equals("c");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_lastEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.lastEntry();
		return entry.getKey().equals("c") && entry.getValue().equals("c");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_firstEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.firstEntry();
		return entry.getKey().equals("a") && entry.getValue().equals("a");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_lowerEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.lowerEntry( "b" );
		return entry.getKey().equals("a") && entry.getValue().equals("a");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_higherEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.higherEntry( "b" );
		return entry.getKey().equals("c") && entry.getValue().equals("c");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_floorEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.floorEntry( "b" );
		return entry.getKey().equals("b") && entry.getValue().equals("b");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_ceilingEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.ceilingEntry( "b" );
		return entry.getKey().equals("b") && entry.getValue().equals("b") &&
			entry.equals( null ) == false && entry.equals( (Object)"a" ) == false &&
			entry.equals( entryOf( "b", "b" ) ) && entry.equals( entryOf( "a", "b" ) ) == false &&
			entry.equals( entryOf( "b", "a" ) ) == false;
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_keySet_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.keySet(), "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_tailMap_inclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.tailMap("b"), "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_tailMap_exclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.tailMap("b", false), "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_tailMap_inclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.tailMap("b"), "b", "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_tailMap_exclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.tailMap("b", false), "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_headMap_inclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.headMap("b", true), "a", "b" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_headMap_exclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.headMap("b"), "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_headMap_inclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.headMap("b", true), "c", "b" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_headMap_exclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.headMap("b"), "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_subMap_inclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.subMap("b", true, "c", true), "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_subMap_exclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.subMap("b", "c"), "b" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_subMap_inclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.subMap("c", true, "b", true), "c", "b" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_subMap_exclusive_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.subMap("c", "b"), "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_descendingKeySet_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.descendingKeySet(), "c", "b", "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_descendingKeySet_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.descendingKeySet(), "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_descendingMap_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		return checkValues( map.descendingMap(), "c", "b", "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_descendingMap_subMap_descendingMap_check(){
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().subMap("c", true, "a", true);
		return checkValues( map.descendingMap(), "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_pollLastEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.pollLastEntry();
		final boolean result = entry.getKey().equals("c") && entry.getValue().equals("c") &&
				checkValues( map, "a", "b" ) && checkValues( field_navigable_map2, "a", "b" );
		field_navigable_map2.put("c", "c");
		return result && checkValues( map, "a", "b", "c" ) && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_pollLastEntry_fail_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		map.clear();
		final Map.Entry<String, String> entry = map.pollLastEntry();
		final boolean result = entry == null;
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( map, "a", "b", "c" ) && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_pollFirstEntry_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		final Map.Entry<String, String> entry = map.pollFirstEntry();
		final boolean result = entry.getKey().equals("a") && entry.getValue().equals("a") &&
				checkValues( map, "b", "c" ) && checkValues( field_navigable_map2, "b", "c" );
		field_navigable_map2.put("a", "a");
		return result && checkValues( map, "a", "b", "c" ) && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_pollFirstEntry_fail_check(){
		final NavigableMap<String, String> map = field_navigable_map2.subMap("a", true, "c", true);
		map.clear();
		final Map.Entry<String, String> entry = map.pollFirstEntry();
		final boolean result = entry == null;
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( map, "a", "b", "c" ) && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return checkEntries( set, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_remove_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "d", true).entrySet();
		final boolean result = set.remove(entryOf( "a", "a" )) && !set.remove(entryOf( "d", "d" )) && !set.remove(entryOf( "x", "x" ))
			&&  checkEntries( set, "b", "c" ) && checkValues( field_navigable_map2, "b", "c" );
		field_navigable_map2.put("a", "a");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_remove_fail_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return !set.remove(entryOf( "a", "x" )) && checkEntries( set, "a", "b", "c" ) && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_remove_null_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		try {
			set.remove(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_retainAll_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		final boolean result = set.retainAll(entrySetOf( "a", "a", "b", "x" )) && checkEntries( set, "a" ) && checkValues( field_navigable_map2, "a" );
		field_navigable_map2.put("b", "b");
		field_navigable_map2.put("c", "c");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_retainAll_fail_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
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
	boolean a001_ascending_map_subMap_entrySet_removeAll_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		final boolean result = set.removeAll(entrySetOf( "a", "x", "b", "b" )) && checkEntries( set, "a", "c" ) && checkValues( field_navigable_map2, "a", "c" );
		field_navigable_map2.put("b", "b");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_removeAll_fail_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
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
	boolean a001_ascending_map_subMap_entrySet_add_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "d", true).entrySet();
		final boolean result = set.add(entryOf( "d", "d" )) && set.add(entryOf( "a", "x" )) && checkEntries( set, "x", "b", "c", "d" ) && checkValues( field_navigable_map2, "x", "b", "c", "d" );
		field_navigable_map2.put( "a", "a" );
		field_navigable_map2.remove( "d" );
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_add_fail_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return !set.add(entryOf( "a", "a" )) && checkEntries( set, "a", "b", "c" ) && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_add_null_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
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
	boolean a001_ascending_map_subMap_entrySet_iterator_remove_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		final Iterator<Map.Entry<String, String>> iterator = set.iterator();
		while( iterator.hasNext() ){
			iterator.next();
			iterator.remove();
			break;
		}
		final boolean result = checkEntries( set, "b", "c" ) && checkValues( field_navigable_map2, "b", "c" );
		field_navigable_map2.put("a", "a");
		return result;
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_clear_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		set.clear();
		final boolean result = checkEntries( set ) && checkValues( field_navigable_map2 );
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_contains_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return set.contains(entryOf("a", "a")) && !set.contains(entryOf("a", "x")) && !set.contains(entryOf("z", "z"));
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_contains_null_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		try {
			set.contains(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_addAll_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "e", true).entrySet();
		final boolean result = set.addAll(entrySetOf("d", "d", "e", "e")) && checkEntries( set, "a", "b", "c", "d", "e" ) && checkValues( field_navigable_map2, "a", "b", "c", "d", "e" );
		field_navigable_map2.remove("d");
		field_navigable_map2.remove("e");
		return result;
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_addAll_fail_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return !set.addAll(entrySetOf("a", "a")) && checkEntries( set, "a", "b", "c" ) && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_addAll_null_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		try {
			set.addAll(null);
			return false;
		} catch( final NullPointerException e ){

		} catch( final Exception e ){
			return false;
		}

		try {
			set.addAll( Arrays.asList( (Map.Entry<String, String>) null ) );
			return false;
		} catch( final NullPointerException e ){

		} catch( final Exception e ){
			return false;
		}

		return true;
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_containsAll_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return set.containsAll(entrySetOf("a", "a")) && !set.containsAll(entrySetOf("a", "x"));
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_containsAll_null_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
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
	boolean a001_ascending_map_subMap_entrySet_toArray_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "b", false).entrySet();
		final Object[] array = set.toArray();
		return array.length == 1 && checkValues( (Map.Entry<String, String>)array[0], "a", "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_toArray_element_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "b", false).entrySet();
		@SuppressWarnings("unchecked")
		final Map.Entry<String, String>[] array = set.toArray(new Map.Entry[]{});
		return array.length == 1 && checkValues( array[0], "a", "a" );
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_toString_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return set.toString().equals("[a=a, b=b, c=c]");
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_isEmpty_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return !set.isEmpty();
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_size_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return set.size() == 3;
	}

	@Test
	@Callable
	boolean a001_ascending_map_subMap_entrySet_equals_check(){
		final Set<Map.Entry<String, String>> set = field_navigable_map2.subMap("a", true, "c", true).entrySet();
		return !set.equals(null);
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_map_1_check(){
		field_navigable_map2.replace(mapOf("a","b", "d", "e"));
		boolean result = checkValues( field_navigable_map2, "b", "e" );
		field_navigable_map2.clear();
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_map_2_check(){
		field_navigable_map2.replace(mapOf("a","a", "d", "d"));
		boolean result = checkValues( field_navigable_map2, "a", "d" );
		field_navigable_map2.clear();
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_map_null_check(){
		try {
			field_navigable_map2.replace((Map<String, String>)null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_iterable_keyOf_1_check(){
		field_navigable_map2.replace(setOf("a", "d"), new SKeyOf<String>(){
			@Override
			public String keyOf(final String value) {
				return value;
			}
		});
		boolean result = checkValues( field_navigable_map2, "a", "d" );
		field_navigable_map2.clear();
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_iterable_keyOf_2_check(){
		field_navigable_map2.replace(setOf("a", "d"), new SKeyOf<String>(){
			@Override
			public String keyOf(final String value) {
				return "a";
			}
		});
		boolean result = checkValues( field_navigable_map2, "a" ) || checkValues( field_navigable_map2, "d" );
		field_navigable_map2.clear();
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_iterable_keyOf_3_check(){
		field_navigable_map2.replace(setOf("d"), new SKeyOf<String>(){
			@Override
			public String keyOf(final String value) {
				return "a";
			}
		});
		boolean result = checkValues( field_navigable_map2, "d" );
		field_navigable_map2.clear();
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_iterable_keyOf_null_1_check(){
		try {
			field_navigable_map2.replace(setOf("a", "d"), null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_iterable_keyOf_null_2_check(){
		try {
			field_navigable_map2.replace((Iterable<? extends String>)null, (SKeyOf<String>)null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_iterable_fail_check(){
		field_navigable_map2.replace(setOf("a", "d"));
		final boolean result = field_navigable_map2.isEmpty();
		field_navigable_map2.clear();
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_replace_iterable_null_check(){
		try {
			field_navigable_map2.replace((Iterable<String>)null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_pollFirstEntry_check(){
		final Map.Entry<String, String> entry = field_navigable_map2.pollFirstEntry();
		final boolean result = entry.getKey().equals("a") && entry.getValue().equals("a") && checkValues( field_navigable_map2, "b", "c" );
		field_navigable_map2.put("a", "a");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_pollFirstEntry_fail_check(){
		field_navigable_map2.clear();
		final Map.Entry<String, String> entry = field_navigable_map2.pollFirstEntry();
		final boolean result = entry == null && checkValues( field_navigable_map2 );
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_pollLastEntry_check(){
		final boolean result = checkValues( field_navigable_map2.pollLastEntry(), "c", "c" ) && checkValues( field_navigable_map2, "a", "b" );
		field_navigable_map2.put("c", "c");
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_pollLastEntry_fail_check(){
		field_navigable_map2.clear();
		final Map.Entry<String, String> entry = field_navigable_map2.pollLastEntry();
		final boolean result = entry == null && checkValues( field_navigable_map2 );
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return result && checkValues( field_navigable_map2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_toDirty_check(){
		try {
			field_navigable_map2.toDirty();
			return true;
		} catch ( final Exception e ) {
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_toDirty_element_check(){
		try {
			field_navigable_map2.toDirty( "a" );
			return true;
		} catch ( final Exception e ) {
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_toDirty_element_fail_check(){
		try {
			field_navigable_map2.toDirty( "x" );
			return true;
		} catch ( final Exception e ) {
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_headMap_inclusive_check(){
		return checkValues( field_navigable_map2.headMap("b", true), "a", "b" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_headMap_inclusive_null_check(){
		try {
			field_navigable_map2.headMap(null, true);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_headMap_exclusive_check(){
		return checkValues( field_navigable_map2.headMap("b"), "a" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_headMap_exclusive_null_check(){
		try {
			field_navigable_map2.headMap(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_tailMap_inclusive_check(){
		return checkValues( field_navigable_map2.tailMap("b"), "b", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_tailMap_inclusive_null_check(){
		try {
			field_navigable_map2.tailMap(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_tailMap_exclusive_check(){
		return checkValues( field_navigable_map2.tailMap("b", false), "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_tailMap_exclusive_null_check(){
		try {
			field_navigable_map2.tailMap(null, false);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_descendingKeySet_check(){
		return checkValues( field_navigable_map2.descendingKeySet(), "c", "b", "a" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_ceilingEntry_check(){
		return checkValues( field_navigable_map2.ceilingEntry("b"), "b", "b" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_ceilingEntry_null_check(){
		try {
			field_navigable_map2.ceilingEntry(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_floorEntry_check(){
		return checkValues( field_navigable_map2.floorEntry("b"), "b", "b" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_floorEntry_null_check(){
		try {
			field_navigable_map2.floorEntry(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_higherEntry_check(){
		return checkValues( field_navigable_map2.higherEntry("b"), "c", "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_higherEntry_null_check(){
		try {
			field_navigable_map2.higherEntry(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_lowerEntry_check(){
		return checkValues( field_navigable_map2.lowerEntry("b"), "a", "a" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_lowerEntry_null_check(){
		try {
			field_navigable_map2.lowerEntry(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_ceilingKey_check(){
		return field_navigable_map2.ceilingKey("b").equals( "b" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_ceilingKey_null_check(){
		try {
			field_navigable_map2.ceilingKey(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_floorKey_check(){
		return field_navigable_map2.floorKey("b").equals( "b" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_floorKey_null_check(){
		try {
			field_navigable_map2.floorKey(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_higherKey_check(){
		return field_navigable_map2.higherKey("b").equals( "c" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_higherKey_null_check(){
		try {
			field_navigable_map2.higherKey(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_lowerKey_check(){
		return field_navigable_map2.lowerKey("b").equals( "a" );
	}

	@Test
	@Callable
	boolean a002_ascending_map_lowerKey_null_check(){
		try {
			field_navigable_map2.lowerKey(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_remove_check(){
		final Collection<String> values = field_navigable_map2.values();
		final boolean result = values.remove("a") && !values.remove("x") && checkValues(values, "b", "c") && checkValues( field_navigable_map2, "b", "c" );
		field_navigable_map2.put("a", "a");
		return result;
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_remove_null_check(){
		final Collection<String> values = field_navigable_map2.values();
		try {
			values.remove(null);
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_retainAll_check(){
		final Collection<String> values = field_navigable_map2.values();
		final boolean result = values.retainAll(setOf("a", "x")) && checkValues( values, "a" ) && checkValues( field_navigable_map2, "a" );
		field_navigable_map2.put("b", "b");
		field_navigable_map2.put("c", "c");
		return result;
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_retainAll_null_check(){
		final Collection<String> values = field_navigable_map2.values();
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
	boolean a002_ascending_map_values_removeAll_check(){
		final Collection<String> values = field_navigable_map2.values();
		final boolean result = values.removeAll(setOf("a", "x")) && checkValues( values, "b", "c" ) && checkValues( field_navigable_map2, "b", "c" );
		field_navigable_map2.put("a", "a");
		return result;
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_removeAll_null_check(){
		final Collection<String> values = field_navigable_map2.values();
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
	boolean a002_ascending_map_values_clear_check(){
		final Collection<String> values = field_navigable_map2.values();
		values.clear();
		final boolean result = checkValues( values ) && checkValues( field_navigable_map2 );
		field_navigable_map2.put("a", "a");
		field_navigable_map2.put("b", "b");
		field_navigable_map2.put("c", "c");
		return result;
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_containsAll_check(){
		final Collection<String> values = field_navigable_map2.values();
		return values.containsAll(setOf("a", "b")) && !values.containsAll(setOf("x"));
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_containsAll_null_check(){
		final Collection<String> values = field_navigable_map2.values();
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
	boolean a002_ascending_map_values_iterator_remove_check(){
		final Collection<String> values = field_navigable_map2.values();
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
			result = checkValues( values, "b", "c" ) && checkValues( field_navigable_map2, "b", "c" );
			break;
		case "b":
			result = checkValues( values, "a", "c" ) && checkValues( field_navigable_map2, "a", "c" );
			break;
		case "c":
			result = checkValues( values, "a", "b" ) && checkValues( field_navigable_map2, "a", "b" );
			break;
		}
		field_navigable_map2.put(removed, removed);
		return result;
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_toArray_check(){
		final Collection<String> values = field_navigable_map2.values();
		final Object[] array = values.toArray();
		Arrays.sort(array);
		return Objects.deepEquals(array, new Object[]{ "a", "b", "c" });
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_toArray_element_check(){
		final Collection<String> values = field_navigable_map2.values();
		final String[] array = values.toArray(new String[]{});
		Arrays.sort(array);
		return Objects.deepEquals(array, new String[]{ "a", "b", "c" });
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_equals_check(){
		final Collection<String> values = field_navigable_map2.values();
		return !values.equals(null);
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_contains_check(){
		final Collection<String> values = field_navigable_map2.values();
		return values.contains( "a" ) && !values.contains( "x" ) && !values.contains( null );
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_toString_check(){
		final Collection<String> values = field_navigable_map2.values();
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
	boolean a002_ascending_map_values_isEmpty_check(){
		final Collection<String> values = field_navigable_map2.values();
		return !values.isEmpty();
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_size_check(){
		final Collection<String> values = field_navigable_map2.values();
		return values.size() == 3;
	}

	@Test
	@Callable
	boolean a002_ascending_map_values_add_check(){
		final Collection<String> values = field_navigable_map2.values();
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
	boolean a002_ascending_map_values_addAll_check(){
		final Collection<String> values = field_navigable_map2.values();
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
	boolean a010_ascending_map_size_check(){
		return field_navigable_map2.size() == 3;
	}

	@Test
	@Callable
	boolean a020_descending_map_size_check(){
		return field_descending_navigable_map2.size() == 3;
	}

	@Test
	@Callable
	boolean a030_ascending_map_each_check() {
		final String[] expected = { "a", "b", "c" };
		int count = 0;
		for( final Map.Entry<String, String> entry: field_navigable_map2.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 3;
	}

	@Test
	@Callable
	boolean a040_descending_map_each_check() {
		final String[] expected = { "c", "b", "a" };
		int count = 0;
		for( final Map.Entry<String, String> entry: field_descending_navigable_map2.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 3;
	}

	@Test
	@Callable
	boolean a050_ascending_map_each_reverse_check() {
		final String[] expected = { "c", "b", "a" };
		int count = 0;
		for( final Map.Entry<String, String> entry: field_navigable_map2.descendingMap().entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 3;
	}

	@Test
	@Callable
	boolean a060_descending_map_each_reverse_check(){
		final String[] expected = { "a", "b", "c" };
		int count = 0;
		for( final Map.Entry<String, String> entry: field_descending_navigable_map2.descendingMap().entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 3;
	}

	@Test
	@Callable
	boolean a070_ascending_map_values_check() {
		final String[] expected = {"a", "b", "c"};
		return Objects.deepEquals( field_navigable_map2.values().toArray(), expected );
	}

	@Test
	@Callable
	boolean a080_descending_map_values_check() {
		final String[] expected = { "c", "b", "a" };
		return Objects.deepEquals( field_descending_navigable_map2.values().toArray(), expected );
	}

	@Test
	@Callable
	boolean a090_ascending_map_toString_check() {
		final NavigableMap<String, String> expected = new TreeMap<>();
		expected.put("a", "a");
		expected.put("b", "b");
		expected.put("c", "c");
		return expected.toString().equals( field_navigable_map2.toString() );
	}

	@Test
	@Callable
	boolean a100_descending_map_toString_check() {
		final NavigableMap<String, String> expected = new TreeMap<>(Collections.reverseOrder());
		expected.put("a", "a");
		expected.put("b", "b");
		expected.put("c", "c");
		return expected.toString().equals(field_descending_navigable_map2.toString());
	}

	@Test
	@Callable
	boolean a110_ascending_map_get_check() {
		return (
			Objects.equals( field_navigable_map2.get( "a" ), "a" ) &&
			Objects.equals( field_navigable_map2.get( "b" ), "b" ) &&
			Objects.equals( field_navigable_map2.get( "c" ), "c" )
		);
	}

	@Test
	@Callable
	boolean a120_descending_map_get_check() {
		return (
			Objects.equals( field_descending_navigable_map2.get( "a" ), "a" ) &&
			Objects.equals( field_descending_navigable_map2.get( "b" ), "b" ) &&
			Objects.equals( field_descending_navigable_map2.get( "c" ), "c" )
		);
	}

	@Test
	@Callable
	boolean a130_ascending_map_isEmpty_check() {
		return field_navigable_map2.isEmpty() == false;
	}

	@Test
	@Callable
	boolean a140_descending_map_isEmpty_check() {
		return field_descending_navigable_map2.isEmpty() == false;
	}

	@Test
	@Callable
	boolean a150_ascending_map_containsKey_check() {
		return (
			Objects.equals( field_navigable_map2.containsKey( "a" ), true ) &&
			Objects.equals( field_navigable_map2.containsKey( "b" ), true ) &&
			Objects.equals( field_navigable_map2.containsKey( "c" ), true ) &&
			Objects.equals( field_navigable_map2.containsKey( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a160_descending_map_containsKey_check() {
		return (
			Objects.equals( field_descending_navigable_map2.containsKey( "a" ), true ) &
			Objects.equals( field_descending_navigable_map2.containsKey( "b" ), true ) &
			Objects.equals( field_descending_navigable_map2.containsKey( "c" ), true ) &
			Objects.equals( field_descending_navigable_map2.containsKey( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a170_ascending_map_containsValue_check() {
		return (
			Objects.equals( field_navigable_map2.containsValue( "a" ), true ) &&
			Objects.equals( field_navigable_map2.containsValue( "b" ), true ) &&
			Objects.equals( field_navigable_map2.containsValue( "c" ), true ) &&
			Objects.equals( field_navigable_map2.containsValue( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a180_descending_map_containsValue_check() {
		return (
			Objects.equals( field_descending_navigable_map2.containsValue( "a" ), true ) &&
			Objects.equals( field_descending_navigable_map2.containsValue( "b" ), true ) &&
			Objects.equals( field_descending_navigable_map2.containsValue( "c" ), true ) &&
			Objects.equals( field_descending_navigable_map2.containsValue( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a190_ascending_map_put_check() {
		return (
			Objects.equals( field_navigable_map2.put( "b", "b" ), "b" ) &&
			Objects.equals( field_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean a200_descending_map_put_check() {
		return (
			Objects.equals( field_descending_navigable_map2.put( "b", "b" ), "b" ) &&
			Objects.equals( field_descending_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{ "c", "b", "a" } )
		);
	}

	@Test
	@Callable
	boolean a210_ascending_map_put_null_value_check() {
		return (
			Objects.equals( field_navigable_map2.put( "b", null ), "b" ) &&
			Objects.equals( field_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a", null, "c"} )
		);
	}

	@Test
	@Callable
	boolean a220_descending_map_put_null_value_check() {
		return (
			Objects.equals( field_descending_navigable_map2.put( "b", null ), "b" ) &&
			Objects.equals( field_descending_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"c", null, "a"} )
		);
	}

	@Test
	@Callable
	boolean a230_ascending_map_put_case_sensitivity_check() {
		return (
			Objects.equals( field_navigable_map2.put( "A", "A" ), null ) &&
			Objects.equals( field_navigable_map2.size(), 4 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"A", "a", null, "c"} )
		);
	}

	@Test
	@Callable
	boolean a240_descending_map_put_case_sensitivity_check() {
		return (
			Objects.equals( field_descending_navigable_map2.put( "A", "A" ), null ) &&
			Objects.equals( field_descending_navigable_map2.size(), 4 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{ "c", null, "a", "A" } )
		);
	}

	@Test
	@Callable
	boolean a250_ascending_map_remove_check() {
		return (
			Objects.equals( field_navigable_map2.remove( "A" ), "A" ) &&
			Objects.equals( field_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{ "a", null, "c" } )
		);
	}

	@Test
	@Callable
	boolean a260_descending_map_remove_check() {
		return (
			Objects.equals( field_descending_navigable_map2.remove( "A" ), "A" ) &&
			Objects.equals( field_descending_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{ "c", null, "a" } )
		);
	}

	@Test
	@Callable
	boolean a270_ascending_map_clear_check() {
		field_navigable_map2.clear();
		return (
			Objects.equals( field_navigable_map2.size(), 0 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{} )
		);
	}

	@Test
	@Callable
	boolean a280_descending_map_clear_check() {
		field_descending_navigable_map2.clear();
		return (
			Objects.equals( field_descending_navigable_map2.size(), 0 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{} )
		);
	}

	@Test
	@Callable
	boolean a290_ascending_map_putAll_check() {
		field_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return (
			Objects.equals( field_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean a300_descending_map_putAll_check() {
		field_descending_navigable_map2.putAll(mapOf("a", "a", "b", "b", "c","c"));
		return (
			Objects.equals( field_descending_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"c", "b", "a"} )
		);
	}

	@Test
	@Callable
	boolean a300_ascending_map_clearAndPut_check() {
		return (
			field_navigable_map2.clearAndPut("a", "a") == null &&
			Objects.equals( field_navigable_map2.size(), 1 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a"} )
		);
	}

	@Test
	@Callable
	boolean a300_descending_map_clearAndPut_check() {
		return (
			field_descending_navigable_map2.clearAndPut("a", "a") == null &&
			Objects.equals( field_descending_navigable_map2.size(), 1 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"a"} )
		);
	}

	@Test
	@Callable
	boolean a310_ascending_map_clearAndPutAll_check() {
		field_navigable_map2.clearAndPutAll(mapOf("a", "a", "b", "b", "c", "c"));
		return (
			Objects.equals( field_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean a310_descending_map_clearAndPutAll_check() {
		field_descending_navigable_map2.clearAndPutAll(mapOf("a", "a", "b", "b", "c","c"));
		return (
			Objects.equals( field_descending_navigable_map2.size(), 3 ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"c", "b", "a"} )
		);
	}

	//--------------------------------------------------------------------
	// SERVER-SIDE TAIL MAP
	//--------------------------------------------------------------------
	@Test
	@Callable
	boolean b000_reset_for_tail_map_tests(){
		return reset();
	}

	@Test
	@Callable
	boolean b010_ascending_tail_map_size_check(){
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		return map.size() == 1;
	}

	@Test
	@Callable
	boolean b020_descending_tail_map_size_check(){
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		return map.size() == 1;
	}

	@Test
	@Callable
	boolean b030_ascending_tail_map_each_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		final String[] expected = { "c" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean b040_descending_tail_map_each_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		final String[] expected = { "a" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean b050_ascending_tail_map_each_reverse_check() {
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().tailMap("b", false);
		final String[] expected = { "a" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean b060_descending_tail_map_each_reverse_check(){
		final NavigableMap<String, String> map = field_descending_navigable_map2.descendingMap().tailMap("b", false);
		final String[] expected = { "c" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean b070_ascending_tail_map_values_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		final String[] expected = { "c" };
		return Objects.deepEquals(map.values().toArray(), expected );
	}

	@Test
	@Callable
	boolean b080_descending_tail_map_values_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		final String[] expected = { "a" };
		return Objects.deepEquals( map.values().toArray(), expected );
	}

	@Test
	@Callable
	boolean b090_ascending_tail_map_toString_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		final NavigableMap<String, String> expected = new TreeMap<>();
		expected.put("c", "c");
		return expected.toString().equals( map.toString() );
	}

	@Test
	@Callable
	boolean b100_descending_tail_map_toString_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		final NavigableMap<String, String> expected = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		expected.put("a", "a");
		return expected.toString().equals( map.toString() );
	}

	@Test
	@Callable
	boolean b110_ascending_tail_map_get_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.get( "a" ), null ) &&
			Objects.equals( map.get( "b" ), null ) &&
			Objects.equals( map.get( "c" ), "c" )
		);
	}

	@Test
	@Callable
	boolean b120_descending_tail_map_get_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.get( "a" ), "a" ) &&
			Objects.equals( map.get( "b" ), null ) &&
			Objects.equals( map.get( "c" ), null )
		);
	}

	@Test
	@Callable
	boolean b130_ascending_tail_map_isEmpty_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		return map.isEmpty() == false;
	}

	@Test
	@Callable
	boolean b140_descending_tail_map_isEmpty_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		return map.isEmpty() == false;
	}

	@Test
	@Callable
	boolean b150_ascending_tail_map_containsKey_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.containsKey( "a" ), false ) &&
			Objects.equals( map.containsKey( "b" ), false ) &&
			Objects.equals( map.containsKey( "c" ), true ) &&
			Objects.equals( map.containsKey( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean b160_descending_tail_map_containsKey_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.containsKey( "a" ), true ) &
			Objects.equals( map.containsKey( "b" ), false ) &
			Objects.equals( map.containsKey( "c" ), false ) &
			Objects.equals( map.containsKey( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean b170_ascending_tail_map_containsValue_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.containsValue( "a" ), false ) &&
			Objects.equals( map.containsValue( "b" ), false ) &&
			Objects.equals( map.containsValue( "c" ), true ) &&
			Objects.equals( map.containsValue( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean b180_descending_tail_map_containsValue_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.containsValue( "a" ), true ) &&
			Objects.equals( map.containsValue( "b" ), false ) &&
			Objects.equals( map.containsValue( "c" ), false ) &&
			Objects.equals( map.containsValue( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean b190_ascending_tail_map_put_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);

		Exception exception = null;
		try {
			map.put("b", "b");
		} catch( final Exception e ){
			exception = e;
		}

		final String putResult = map.put( "d", "d" );

		return (
			exception != null &&
			Objects.equals( putResult, null ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"c", "d"} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a", "b", "c", "d"} )
		);
	}

	@Test
	@Callable
	boolean b200_descending_tail_map_put_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);

		Exception exception = null;
		try {
			map.put("b", "b");
		} catch( final Exception e ){
			exception = e;
		}

		return (
			exception != null &&
			Objects.equals( map.put( "Z", "Z" ), null ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{ "a", "Z" } ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"c", "b", "a", "Z"} )
		);
	}

	@Test
	@Callable
	boolean b210_ascending_tail_map_put_null_value_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.put( "d", null ), "d" ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"c", null} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a", "b", "c", null} )
		);
	}

	@Test
	@Callable
	boolean b220_descending_tail_map_put_null_value_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.put( "Z", null ), "Z" ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"a", null} ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"c", "b", "a", null} )
		);
	}

	@Test
	@Callable
	boolean b230_ascending_tail_map_remove_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.remove( "d" ), null ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{ "c" } ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{ "a", "b", "c" } )
		);
	}

	@Test
	@Callable
	boolean b240_descending_tail_map_remove_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		return (
			Objects.equals( map.remove( "Z" ), null ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{ "a" } ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{ "c", "b", "a" } )
		);
	}

	@Test
	@Callable
	boolean b250_ascending_tail_map_clear_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		map.clear();
		return (
			Objects.equals( map.size(), 0 ) &&
			Objects.equals( field_navigable_map2.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{ "a", "b" } )
		);
	}

	@Test
	@Callable
	boolean b260_descending_tail_map_clear_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		map.clear();
		return (
			Objects.equals( map.size(), 0 ) &&
			Objects.equals( field_descending_navigable_map2.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{} ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{ "c", "b" } )
		);
	}

	@Test
	@Callable
	boolean b270_ascending_tail_map_putAll_check() {
		final NavigableMap<String, String> map = field_navigable_map2.tailMap("b", false);
		map.putAll(mapOf("c", "c", "d", "d", "e", "e"));
		return (
			Objects.equals( map.size(), 3 ) &&
			Objects.equals( field_navigable_map2.size(), 5 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"c", "d", "e"} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"a", "b", "c", "d", "e"} )
		);
	}

	@Test
	@Callable
	boolean b280_descending_tail_map_putAll_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.tailMap("b", false);
		map.putAll(mapOf("a", "a", "Z", "Z", "Y", "Y"));
		return (
			Objects.equals( map.size(), 3 ) &&
			Objects.equals( field_descending_navigable_map2.size(), 5 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"a", "Z", "Y"} ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"c", "b", "a", "Z", "Y"} )
		);
	}

	//--------------------------------------------------------------------
	// SERVER-SIDE HEAD MAP
	//--------------------------------------------------------------------
	@Test
	@Callable
	boolean c000_reset_for_head_map_tests(){
		return reset();
	}

	@Test
	@Callable
	boolean c010_ascending_head_map_size_check(){
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		return map.size() == 1;
	}

	@Test
	@Callable
	boolean c020_descending_head_map_size_check(){
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		return map.size() == 1;
	}

	@Test
	@Callable
	boolean c030_ascending_head_map_each_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		final String[] expected = { "a" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean c040_descending_head_map_each_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		final String[] expected = { "c" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean c050_ascending_head_map_each_reverse_check() {
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().headMap("b", false);
		final String[] expected = { "c" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean c060_descending_head_map_each_reverse_check(){
		final NavigableMap<String, String> map = field_descending_navigable_map2.descendingMap().headMap("b", false);
		final String[] expected = { "a" };
		int count = 0;
		for( final Map.Entry<String, String> entry: map.entrySet() ){
			if( Objects.equals(entry.getKey(), expected[ count ]) != true ) return false;
			if( Objects.equals(entry.getValue(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return count == 1;
	}

	@Test
	@Callable
	boolean c070_ascending_head_map_values_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		final String[] expected = { "a" };
		return Objects.deepEquals(map.values().toArray(), expected );
	}

	@Test
	@Callable
	boolean c080_descending_head_map_values_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		final String[] expected = { "c" };
		return Objects.deepEquals( map.values().toArray(), expected );
	}

	@Test
	@Callable
	boolean c090_ascending_head_map_toString_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		final NavigableMap<String, String> expected = new TreeMap<>();
		expected.put("a", "a");
		return expected.toString().equals( map.toString() );
	}

	@Test
	@Callable
	boolean c100_descending_head_map_toString_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		final NavigableMap<String, String> expected = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		expected.put("c", "c");
		return expected.toString().equals( map.toString() );
	}

	@Test
	@Callable
	boolean c110_ascending_head_map_get_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.get( "a" ), "a" ) &&
			Objects.equals( map.get( "b" ), null ) &&
			Objects.equals( map.get( "c" ), null )
		);
	}

	@Test
	@Callable
	boolean c120_descending_head_map_get_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.get( "a" ), null ) &&
			Objects.equals( map.get( "b" ), null ) &&
			Objects.equals( map.get( "c" ), "c" )
		);
	}

	@Test
	@Callable
	boolean c130_ascending_head_map_isEmpty_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		return map.isEmpty() == false;
	}

	@Test
	@Callable
	boolean c140_descending_head_map_isEmpty_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		return map.isEmpty() == false;
	}

	@Test
	@Callable
	boolean c150_ascending_head_map_containsKey_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.containsKey( "a" ), true ) &&
			Objects.equals( map.containsKey( "b" ), false ) &&
			Objects.equals( map.containsKey( "c" ), false ) &&
			Objects.equals( map.containsKey( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean c160_descending_head_map_containsKey_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.containsKey( "a" ), false ) &
			Objects.equals( map.containsKey( "b" ), false ) &
			Objects.equals( map.containsKey( "c" ), true ) &
			Objects.equals( map.containsKey( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean c170_ascending_head_map_containsValue_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.containsValue( "a" ), true ) &&
			Objects.equals( map.containsValue( "b" ), false ) &&
			Objects.equals( map.containsValue( "c" ), false ) &&
			Objects.equals( map.containsValue( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean c180_descending_head_map_containsValue_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.containsValue( "a" ), false ) &&
			Objects.equals( map.containsValue( "b" ), false ) &&
			Objects.equals( map.containsValue( "c" ), true ) &&
			Objects.equals( map.containsValue( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean c190_ascending_head_map_put_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);

		Exception exception = null;
		try {
			map.put("b", "b");
		} catch( final Exception e ){
			exception = e;
		}

		return (
			exception != null &&
			Objects.equals( map.put( "Z", "Z" ), null ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"Z", "a"} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"Z", "a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean c200_descending_head_map_put_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);

		Exception exception = null;
		try {
			map.put("b", "b");
		} catch( final Exception e ){
			exception = e;
		}

		return (
			exception != null &&
			Objects.equals( map.put( "d", "d" ), null ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{ "d", "c" } ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"d", "c", "b", "a"} )
		);
	}

	@Test
	@Callable
	boolean c210_ascending_head_map_put_null_value_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.put( "Z", null ), "Z" ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{null, "a"} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{null, "a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean c220_descending_head_map_put_null_value_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.put( "d", null ), "d" ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{null, "c"} ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{null, "c", "b", "a"} )
		);
	}

	@Test
	@Callable
	boolean c230_ascending_head_map_remove_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.remove( "Z" ), null ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{ "a" } ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{ "a", "b", "c" } )
		);
	}

	@Test
	@Callable
	boolean c240_descending_head_map_remove_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		return (
			Objects.equals( map.remove( "d" ), null ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{ "c" } ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{ "c", "b", "a" } )
		);
	}

	@Test
	@Callable
	boolean c250_ascending_head_map_clear_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		map.clear();
		return (
			Objects.equals( map.size(), 0 ) &&
			Objects.equals( field_navigable_map2.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{ "b", "c" } )
		);
	}

	@Test
	@Callable
	boolean c260_descending_head_map_clear_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		map.clear();
		return (
			Objects.equals( map.size(), 0 ) &&
			Objects.equals( field_descending_navigable_map2.size(), 2 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{} ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{ "b", "a" } )
		);
	}

	@Test
	@Callable
	boolean c270_ascending_head_map_putAll_check() {
		final NavigableMap<String, String> map = field_navigable_map2.headMap("b", false);
		map.putAll(mapOf("a", "a", "Z", "Z", "Y", "Y"));
		return (
			Objects.equals( map.size(), 3 ) &&
			Objects.equals( field_navigable_map2.size(), 5 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"Y", "Z", "a"} ) &&
			Objects.deepEquals( field_navigable_map2.values().toArray(), new String[]{"Y", "Z", "a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean c280_descending_head_map_putAll_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.headMap("b", false);
		map.putAll(mapOf("c", "c", "d", "d", "e", "e"));
		return (
			Objects.equals( map.size(), 3 ) &&
			Objects.equals( field_descending_navigable_map2.size(), 5 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"e", "d", "c"} ) &&
			Objects.deepEquals( field_descending_navigable_map2.values().toArray(), new String[]{"e", "d", "c", "b", "a"} )
		);
	}

	//----------------------------------------------------------------------
	// DESCENDING MAP OF DESCENDING MAP
	//----------------------------------------------------------------------
	@Test
	@Callable
	boolean d000_reset_for_descending_map_tests(){
		return reset();
	}

	@Test
	@Callable
	boolean c010_descending_map_of_descending_map_of_ascending_map_size_check() {
		final NavigableMap<String, String> map = field_navigable_map2.descendingMap().descendingMap();
		return (
			Objects.deepEquals(map.values().toArray(), new String[]{"a", "b", "c"}) &&
			Objects.equals(map.size(), 3)
		);
	}

	@Test
	@Callable
	boolean d020_descending_map_of_descending_map_of_descending_map_size_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.descendingMap().descendingMap();
		map.clear();
		if( Objects.equals(map.isEmpty(), true) != true ) return false;

		map.putAll(mapOf("a", "a", "b", "b", "c", "c"));
		return (
			Objects.equals( map.size(), 3 ) &&
			Objects.deepEquals( map.values().toArray(), new String[]{"c", "b", "a"} )
		);
	}

	//----------------------------------------------------------------------
	// SUB MAP
	//----------------------------------------------------------------------
	@Test
	@Callable
	boolean e000_reset_for_sub_map_tests(){
		return reset();
	}

	@Test
	@Callable
	boolean e010_ascending_map_subMap_2_args_check() {
		final SortedMap<String, String> map = field_navigable_map2.subMap( "a", "b" );
		return (
			Objects.deepEquals( map.values().toArray(), new String[]{ "a" } ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean e020_ascending_map_subMap_4_args_check() {
		final NavigableMap<String, String> map = field_navigable_map2.subMap( "a", false, "b", false );
		return (
			Objects.deepEquals( map.values().toArray(), new String[]{} ) &&
			Objects.equals( map.size(), 0 ) &&
			Objects.equals( map.isEmpty(), true )
		);
	}

	@Test
	@Callable
	boolean e030_descending_map_subMap_2_args_check() {
		final SortedMap<String, String> map = field_descending_navigable_map2.subMap( "c", "b" );
		return (
			Objects.deepEquals( map.values().toArray(), new String[]{"c"} ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean e040_descending_map_subMap_4_args_check() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.subMap( "c", false, "b", false );
		return (
			Objects.deepEquals( map.values().toArray(), new String[]{} ) &&
			Objects.equals( map.size(), 0 ) &&
			Objects.equals( map.isEmpty(), true )
		);
	}

	//----------------------------------------------------------------------
	// SUB MAP OF SUB MAP
	//----------------------------------------------------------------------
	@Test
	@Callable
	boolean f000_sub_map_of_sub_map_of_ascending_map_check_1() {
		final NavigableMap<String, String> map = field_navigable_map2.subMap( "a", true, "c", false ).subMap( "a", true, "c", false );
		return (
			Objects.deepEquals( map.values().toArray(), new String[]{"a", "b"} ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean f010_sub_map_of_sub_map_of_ascending_map_check_2() {
		final NavigableMap<String, String> map = field_navigable_map2.subMap( "a", false, "c", false );

		Exception exception = null;
		try {
			map.subMap( "a", true, "c", false );
		} catch( final Exception e ){
			exception = e;
		}

		return (
			exception != null &&
			Objects.deepEquals( map.values().toArray(), new String[]{"b"} ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean f020_sub_map_of_sub_map_of_ascending_map_check_3() {
		final NavigableMap<String, String> map = field_navigable_map2.subMap( "a", true, "c", false );

		Exception exception = null;
		try {
			map.subMap( "a", true, "c", true );
		} catch( final Exception e ){
			exception = e;
		}

		return (
			exception != null &&
			Objects.deepEquals( map.values().toArray(), new String[]{"a", "b"} ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean f030_sub_map_of_sub_map_of_descending_map_check_1() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.subMap( "c", true, "a", false ).subMap( "c", true, "a", false );
		return (
			Objects.deepEquals( map.values().toArray(), new String[]{"c", "b"} ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean f040_sub_map_of_sub_map_of_descending_map_check_2() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.subMap( "c", false, "a", false );

		Exception exception = null;
		try {
			map.subMap( "c", true, "a", false );
		} catch( final Exception e ){
			exception = e;
		}

		return (
			exception != null &&
			Objects.deepEquals( map.values().toArray(), new String[]{"b"} ) &&
			Objects.equals( map.size(), 1 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean f050_sub_map_of_sub_map_of_descending_map_check_3() {
		final NavigableMap<String, String> map = field_descending_navigable_map2.subMap( "c", true, "a", false );

		Exception exception = null;
		try {
			map.subMap( "c", true, "a", true );
		} catch( final Exception e ){
			exception = e;
		}

		return (
			exception != null &&
			Objects.deepEquals( map.values().toArray(), new String[]{"c", "b"} ) &&
			Objects.equals( map.size(), 2 ) &&
			Objects.equals( map.isEmpty(), false )
		);
	}

	//----------------------------------------------------------------------
	// DATA SYNC
	//----------------------------------------------------------------------
	@Autowired
	SBoolean sync_ascending_map_result;

	@Autowired
	SBoolean sync_descending_map_result;

	@Callable
	boolean start_sync(){
		sync_ascending_map.clear();
		sync_ascending_map.put("a", "a");
		sync_ascending_map.put("c", "c");
		sync_ascending_map.put("b", "b");

		sync_descending_map.clear();
		sync_descending_map.put("a", "a");
		sync_descending_map.put("c", "c");
		sync_descending_map.put("b", "b");

		return true;
	}

	@OnChange("sync_ascending_map")
	void onSyncAscendingMapChange( final SortedMap<String, String> added, final SortedMap<String, String> removed ){
		sync_ascending_map_result.set(
			Objects.deepEquals( added.values().toArray(), new String[]{} ) &&
			Objects.deepEquals( removed.values().toArray(), new String[]{ "a", "b", "c" } )
		);
	}

	@OnChange("sync_descending_map")
	void onSyncDescendingMapChange( final SortedMap<String, String> added, final SortedMap<String, String> removed ){
		sync_descending_map_result.set(
			Objects.deepEquals( added.values().toArray(), new String[]{} ) &&
			Objects.deepEquals( removed.values().toArray(), new String[]{ "c", "b", "a" } )
		);
	}

	//----------------------------------------------------------------------
	// NON-NULL
	//----------------------------------------------------------------------
	@Autowired @NonNull
	SNavigableMap<String> field_navigable_map_nonnull;

	@Callable
	boolean nonnull_navigable_map_putAll_check() {
		field_navigable_map_nonnull.clear();
		field_navigable_map_nonnull.putAll(mapOf("a", "a", "b", "b", "c", "c"));

		return (
			field_navigable_map_nonnull.size() == 3 &&
			checkValues(field_navigable_map_nonnull, "a", "b", "c")
		);
	}

	@Callable
	boolean nonnull_navigable_map_putAll_null_check() {
		try {
			field_navigable_map_nonnull.putAll(mapOf( "d", (String)null ));
			return false;
		} catch( final Exception e ){

		}

		return (
			field_navigable_map_nonnull.size() == 3 &&
			checkValues(field_navigable_map_nonnull, "a", "b", "c")
		);
	}

	@Callable
	boolean nonnull_navigable_map_put_check() {
		return (
			field_navigable_map_nonnull.put( "d", "d" ) == null &&
			field_navigable_map_nonnull.size() == 4 &&
			checkValues( field_navigable_map_nonnull, "a", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_navigable_map_put_null_value_check() {
		try {
			field_navigable_map_nonnull.put( "e", null );
			return false;
		} catch( final Exception e ){

		}

		return (
			field_navigable_map_nonnull.size() == 4 &&
			checkValues( field_navigable_map_nonnull, "a", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_navigable_map_get_check() {
		return (
			field_navigable_map_nonnull.get( "a" ).equals( "a" ) &&
			field_navigable_map_nonnull.get( "b" ).equals( "b" ) &&
			field_navigable_map_nonnull.get( "c" ).equals( "c" ) &&
			field_navigable_map_nonnull.get( "d" ).equals( "d" ) &&
			field_navigable_map_nonnull.get( "e" ) == null
		);
	}

	//----------------------------------------------------------------------
	// UNINITIALIZED
	//----------------------------------------------------------------------
	@Autowired @Uninitialized
	SNavigableMap<String> field_navigable_map_uninitialized;

	@Callable
	void initialize( final String key, final String value ){
		field_navigable_map_uninitialized.put( key, value );
	}

	@Autowired @Uninitialized
	SNavigableMap<String> field_navigable_map_uninitialized2;

	@Callable
	void initialize(){
		field_navigable_map_uninitialized2.initialize();
	}

	@Callable
	boolean initialize_twice_check(){
		try {
			field_navigable_map_uninitialized2.initialize();
		} catch( final Exception e ){
			return false;
		}

		return field_navigable_map_uninitialized2.isInitialized();
	}
}
