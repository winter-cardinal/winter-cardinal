/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package ajax;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class AjaxTest {
	@Test
	public void index(){
		test("index.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/ajax/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(AjaxApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
