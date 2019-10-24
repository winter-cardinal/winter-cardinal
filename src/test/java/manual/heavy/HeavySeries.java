/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.heavy;

import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.data.SROQueue;

public class HeavySeries {
	@Autowired
	SROQueue<Double[]> points;

	@OnCreate
	void onCreate(){
		points.clear();
		for( int i=0; i<22319; ++i ){
			points.add(new Double[]{ Math.random(), (double) System.currentTimeMillis() });
		}
	}
}
