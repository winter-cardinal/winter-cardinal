/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package basics;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;

import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SInteger;

@Controller
public class BasicsExecuteController extends AbstractController {
	//-----------------------------------------
	// EXECUTE
	//-----------------------------------------
	@Callable
	void start(){
		count.set(0);
		total.set(0);
		execute("a", 12);
	}

	@Callable
	void restart(){
		execute("a", 24);
	}

	@Autowired
	SInteger count;

	@Autowired
	SInteger total;

	@OnTime
	void a( final int number ){
		count.getAndIncrement();
		execute("b", number + 1);
	}

	@OnTime
	void b( final int number ){
		count.getAndIncrement();
		execute("c", number + 1);
	}

	@OnTime
	@Locked
	void c( final int number ){
		count.getAndIncrement();
		total.set(number);
	}

	//-----------------------------------------
	// EXECUTE RUNNABLE
	//-----------------------------------------
	@Callable
	void start_runnable(){
		execute( new Runnable(){
			@Override
			public void run() {
				count.incrementAndGet();
			}
		});
	}

	//-----------------------------------------
	// EXECUTE CALLABLE
	//-----------------------------------------
	@Callable
	void start_callable(){
		final Future<String> future = execute( new java.util.concurrent.Callable<String>(){
			@Override
			public String call() throws Exception {
				return "a";
			}
		});

		if( future.isCancelled() == true ) return;

		try {
			if( Objects.equal(future.get(), "a") != true ) return;
		} catch (final Exception e) {
			return;
		}

		if( future.isDone() != true ) return;
		count.incrementAndGet();
	}
}
