/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

(function(){
	"use strict";

	const util = wcardinal.util.util;

	const getNow = ( (typeof performance !== "undefined" && typeof performance.now !== "undefined") ? function(){
		return performance.now();
	} : util.now );

	const toElapsed = function( endTime, startTime ) {
		return Math.max( 0, endTime - startTime );
	};

	const getElapsed = function( startTime ) {
		return toElapsed( getNow(), startTime );
	};

	// Callback class
	class Callback {
		constructor(){
			this._done = false;
			this._stime = 0;
		}

		taking(){
			return getElapsed( this._stime );
		}

		took(){
			return toElapsed( this._etime, this._stime );
		}

		success(){
			console.log( this._name + " ( "+this.took()+" ms )" );
			return this;
		}

		fail( data ){
			this._done = true;
			this._etime = getNow();
			throw new Error( this._name + ": " + data );
		}

		assert( result, data ){
			if( result !== true ) {
				this.fail( data );
			}
			return this;
		}

		timeout( limit ){
			return this.fail( "Excess the limit " + limit + " ms" );
		}

		within( limit, func, interval ){
			interval = interval || 100;
			var stime = getNow();
			var loopBound = util.bind(function(){
				func();
				if( this._done === true ) {
					return;
				}

				if( limit < getElapsed( stime ) ) {
					this.timeout( limit );
					return;
				}

				setTimeout( loopBound, 100 );
			},this);
			loopBound();
			return this;
		}

		assertEquals( value, expected ){
			return this.assert( util.isEqual(value, expected), "Expected "+ expected + " but " + value );
		}

		assertNotEquals( value, notExpected ){
			return this.assert( ! util.isEqual(value, notExpected), "Not expecting " + notExpected );
		}

		assertException( func, thisArg ){
			var exception = null;
			try {
				func.call( thisArg );
			} catch( e ){
				exception = e;
			}
			return this.assert( exception != null, "Expected an exception" );
		}

		assertNotException( value, expected ){
			var exception = null;
			try {
				func.call( thisArg );
			} catch( e ){
				exception = e;
			}
			return this.assert( exception == null, "Not expecting an exception "+exception );
		}

		done(){
			this._done = true;
			this._etime = getNow();
			return this;
		}

		reset( name ){
			this._name = name;
			this._done = false;
			this._stime = getNow();
			return this;
		}
	}

	// Tests class
	class Tests {
		constructor(){
			this._tests = [];
		}

		test( name, func, timeout ) {
			timeout = (timeout == null ? 20000 : timeout);

			if( util.isString( name ) ) {
				this._tests.push({
					name: name,
					func: func,
					timeout: timeout
				});
			} else {
				var makeTestFunction = function( c, methodName ){
					return function( cb ){
						c[ methodName ]()
						.then(function( result ){
							if( result ) {
								cb.done();
							} else {
								cb.fail( result );
							}
						}, function( reason ){
							cb.fail( reason );
						});
					};
				};

				var controller = name;
				if( util.isString( func ) ) {
					name = func;
					this._tests.push({
						name: "Method "+name,
						func: makeTestFunction( controller, name ),
						timeout: timeout
					});
				} else {
					var names = func;
					for( var i=0, imax=names.length; i<imax; ++i ) {
						name = names[ i ];
						this._tests.push({
							name: "Method "+name,
							func: makeTestFunction( controller, name ),
							timeout: timeout
						});
					}
				}
			}
			return this;
		}

		execute(){
			var i = -1;
			var callback = new Callback();
			var tests = this._tests;
			var startTime = getNow();
			var loop = function(){
				if( i === -1 || callback._done ){
					if( callback._done ) {
						callback.success();
					}

					i += 1;
					if( i < tests.length ) {
						callback.reset( tests[ i ].name );
						tests[ i ].func( callback );
					}
				}

				if( i < tests.length ) {
					var timeout = tests[ i ].timeout;
					if( callback.taking() < timeout ) {
						setTimeout( loop, 5 );
					} else {
						callback.timeout( timeout );
					}
				} else {
					console.log( "Finished "+tests.length+" tests ( "+getElapsed( startTime )+" ms )" );
				}
			};
			loop();
		}
	}

	// Puppeteer class
	class Puppeteer {
		test( name, func, timeout ){
			return new Tests().test( name, func, timeout );
		}
	}

	// Patch
	window.Puppeteer = Puppeteer;
	window.puppeteer = new Puppeteer();
}());
