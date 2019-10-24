/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SBoolean;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SList;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Uninitialized;
import test.PuppeteerTest;
import test.annotation.Test;

@Controller
public class BasicsListController {
	@Autowired
	SList<String> field_list;

	@OnCreate
	void init(){
		field_list.add( "a" );
		field_list.add( "b" );
		field_list.add( "c" );
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

	String[] values( final Map<Integer, String> map ){
		final String[] result = map.values().toArray(new String[]{});
		Arrays.sort(result, new StringComparator());
		return result;
	}

	boolean checkValues( final List<String> list, final String... values ){
		return Objects.deepEquals( list.toArray(new String[]{}), values );
	}

	boolean checkValues( final Map<Integer, String> map, final String... values ){
		return Objects.deepEquals( values(map), values );
	}

	List<String> listOf( final String... entries ){
		final List<String> result = new ArrayList<>();
		for( int i=0; i < entries.length; ++i ){
			result.add(entries[ i ]);
		}
		return result;
	}

	//--------------------------------------------------------------------
	// SERVER-SIDE TESTS
	//--------------------------------------------------------------------
	@Autowired
	SList<String> field_list2;

	@Autowired
	SClass<List<String>> test_methods;

	@OnCreate
	void init2(){
		field_list2.add( "a" );
		field_list2.add( "b" );
		field_list2.add( "c" );

		final List<String> methods = PuppeteerTest.findTestMethods( this.getClass() );
		Collections.sort( methods );
		test_methods.set( methods );
	}

	@Test
	@Callable
	boolean a01_list_replace_1_check(){
		field_list2.replace(listOf("a", "d", "e", "f", "g"));
		final boolean result = checkValues( field_list2, "a", "d", "e", "f", "g" );
		field_list2.clear();
		field_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_list_replace_2_check(){
		field_list2.replace(listOf((String)null));
		final boolean result = checkValues( field_list2, (String)null );
		field_list2.clear();
		field_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_list_replace_3_check(){
		try {
			field_list2.replace(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_retainAll_1_check(){
		final boolean result = field_list2.retainAll(listOf("a", "d", "e")) &&
			checkValues( field_list2, "a" );
		field_list2.clear();
		field_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_list_retainAll_2_check(){
		final boolean result = !field_list2.retainAll(listOf("a", "b", "c", "d")) &&
			checkValues( field_list2, "a", "b", "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_retainAll_3_check(){
		return !field_list2.retainAll(listOf("a", "b", "c", null)) && checkValues( field_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_list_remove_1_check(){
		final boolean result = field_list2.remove("b") && checkValues( field_list2, "a", "c" );
		field_list2.add(1, "b");
		return result;
	}

	@Test
	@Callable
	boolean a01_list_remove_2_check(){
		return !field_list2.remove("d") && checkValues( field_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_list_remove_3_check(){
		return !field_list2.remove(null) && checkValues( field_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_list_remove_4_check(){
		final boolean result = Objects.equals( field_list2.remove(1), "b" ) && checkValues( field_list2, "a", "c" );
		field_list2.add(1, "b");
		return result;
	}

	@Test
	@Callable
	boolean a01_list_remove_5_check(){
		try {
			field_list2.remove(4);
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_addAll_1_check(){
		final boolean result = field_list2.addAll(listOf("d", "e")) && checkValues( field_list2, "a", "b", "c", "d", "e" );
		field_list2.remove( 4 );
		field_list2.remove( 3 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_addAll_2_check(){
		final boolean result = field_list2.addAll(listOf((String)null)) && checkValues( field_list2, "a", "b", "c", null );
		field_list2.remove( 3 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_addAll_3_check(){
		try {
			field_list2.addAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_addAll_4_check(){
		final boolean result = field_list2.addAll(1, listOf("d", "e")) && checkValues( field_list2, "a", "d", "e", "b", "c" );
		field_list2.remove( 2 );
		field_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_addAll_5_check(){
		final boolean result = field_list2.addAll(1, listOf((String)null)) && checkValues( field_list2, "a", null, "b", "c" );
		field_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_addAll_6_check(){
		try {
			field_list2.addAll(1, null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_removeAll_1_check(){
		final boolean result = field_list2.removeAll(listOf("a", "b")) && checkValues( field_list2, "c" );
		field_list2.add(0, "b");
		field_list2.add(0, "a");
		return result;
	}

	@Test
	@Callable
	boolean a01_list_removeAll_2_check(){
		return !field_list2.removeAll(listOf((String)null)) && checkValues( field_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_list_removeAll_3_check(){
		try {
			field_list2.removeAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_toDirty_check(){
		try {
			field_list2.toDirty();
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_toDirty_element_check(){
		try {
			field_list2.toDirty( 1 );
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_toDirty_fail_check(){
		try {
			field_list2.toDirty( -1 );
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_check(){
		return checkValues( field_list2.subList(1, 2), "b" );
	}

	@Test
	@Callable
	boolean a01_list_subList_toString_check(){
		return field_list2.subList(1, 2).toString().equals(listOf("b").toString());
	}

	@Test
	@Callable
	boolean a01_list_subList_toArray_check(){
		return Objects.deepEquals( field_list2.subList(1, 2).toArray(), new Object[]{ "b" });
	}

	@Test
	@Callable
	boolean a01_list_subList_size_check(){
		return field_list2.subList(1, 2).size() == 1;
	}

	@Test
	@Callable
	boolean a01_list_subList_isEmpty_check(){
		return !field_list2.subList(1, 2).isEmpty();
	}

	@Test
	@Callable
	boolean a01_list_subList_equals_check(){
		return field_list2.subList(1, 2).equals( listOf("b") );
	}

	@Test
	@Callable
	boolean a01_list_subList_indexOf_check(){
		return field_list2.subList(1, 2).indexOf( "b" ) == 0;
	}

	@Test
	@Callable
	boolean a01_list_subList_lastIndexOf_check(){
		return field_list2.subList(1, 2).lastIndexOf( "b" ) == 0;
	}

	@Test
	@Callable
	boolean a01_list_subList_containsAll_1_check(){
		return field_list2.subList(1, 2).containsAll(listOf("b")) && !field_list2.subList(1, 2).containsAll(listOf((String)null));
	}

	@Test
	@Callable
	boolean a01_list_subList_containsAll_2_check(){
		try {
			field_list2.subList(1, 2).containsAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_contains_check(){
		return field_list2.subList(1, 2).contains("b") && !field_list2.subList(1, 2).contains(null);
	}

	@Test
	@Callable
	boolean a01_list_subList_listIterator_1_check(){
		final String[] expected = new String[]{ "b" };
		int count = 0;
		final ListIterator<String> iterator = field_list2.subList(1, 2).listIterator();
		while( iterator.hasNext() ){
			if( expected.length <= count || Objects.equals(iterator.next(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_list_subList_listIterator_2_check(){
		final String[] expected = new String[]{ "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_list2.subList(0, 3).listIterator( 1 );
		while( iterator.hasNext() ){
			if( expected.length <= count || Objects.equals(iterator.next(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_list_subList_iterator_check(){
		final String[] expected = new String[]{ "b" };
		int count = 0;
		final Iterator<String> iterator = field_list2.subList(1, 2).iterator();
		while( iterator.hasNext() ){
			if( expected.length <= count || Objects.equals(iterator.next(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_list_subList_retainAll_1_check(){
		return !field_list2.subList(0, 3).retainAll( listOf( "a", "b", "c", "d" ) );
	}

	@Test
	@Callable
	boolean a01_list_subList_retainAll_2_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = list.retainAll( listOf( "a", "b" ) ) && checkValues( list, "a", "b" );
		field_list2.add( "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_retainAll_3_check(){
		final List<String> list = field_list2.subList(0, 3);
		try {
			list.retainAll( null );
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_removeAll_1_check(){
		return !field_list2.subList(0, 3).removeAll( listOf( "x", "y" ) );
	}

	@Test
	@Callable
	boolean a01_list_subList_removeAll_2_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = list.removeAll( listOf( "a", "b" ) ) && checkValues( list, "c" );
		field_list2.add( 0, "b" );
		field_list2.add( 0, "a" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_removeAll_3_check(){
		final List<String> list = field_list2.subList(0, 3);
		try {
			list.removeAll( null );
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_clear_check(){
		final List<String> list = field_list2.subList(0, 3);
		list.clear();
		final boolean result = checkValues( list ) && checkValues( field_list2 );
		field_list2.addAll(listOf( "a", "b", "c" ));
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_remove_1_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = list.remove( "b" ) && !list.remove( null ) && checkValues( list, "a", "c" ) && checkValues( field_list2, "a", "c" );
		field_list2.add( 1, "b" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_remove_2_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = list.remove( 1 ).equals("b") && checkValues( list, "a", "c" ) && checkValues( field_list2, "a", "c" );
		field_list2.add( 1, "b" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_remove_3_check(){
		final List<String> list = field_list2.subList(0, 3);
		try {
			list.remove( 4 );
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_get_1_check(){
		final List<String> list = field_list2.subList(0, 3);
		return list.get( 1 ).equals("b");
	}

	@Test
	@Callable
	boolean a01_list_subList_get_2_check(){
		final List<String> list = field_list2.subList(0, 3);
		try {
			list.get( 4 );
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_addAll_1_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = list.addAll( listOf( "d", "e" ) ) && checkValues( list, "a", "b", "c", "d", "e" ) && checkValues( field_list2, "a", "b", "c", "d", "e" );
		field_list2.remove( 4 );
		field_list2.remove( 3 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_addAll_2_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = !list.addAll( listOf() ) && checkValues( list, "a", "b", "c" ) && checkValues( field_list2, "a", "b", "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_addAll_3_check(){
		final List<String> list = field_list2.subList(0, 3);
		try {
			list.addAll( null );
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_addAll_4_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = list.addAll( 1, listOf( "d", "e" ) ) && checkValues( list, "a", "d", "e", "b", "c" ) && checkValues( field_list2, "a", "d", "e", "b", "c" );
		field_list2.remove( 2 );
		field_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_addAll_5_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = !list.addAll( 1, listOf() ) && checkValues( list, "a", "b", "c" ) && checkValues( field_list2, "a", "b", "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_addAll_6_check(){
		final List<String> list = field_list2.subList(0, 3);
		try {
			list.addAll( 1, null );
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_subList_add_1_check(){
		final List<String> list = field_list2.subList(0, 2);
		final boolean result = list.add( "d" ) && checkValues( list, "a", "b", "d" ) && checkValues( field_list2, "a", "b", "d", "c" );
		field_list2.remove( 2 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_add_2_check(){
		final List<String> list = field_list2.subList(0, 2);
		list.add( 1, "d" );
		final boolean result = checkValues( list, "a", "d", "b" ) && checkValues( field_list2, "a", "d", "b", "c" );
		field_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_set_check(){
		final List<String> list = field_list2.subList(0, 3);
		final boolean result = list.set( 1, "d" ).equals("b") && checkValues( list, "a", "d", "c" ) && checkValues( field_list2, "a", "d", "c" );
		field_list2.set( 1, "b" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_subList_subList_check(){
		final List<String> list = field_list2.subList(0, 3).subList(1, 2);
		return checkValues( list, "b" ) && checkValues( field_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_list_iterator_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		for( String value: field_list2 ) {
			if( expected.length <= count || Objects.equals(value, expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_list_listIterator_1_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_list2.listIterator();
		while( iterator.hasNext() ) {
			final String value = iterator.next();
			if( expected.length <= count || Objects.equals(value, expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_list_listIterator_2_check(){
		final String[] expected = new String[]{ "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_list2.listIterator( 1 );
		while( iterator.hasNext() ) {
			final String value = iterator.next();
			if( expected.length <= count || Objects.equals(value, expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_list_listIterator_3_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_list2.listIterator();

		while( iterator.hasNext() ) {
			if( iterator.hasPrevious() ) {
				if( iterator.previousIndex() != count - 1 ) return false;
				final String previous = iterator.previous();
				if( Objects.equals(previous, expected[ count-1 ]) != true ) return false;
				iterator.next();
			}

			if( iterator.nextIndex() != count ) return false;
			final String value = iterator.next();
			if( expected.length <= count || Objects.equals(value, expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_list_listIterator_4_check(){
		final ListIterator<String> iterator = field_list2.listIterator();
		while( iterator.hasNext() ) {
			iterator.next();
			iterator.remove();
		}
		final boolean result = checkValues( field_list2 );
		field_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_list_listIterator_5_check(){
		final ListIterator<String> iterator = field_list2.listIterator();
		while( iterator.hasNext() ) {
			iterator.next();
			iterator.add("x");
			break;
		}
		final boolean result = checkValues( field_list2, "a", "x", "b", "c" );
		field_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_listIterator_6_check(){
		final ListIterator<String> iterator = field_list2.listIterator();
		while( iterator.hasNext() ) {
			iterator.next();
			iterator.set( "x" );
			break;
		}
		final boolean result = checkValues( field_list2, "x", "b", "c" );
		field_list2.set( 0, "a" );
		return result;
	}

	@Test
	@Callable
	boolean a01_list_containsAll_1_check(){
		return field_list2.containsAll(listOf("a", "b", "c"));
	}

	@Test
	@Callable
	boolean a01_list_containsAll_2_check(){
		return !field_list2.containsAll(listOf("a", "b", "c", "d"));
	}

	@Test
	@Callable
	boolean a01_list_containsAll_3_check(){
		return !field_list2.containsAll(listOf((String)null));
	}

	@Test
	@Callable
	boolean a01_list_containsAll_4_check(){
		try {
			field_list2.containsAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_list_indexOf_check(){
		return field_list2.indexOf( "a" ) == 0 && field_list2.indexOf( "d" ) < 0 && field_list2.indexOf( null ) < 0;
	}

	@Test
	@Callable
	boolean a01_list_lastIndexOf_check(){
		return field_list2.lastIndexOf( "a" ) == 0 && field_list2.lastIndexOf( "d" ) < 0 && field_list2.lastIndexOf( null ) < 0;
	}

	@Test
	@Callable
	boolean a01_list_equals_check(){
		return !field_list2.equals( null ) && field_list2.equals( listOf( "a", "b", "c" ) );
	}

	@Test
	@Callable
	boolean a01_list_toArray_check(){
		return Objects.deepEquals( field_list2.toArray(), new Object[]{ "a", "b", "c" } );
	}

	@Test
	@Callable
	boolean a01_list_isNonNull_check(){
		return !field_list2.isNonNull();
	}

	@Test
	@Callable
	boolean a01_list_tryLock_check(){
		if( field_list2.tryLock() ){
			try {
				return true;
			} finally {
				field_list2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a01_list_tryLock_timeout_check(){
		if( field_list2.tryLock( 100, TimeUnit.MILLISECONDS ) ){
			try {
				return true;
			} finally {
				field_list2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a01_list_isLocked_check(){
		return field_list2.isLocked();
	}

	@Test
	@Callable
	boolean a010_list_size_check(){
		return field_list2.size() == 3;
	}

	@Test
	@Callable
	boolean a030_list_each_check() {
		final String[] expected = { "a", "b", "c" };
		for( int i=0; i<field_list2.size(); ++i ){
			if( Objects.equals(expected[ i ], field_list2.get(i)) != true ) return false;
		}
		return field_list2.size() == 3;
	}

	@Test
	@Callable
	boolean a070_list_values_check() {
		final String[] expected = {"a", "b", "c"};
		return Objects.deepEquals( field_list2.toArray(new String[]{}), expected );
	}

	@Test
	@Callable
	boolean a090_list_toString_check() {
		final String result = field_list2.toString();
		return "[a, b, c]".equals( result );
	}

	@Test
	@Callable
	boolean a110_list_get_check() {
		return (
			Objects.equals( field_list2.get( 0 ), "a" ) &&
			Objects.equals( field_list2.get( 1 ), "b" ) &&
			Objects.equals( field_list2.get( 2 ), "c" )
		);
	}

	@Test
	@Callable
	boolean a130_list_isEmpty_check() {
		return field_list2.isEmpty() == false;
	}

	@Test
	@Callable
	boolean a150_list_contains_check() {
		return (
			Objects.equals( field_list2.contains( "a" ), true ) &&
			Objects.equals( field_list2.contains( "b" ), true ) &&
			Objects.equals( field_list2.contains( "c" ), true ) &&
			Objects.equals( field_list2.contains( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a190_list_add_check() {
		return (
			Objects.equals( field_list2.add( "d" ), true ) &&
			Objects.equals( field_list2.size(), 4 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{"a", "b", "c", "d"} )
		);
	}

	@Test
	@Callable
	boolean a210_list_add_null_value_check() {
		return (
			Objects.equals( field_list2.add( null ), true ) &&
			Objects.equals( field_list2.size(), 5 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{"a", "b", "c", "d", null} )
		);
	}

	@Test
	@Callable
	boolean a230_list_add_middle_check() {
		field_list2.add( 1, "e" );
		return (
			Objects.equals( field_list2.size(), 6 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{"a", "e", "b", "c", "d", null} )
		);
	}

	@Test
	@Callable
	boolean a240_list_set_check() {
		return (
			Objects.equals( field_list2.set( 1, "b" ), "e" ) &&
			Objects.equals( field_list2.size(), 6 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{"a", "b", "b", "c", "d", null} )
		);
	}

	@Test
	@Callable
	boolean a250_list_remove_check() {
		return (
			Objects.equals( field_list2.remove( 1 ), "b" ) &&
			Objects.equals( field_list2.size(), 5 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{ "a", "b", "c", "d", null } )
		);
	}

	@Test
	@Callable
	boolean a270_list_clear_check() {
		field_list2.clear();
		return (
			Objects.equals( field_list2.size(), 0 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{} )
		);
	}

	@Test
	@Callable
	boolean a290_list_addAll_check() {
		field_list2.addAll(listOf("a", "b", "c"));
		return (
			Objects.equals( field_list2.size(), 3 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{"a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean a300_list_addAll_check() {
		return (
			field_list2.clearAndAdd( "a" ) &&
			Objects.equals( field_list2.size(), 1 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{"a"} )
		);
	}

	@Test
	@Callable
	boolean a310_list_addAll_check() {
		return (
			field_list2.clearAndAddAll(listOf("a", "b", "c")) &&
			Objects.equals( field_list2.size(), 3 ) &&
			Objects.deepEquals( field_list2.toArray(new String[]{}), new String[]{"a", "b", "c"} )
		);
	}

	//----------------------------------------------------------------------
	// DATA SYNC
	//----------------------------------------------------------------------
	@Autowired
	SList<String> sync_list;

	@Autowired
	SBoolean sync_list_result;

	@Callable
	boolean start_sync(){
		sync_list.clear();
		sync_list.add("a");
		sync_list.add("b");
		sync_list.add("c");

		return true;
	}

	@OnChange("sync_list")
	void onSyncListChange( final SortedMap<Integer, String> added, final SortedMap<Integer, String> removed ){
		sync_list_result.set(
			Objects.deepEquals( values(added), new String[]{} ) &&
			Objects.deepEquals( values(removed), new String[]{ "a", "b", "c" } )
		);
	}

	//----------------------------------------------------------------------
	// NON-NULL
	//----------------------------------------------------------------------
	@Autowired @NonNull
	SList<String> field_list_nonnull;

	@Callable
	boolean nonnull_list_addAll_check() {
		field_list_nonnull.clear();
		field_list_nonnull.addAll(listOf("a", "b", "c"));

		return (
			field_list_nonnull.size() == 3 &&
			checkValues( field_list_nonnull, "a", "b", "c" )
		);
	}

	@Callable
	boolean nonnull_list_addAll_null_check() {
		try {
			field_list_nonnull.addAll(listOf( "x", null ));
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_list_nonnull.size() == 3 &&
				checkValues( field_list_nonnull, "a", "b", "c" )
			);
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_list_add_check() {
		return (
			field_list_nonnull.add( "d" ) == true &&
			field_list_nonnull.size() == 4 &&
			checkValues( field_list_nonnull, "a", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_list_add_null_value_check() {
		try {
			field_list_nonnull.add( null );
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_list_nonnull.size() == 4 &&
				checkValues( field_list_nonnull, "a", "b", "c", "d" )
			);
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_list_add_middle_check() {
		field_list_nonnull.add( 1, "e" );
		return (
			field_list_nonnull.size() == 5 &&
			checkValues( field_list_nonnull, "a", "e", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_list_get_check() {
		return (
			field_list_nonnull.get( 0 ).equals( "a" ) &&
			field_list_nonnull.get( 1 ).equals( "e" ) &&
			field_list_nonnull.get( 2 ).equals( "b" ) &&
			field_list_nonnull.get( 3 ).equals( "c" ) &&
			field_list_nonnull.get( 4 ).equals( "d" )
		);
	}

	@Callable
	boolean nonnull_list_set_check() {
		return (
			field_list_nonnull.set( 1, "E" ).equals("e") &&
			field_list_nonnull.size() == 5 &&
			checkValues( field_list_nonnull, "a", "E", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_list_set_null_check() {
		try {
			field_list_nonnull.set( 1, null );
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_list_nonnull.size() == 5 &&
				checkValues( field_list_nonnull, "a", "E", "b", "c", "d" )
			);
		} catch( final Exception e ) {
			return false;
		}
	}

	//----------------------------------------------------------------------
	// UNINITIALIZED
	//----------------------------------------------------------------------
	@Autowired @Uninitialized
	SList<String> field_list_uninitialized;

	@Callable
	void initialize( final String value ){
		field_list_uninitialized.add( value );
	}

	@Autowired @Uninitialized
	SList<String> field_list_uninitialized2;

	@Callable
	void initialize(){
		field_list_uninitialized2.initialize();
	}

	@Callable
	boolean initialize_twice_check(){
		try {
			field_list_uninitialized2.initialize();
		} catch ( final Exception e ){
			return false;
		}

		return field_list_uninitialized2.isInitialized();
	}
}
