/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package other;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;

import com.fasterxml.jackson.databind.node.NullNode;

import org.wcardinal.controller.data.SKeyOf;
import org.wcardinal.controller.data.SMovableList;
import org.wcardinal.controller.data.internal.SKeyOfImpl;
import org.wcardinal.controller.data.internal.SQueueValues;
import org.wcardinal.controller.internal.info.DynamicDataObject;
import org.wcardinal.controller.scope.ControllerScope;
import org.wcardinal.controller.scope.ControllerScopeAttributes;
import org.wcardinal.controller.scope.ControllerScopeAttributesHolder;
import org.wcardinal.controller.scope.shared.SharedComponentScope;
import org.wcardinal.exception.AmbiguousExceptionHandlerException;
import org.wcardinal.exception.IllegalArgumentTypeException;
import org.wcardinal.exception.IllegalConstantException;
import org.wcardinal.exception.IllegalDecorationException;
import org.wcardinal.exception.IllegalFieldException;
import org.wcardinal.exception.IllegalModifierException;
import org.wcardinal.exception.IllegalReturnTypeException;
import org.wcardinal.exception.NotReadyException;
import org.wcardinal.exception.NullArgumentException;
import org.wcardinal.exception.TaskOverloadException;
import org.wcardinal.exception.UnknownFieldException;
import org.wcardinal.exception.UnknownTargetException;
import org.wcardinal.io.message.MessageFormatException;
import org.wcardinal.io.message.MessageWriter;
import org.wcardinal.io.message.MessageWriterSender;
import org.wcardinal.io.message.ReceivedRequestMessage;
import org.wcardinal.io.message.ReceivedRequestMessages;
import org.wcardinal.io.message.RequestMessage;
import org.wcardinal.io.message.RequestMessageUpdate;
import org.wcardinal.io.message.RequestMessageUpdateAndAck;
import org.wcardinal.util.Pair;
import org.wcardinal.util.json.Json;

@Retention(RetentionPolicy.RUNTIME)
@interface Id {

}

@Retention(RetentionPolicy.RUNTIME)
@interface Ids {

}

class A {
	@Id @Ids
	long x;
	long y;

	public A( final long x, final long y ) {
		this.x = x;
		this.y = y;
	}
}

class B {
	long x;
	long y;

	@Id @Ids
	long getX() { return x * 2; }
	long getY() { return y; }

	public B( final long x, final long y ) {
		this.x = x;
		this.y = y;
	}
}

class C {
	@Id
	long x;
	@Ids
	long y;

	@Id
	long getX() { return x * 2; }
	@Ids
	long getY() { return y; }

	public C( final long x, final long y ) {
		this.x = x;
		this.y = y;
	}
}

class D {
	@Ids
	long x;
	long y;

	@Ids
	long getX() { return x * 2; }
	long getY() { return y; }

	public D( final long x, final long y ) {
		this.x = x;
		this.y = y;
	}
}

class E {
	long x;
	long y;

	@Id
	long getX() { throw new RuntimeException(); }
	long getY() { return y; }

	public E( final long x, final long y ) {
		this.x = x;
		this.y = y;
	}
}

public class OtherTest {
	@Test
	public void sMovableListMove(){
		final SMovableList.Move<String> move = new SMovableList.Move<String>();
		Assert.assertEquals( move.getNewIndex(), 0 );
		Assert.assertEquals( move.getOldIndex(), 0 );
		Assert.assertEquals( move.getValue(), null );
		Assert.assertNotEquals( move.toString(), null );
	}

	@Test
	public void sMovableListUpdate(){
		final SMovableList.Update<String> update = new SMovableList.Update<String>();
		Assert.assertEquals( update.getNewValue(), null );
		Assert.assertEquals( update.getOldValue(), null );
		Assert.assertNotEquals( update.toString(), null );
	}

	@Test
	public void surrogatePair() throws IOException{
		final char ch = '\uD842'; // kuchi + shichi (high), cp=134047
		final char cl = '\uDF9F'; // kuchi + shichi (low), cp=134047

		final String s = new String(new char[] { ch, cl });
		final List<CharSequence> result = new ArrayList<>();
		final MessageWriter writer = new MessageWriter( new char[ 3 ], new MessageWriterSender() {
			@Override
			public void send( final CharSequence message, final boolean isLast ) {
				result.add( message );
			}
		});
		writer.write( s );
		writer.write( s );
		writer.write( s );
		writer.close();

		Assert.assertEquals("size", 3, result.size() );
		Assert.assertEquals("1st element", s, result.get( 0 ) );
		Assert.assertEquals("2nd element", s, result.get( 1 ) );
		Assert.assertEquals("3rd element", s, result.get( 2 ) );
	}

