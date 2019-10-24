/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package syntax;

import org.junit.Test;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.SpringApplication;

import org.wcardinal.exception.AmbiguousExceptionHandlerException;
import org.wcardinal.exception.IllegalArgumentTypeException;
import org.wcardinal.exception.IllegalConstantException;
import org.wcardinal.exception.IllegalDecorationException;
import org.wcardinal.exception.IllegalFieldException;
import org.wcardinal.exception.IllegalModifierException;
import org.wcardinal.exception.IllegalReturnTypeException;
import org.wcardinal.exception.TaskOverloadException;
import org.wcardinal.exception.UnknownTargetException;

class Assert {
	static int port = 8080;

	public static void success( final Class<?> applicationClass ) {
		SpringApplication.exit( SpringApplication.run( applicationClass, new String[] {"--server.port="+(port++)} ) );
	}

	private static void check( final Exception e, final Class<? extends Exception> expected, final Exception root ) {
		final Throwable cause = e.getCause();
		if( cause instanceof UnsatisfiedDependencyException ) {
			check( (UnsatisfiedDependencyException) cause, expected, root );
		} else if( cause instanceof BeanCreationException ) {
			check( (BeanCreationException) cause, expected, root );
		} else if( cause instanceof BeanInstantiationException ) {
			if( expected.isInstance( cause.getCause() ) ) {
				return;
			}
			throw new IllegalStateException( root );
		} else {
			throw new IllegalStateException( root );
		}
	}

	public static void fail( final Class<?> applicationClass, final Class<? extends Exception> exceptionClass ) {
		try {
			SpringApplication.run( applicationClass, new String[] {"--server.port="+(port++)} );
			throw new IllegalStateException();
		} catch( final Exception e ) {
			check( e, exceptionClass, e );
		}
	}
}

public class SyntaxCheckTest {
	@Test
	public void fieldConstantSuccess() {
		Assert.success( syntax.field.constant.success.SyntaxApplication.class );
	}

