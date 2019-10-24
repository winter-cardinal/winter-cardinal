/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodTypeCheckerForCallableExceptionHandler extends MethodTypeCheckerForTaskExceptionHandler {
	static final Logger logger = LoggerFactory.getLogger(MethodTypeCheckerForCallableExceptionHandler.class);
	public static MethodTypeChecker INSTANCE = new MethodTypeCheckerForCallableExceptionHandler();

	MethodTypeCheckerForCallableExceptionHandler(){}
}