	public void exceptionMethod(){}

	@Test
	public void exception() throws NoSuchMethodException, SecurityException{
		// AmbiguousExceptionHandlerException
		final String message = "abc";
		Assert.assertEquals( new AmbiguousExceptionHandlerException( message ).getMessage(), message );
		try {
			throw new AmbiguousExceptionHandlerException( null, null );
		} catch( final Exception e ){
			Assert.assertTrue( e instanceof NullPointerException );
		}
		final Method method = this.getClass().getMethod("exceptionMethod");
		Assert.assertNotEquals( new AmbiguousExceptionHandlerException( method, method ).getMessage(), null );

		// NotReadyException
		new NotReadyException();
		Assert.assertEquals( new NotReadyException( message ).getMessage(), message );

		// NullArgumentException
		new NullArgumentException();
		Assert.assertEquals( new NullArgumentException( message ).getMessage(), message );

		// IllegalArgumentTypeException
		Assert.assertEquals( new IllegalArgumentTypeException( message ).getMessage(), message );

		// UnknownTargetException
		Assert.assertEquals( new UnknownTargetException( message ).getMessage(), message );

		// UnknownFieldException
		Assert.assertEquals( new UnknownFieldException( message ).getMessage(), message );

		// IllegalDecorationException
		Assert.assertEquals( new IllegalDecorationException( message ).getMessage(), message );

		// IllegalModifierException
		Assert.assertEquals( new IllegalModifierException( message ).getMessage(), message );

		// IllegalFieldException
		Assert.assertEquals( new IllegalFieldException( message ).getMessage(), message );

		// TaskOverloadException
		Assert.assertEquals( new TaskOverloadException( message ).getMessage(), message );

		// IllegalConstantException
		Assert.assertEquals( new IllegalConstantException( message ).getMessage(), message );

		// IllegalReturnTypeException
		Assert.assertEquals( new IllegalReturnTypeException( message ).getMessage(), message );
	}

	@Test
	public void pair(){
		final Pair<String, String> pair = new Pair<>();
		Assert.assertEquals( pair.left, null );
		Assert.assertEquals( pair.right, null );
	}

	@Test
	public void controllerScopeAttributes(){
		try {
			ControllerScopeAttributesHolder.get();
		} catch( final Exception e ){
			Assert.assertTrue( e instanceof IllegalStateException );
		}

		ControllerScopeAttributesHolder.set( null );

		try {
			ControllerScopeAttributesHolder.get();
		} catch( final Exception e ){
			Assert.assertTrue( e instanceof IllegalStateException );
		}
	}

	@Test
	public void controllerScope() {
		final ControllerScopeAttributes controllerScopeAttributes = new ControllerScopeAttributes( "a", "b" );
		controllerScopeAttributes.registerDestructionCallback("Cardinal", new Runnable() {
			@Override
			public void run() {}
		});
		ControllerScopeAttributesHolder.set( controllerScopeAttributes );
		final ControllerScope controllerScope = new ControllerScope();
		Assert.assertNull( controllerScope.remove( "Cardinal" ) );
		Assert.assertEquals(controllerScope.get("Cardinal", new ObjectFactory<ControllerScope>() {
			@Override
			public ControllerScope getObject() throws BeansException {
				return controllerScope;
			}
		}), controllerScope);
		Assert.assertEquals( controllerScope.remove( "Cardinal" ), controllerScope );
		Assert.assertNull( controllerScope.resolveContextualObject( "Cardinal" ) );
		Assert.assertEquals( controllerScope.getConversationId(), "a-b" );
		ControllerScopeAttributesHolder.set( null );
		controllerScopeAttributes.registerDestructionCallback("Cardinal", new Runnable() {
			@Override
			public void run() {}
		});
		controllerScopeAttributes.destroy();
	}

