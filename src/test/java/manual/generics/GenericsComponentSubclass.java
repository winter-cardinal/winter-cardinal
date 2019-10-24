/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.generics;

import org.wcardinal.controller.annotation.Component;

@Component
public class GenericsComponentSubclass<T> extends GenericsComponentSuper<T, Integer> {
}
