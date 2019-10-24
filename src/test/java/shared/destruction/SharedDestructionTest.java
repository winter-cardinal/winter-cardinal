/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package shared.destruction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class SharedDestructionTest {
	@Test
	public void test(){
		PuppeteerTest.test( "http://localhost:8080/shared/destruction/index.html" );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(SharedDestructionApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}
}
