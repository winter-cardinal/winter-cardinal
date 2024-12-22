/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package constructor_injection;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SInteger;
import org.wcardinal.controller.data.SList;

@Component
public class ConstructorInjectionComponent {
	public final SInteger scalar;
	public final SList<Integer> list;

	ConstructorInjectionComponent(final SInteger scalar, final SList<Integer> list) {
		this.scalar = scalar;
		this.list = list;
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
