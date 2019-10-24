/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package variable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class VariableTest {
	@Test
	public void variable(){
		test("variable");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(VariableApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
