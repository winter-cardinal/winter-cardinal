/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.generics;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Page;
import org.wcardinal.controller.data.SClass;

@Page
public class GenericsPage<T> {
	@Autowired
	SClass<T> field_class;
}
