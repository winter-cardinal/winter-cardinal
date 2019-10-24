/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SClass;
import org.wcardinal.controller.data.SROQueue;
import org.wcardinal.controller.data.annotation.NonNull;
import org.wcardinal.controller.data.annotation.Uninitialized;
import test.PuppeteerTest;
import test.annotation.Test;
import org.wcardinal.util.thread.Unlocker;

@Controller
public class BasicsQueueController {
	@Autowired
	SROQueue<String> field_ro_queue;

	@Autowired
	SClass<List<String>> test_methods;

	@OnCreate
	void onCreate(){
		field_ro_queue.capacity( 3 );
		field_ro_queue.add( "a" );
		field_ro_queue.add( "b" );
		field_ro_queue.add( "c" );

		final List<String> methods = PuppeteerTest.findTestMethods( this.getClass() );
		Collections.sort( methods );
		test_methods.set( methods );
	}

	boolean checkValues( final Queue<String> queue, final String... values ) {
		return Objects.deepEquals( queue.toArray(new String[]{}), values );
	}

	List<String> listOf( final String... entries ){
		final List<String> result = new ArrayList<>();
		for( int i=0; i < entries.length; ++i ){
			result.add(entries[ i ]);
		}
		return result;
	}

	Queue<String> queueOf( final String... entries ){
		final Queue<String> result = new ArrayDeque<>();
		for( int i=0; i < entries.length; ++i ){
			result.add(entries[ i ]);
		}
		return result;
	}

	//--------------------------------------------------------------------
	// SERVER-SIDE TESTS
	//--------------------------------------------------------------------
	@Test
	@Callable
	boolean a000_reset_for_server_side_tests(){
		field_ro_queue.clear();
		field_ro_queue.add( "a" );
		field_ro_queue.add( "b" );
		field_ro_queue.add( "c" );
		return true;
	}

	@Test
	@Callable
	boolean a001_queue_equals_check(){
		return
			field_ro_queue.equals( null ) == false &&
			field_ro_queue.equals( field_ro_queue ) &&
			field_ro_queue.equals( queueOf( "a", "b" ) ) == false &
			field_ro_queue.equals( queueOf( "d", "d", "d" ) ) == false &
			field_ro_queue.equals( queueOf( "a", "b", "c" ) ) == true &
			field_ro_queue.equals( (Object) null ) == false &&
			field_ro_queue.equals( (Object) field_ro_queue ) &&
			field_ro_queue.equals( (Object) queueOf( "a", "b" ) ) == false &
			field_ro_queue.equals( (Object) queueOf( "d", "d", "d" ) ) == false &
			field_ro_queue.equals( (Object) queueOf( "a", "b", "c" ) ) == true;
	}

	@Test
	@Callable
	boolean a002_queue_iterator_check(){
		final String[] expected = new String[]{ "a", "b", "c" };
		int count = 0;
		for( final String value: field_ro_queue ) {
			if( expected.length <= count || Objects.equals( expected[ count ], value ) != true ) return false;
			count += 1;
		}
		return true;
	}

