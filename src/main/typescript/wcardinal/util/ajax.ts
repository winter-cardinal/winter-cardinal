/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { hasOwn } from "./lang/has-own";
import { now } from "./lang/now";
import { PlainObject } from "./lang/plain-object";

export type AjaxSuccessHandler = ( responseTextOrJson: any, xhr: XMLHttpRequest ) => void;
export type AjaxErrorHandler = ( reason: string, xhr: XMLHttpRequest ) => void;
export type AjaxCompleteHandler = ( xhr: XMLHttpRequest ) => void;

export interface AjaxSettings {
	url: string;
	cache?: boolean;
	dataType?: "json";
	context?: unknown;
	success?: AjaxSuccessHandler;
	error?: AjaxErrorHandler;
	complete?: AjaxCompleteHandler;
	headers?: PlainObject<string | null | undefined>;
	timeout?: number;
	data?: BodyInit | null;
}

export class Ajax {
	private static INSTANCE: Ajax | null = null;
	private _n: number;

	constructor() {
		this._n = now();
	}

	send( method: string, settings: AjaxSettings ) {
		let isCompleted = false;

		const makeOnError = ( reason: string ) => {
			return () => {
				if( ! isCompleted ) {
					isCompleted = true;

					if( settings.error != null ) {
						try {
							settings.error.call( settings.context, reason, xhr );
						} catch( e ) {
							// DO NOTHING
						}
					}

					if( settings.complete != null ) {
						settings.complete.call( settings.context, xhr );
					}
				}
			};
		};

		// XHR
		const xhr = new XMLHttpRequest();

		// Open
		let url = settings.url;
		if( settings.cache === false ) {
			url += (0 <= url.lastIndexOf( "?" ) ? "&" : "?") + "_=" + (this._n++);
		}
		xhr.open( method, url, true );

		// Load
		xhr.onload = () => {
			if( ! isCompleted ) {
				isCompleted = true;

				const status = xhr.status;
				if( (200 <= status && status < 300) || status === 0 || status === 304 || status === 1223 ) {
					if( settings.success != null ) {
						if( settings.dataType === "json" ) {
							let json: unknown = null;
							try {
								json = JSON.parse( xhr.responseText );
							} catch( e ) {
								if( settings.error != null ) {
									try {
										settings.error.call( settings.context, "parseerror", xhr );
									} catch( e ) {
										// DO NOTHING
									}
								}

								json = null;
							}

							if( json != null ) {
								try {
									settings.success.call( settings.context, json, xhr );
								} catch( e ) {
									// DO NOTHING
								}
							}
						} else {
							try {
								settings.success.call( settings.context, xhr.responseText, xhr );
							} catch( e ) {
								// DO NOTHING
							}
						}
					}
				} else {
					if( settings.error != null ) {
						try {
							settings.error.call( settings.context, xhr.statusText, xhr );
						} catch( e ) {
							// DO NOTHING
						}
					}
				}

				if( settings.complete != null ) {
					settings.complete.call( settings.context, xhr );
				}
			}
		};

		// Error
		const onError = xhr.onerror = makeOnError( "error" );

		// Abort
		xhr.onabort = makeOnError( "abort" );

		// Headers
		const headers = settings.headers;
		if( headers != null ) {
			for( const name in headers ) {
				const header = headers[ name ];
				if( header != null ) {
					xhr.setRequestHeader( name, header );
				}
			}
		}
		xhr.setRequestHeader( "X-Requested-With", "XMLHttpRequest" );

		// Timeout
		if( settings.timeout != null ) {
			xhr.timeout = settings.timeout;
			xhr.ontimeout = makeOnError( "timeout" );
			if( 0 < settings.timeout ) {
				// For browsers which do not support the `timeout`
				self.setTimeout(() => {
					xhr.abort();
				}, settings.timeout );
			}
		}

		// Send
		try {
			if( settings.data != null ) {
				xhr.send( settings.data );
			} else {
				xhr.send();
			}
		} catch( e ) {
			onError();
		}

		return this;
	}

	get( settings: AjaxSettings ) {
		return this.send( "GET", settings );
	}

	post( settings: AjaxSettings ) {
		return this.send( "POST", settings );
	}

	delete( settings: AjaxSettings ) {
		return this.send( "DELETE", settings );
	}

	patch( settings: AjaxSettings ) {
		return this.send( "PATCH", settings );
	}

	encode( data: PlainObject ) {
		const parameters: string[] = [];
		const euc = encodeURIComponent;
		for( const key in data ) {
			if( hasOwn( data, key ) ) {
				parameters.push( `${euc( key )}=${euc( String(data[ key ]) )}` );
			}
		}
		return parameters.join( "&" );
	}

	// Instance
	static getInstance(): Ajax {
		if( this.INSTANCE == null ) {
			this.INSTANCE = new Ajax();
		}
		return this.INSTANCE;
	}
}
