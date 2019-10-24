/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

(function(){
	"use strict";

	var util = wcardinal.util.util;

	var toElement = function( elementOrSelector ) {
		if( util.isString( elementOrSelector ) ) {
			return document.querySelector( elementOrSelector );
		} else {
			return elementOrSelector;
		}
	};

	var generateColorCode = function(){
		return parseInt(Math.random()  * Math.pow(16, 5));
	};

	var OBJECT_CONVERTERS = {
		get: function( controller, i ){
			return controller.circles.get( i );
		},

		set: function( controller, i, circle ) {
			controller.circles.set( i, circle );
		},

		toColor: function( circle ){
			return circle.color;
		},

		fromColor: function( circle, color ) {
			circle.color = color;
		}
	};

	var ARRAY_CONVERTERS = {
		get: function( controller, i ){
			return controller.circles.get( i );
		},

		set: function( controller, i, circle ) {
			controller.circles.set( i, circle );
		},

		toColor: function( circle ){
			return circle[ 2 ];
		},

		fromColor: function( circle, color ) {
			circle[ 2 ] = color;
		}
	};

	var RESET_OBJECT_CONVERTERS = {
		get: function( controller, i ){
			return controller.circles.reset( i );
		},

		set: function( controller, i, circle ) {},

		toColor: function( circle ){
			return circle.color;
		},

		fromColor: function( circle, color ) {
			circle.color = color;
		}
	};

	var RESET_ARRAY_CONVERTERS = {
		get: function( controller, i ){
			return controller.circles.reset( i );
		},

		set: function( controller, i, circle ) {},

		toColor: function( circle ){
			return circle[ 2 ];
		},

		fromColor: function( circle, color ) {
			circle[ 2 ] = color;
		}
	};

	var VANILLA_OBJECT_CONVERTERS = {
		get: function( controller, i ){
			return controller.circles[ i ];
		},

		set: function( controller, i, circle ) {},

		toColor: function( circle ){
			return circle.color;
		},

		fromColor: function( circle, color ) {
			circle.color = color;
		}
	};

	var VANILLA_ARRAY_CONVERTERS = {
		get: function( controller, i ){
			return controller.circles[ i ];
		},

		set: function( controller, i, circle ) {},

		toColor: function( circle ){
			return circle[ 2 ];
		},

		fromColor: function( circle, color ) {
			circle[ 2 ] = color;
		}
	};

	var toConverters = function( controller, toConverters ) {
		if( util.isArray( controller.circles ) ) {
			if( util.isEmpty( controller.circles ) ) {
				return VANILLA_OBJECT_CONVERTERS;
			} else {
				var circle = controller.circles[ 0 ];
				if( util.isArray( circle ) ) {
					return VANILLA_ARRAY_CONVERTERS;
				} else {
					return VANILLA_OBJECT_CONVERTERS;
				}
			}
		} else {
			if( wcardinal.controller.data.SList.prototype.reset != null ) {
				if( controller.circles.isEmpty() ) {
					return RESET_OBJECT_CONVERTERS;
				} else {
					var circle = controller.circles.get( 0 );
					if( util.isArray( circle ) ) {
						return RESET_ARRAY_CONVERTERS;
					} else {
						return RESET_OBJECT_CONVERTERS;
					}
				}
			} else {
				if( controller.circles.isEmpty() ) {
					return OBJECT_CONVERTERS;
				} else {
					var circle = controller.circles.get( 0 );
					if( util.isArray( circle ) ) {
						return ARRAY_CONVERTERS;
					} else {
						return OBJECT_CONVERTERS;
					}
				}
			}
		}
	};

	var nop = function(){};

	util.define( "app.util.CircleUpdater", [], {
		constructor: function( elementOrSelector, controller, converters ) {
			converters = util.merge( {}, toConverters( controller ), converters || {} );
			controller.lock = controller.lock || nop;
			controller.unlock = controller.unlock || nop;

			var timerResult = toElement( elementOrSelector );
			var timer = new app.util.Timer( "Update", function( name, min, max, took, count, mavg ){
				timerResult.innerText = "min " + min.toFixed(3) +" avg " + (took/count).toFixed(3) + " max " + max.toFixed(3) + " count " + count.toFixed(3) + " moving avg " + mavg.toFixed(3);
			});

			var doUpdate = function(){
				var w = controller.N_WIDTH;
				var h = controller.N_HEIGHT;
				var l = h * w;
				for( var i=0, imax=l/5; i<imax; ++i ){
					var index = parseInt( Math.random() * l);
					var circle = converters.get( controller, index );
					converters.fromColor( circle, generateColorCode() );
					converters.set( controller, index, circle );
				}
			};

			var update = function(){
				var now1 = util.now();

				timer.start();
				controller.lock();
				try{
					doUpdate();
				} finally {
					controller.unlock();
				}
				timer.end();

				var now2 = util.now();
				setTimeout(update, Math.max( 0, now1 + controller.INTERVAL - now2 ));
			};

			update();
		}
	});
}());
