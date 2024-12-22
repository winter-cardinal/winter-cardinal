/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package constructor_injection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class ConstructorInjectionTest {
	@Test
	public void index(){
		test("index.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/constructor-injection/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(ConstructorInjectionApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
