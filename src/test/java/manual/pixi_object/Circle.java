/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.pixi_object;

public class Circle{
	public double x;
	public double y;
	public int color;
	public int radius;
	public Circle(){
		this.x = 0;
		this.y = 0;
		this.color = 0;
		this.radius = 2;
	}
	public Circle( final double x, final double y, final int color, final int radius ){
		this.x = x;
		this.y = y;
		this.color = color;
		this.radius = radius;
	}
}