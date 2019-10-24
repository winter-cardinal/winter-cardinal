/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.internal.info;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize( using=StaticInstanceInfoSerializer.class )
public class StaticInstanceInfo extends Info<Object, StaticInstanceInfo> {
	public Map<String, Object> constants;

	public StaticInstanceInfo(){
		this.constants = new HashMap<>();
	}
}
