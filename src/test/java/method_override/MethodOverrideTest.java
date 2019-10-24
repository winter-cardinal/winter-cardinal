/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package method_override;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class MethodOverrideTest {
	@Test
	public void index(){
		test("index.html");
	}

	public void test( final String path ) {
		PuppeteerTest.test( "http://localhost:8080/method-override/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(MethodOverrideApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