	@Test
	public void sharedComponentScope() {
		final SharedComponentScope sharedComponentScope = new SharedComponentScope();
		sharedComponentScope.registerDestructionCallback("Cardinal", new Runnable() {
			@Override
			public void run() {}
		});
		Assert.assertNull( sharedComponentScope.remove( "Cardinal" ) );
		Assert.assertEquals(sharedComponentScope.get("Cardinal", new ObjectFactory<SharedComponentScope>() {
			@Override
			public SharedComponentScope getObject() throws BeansException {
				return sharedComponentScope;
			}
		}), sharedComponentScope);
		Assert.assertEquals(sharedComponentScope.get( "Cardinal", null ), sharedComponentScope);
		Assert.assertEquals( sharedComponentScope.remove( "Cardinal" ), sharedComponentScope );
		Assert.assertNull( sharedComponentScope.resolveContextualObject( "Cardinal" ) );
		Assert.assertNull( sharedComponentScope.getConversationId() );
		sharedComponentScope.registerDestructionCallback("Cardinal", new Runnable() {
			@Override
			public void run() {}
		});
		sharedComponentScope.destroy();
	}

	@Test
	public void receivedRequestMessage() throws Exception {
		final ReceivedRequestMessage receivedRequestMessage = new ReceivedRequestMessage( "a", null );
		Assert.assertNotEquals( receivedRequestMessage.toString(), null );
	}

	@Test
	public void requestMessage() throws Exception {
		final RequestMessage requestMessage = new RequestMessageUpdate( null );
		Assert.assertNotEquals( requestMessage.toString(), null );
	}

	@Test
	public void requestMessageUpdateAndAck() throws Exception {
		final RequestMessageUpdateAndAck requestMessageUpdateAndAck = new RequestMessageUpdateAndAck( null, null );
		Assert.assertNotEquals( requestMessageUpdateAndAck.getType(), null );
		Assert.assertNull( requestMessageUpdateAndAck.getAck() );
		Assert.assertNull( requestMessageUpdateAndAck.getArgument() );
	}

	@Test
	public void receivedRequestMessages() throws Exception {
		try {
			ReceivedRequestMessages.parse( "[" );
			throw new AssertionError();
		} catch ( final MessageFormatException e ) {

		}

		try {
			ReceivedRequestMessages.parse( NullNode.instance );
		} catch ( final MessageFormatException e ) {

		}

		try {
			ReceivedRequestMessages.parse( Json.mapper.createObjectNode() );
		} catch ( final MessageFormatException e ) {

		}

		try {
			ReceivedRequestMessages.parse( Json.mapper.createArrayNode() );
		} catch ( final MessageFormatException e ) {

		}
	}

	@Test
	public void skeyOf() {
		final SKeyOf<A> skeyOfA = new SKeyOfImpl<>( A.class );
		Assert.assertEquals( "1", skeyOfA.keyOf( new A( 1, 2 ) ) );

		final SKeyOf<B> skeyOfB = new SKeyOfImpl<>( B.class );
		Assert.assertEquals( "2", skeyOfB.keyOf( new B( 1, 5 ) ) );

		final SKeyOf<C> skeyOfC = new SKeyOfImpl<>( C.class );
		Assert.assertEquals( "1", skeyOfC.keyOf( new C( 1, 5 ) ) );

		final SKeyOf<D> skeyOfD = new SKeyOfImpl<>( D.class );
		Assert.assertNull( skeyOfD.keyOf( new D( 1, 2 ) ) );

		final SKeyOf<E> skeyOfE = new SKeyOfImpl<>( E.class );
		Assert.assertNull( skeyOfE.keyOf( new E( 1, 2 ) ) );
	}

	@Test
	public void dynamicVariableInfo() throws IOException {
		// TO JSON
		final DynamicDataObject info = new DynamicDataObject( 5, 1, 2 );
		final String json = Json.convert( info );
		if( json.matches( "\\[\\s*5\\s*,\\s*1\\s*,\\s*2\\s*\\]" ) != true ) {
			throw new AssertionError();
		}

		// FROM JSON
		final DynamicDataObject read = Json.convert( json, DynamicDataObject.class );
		if( read.revision != 5 || read.type != 1 || ! Objects.equals( read.data, 2 ) ) {
			throw new AssertionError();
		}
	}

