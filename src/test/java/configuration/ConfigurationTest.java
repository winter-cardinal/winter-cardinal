/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package configuration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.PuppeteerTest;

public class ConfigurationTest {
	@Test
	public void configuration(){
	}

	@BeforeClass
	public static void before() {
		PuppeteerTest.start(ConfigurationApplication.class, getArgs());
	}

	@AfterClass
	public static void after() {
		PuppeteerTest.end();
	}

	public static String[] getArgs() {
		return new String[]{ "--wcardinal.thread.pool.size=2", "--wcardinal.controller.http=false" };
	}
}
