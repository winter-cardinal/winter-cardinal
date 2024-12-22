/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package constructor_injection;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.SList;

@Controller
public class ConstructorInjectionController {
	public final SInteger scalar;
	public final SList<Integer> list;
	public final ConstructorInjectionComponent component;

	ConstructorInjectionController(final SInteger scalar, final SList<Integer> list, final ConstructorInjectionComponent component) {
		this.scalar = scalar;
		this.list = list;
		this.component = component;
	}

	@OnCreate
	void init() {
		scalar.set(42);
		list.add(42);
	}

	@Callable
	Integer getScalar() {
		return scalar.get();
	}

	@Callable
	Integer getList(int index) {
		return list.get(index);
	}
}