	@Test
	@Callable
	boolean a003_queue_iterator_remove_check(){
		final Iterator<String> iterator = field_ro_queue.iterator();
		while( iterator.hasNext() ){
			iterator.next();
			try {
				iterator.remove();
			} catch( final UnsupportedOperationException e ){
				return true;
			} catch( final Exception e ){
				return false;
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a010_queue_size_check(){
		return field_ro_queue.size() == 3;
	}

	@Test
	@Callable
	boolean a020_queue_capacity_check(){
		return field_ro_queue.capacity() == 3;
	}

	@Test
	@Callable
	boolean a030_queue_contains_check(){
		return (
			Objects.equals( field_ro_queue.contains( "a" ), true ) &&
			Objects.equals( field_ro_queue.contains( "b" ), true ) &&
			Objects.equals( field_ro_queue.contains( "c" ), true ) &&
			Objects.equals( field_ro_queue.contains( "d" ), false )
		);
	}

	@Test
	@Callable
	boolean a040_queue_containsAll_check(){
		return (
			Objects.equals( field_ro_queue.containsAll( listOf( "a", "b", "c" ) ), true ) &&
			Objects.equals( field_ro_queue.containsAll( listOf( "a", "b", "c", "d" ) ), false )
		);
	}

	@Test
	@Callable
	boolean a050_queue_element_check(){
		return (
			Objects.equals( field_ro_queue.element(), "a" )
		);
	}

	@Test
	@Callable
	boolean a060_queue_peek_check(){
		return (
			Objects.equals( field_ro_queue.peek(), "a" )
		);
	}

	@Test
	@Callable
	boolean a070_queue_isEmpty_check(){
		return (
			Objects.equals( field_ro_queue.isEmpty(), false )
		);
	}

	@Test
	@Callable
	boolean a071_queue_isNonNull_check(){
		return ! field_ro_queue.isNonNull();
	}

	@Test
	@Callable
	boolean a072_queue_isReadOnly_check(){
		return field_ro_queue.isReadOnly();
	}

	@Test
	@Callable
	boolean a080_queue_isLocked_check(){
		return (
			Objects.equals( field_ro_queue.isLocked(), true )
		);
	}

	@Test
	@Callable
	boolean a090_queue_lock_check(){
		try ( final Unlocker unlocker = field_ro_queue.lock() ) {
			return (
				Objects.equals( field_ro_queue.isLocked(), true )
			);
		}
	}

	@Test
	@Callable
	boolean a091_queue_tryLock_check(){
		if( field_ro_queue.tryLock() ) {
			try {
				return true;
			} finally {
				field_ro_queue.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a092_queue_tryLock_timeout_check(){
		if( field_ro_queue.tryLock( 100, TimeUnit.MILLISECONDS ) ) {
			try {
				return true;
			} finally {
				field_ro_queue.unlock();
			}
		}
		return false;
	}

	@Test
	@Callable
	boolean a100_queue_toArray_check(){
		return (
			Objects.deepEquals( field_ro_queue.toArray(), new Object[]{ "a", "b", "c" } )
		);
	}

	@Test
	@Callable
	boolean a110_queue_toArray_string_check(){
		return (
			Objects.deepEquals( field_ro_queue.toArray(new String[]{}), new String[]{ "a", "b", "c" } )
		);
	}

	@Test
	@Callable
	boolean a120_queue_toString_check(){
		return (
			Objects.equals( field_ro_queue.toString(), "[a, b, c]" )
		);
	}

	@Test
	@Callable
	boolean a130_queue_add_check(){
		return (
			Objects.equals( field_ro_queue.add( "d" ), true ) &&
			Objects.deepEquals( field_ro_queue.toArray(new String[]{}), new String[]{ "b", "c", "d" } )
		);
	}

	@Test
	@Callable
	boolean a140_queue_addAll_check(){
		return (
			Objects.equals( field_ro_queue.addAll( listOf( "e", "f" ) ), true ) &&
			Objects.deepEquals( field_ro_queue.toArray(new String[]{}), new String[]{ "d", "e", "f" } )
		);
	}

	@Test
	@Callable
	boolean a150_queue_capacity_resize_check(){
		return (
			Objects.equals( field_ro_queue.capacity( 2 ), 3 ) &&
			Objects.deepEquals( field_ro_queue.toArray(new String[]{}), new String[]{ "e", "f" } )
		);
	}

	@Test
	@Callable
	boolean a160_queue_remove_check(){
		return (
			Objects.equals( field_ro_queue.remove(), "e" ) &&
			Objects.deepEquals( field_ro_queue.toArray(new String[]{}), new String[]{ "f" } )
		);
	}

	@Test
	@Callable
	boolean a170_queue_remove_object_check(){
		try {
			field_ro_queue.remove(null);
			return false;
		} catch( final Exception e ){
			return true;
		}
	}

	@Test
	@Callable
	boolean a180_queue_removeAll_check(){
		try {
			field_ro_queue.removeAll(null);
			return false;
		} catch( final Exception e ){
			return true;
		}
	}

	@Test
	@Callable
	boolean a190_queue_retainAll_check(){
		try {
			field_ro_queue.retainAll(null);
			return false;
		} catch( final Exception e ){
			return true;
		}
	}

	@Test
	@Callable
	boolean a200_queue_offer_check(){
		return (
			Objects.equals( field_ro_queue.offer( "g" ), true ) &&
			Objects.deepEquals( field_ro_queue.toArray(new String[]{}), new String[]{ "f", "g" } )
		);
	}

	@Test
	@Callable
	boolean a205_queue_offer_null_check(){
		return (
			Objects.equals( field_ro_queue.offer( null ), true ) &&
			checkValues( field_ro_queue, "g", null )
		);
	}

	@Test
	@Callable
	boolean a210_queue_poll_check(){
		return (
			Objects.equals( field_ro_queue.poll(), "g" ) &&
			checkValues( field_ro_queue, (String)null )
		);
	}

	@Test
	@Callable
	boolean a220_queue_clear_check(){
		field_ro_queue.clear();
		return checkValues( field_ro_queue );
	}

	@Test
	@Callable
	boolean a230_queue_poll_on_empty_check(){
		return (
			Objects.deepEquals( field_ro_queue.poll(), null )
		);
	}

	@Test
	@Callable
	boolean a240_queue_clearAndAdd_check(){
		if (
			field_ro_queue.clearAndAdd( "x" ) &&
			checkValues( field_ro_queue, "x" )
		) {
			int capacity = field_ro_queue.capacity();
			try {
				field_ro_queue.capacity( 0 );
				field_ro_queue.clearAndAdd( "x" );
				return true;
			} catch( final Exception e ) {
				return false;
			} finally {
				field_ro_queue.capacity( capacity );
			}
		}

		return false;
	}

	@Test
	@Callable
	boolean a250_queue_clearAndOffer_check(){
		return (
			field_ro_queue.clearAndOffer( "x" ) &&
			checkValues( field_ro_queue, "x" )
		);
	}

	@Test
	@Callable
	boolean a260_queue_clearAndAddAll_check(){
		if(
			field_ro_queue.clearAndAddAll(listOf( "x", "y" )) &&
			checkValues( field_ro_queue, "x", "y" )
		) {
			int capacity = field_ro_queue.capacity();
			try {
				field_ro_queue.capacity( 0 );
				field_ro_queue.clearAndAddAll( listOf( "x" ) );
				return true;
			} catch( final Exception e ) {
				return false;
			} finally {
				field_ro_queue.capacity( capacity );
			}
		}

		return false;
	}

	@Test
	@Callable
	boolean a270_queue_clearAndOfferAll_check(){
		if (
			field_ro_queue.clearAndOfferAll(listOf( "x", "y" )) &&
			checkValues( field_ro_queue, "x", "y" )
		) {
			try {
				field_ro_queue.clearAndOfferAll( null );
				return false;
			} catch( NullPointerException e ) {
				if( checkValues( field_ro_queue, "x", "y" ) != true ) {
					return false;
				}
			} catch( Exception e ) {
				return false;
			}

			final int capacity = field_ro_queue.capacity();
			field_ro_queue.capacity( 0 );
			try {
				return field_ro_queue.clearAndOfferAll( listOf( "x" ) );
			} finally {
				field_ro_queue.capacity( capacity );
			}
		}
		return false;
	}

	//----------------------------------------------------------------------
	// DATA SYNC
	//----------------------------------------------------------------------
	@Autowired
	SROQueue<String> sync_ro_queue;

	@Callable
	boolean start_sync(){
		sync_ro_queue.clear();
		sync_ro_queue.add("a");
		sync_ro_queue.add("b");
		sync_ro_queue.add("c");

		return true;
	}

	//----------------------------------------------------------------------
	// NON-NULL
	//----------------------------------------------------------------------
	@Autowired @NonNull
	SROQueue<String> field_ro_queue_nonnull;

	@Callable
	boolean nonnull_queue_isNonNull_check(){
		return field_ro_queue_nonnull.isNonNull();
	}

	@Callable
	boolean nonnull_queue_isReadOnly_check(){
		return field_ro_queue_nonnull.isReadOnly();
	}

	@Callable
	void nonnull_queue_add( final String value ){
		field_ro_queue_nonnull.add( value );
	}

	@Callable
	boolean nonnull_queue_addAll_check() {
		field_ro_queue_nonnull.clear();
		field_ro_queue_nonnull.addAll(listOf("a", "b", "c"));

		return (
			field_ro_queue_nonnull.size() == 3 &&
			checkValues(field_ro_queue_nonnull, "a", "b", "c")
		);
	}

	@Callable
	boolean nonnull_queue_addAll_null_check() {
		try {
			field_ro_queue_nonnull.addAll(listOf( "x", (String)null ));
			return false;
		} catch ( final NullPointerException e ){
			return (
				field_ro_queue_nonnull.size() == 3 &&
				checkValues(field_ro_queue_nonnull, "a", "b", "c")
			);
		} catch ( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_queue_add_check() {
		return (
			field_ro_queue_nonnull.add( "d" ) == true &&
			field_ro_queue_nonnull.size() == 4 &&
			checkValues( field_ro_queue_nonnull, "a", "b", "c", "d" )
		);
	}

	@Callable
	boolean nonnull_queue_add_null_value_check() {
		try {
			field_ro_queue_nonnull.add( null );
			return false;
		} catch( final NullPointerException e ){
			return (
				field_ro_queue_nonnull.size() == 4 &&
				checkValues( field_ro_queue_nonnull, "a", "b", "c", "d" )
			);
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_queue_offer_value_check() {
		try {
			if( field_ro_queue_nonnull.offer( "e" ) ){
				return checkValues( field_ro_queue_nonnull, "a", "b", "c", "d", "e" );
			} else {
				return false;
			}
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_queue_offer_null_value_check() {
		try {
			field_ro_queue_nonnull.offer( null );
			return false;
		} catch( final NullPointerException e ) {
			return true;
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_queue_clearAndOffer_check() {
		try {
			field_ro_queue_nonnull.clearAndOffer( (String)null );
			return false;
		} catch( final NullPointerException e ) {
			field_ro_queue_nonnull.capacity( 0 );
			if( field_ro_queue_nonnull.clearAndOffer( "a" ) ){
				field_ro_queue_nonnull.capacity( Integer.MAX_VALUE );
				return true;
			} else {
				return false;
			}
		} catch( final Exception e ){
			return false;
		}
	}

	@Callable
	boolean nonnull_queue_clearAndOfferAll_check() {
		try {
			field_ro_queue_nonnull.clearAndOfferAll( listOf( (String)null ) );
			return false;
		} catch( final NullPointerException e ) {
			if( field_ro_queue_nonnull.clearAndOfferAll( listOf( "a", "b", "c" ) ) ){
				return checkValues( field_ro_queue_nonnull, "a", "b", "c" );
			} else {
				return false;
			}
		} catch( final Exception e ){
			return false;
		}
	}

	//----------------------------------------------------------------------
	// UNINITIALIZED
	//----------------------------------------------------------------------
	@Autowired @Uninitialized
	SROQueue<String> field_ro_queue_uninitialized;

	@Callable
	void initialize( final String value ){
		field_ro_queue_uninitialized.offer( value );
	}

	@Autowired @Uninitialized
	SROQueue<String> field_ro_queue_uninitialized2;

	@Callable
	void initialize(){
		field_ro_queue_uninitialized2.initialize();
	}

	@Callable
	boolean initialize_twice_check(){
		try {
			field_ro_queue_uninitialized2.initialize();
			return field_ro_queue_uninitialized2.isInitialized();
		} catch ( final Exception e ){
			return false;
		}
	}

	//----------------------------------------------------------------------
	// CAPACITY RELATED
	//----------------------------------------------------------------------
	@Autowired
	SROQueue<String> field_ro_queue_capacity_zero;
	@Autowired
	SROQueue<String> field_ro_queue_capacity_one;
	@OnCreate
	void onCreateCapacity(){
		field_ro_queue_capacity_zero.capacity( 0 );
		field_ro_queue_capacity_one.capacity( 1 );
	}
	@Callable
	boolean capacity_add_fail(){
		try {
			field_ro_queue_capacity_zero.add( "a" );
			return true;
		} catch( final Exception e ){
			return false;
		}
	}
	@Callable
	boolean capacity_addAll_fail(){
		try {
			field_ro_queue_capacity_zero.addAll(listOf( "a" ) );
			return true;
		} catch( final Exception e ){
			return false;
		}
	}
	@Callable
	boolean capacity_addAll_zero(){
		try {
			if( field_ro_queue_capacity_zero.addAll(ImmutableList.<String>of()) ) {
				return false;
			} else {
				return true;
			}
		} catch( final Exception e ){
			return false;
		}
	}
	@Callable
	boolean capacity_addAll_overflow(){
		try {
			if( field_ro_queue_capacity_one.addAll(ImmutableList.<String>of( "a", "b", "c" )) ) {
				return checkValues(field_ro_queue_capacity_one, "c");
			} else {
				return false;
			}
		} catch( final Exception e ){
			return false;
		}
	}
	@Callable
	boolean capacity_offer_fail(){
		try {
			if( field_ro_queue_capacity_zero.offer( "a" ) ) {
				return true;
			} else {
				return false;
			}
		} catch( final Exception e ){
			return false;
		}
	}
	@Callable
	boolean capacity_remove_fail(){
		try {
			field_ro_queue_capacity_zero.remove();
			return false;
		} catch( final NoSuchElementException e ){
			return true;
		} catch( final Exception e ){
			return false;
		}
	}
}
