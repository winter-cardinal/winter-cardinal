/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

const puppeteer = require( "puppeteer" );
const url = process.argv[ 2 ];
const NG_WORD = "7vhu64qkpun";
const EXIT_WORD_MATCHER = /^Finished\s\d+\stests\s\(\s[+-]?(\d+)?(\.\d+)?\sms\s\)$/;

// Remove proxy settings because the Chrome and the puppeteer itself uses a WebSocket.
[ "HTTP_PROXY", "HTTPS_PROXY", "FTP_PROXY" ]
.forEach( name => {
	delete process.env[ name ];
	delete process.env[ name.toLowerCase() ];
});

// Helper
var stop = ( browser, page, message, e ) => {
	if( e != null ) {
		if( message != null ) {
			console.error( message, e );
			console.error( NG_WORD+message, e );
		} else {
			console.error( e );
			console.error( NG_WORD, e );
		}
	} else {
		console.log( message );
	}
	if( browser != null ) {
		browser.close();
	}
};

//
puppeteer.launch()
.then( browser => {
	console.info( "Page", url );
	browser.newPage()
	.then( page => {
		page.on( "console", msg => {
			const text = msg.text();
			if( EXIT_WORD_MATCHER.test( text ) ) {
				stop( browser, page, text, null );
			} else {
				Promise.all(msg.args().map( arg => {
					return arg.executionContext().evaluate(obj => {
						if( typeof obj === "string" || obj instanceof String ) {
							return obj;
						} else {
							return JSON.stringify( obj );
						}
					}, arg);
				}))
				.then( results => {
					console.log( results.join( " " ) );
				})
				.catch( () => {
					// Fallback
					console.log( text );
				});
			}
		});

		page.on( "pageerror", e => {
			stop( browser, page, null, e );
		});

		page.goto( url )
		.catch( e => {
			stop( browser, page, "Failed to navigate to the page", url, e );
		});
	})
	.catch( e => {
		stop( browser, null, "Failed to create a new page", e );
	});
})
.catch( e => {
	stop( null, null, "Failed to launch", e );
});
