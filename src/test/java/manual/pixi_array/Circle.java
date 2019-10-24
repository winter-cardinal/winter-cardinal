/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package manual.pixi_array;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat( shape=Shape.ARRAY )
public class Circle {
	public double x;
	public double y;
	public int color;
	public int radius;

	@JsonCreator
	public Circle(
		@JsonProperty( "x" ) final double x,
		@JsonProperty( "y" ) final double y,
		@JsonProperty( "color" ) final int color,
		@JsonProperty( "radius" ) final int radius
	){
		this.x = x;
		this.y = y;
		this.color = color;
		this.radius = radius;
	}
}