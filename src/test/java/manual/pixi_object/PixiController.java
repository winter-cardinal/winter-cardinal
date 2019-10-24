/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.pixi_object;

import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Constant;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Locked;
import org.wcardinal.controller.annotation.OnTime;
import org.wcardinal.controller.data.SList;
import org.wcardinal.util.measure.Timers;
import org.wcardinal.controller.AbstractController;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
class PixiController extends AbstractController{

	@Constant
	static final int HEIGHT = 250;

	@Constant
	static final int WIDTH = 250;

	@Constant
	static final int N_HEIGHT = 173;

	@Constant
	static final int N_WIDTH = 173;

	@Constant
	static final int INTERVAL = 50;

	@Autowired
	SList<Circle> circles;

	@Callable
	void init(){
		double dh = ((double)WIDTH) / N_WIDTH;
		double dw = ((double)WIDTH) / N_WIDTH;
		for( int ih = 0; ih < N_HEIGHT; ++ih ) {
			for( int iw = 0; iw < N_WIDTH; ++iw ) {
				circles.add(new Circle( iw * dw, ih * dh, 0, 1 ));
			}
		}
	}

	private int generateColorCode(){
		return (int) Math.round(Math.random() * Math.pow(16, 5));
	}

	@Callable
	void start(){
		timeout( "update", 0 );
	}

	@OnTime
	@Locked
	private void update(){
		final long now1 = System.currentTimeMillis();
		Timers.start("Update");
		final int w = N_WIDTH;
		final int h = N_HEIGHT;
		final int l = h * w;
		for( int i=0, imax=l/5; i < imax; ++i ){
			final int index = (int)(Math.random() * l);
			final Circle circle = circles.get( index );
			circle.color = generateColorCode();
			circles.set( index, circle );
		}
		Timers.end("Update");
		final long now2 = System.currentTimeMillis();
		timeout( "update", Math.max( 0, now1 + INTERVAL - now2 ) );
	}
}