	@Test
	public void sQueueValues() {
		final SQueueValues<Integer> values = new SQueueValues<Integer>();
		Assert.assertTrue( values.add( 0 ) );
		Assert.assertTrue( values.add( 1 ) );
		Assert.assertTrue( values.add( 2 ) );
		Assert.assertTrue( values.add( 3 ) );
		Assert.assertEquals( (Integer)0, values.remove() );
		Assert.assertEquals( (Integer)1, values.remove() );
		Assert.assertTrue( values.add( 4 ) );
		Assert.assertEquals( (Integer)2, values.remove() );
		Assert.assertTrue( values.add( 5 ) );
		Assert.assertEquals( 3, values.size() );
		Assert.assertFalse( values.contains( 0 ) );
		Assert.assertTrue( values.contains( 3 ) );
		Assert.assertArrayEquals( new Object[] { 3, 4, 5 }, values.toArray() );
		Assert.assertArrayEquals( new Integer[] { 3, 4, 5 }, values.toArray(new Integer[ 0 ]) );
		Assert.assertArrayEquals( new Integer[] { 3, 4, 5 }, values.toArray(new Integer[ 3 ]) );
		Assert.assertTrue( values.addAll( Arrays.asList( 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ) ) );
		Assert.assertEquals( 13, values.size() );
		Assert.assertArrayEquals( new Object[] { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 }, values.toArray() );
		Assert.assertFalse( values.contains( 0 ) );
		Assert.assertTrue( values.contains( 13 ) );
		Assert.assertFalse( values.containsAll( Arrays.asList( 10, 11, 20 ) ) );
		Assert.assertTrue( values.containsAll( Arrays.asList( 10, 11, 12 ) ) );
		Assert.assertEquals( (Integer)3, values.remove() );
		Assert.assertEquals( (Integer)4, values.remove() );
		Assert.assertEquals( (Integer)5, values.remove() );
		Assert.assertEquals( (Integer)6, values.remove() );
		Assert.assertEquals( (Integer)7, values.remove() );
		Assert.assertEquals( (Integer)8, values.remove() );
		Assert.assertEquals( (Integer)9, values.remove() );
		Assert.assertEquals( (Integer)10, values.remove() );
		Assert.assertEquals( (Integer)11, values.remove() );
		Assert.assertEquals( (Integer)12, values.remove() );
		Assert.assertEquals( 3, values.size() );
		Assert.assertArrayEquals( new Object[] { 13, 14, 15 }, values.toArray() );

		try {
			values.containsAll( null );
			Assert.fail();
		} catch( final Exception e ) {
			Assert.assertTrue( e instanceof NullPointerException );
		}

		values.clear();
		Assert.assertTrue( values.containsAll( Collections.emptyList() ) );
		Assert.assertFalse( values.containsAll(Arrays.asList( 1, 2, 3 )) );

		Assert.assertFalse( values.contains( null ) );

		try {
			values.element();
			Assert.fail();
		} catch( final Exception e ) {
			Assert.assertTrue( e instanceof NoSuchElementException );
		}

		Assert.assertNull( values.peek() );
		Assert.assertNull( values.poll() );

		final Integer[] array = new Integer[] { 1, null };
		Assert.assertTrue( values.toArray(array) == array );
		Assert.assertArrayEquals( array, new Integer[] { null, null } );

		try {
			values.addAll( null );
		} catch( final Exception e ) {
			Assert.assertTrue( e instanceof NullPointerException );
		}

		try {
			values.remove( null );
		} catch( final Exception e ) {
			Assert.assertTrue( e instanceof UnsupportedOperationException );
		}

		try {
			values.removeAll( null );
		} catch( final Exception e ) {
			Assert.assertTrue( e instanceof UnsupportedOperationException );
		}

		try {
			values.retainAll( null );
		} catch( final Exception e ) {
			Assert.assertTrue( e instanceof UnsupportedOperationException );
		}

		Assert.assertFalse( values.equals( (Object)null ) );
		Assert.assertTrue( values.equals( (Object)Collections.<Integer>emptyList() ) );
		Assert.assertFalse( values.equals( (Object)Arrays.asList( 1, 2, 3 ) ) );

		Assert.assertTrue( values.offer( 2 ) );
		Assert.assertTrue( values.equals(Arrays.asList( 2 )) );
	}
}
