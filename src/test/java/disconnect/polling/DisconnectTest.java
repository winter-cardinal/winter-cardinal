/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package disconnect.polling;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class DisconnectTest {
	@Test
	public void disconnect(){
		test("index.html");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/disconnect/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(DisconnectApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