	@Test
	public void fieldConstantFail1() {
		Assert.fail( syntax.field.constant.fail1.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail2() {
		Assert.fail( syntax.field.constant.fail2.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail3() {
		Assert.fail( syntax.field.constant.fail3.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail4() {
		Assert.fail( syntax.field.constant.fail4.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail5() {
		Assert.fail( syntax.field.constant.fail5.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail6() {
		Assert.fail( syntax.field.constant.fail6.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail7() {
		Assert.fail( syntax.field.constant.fail7.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail8() {
		Assert.fail( syntax.field.constant.fail8.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldConstantFail9() {
		Assert.fail( syntax.field.constant.fail9.SyntaxApplication.class, IllegalConstantException.class );
	}

	@Test
	public void fieldFail1() {
		Assert.fail( syntax.field.fail1.SyntaxApplication.class, IllegalDecorationException.class );
	}

	@Test
	public void fieldSharedFail1() {
		Assert.fail( syntax.field.shared.fail1.SyntaxApplication.class, IllegalFieldException.class );
	}

	@Test
	public void fieldSharedFail2() {
		Assert.fail( syntax.field.shared.fail2.SyntaxApplication.class, IllegalFieldException.class );
	}

	@Test
	public void fieldSharedFail3() {
		Assert.fail( syntax.field.shared.fail3.SyntaxApplication.class, IllegalFieldException.class );
	}

	@Test
	public void fieldSharedFail4() {
		Assert.fail( syntax.field.shared.fail4.SyntaxApplication.class, IllegalFieldException.class );
	}

	@Test
	public void fieldSharedFail5() {
		Assert.fail( syntax.field.shared.fail5.SyntaxApplication.class, IllegalDecorationException.class );
	}

	@Test
	public void callableExceptionHandlerFail1() {
		Assert.fail( syntax.method.callableexceptionhandler.fail1.SyntaxApplication.class, UnknownTargetException.class );
	}

	@Test
	public void onChangeSuccess() {
		Assert.success( syntax.method.onchange.success.SyntaxApplication.class );
	}

	@Test
	public void onChangeFail1() {
		Assert.fail( syntax.method.onchange.fail1.SyntaxApplication.class, UnknownTargetException.class );
	}

	@Test
	public void onChangeFail2() {
		Assert.fail( syntax.method.onchange.fail2.SyntaxApplication.class, UnknownTargetException.class );
	}

	@Test
	public void onChangeFail3() {
		Assert.fail( syntax.method.onchange.fail3.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail4() {
		Assert.fail( syntax.method.onchange.fail4.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail5() {
		Assert.fail( syntax.method.onchange.fail5.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail6() {
		Assert.fail( syntax.method.onchange.fail6.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail7() {
		Assert.fail( syntax.method.onchange.fail7.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail8() {
		Assert.fail( syntax.method.onchange.fail8.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail9() {
		Assert.fail( syntax.method.onchange.fail9.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail10() {
		Assert.fail( syntax.method.onchange.fail10.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail11() {
		Assert.fail( syntax.method.onchange.fail11.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail12() {
		Assert.fail( syntax.method.onchange.fail12.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail13() {
		Assert.fail( syntax.method.onchange.fail13.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail14() {
		Assert.fail( syntax.method.onchange.fail14.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail15() {
		Assert.fail( syntax.method.onchange.fail15.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail16() {
		Assert.fail( syntax.method.onchange.fail16.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail17() {
		Assert.fail( syntax.method.onchange.fail17.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onChangeFail18() {
		Assert.fail( syntax.method.onchange.fail18.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onCheckSuccess() {
		Assert.success( syntax.method.oncheck.success.SyntaxApplication.class );
	}

	@Test
	public void onCheckFail1() {
		Assert.fail( syntax.method.oncheck.fail1.SyntaxApplication.class, IllegalModifierException.class );
	}

	@Test
	public void onCheckFail2() {
		Assert.fail( syntax.method.oncheck.fail2.SyntaxApplication.class, IllegalReturnTypeException.class );
	}

	@Test
	public void onCheckFail3() {
		Assert.fail( syntax.method.oncheck.fail3.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onCheckFail4() {
		Assert.fail( syntax.method.oncheck.fail4.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onCheckFail5() {
		Assert.fail( syntax.method.oncheck.fail5.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onCreateSuccess() {
		Assert.success( syntax.method.oncreate.success.SyntaxApplication.class );
	}

	@Test
	public void onCreateFail1() {
		Assert.fail( syntax.method.oncreate.fail1.SyntaxApplication.class, IllegalDecorationException.class );
	}

	@Test
	public void onCreateFail2() {
		Assert.fail( syntax.method.oncreate.fail2.SyntaxApplication.class, IllegalDecorationException.class );
	}

	@Test
	public void onCreateFail3() {
		Assert.fail( syntax.method.oncreate.fail3.SyntaxApplication.class, IllegalDecorationException.class );
	}

	@Test
	public void onCreateFail4() {
		Assert.fail( syntax.method.oncreate.fail4.SyntaxApplication.class, IllegalDecorationException.class );
	}

	@Test
	public void onDestroySuccess() {
		Assert.success( syntax.method.ondestroy.success.SyntaxApplication.class );
	}

	@Test
	public void onDestroyFail1() {
		Assert.fail( syntax.method.ondestroy.fail1.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onHideSuccess() {
		Assert.success( syntax.method.onhide.success.SyntaxApplication.class );
	}

	@Test
	public void onHideFail1() {
		Assert.fail( syntax.method.onhide.fail1.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onHideFail2() {
		Assert.fail( syntax.method.onhide.fail2.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onHideFail3() {
		Assert.fail( syntax.method.onhide.fail3.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onRequestSuccess() {
		Assert.success( syntax.method.onrequest.success.SyntaxApplication.class );
	}

	@Test
	public void onRequestFail1() {
		Assert.fail( syntax.method.onrequest.fail1.SyntaxApplication.class, IllegalModifierException.class );
	}

	@Test
	public void onRequestFail2() {
		Assert.fail( syntax.method.onrequest.fail2.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onRequestFail3() {
		Assert.fail( syntax.method.onrequest.fail3.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onRequestFail4() {
		Assert.fail( syntax.method.onrequest.fail4.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onShowSuccess() {
		Assert.success( syntax.method.onshow.success.SyntaxApplication.class );
	}

	@Test
	public void onShowFail1() {
		Assert.fail( syntax.method.onshow.fail1.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onShowFail2() {
		Assert.fail( syntax.method.onshow.fail2.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onShowFail3() {
		Assert.fail( syntax.method.onshow.fail3.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void taskSuccess() {
		Assert.success( syntax.method.task.success.SyntaxApplication.class );
	}

	@Test
	public void taskFail1() {
		Assert.fail( syntax.method.task.fail1.SyntaxApplication.class, TaskOverloadException.class );
	}

	@Test
	public void taskFail2() {
		Assert.fail( syntax.method.task.fail2.SyntaxApplication.class, TaskOverloadException.class );
	}

	@Test
	public void taskExceptionHandlerSuccess() {
		Assert.success( syntax.method.taskexceptionhandler.success.SyntaxApplication.class );
	}

	@Test
	public void taskExceptionHandlerFail1() {
		Assert.fail( syntax.method.taskexceptionhandler.fail1.SyntaxApplication.class, UnknownTargetException.class );
	}

	@Test
	public void taskExceptionHandlerFail2() {
		Assert.fail( syntax.method.taskexceptionhandler.fail2.SyntaxApplication.class, AmbiguousExceptionHandlerException.class );
	}

	@Test
	public void taskExceptionHandlerFail3() {
		Assert.fail( syntax.method.taskexceptionhandler.fail3.SyntaxApplication.class, AmbiguousExceptionHandlerException.class );
	}

	@Test
	public void taskExceptionHandlerFail4() {
		Assert.fail( syntax.method.taskexceptionhandler.fail4.SyntaxApplication.class, IllegalReturnTypeException.class );
	}

	@Test
	public void exceptionHandlerSuccess() {
		Assert.success( syntax.method.exceptionhandler.success.SyntaxApplication.class );
	}

	@Test
	public void exceptionHandlerFail1() {
		Assert.fail( syntax.method.exceptionhandler.fail1.SyntaxApplication.class, UnknownTargetException.class );
	}

	@Test
	public void exceptionHandlerFail2() {
		Assert.fail( syntax.method.exceptionhandler.fail2.SyntaxApplication.class, AmbiguousExceptionHandlerException.class );
	}

	@Test
	public void exceptionHandlerFail3() {
		Assert.fail( syntax.method.exceptionhandler.fail3.SyntaxApplication.class, AmbiguousExceptionHandlerException.class );
	}

	@Test
	public void exceptionHandlerFail4() {
		Assert.fail( syntax.method.exceptionhandler.fail4.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void exceptionHandlerFail5() {
		Assert.fail( syntax.method.exceptionhandler.fail5.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onIdleCheckSuccess() {
		Assert.success( syntax.method.onidlecheck.success.SyntaxApplication.class );
	}

	@Test
	public void onIdleCheckFail1() {
		Assert.fail( syntax.method.onidlecheck.fail1.SyntaxApplication.class, IllegalReturnTypeException.class );
	}

	@Test
	public void onIdleCheckFail2() {
		Assert.fail( syntax.method.onidlecheck.fail2.SyntaxApplication.class, IllegalReturnTypeException.class );
	}

	@Test
	public void onIdleCheckFail3() {
		Assert.fail( syntax.method.onidlecheck.fail3.SyntaxApplication.class, IllegalReturnTypeException.class );
	}

	@Test
	public void onIdleCheckFail4() {
		Assert.fail( syntax.method.onidlecheck.fail4.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}

	@Test
	public void onIdleCheckFail5() {
		Assert.fail( syntax.method.onidlecheck.fail5.SyntaxApplication.class, IllegalArgumentTypeException.class );
	}
}
