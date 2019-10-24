/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

(function(){
	"use strict";

	var util = wcardinal.util.util;
	var logger = wcardinal.util.logger;

	var toElement = function( elementOrSelector ) {
		if( util.isString( elementOrSelector ) ) {
			return document.querySelector( elementOrSelector );
		} else {
			return elementOrSelector;
		}
	};

	var toAvg = function( values ){
		var result = 0;
		for( var i=0, imax=values.length; i<imax; ++i ) {
			result += values[ i ];
		}
		return result / values.length;
	};

	var HISTORY_SIZE = 60;

	var addHistory = function( target, value ) {
		var history = target._history;
		history.push( value );
		if( HISTORY_SIZE < history.length ) {
			history.shift();
			var result = toAvg( history );
			if( ! target._isPrinted ) {
				target._isPrinted = true;

				var newBody = document.implementation.createHTMLDocument("").body;
				newBody.innerHTML = '<p>'+target._name+' '+result+' ['+history.join( ", " )+']</p>';
				document.body.appendChild( newBody.children[ 0 ] );
			}
			return result;
		} else {
			return toAvg( history );
		}
	};

	util.define( "app.util.Counter", [], {
		constructor: function( name, onReport ){
			this._count = 0;
			this._total = 0;
			this._min = +Infinity;
			this._max = -Infinity;
			this._mavg = 0;
			this._history = [];
			this._isPrinted = false;
			this._previous = util.now();
			this._name = name;
			this._onReport = onReport || this.onReport;
		},

		count: function( count ){
			count = count || 1;
			this._count += 1;
			this._total += count;
			this._min = Math.min( count, this._min );
			this._max = Math.max( count, this._max );
			var now = util.now();
			var elapsed = now - this._previous;
			if( 1000 <= elapsed ) {
				this._mavg = addHistory( this, elapsed/this._count );
				this._onReport( this._name, elapsed, this._count, this._total, this._min, this._max, this._mavg );
				this._count = 0;
				this._total = 0;
				this._min = +Infinity;
				this._max = -Infinity;
				this._previous = now;
			}
		},

		onReport: function( name, elapsed, count, total, min, max, mavg ){
			logger.log( name, "interval", (elapsed/count).toFixed(3), "count", count, "min", min, "avg", (total/count).toFixed(3), "max", max, "total", total, "moving avg", mavg );
		},

		"static": {
			bind: function( elementOrSelector, controller ){
				var counterResult = toElement( elementOrSelector );
				var counter = new app.util.Counter( "Interval", function( name, elapsed, count, total, min, max, mavg ){
					counterResult.innerText = "interval " + (elapsed/count).toFixed(3) + " moving avg " + mavg.toFixed(3);
				});
				if( controller != null ) {
					controller.on( "change", function(){
						counter.count();
					});
				}
				return counter;
			}
		}
	});

	util.define( "app.util.Timer", [], {
		constructor: function( name, onReport ){
			this._count = 0;
			this._took = 0;
			this._max = -Infinity;
			this._min = +Infinity;
			this._mavg = 0;
			this._history = [];
			this._isPrinted = false;
			this._previous = util.now();
			this._name = name;
			this._onReport = onReport || this.onReport;
		},

		start: function(){
			this._start = util.now();
		},

		end: function() {
			var end = util.now();
			this.took( end - this._start );
		},

		took: function( took ){
			this._count += 1;
			this._took += took;
			this._max = Math.max( this._max, took );
			this._min = Math.min( this._min, took );
			var now = util.now();
			var elapsed = now - this._previous;
			if( 1000 <= elapsed ) {
				this._mavg = addHistory( this, this._took/this._count );
				this._onReport( this._name, this._min, this._max, this._took, this._count, this._mavg );
				this._count = 0;
				this._took = 0;
				this._max = -Infinity;
				this._min = +Infinity;
				this._previous = now;
			}
		},

		onReport: function( name, min, max, took, count, mavg ){
			logger.log( name, "min", min, "avg", took/count, "max", max, "count", count, "moving avg", mavg );
		}
	});
}());
