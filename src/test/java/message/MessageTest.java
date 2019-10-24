/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package message;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class MessageTest {
	@Test
	public void en(){
		test("en");
	}

	@Test
	public void ja(){
		test("ja");
	}

	@Test
	public void enb(){
		test("en/b");
	}

	@Test
	public void jab(){
		test("ja/b");
	}

	public void test( final String path ){
		PuppeteerTest.test( "http://localhost:8080/message/"+path );
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(MessageApplication.class);
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

}
