/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import org.springframework.beans.factory.annotation.Autowired;
import org.wcardinal.controller.StreamingResult;
import org.wcardinal.controller.annotation.Ajax;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.CallableExceptionHandler;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Unlocked;

@Controller
public class BasicsCallableAjaxController {
	@Autowired
	BasicsCallableAjaxComponent component;

	@Callable
	@Ajax
	int callable_ajax( final int value ){
		return value * 3;
	}

	@Callable
	int callable_nonajax( final int value ){
		return value * 3;
	}

	@Callable
	@Ajax
	@Unlocked
	int callable_ajax_unlocked( final int value ){
		return value * 3;
	}

	@Callable
	@Ajax
	StreamingResult callable_ajax_streaming( final int value ){
		return (generator) -> {
			generator.writeNumber(value * 3);
		};
	}

	@Callable
	@Ajax
	StreamingResult callable_ajax_streaming_array( final int value ){
		return (generator) -> {
			generator.writeStartArray();
			for (int i = 0; i < 3; ++i) {
				generator.writeNumber(i);
			}
			generator.writeEndArray();
		};
	}

	@Callable
	@Ajax
	int callable_ajax_exception( final int value ){
		throw new RuntimeException("exception for test");
	}

	@Callable
	@Ajax
	@Unlocked
	int callable_ajax_exception_unlocked( final int value ){
		throw new RuntimeException("exception for test");
	}

	@Callable
	@Ajax
	int callable_ajax_exception_handled1( final int value ){
		throw new RuntimeException("exception for test");
	}

	@CallableExceptionHandler({ "callable_ajax_exception_handled1" })
	String callable_ajax_exception_handler1() {
		return "handler1";
	}

	@Callable
	@Ajax
	int callable_ajax_exception_handled2( final int value ){
		throw new RuntimeException("exception for test");
	}

	@CallableExceptionHandler({ "callable_ajax_exception_handled2" })
	@Unlocked
	String callable_ajax_exception_handler2() {
		return "handler2";
	}
}
