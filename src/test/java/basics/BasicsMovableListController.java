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
import org.wcardinal.controller.data.SMovableList;
import org.wcardinal.controller.data.SMovableList.Move;
import org.wcardinal.controller.data.SList.Update;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Uninitialized;
import test.PuppeteerTest;
import test.annotation.Test;

@Controller
public class BasicsMovableListController {
	@Autowired
	SMovableList<String> field_movable_list;

	@OnCreate
	void init(){
		field_movable_list.add( "a" );
		field_movable_list.add( "b" );
		field_movable_list.add( "c" );
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
	SMovableList<String> field_movable_list2;

	@Autowired
	SClass<List<String>> test_methods;

	@OnCreate
	void init2(){
		field_movable_list2.add( "a" );
		field_movable_list2.add( "b" );
		field_movable_list2.add( "c" );

		final List<String> methods = PuppeteerTest.findTestMethods( this.getClass() );
		Collections.sort( methods );
		test_methods.set( methods );
	}

	@Test
	@Callable
	boolean a000_reset_for_server_side_tests(){
		field_movable_list2.clear();
		field_movable_list2.add( "a" );
		field_movable_list2.add( "b" );
		field_movable_list2.add( "c" );
		return true;
	}

	@Test
	@Callable
	boolean a01_movable_list_replace_1_check(){
		field_movable_list2.replace(listOf("a", "d", "e", "f", "g"));
		final boolean result = checkValues( field_movable_list2, "a", "d", "e", "f", "g" );
		field_movable_list2.clear();
		field_movable_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_replace_2_check(){
		field_movable_list2.replace(listOf((String)null));
		final boolean result = checkValues( field_movable_list2, (String)null );
		field_movable_list2.clear();
		field_movable_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_replace_3_check(){
		try {
			field_movable_list2.replace(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_retainAll_1_check(){
		final boolean result = field_movable_list2.retainAll(listOf("a", "d", "e")) &&
			checkValues( field_movable_list2, "a" );
		field_movable_list2.clear();
		field_movable_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_retainAll_2_check(){
		final boolean result = !field_movable_list2.retainAll(listOf("a", "b", "c", "d")) &&
			checkValues( field_movable_list2, "a", "b", "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_retainAll_3_check(){
		return !field_movable_list2.retainAll(listOf("a", "b", "c", null)) && checkValues( field_movable_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_movable_list_remove_1_check(){
		final boolean result = field_movable_list2.remove("b") && checkValues( field_movable_list2, "a", "c" );
		field_movable_list2.add(1, "b");
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_remove_2_check(){
		return !field_movable_list2.remove("d") && checkValues( field_movable_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_movable_list_remove_3_check(){
		return !field_movable_list2.remove(null) && checkValues( field_movable_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_movable_list_remove_4_check(){
		final boolean result = Objects.equals( field_movable_list2.remove(1), "b" ) && checkValues( field_movable_list2, "a", "c" );
		field_movable_list2.add(1, "b");
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_remove_5_check(){
		try {
			field_movable_list2.remove(4);
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_addAll_1_check(){
		final boolean result = field_movable_list2.addAll(listOf("d", "e")) && checkValues( field_movable_list2, "a", "b", "c", "d", "e" );
		field_movable_list2.remove( 4 );
		field_movable_list2.remove( 3 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_addAll_2_check(){
		final boolean result = field_movable_list2.addAll(listOf((String)null)) && checkValues( field_movable_list2, "a", "b", "c", null );
		field_movable_list2.remove( 3 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_addAll_3_check(){
		try {
			field_movable_list2.addAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_addAll_4_check(){
		final boolean result = field_movable_list2.addAll(1, listOf("d", "e")) && checkValues( field_movable_list2, "a", "d", "e", "b", "c" );
		field_movable_list2.remove( 2 );
		field_movable_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_addAll_5_check(){
		final boolean result = field_movable_list2.addAll(1, listOf((String)null)) && checkValues( field_movable_list2, "a", null, "b", "c" );
		field_movable_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_addAll_6_check(){
		try {
			field_movable_list2.addAll(1, null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_removeAll_1_check(){
		final boolean result = field_movable_list2.removeAll(listOf("a", "b")) && checkValues( field_movable_list2, "c" );
		field_movable_list2.add(0, "b");
		field_movable_list2.add(0, "a");
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_removeAll_2_check(){
		return !field_movable_list2.removeAll(listOf((String)null)) && checkValues( field_movable_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_movable_list_removeAll_3_check(){
		try {
			field_movable_list2.removeAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_toDirty_check(){
		try {
			field_movable_list2.toDirty();
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_toDirty_element_check(){
		try {
			field_movable_list2.toDirty( 1 );
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_toDirty_fail_check(){
		try {
			field_movable_list2.toDirty( -1 );
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_check(){
		return checkValues( field_movable_list2.subList(1, 2), "b" );
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_toString_check(){
		return field_movable_list2.subList(1, 2).toString().equals(listOf("b").toString());
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_toArray_check(){
		return Objects.deepEquals( field_movable_list2.subList(1, 2).toArray(), new Object[]{ "b" });
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_size_check(){
		return field_movable_list2.subList(1, 2).size() == 1;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_isEmpty_check(){
		return !field_movable_list2.subList(1, 2).isEmpty();
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_equals_check(){
		return field_movable_list2.subList(1, 2).equals( listOf("b") );
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_indexOf_check(){
		return field_movable_list2.subList(1, 2).indexOf( "b" ) == 0;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_lastIndexOf_check(){
		return field_movable_list2.subList(1, 2).lastIndexOf( "b" ) == 0;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_containsAll_1_check(){
		return field_movable_list2.subList(1, 2).containsAll(listOf("b")) && !field_movable_list2.subList(1, 2).containsAll(listOf((String)null));
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_containsAll_2_check(){
		try {
			field_movable_list2.subList(1, 2).containsAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_contains_check(){
		return field_movable_list2.subList(1, 2).contains("b") && !field_movable_list2.subList(1, 2).contains(null);
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_listIterator_1_check(){
		final String[] expected = new String[]{ "b" };
		int count = 0;
		final ListIterator<String> iterator = field_movable_list2.subList(1, 2).listIterator();
		while( iterator.hasNext() ){
			if( expected.length <= count || Objects.equals(iterator.next(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_listIterator_2_check(){
		final String[] expected = new String[]{ "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_movable_list2.subList(0, 3).listIterator( 1 );
		while( iterator.hasNext() ){
			if( expected.length <= count || Objects.equals(iterator.next(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_iterator_check(){
		final String[] expected = new String[]{ "b" };
		int count = 0;
		final Iterator<String> iterator = field_movable_list2.subList(1, 2).iterator();
		while( iterator.hasNext() ){
			if( expected.length <= count || Objects.equals(iterator.next(), expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_retainAll_1_check(){
		return !field_movable_list2.subList(0, 3).retainAll( listOf( "a", "b", "c", "d" ) );
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_retainAll_2_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = list.retainAll( listOf( "a", "b" ) ) && checkValues( list, "a", "b" );
		field_movable_list2.add( "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_retainAll_3_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
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
	boolean a01_movable_list_subList_removeAll_1_check(){
		return !field_movable_list2.subList(0, 3).removeAll( listOf( "x", "y" ) );
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_removeAll_2_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = list.removeAll( listOf( "a", "b" ) ) && checkValues( list, "c" );
		field_movable_list2.add( 0, "b" );
		field_movable_list2.add( 0, "a" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_removeAll_3_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
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
	boolean a01_movable_list_subList_clear_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		list.clear();
		final boolean result = checkValues( list ) && checkValues( field_movable_list2 );
		field_movable_list2.addAll(listOf( "a", "b", "c" ));
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_remove_1_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = list.remove( "b" ) && !list.remove( null ) && checkValues( list, "a", "c" ) && checkValues( field_movable_list2, "a", "c" );
		field_movable_list2.add( 1, "b" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_remove_2_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = list.remove( 1 ).equals("b") && checkValues( list, "a", "c" ) && checkValues( field_movable_list2, "a", "c" );
		field_movable_list2.add( 1, "b" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_remove_3_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
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
	boolean a01_movable_list_subList_get_1_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		return list.get( 1 ).equals("b");
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_get_2_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
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
	boolean a01_movable_list_subList_addAll_1_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = list.addAll( listOf( "d", "e" ) ) && checkValues( list, "a", "b", "c", "d", "e" ) && checkValues( field_movable_list2, "a", "b", "c", "d", "e" );
		field_movable_list2.remove( 4 );
		field_movable_list2.remove( 3 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_addAll_2_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = !list.addAll( listOf() ) && checkValues( list, "a", "b", "c" ) && checkValues( field_movable_list2, "a", "b", "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_addAll_3_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
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
	boolean a01_movable_list_subList_addAll_4_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = list.addAll( 1, listOf( "d", "e" ) ) && checkValues( list, "a", "d", "e", "b", "c" ) && checkValues( field_movable_list2, "a", "d", "e", "b", "c" );
		field_movable_list2.remove( 2 );
		field_movable_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_addAll_5_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = !list.addAll( 1, listOf() ) && checkValues( list, "a", "b", "c" ) && checkValues( field_movable_list2, "a", "b", "c" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_addAll_6_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
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
	boolean a01_movable_list_subList_add_1_check(){
		final List<String> list = field_movable_list2.subList(0, 2);
		final boolean result = list.add( "d" ) && checkValues( list, "a", "b", "d" ) && checkValues( field_movable_list2, "a", "b", "d", "c" );
		field_movable_list2.remove( 2 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_add_2_check(){
		final List<String> list = field_movable_list2.subList(0, 2);
		list.add( 1, "d" );
		final boolean result = checkValues( list, "a", "d", "b" ) && checkValues( field_movable_list2, "a", "d", "b", "c" );
		field_movable_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_set_check(){
		final List<String> list = field_movable_list2.subList(0, 3);
		final boolean result = list.set( 1, "d" ).equals("b") && checkValues( list, "a", "d", "c" ) && checkValues( field_movable_list2, "a", "d", "c" );
		field_movable_list2.set( 1, "b" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_subList_subList_check(){
		final List<String> list = field_movable_list2.subList(0, 3).subList(1, 2);
		return checkValues( list, "b" ) && checkValues( field_movable_list2, "a", "b", "c" );
	}

	@Test
	@Callable
	boolean a01_movable_list_iterator_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		for( String value: field_movable_list2 ) {
			if( expected.length <= count || Objects.equals(value, expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_movable_list_listIterator_1_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_movable_list2.listIterator();
		while( iterator.hasNext() ) {
			final String value = iterator.next();
			if( expected.length <= count || Objects.equals(value, expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_movable_list_listIterator_2_check(){
		final String[] expected = new String[]{ "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_movable_list2.listIterator( 1 );
		while( iterator.hasNext() ) {
			final String value = iterator.next();
			if( expected.length <= count || Objects.equals(value, expected[ count ]) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a01_movable_list_listIterator_3_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		final ListIterator<String> iterator = field_movable_list2.listIterator();

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
	boolean a01_movable_list_listIterator_4_check(){
		final ListIterator<String> iterator = field_movable_list2.listIterator();
		while( iterator.hasNext() ) {
			iterator.next();
			iterator.remove();
		}
		final boolean result = checkValues( field_movable_list2 );
		field_movable_list2.addAll(listOf("a", "b", "c"));
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_listIterator_5_check(){
		final ListIterator<String> iterator = field_movable_list2.listIterator();
		while( iterator.hasNext() ) {
			iterator.next();
			iterator.add("x");
			break;
		}
		final boolean result = checkValues( field_movable_list2, "a", "x", "b", "c" );
		field_movable_list2.remove( 1 );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_listIterator_6_check(){
		final ListIterator<String> iterator = field_movable_list2.listIterator();
		while( iterator.hasNext() ) {
			iterator.next();
			iterator.set( "x" );
			break;
		}
		final boolean result = checkValues( field_movable_list2, "x", "b", "c" );
		field_movable_list2.set( 0, "a" );
		return result;
	}

	@Test
	@Callable
	boolean a01_movable_list_containsAll_1_check(){
		return field_movable_list2.containsAll(listOf("a", "b", "c"));
	}

	@Test
	@Callable
	boolean a01_movable_list_containsAll_2_check(){
		return !field_movable_list2.containsAll(listOf("a", "b", "c", "d"));
	}

	@Test
	@Callable
	boolean a01_movable_list_containsAll_3_check(){
		return !field_movable_list2.containsAll(listOf((String)null));
	}

	@Test
	@Callable
	boolean a01_movable_list_containsAll_4_check(){
		try {
			field_movable_list2.containsAll(null);
			return false;
		} catch( final NullPointerException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Test
	@Callable
	boolean a01_movable_list_indexOf_check(){
		return field_movable_list2.indexOf( "a" ) == 0 && field_movable_list2.indexOf( "d" ) < 0 && field_movable_list2.indexOf( null ) < 0;
	}

	@Test
	@Callable
	boolean a01_movable_list_lastIndexOf_check(){
		return field_movable_list2.lastIndexOf( "a" ) == 0 && field_movable_list2.lastIndexOf( "d" ) < 0 && field_movable_list2.lastIndexOf( null ) < 0;
	}

	@Test
	@Callable
	boolean a01_movable_list_equals_check(){
		return !field_movable_list2.equals( null ) && field_movable_list2.equals( listOf( "a", "b", "c" ) );
	}

	@Test
	@Callable
	boolean a01_movable_list_toArray_check(){
		return Objects.deepEquals( field_movable_list2.toArray(), new Object[]{ "a", "b", "c" } );
	}

	@Test
	@Callable
	boolean a01_movable_list_isNonNull_check(){
		return !field_movable_list2.isNonNull();
	}

	@Test
	@Callable
	boolean a01_movable_list_tryLock_check(){
		if( field_movable_list2.tryLock() ){
			try {
				return true;
			} finally {
				field_movable_list2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a01_movable_list_tryLock_timeout_check(){
		if( field_movable_list2.tryLock( 100, TimeUnit.MILLISECONDS ) ){
			try {
				return true;
			} finally {
				field_movable_list2.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a01_movable_list_isLocked_check(){
		return field_movable_list2.isLocked();
	}

	@Test
	@Callable
	boolean a010_movable_list_size_check(){
		return field_movable_list2.size() == 3;
	}

	@Test
	@Callable
	boolean a030_movable_list_each_check() {
		final String[] expected = { "a", "b", "c" };
		for( int i=0; i<field_movable_list2.size(); ++i ){
			if( Objects.equals(expected[ i ], field_movable_list2.get(i)) != true ) return false;
		}
		return field_movable_list2.size() == 3;
	}

	@Test
	@Callable
	boolean a070_movable_list_values_check() {
		final String[] expected = {"a", "b", "c"};
		return Objects.deepEquals( field_movable_list2.toArray(new String[]{}), expected );
	}

	@Test
	@Callable
	boolean a090_movable_list_toString_check() {
		final String result = field_movable_list2.toString();
		return "[a, b, c]".equals( result );
	}

	@Test
	@Callable
	boolean a110_movable_list_get_check() {
		return (
			Objects.equals( field_movable_list2.get( 0 ), "a" ) &&
			Objects.equals( field_movable_list2.get( 1 ), "b" ) &&
			Objects.equals( field_movable_list2.get( 2 ), "c" )
		);
	}

	@Test
	@Callable
	boolean a130_movable_list_isEmpty_check() {
		return field_movable_list2.isEmpty() == false;
	}

	@Test
	@Callable
	boolean a150_movable_list_contains_check() {
		return (
			Objects.equals( field_movable_list2.contains( "a" ), true ) &&
			Objects.equals( field_movable_list2.contains( "b" ), true ) &&
			Objects.equals( field_movable_list2.contains( "c" ), true ) &&
			Objects.equals( field_movable_list2.contains( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a190_movable_list_add_check() {
		return (
			Objects.equals( field_movable_list2.add( "d" ), true ) &&
			Objects.equals( field_movable_list2.size(), 4 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{"a", "b", "c", "d"} )
		);
	}

	@Test
	@Callable
	boolean a210_movable_list_add_null_value_check() {
		return (
			Objects.equals( field_movable_list2.add( null ), true ) &&
			Objects.equals( field_movable_list2.size(), 5 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{"a", "b", "c", "d", null} )
		);
	}

	@Test
	@Callable
	boolean a230_movable_list_add_middle_check() {
		field_movable_list2.add( 1, "e" );
		return (
			Objects.equals( field_movable_list2.size(), 6 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{"a", "e", "b", "c", "d", null} )
		);
	}

	@Test
	@Callable
	boolean a240_movable_list_set_check() {
		return (
			Objects.equals( field_movable_list2.set( 1, "b" ), "e" ) &&
			Objects.equals( field_movable_list2.size(), 6 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{"a", "b", "b", "c", "d", null} )
		);
	}

	@Test
	@Callable
	boolean a250_movable_list_remove_check() {
		return (
			Objects.equals( field_movable_list2.remove( 1 ), "b" ) &&
			Objects.equals( field_movable_list2.size(), 5 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{ "a", "b", "c", "d", null } )
		);
	}

	@Test
	@Callable
	boolean a270_movable_list_clear_check() {
		field_movable_list2.clear();
		return (
			Objects.equals( field_movable_list2.size(), 0 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{} )
		);
	}

	@Test
	@Callable
	boolean a290_movable_list_addAll_check() {
		field_movable_list2.addAll(listOf("a", "b", "c"));
		return (
			Objects.equals( field_movable_list2.size(), 3 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{"a", "b", "c"} )
		);
	}

	@Test
	@Callable
	boolean a300_movable_list_clearAndAdd_check() {
		return (
			field_movable_list2.clearAndAdd( "a" ) &&
			Objects.equals( field_movable_list2.size(), 1 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{"a"} )
		);
	}

	@Test
	@Callable
	boolean a310_movable_list_clearAndAddAll_check() {
		return (
			field_movable_list2.clearAndAddAll(listOf("a", "b", "c")) &&
			Objects.equals( field_movable_list2.size(), 3 ) &&
			Objects.deepEquals( field_movable_list2.toArray(new String[]{}), new String[]{"a", "b", "c"} )
		);
	}

	//----------------------------------------------------------------------
	// DATA SYNC
	//----------------------------------------------------------------------
	@Autowired
	SMovableList<String> sync_movable_list;

	@Autowired
	SBoolean sync_movable_list_result;

	@Callable
	boolean start_sync(){
		sync_movable_list.clear();
		sync_movable_list.add("a");
		sync_movable_list.add("b");
		sync_movable_list.add("c");

		return true;
	}

	@OnChange("sync_movable_list")
	void onSyncListChange( final SortedMap<Integer, String> added, final SortedMap<Integer, String> removed ){
		sync_movable_list_result.set(
			Objects.deepEquals( values(added), new String[]{} ) &&
			Objects.deepEquals( values(removed), new String[]{ "a", "b", "c" } )
		);
	}

	//----------------------------------------------------------------------
	// NON-NULL
	//----------------------------------------------------------------------
	@Autowired @NonNull
	SMovableList<String> field_movable_list_nonnull;

	@Callable
	boolean nonnull_movable_list_addAll_check() {
		field_movable_list_nonnull.clear();
		field_movable_list_nonnull.addAll(listOf("a", "b", "c"));

		return (
			field_movable_list_nonnull.size() == 3 &&
			checkValues( field_movable_list_nonnull, "a", "b", "c" )
		);
	}

	@Callable
	boolean nonnull_movable_list_addAll_null_check() {
		try {
			field_movable_list_nonnull.addAll(listOf( "x", null ));
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_movable_list_nonnull.size() == 3 &&
				checkValues( field_movable_list_nonnull, "a", "b", "c" )
			);
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_movable_list_add_check() {
		return (
			field_movable_list_nonnull.add( "d" ) == true &&
			field_movable_list_nonnull.size() == 4 &&
			checkValues( field_movable_list_nonnull, "a", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_movable_list_add_null_value_check() {
		try {
			field_movable_list_nonnull.add( null );
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_movable_list_nonnull.size() == 4 &&
				checkValues( field_movable_list_nonnull, "a", "b", "c", "d" )
			);
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_movable_list_add_middle_check() {
		field_movable_list_nonnull.add( 1, "e" );
		return (
			field_movable_list_nonnull.size() == 5 &&
			checkValues( field_movable_list_nonnull, "a", "e", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_movable_list_get_check() {
		return (
			field_movable_list_nonnull.get( 0 ).equals( "a" ) &&
			field_movable_list_nonnull.get( 1 ).equals( "e" ) &&
			field_movable_list_nonnull.get( 2 ).equals( "b" ) &&
			field_movable_list_nonnull.get( 3 ).equals( "c" ) &&
			field_movable_list_nonnull.get( 4 ).equals( "d" )
		);
	}

	@Callable
	boolean nonnull_movable_list_set_check() {
		return (
			field_movable_list_nonnull.set( 1, "E" ).equals("e") &&
			field_movable_list_nonnull.size() == 5 &&
			checkValues( field_movable_list_nonnull, "a", "E", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_movable_list_set_null_check() {
		try {
			field_movable_list_nonnull.set( 1, null );
			return false;
		} catch( final NullPointerException e ) {
			return (
				field_movable_list_nonnull.size() == 5 &&
				checkValues( field_movable_list_nonnull, "a", "E", "b", "c", "d" )
			);
		} catch( final Exception e ) {
			return false;
		}
	}

	//----------------------------------------------------------------------
	// UNINITIALIZED
	//----------------------------------------------------------------------
	@Autowired @Uninitialized
	SMovableList<String> field_movable_list_uninitialized;

	@Callable
	void initialize( final String value ){
		field_movable_list_uninitialized.add( value );
	}

	@Autowired @Uninitialized
	SMovableList<String> field_movable_list_uninitialized2;

	@Callable
	void initialize(){
		field_movable_list_uninitialized2.initialize();
	}

	@Callable
	boolean initialize_twice_check(){
		try {
			field_movable_list_uninitialized2.initialize();
		} catch ( final Exception e ){
			return false;
		}

		return field_movable_list_uninitialized2.isInitialized();
	}

	//----------------------------------------------------------------------
	// MOVE
	//----------------------------------------------------------------------
	@Autowired
	SMovableList<String> field_movable_list_move;

	@Autowired
	SBoolean field_movable_list_move_added;

	@Autowired
	SBoolean field_movable_list_move_moved_case1;

	@Autowired
	SBoolean field_movable_list_move_moved_case2;

	@Autowired
	SBoolean field_movable_list_move_moved_case6;

	@Autowired
	SBoolean field_movable_list_move_moved_case7;

	@Autowired
	SBoolean field_movable_list_move_moved_case8;

	@OnChange( "field_movable_list_move" )
	void onChange(
		SortedMap<Integer, String> added,
		SortedMap<Integer, String> removed,
		SortedMap<Integer, Update<String>> updated,
		List<Move<String>> moved
	){
		if(
			checkValues( added, "a", "b", "c" ) &&
			removed.size() == 0 &&
			moved.size() == 0 &&
			updated.size() == 0
		){
			field_movable_list_move_added.set(true);
		} else if(
			added.size() == 0 &&
			removed.size() == 0 &&
			moved.size() == 1 &&
			updated.size() == 0
		) {
			if( moved.get( 0 ).getNewIndex() == 2 && moved.get( 0 ).getOldIndex() == 0 && moved.get( 0 ).getValue().equals("a") ){
				field_movable_list_move_moved_case1.set(true);
			} else if( moved.get( 0 ).getNewIndex() == 0 && moved.get( 0 ).getOldIndex() == 2 && moved.get( 0 ).getValue().equals("a") ){
				field_movable_list_move_moved_case2.set(true);
			}
		} else if(
			added.size() == 0 &&
			removed.size() == 0 &&
			moved.size() == 0 &&
			updated.size() == 1
		) {
			if( updated.firstKey() == 0 && updated.get( 0 ).getNewValue().equals( "x" ) && updated.get( 0 ).getOldValue().equals( "a" ) ) {
				field_movable_list_move_moved_case6.set(true);
			}
		} else if(
			added.size() == 1 &&
			removed.size() == 1 &&
			moved.size() == 1 &&
			updated.size() == 1
		) {
			if( added.firstKey() == 1 && added.get( 1 ).equals( "y" ) &&
				removed.firstKey() == 2 && removed.get( 2 ).equals( "c" ) &&
				moved.get( 0 ).getNewIndex() == 2 && moved.get( 0 ).getOldIndex() == 0 && moved.get( 0 ).getValue().equals( "x" ) &&
				updated.firstKey() == 2 && updated.get( 2 ).getNewValue().equals("a") && updated.get( 2 ).getOldValue().equals( "x" ) ) {
				field_movable_list_move_moved_case7.set(true);
			}
		} else if(
			added.size() == 0 &&
			removed.size() == 0 &&
			moved.size() == 1 &&
			updated.size() == 1
		) {
			if( moved.get( 0 ).getNewIndex() == 1 && moved.get( 0 ).getOldIndex() == 0 && moved.get( 0 ).getValue().equals( "y" ) &&
				updated.firstKey() == 1 && updated.get( 1 ).getNewValue().equals( "a" ) && updated.get( 1 ).getOldValue().equals( "y" ) ) {
				field_movable_list_move_moved_case8.set(true);
			}
		}
	}

	@Autowired
	SMovableList<String> field_movable_list_move2;

	@OnCreate
	void move_init(){
		field_movable_list_move2.add( "a" );
		field_movable_list_move2.add( "b" );
		field_movable_list_move2.add( "c" );
	}

	@Callable
	void move_case1(){
		field_movable_list_move2.move( 0, 2 );
	}

	@Callable
	void move_case2(){
		field_movable_list_move2.move( 2, 0 );
	}

	@Callable
	void move_case3(){
		field_movable_list_move2.move( 1, 1 );
	}

	@Callable
	boolean move_case4(){
		try {
			field_movable_list_move2.move( -1, 0 );
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		}
	}

	@Callable
	boolean move_case5(){
		try {
			field_movable_list_move2.move( 0, 4 );
			return false;
		} catch( final IndexOutOfBoundsException e ){
			return true;
		}
	}

	@Callable
	void move_case6(){
		field_movable_list_move2.set( 0, "x" );
	}

	@Callable
	void move_case7(){
		field_movable_list_move2.set( 0, "a" );
		field_movable_list_move2.move( 0, 1 );
		field_movable_list_move2.remove( 2 );
		field_movable_list_move2.add( 0, "y" );
	}

	@Callable
	void move_case8(){
		field_movable_list_move2.set( 0, "a" );
		field_movable_list_move2.move( 0, 1 );
	}

	@Autowired
	SMovableList<String> field_movable_list_cross_move;

	@OnCreate
	void cross_move_init(){
		field_movable_list_cross_move.add("a");
		field_movable_list_cross_move.add("b");
		field_movable_list_cross_move.add("c");
		field_movable_list_cross_move.add("d");
	}

	@Callable
	void cross_move(){
		field_movable_list_cross_move.move( 2, 0 );
		field_movable_list_cross_move.move( 3, 0 );
	}

	@Autowired
	SMovableList<String> field_movable_list_complex_move;

	@OnCreate
	void complex_move_init(){
		field_movable_list_complex_move.add( "a" );
		field_movable_list_complex_move.add( "b" );
		field_movable_list_complex_move.add( "c" );
		field_movable_list_complex_move.add( "d" );
		field_movable_list_complex_move.add( "e" );
	}

	@Callable
	void complex_move(){
		field_movable_list_complex_move.move( 0, 4 );
		field_movable_list_complex_move.move( 2, 3 );
		field_movable_list_complex_move.move( 0, 2 );
	}

	@Autowired
	SMovableList<String> field_movable_list_complex_move_client;

	@Autowired
	SBoolean field_movable_list_complex_move_client_init;

	@Autowired
	SBoolean field_movable_list_complex_move_client_result;

	@OnChange( "field_movable_list_complex_move_client" )
	void on_complex_move( SortedMap<Integer, String> added, SortedMap<Integer, String> removed,
			SortedMap<Integer, Update<String>> updated, List<Move<String>> newMoved, List<Move<String>> oldMoved ) {
		if( field_movable_list_complex_move_client.equals(Arrays.asList("a", "b", "c", "d", "e")) ) {
			field_movable_list_complex_move_client_init.set( true );
		} else if(
			added.size() == 0 && removed.size() == 0 &&
			updated.size() == 0 && oldMoved.size() == 3 && newMoved.size() == 3 &&
			newMoved.get( 0 ).getNewIndex() == 2 && newMoved.get( 0 ).getOldIndex() == 1 && newMoved.get( 0 ).getValue().equals( "b" ) &&
			newMoved.get( 1 ).getNewIndex() == 3 && newMoved.get( 1 ).getOldIndex() == 3 && newMoved.get( 1 ).getValue().equals( "d" ) &&
			newMoved.get( 2 ).getNewIndex() == 4 && newMoved.get( 2 ).getOldIndex() == 0 && newMoved.get( 2 ).getValue().equals( "a" ) &&
			oldMoved.get( 0 ).getNewIndex() == 4 && oldMoved.get( 0 ).getOldIndex() == 0 && oldMoved.get( 0 ).getValue().equals( "a" ) &&
			oldMoved.get( 1 ).getNewIndex() == 2 && oldMoved.get( 1 ).getOldIndex() == 1 && oldMoved.get( 1 ).getValue().equals( "b" ) &&
			oldMoved.get( 2 ).getNewIndex() == 3 && oldMoved.get( 2 ).getOldIndex() == 3 && oldMoved.get( 2 ).getValue().equals( "d" )
		) {
			field_movable_list_complex_move_client_result.set( true );
		}
	}
}
