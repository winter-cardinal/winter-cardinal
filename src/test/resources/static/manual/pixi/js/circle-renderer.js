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

	function makeCircleTexture( radius ){
		var graphics = new PIXI.Graphics();
		graphics.beginFill( 0xffffff );
		graphics.drawCircle(0, 0, radius);
		graphics.endFill();
		return graphics.generateCanvasTexture();
	};

	var makeCircle = function( data, texture, converters ){
		var result = new PIXI.Sprite();
		result.position.x = converters.toX( data );
		result.position.y = converters.toY( data );
		result.texture = texture;
		result.tint = converters.toColor( data );
		return result;
	};

	var makeStage = function( controller, converters ) {
		var size = converters.size( controller );
		var result = new PIXI.particles.ParticleContainer( size, {position: true, tint: true} );
		var texture = makeCircleTexture( 1 );

		for( var i=0; i<size; ++i ) {
			var circle = converters.get( controller, i );
			result.addChild( makeCircle( circle, texture, converters ) );
		}
		return result;
	};

	var makeRenderer = function( elementOrSelector, controller ) {
		return PIXI.autoDetectRenderer(controller.WIDTH, controller.HEIGHT, {
			backgroundColor: 0xffffff,
			view: toElement( elementOrSelector )
		});
	};

	var OBJECT_CONVERTERS = {
		size: function( controller ) {
			return controller.circles.size();
		},

		get: function( controller, index ) {
			return controller.circles.get( index );
		},

		toX: function( data ){
			return data.x;
		},

		toY: function( data ){
			return data.y;
		},

		toColor: function( data ){
			return data.color;
		}
	};

	var ARRAY_CONVERTERS = {
		size: function( controller ) {
			return controller.circles.size();
		},

		get: function( controller, index ) {
			return controller.circles.get( index );
		},

		toX: function( data ){
			return data[ 0 ];
		},

		toY: function( data ){
			return data[ 1 ];
		},

		toColor: function( data ){
			return data[ 2 ];
		}
	};

	var VANILLA_OBJECT_CONVERTERS = {
		size: function( controller ) {
			return controller.circles.length;
		},

		get: function( controller, index ) {
			return controller.circles[ index ];
		},

		toX: function( data ){
			return data.x;
		},

		toY: function( data ){
			return data.y;
		},

		toColor: function( data ){
			return data.color;
		}
	};

	var VANILLA_ARRAY_CONVERTERS = {
		size: function( controller ) {
			return controller.circles.length;
		},

		get: function( controller, index ) {
			return controller.circles[ index ];
		},

		toX: function( data ){
			return data[ 0 ];
		},

		toY: function( data ){
			return data[ 1 ];
		},

		toColor: function( data ){
			return data[ 2 ];
		}
	};

	var toConverters = function( controller ){
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
	};

	util.define( "app.util.CircleRenderer", [], {
		constructor: function( elementOrSelector, controller, converters ){
			converters = util.merge( {}, toConverters( controller ), converters || {} );

			var renderer = this.renderer = makeRenderer( elementOrSelector, controller );
			var stage = this.stage = makeStage( controller, converters );

			if( util.isFunction( controller.circles.on ) ) {
				controller.circles.on( "change", function( e, added, removed, updated ){
					// Updated
					for( var i=0, imax=updated.length; i<imax; ++i ) {
						var update = updated[ i ];
						stage.getChildAt( update.index ).tint = converters.toColor( update.newValue );
					}

					// Render
					renderer.render( stage );
				});
			}
		},

		render: function(){
			this.renderer.render( this.stage );
		},

		getRenderer: function(){
			return this.renderer;
		},

		getStage: function(){
			return this.stage;
		},

		"static": {
			OBJECT_CONVERTERS: OBJECT_CONVERTERS,
			ARRAY_CONVERTERS: ARRAY_CONVERTERS
		}
	});
}());
