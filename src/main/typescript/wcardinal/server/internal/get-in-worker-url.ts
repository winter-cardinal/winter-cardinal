/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { LoggerImpl as Logger } from "../../util/internal/logger-impl";

const FILENAME_BODY = "wcardinal.in-worker";
const MIN_JS_EXTENSION = ".min.js";
const JS_EXTENSION = ".js";

export const getInWorkerUrl = (): string => {
	const scripts = document.getElementsByTagName( "script" );
	for( let i = scripts.length - 1; 0 <= i; --i ) {
		let src = scripts[ i ].getAttribute( "src" );
		if( src != null ) {
			// Rmove and store the query string part
			const queryStringIndex = src.indexOf( "?" );
			let queryString = "";
			if( 0 <= queryStringIndex ) {
				queryString = src.substring( queryStringIndex );
				src = src.substring( 0, queryStringIndex );
			}

			// Remove and store the file name part
			let fileNameString = "";
			const slashIndex = src.lastIndexOf( "/" );
			if( 0 <= slashIndex ) {
				fileNameString = src.substring( slashIndex + 1 );
				src = src.substring( 0, slashIndex + 1 );
			} else {
				fileNameString = src;
				src = "";
			}

			// Extension
			const extensionString = (
				0 <= fileNameString.indexOf( MIN_JS_EXTENSION ) ?
				MIN_JS_EXTENSION : JS_EXTENSION
			);

			return src + FILENAME_BODY + extensionString + queryString;
		}
	}

	Logger.getInstance().error(
		`Failed to auto-detect the WebWorker URL.` +
		`Falled back to the default "${FILENAME_BODY + MIN_JS_EXTENSION}".`
	);
	return FILENAME_BODY + MIN_JS_EXTENSION;
};
