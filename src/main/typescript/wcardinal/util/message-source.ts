/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

import { format } from "./lang/format";
import { PlainObject } from "./lang/plain-object";

/**
 * Provides methods for retrieving messages.
 *
 * #### Step 1: Replace the MessageSource class
 *
 * To use this class, the MessageSource on a server must implement the interface
 * `org.wcardinal.util.message.ExposableMessageSource`. There are the two out-of-the-box
 * implementations of this interface: `ExposableReloadableResourceBundleMessageSource`
 * and `ExposableResourceBundleMessageSource` in the package `org.wcardinal.util.message`.
 *
 * ```java
 * // Java
 * import org.springframework.context.MessageSource;
 * import org.springframework.context.annotation.Bean;
 * import org.springframework.context.annotation.Configuration;
 * import org.wcardinal.util.message.ExposableReloadableResourceBundleMessageSource;
 *
 * @Configuration
 * public class MessageSourceConfig {
 *     @Bean
 *     public MessageSource messageSource(){
 *         final ExposableReloadableResourceBundleMessageSource result
 *             = new ExposableReloadableResourceBundleMessageSource();
 *         result.setBasename("classpath:/i18n/messages");
 *         result.setDefaultEncoding("UTF-8");
 *         return result;
 *     }
 * }
 * ```
 * &NewLine;
 * #### Step 2: Embed a script
 *
 * And then embed a message script obtained by `ExposableMessages#getScript` as follows:
 *
 * ```java
 * // Java
 * import org.wcardinal.util.message.ExposableMessages;
 *
 * @Controller
 * public class MessageMvcController {
 *     @Autowired
 *     ExposableMessages messages;
 *
 *     @RequestMapping( "/" )
 *     ModelAndView en( final HttpServletRequest req ) {
 *         final ModelAndView mav = new ModelAndView();
 *         mav.addObject( "messageScript", messages.getScript( Locale.ENGLISH ) );
 *         mav.setViewName("sample-view");
 *         return mav;
 *     }
 * }
 * ```
 * &NewLine;
 * ```html
 * <!-- HTML Template (sample-view.html) -->
 * <!-- Must be placed after the wcardinal script -->
 * <script th:utext="${ messageScript }"></script>
 * ```
 *
 * Please note that the message script must be loaded after the `wcardinal.min.js`.
 *
 * Now, translated messages can be obtained by calling `MessageSource#get` with message IDs.
 *
 * ```javascript
 * // JavaScript
 * messageSource.get( "foo.bar.1" );
 * ```
 *
 * For parameterized messages, pass parameters to the get method.
 *
 * ```javascript
 * // JavaScript
 * messageSource.get( "foo.bar.1", 123, "William" );
 * ```
 */
export class MessageSource {
	private static INSTANCE: MessageSource | null = null;
	source!: PlainObject<string>;

	/**
	 * Returns the message of the specified message ID.
	 *
	 * @method get
	 * @param id message ID
	 * @param parameters message parameters
	 * @returns message of the specified message ID
	 */
	get( id: string, ...parameter0: unknown[] ): string;
	get( id: string ): string {
		const translated = this.source[ id ];
		if( translated == null ) {
			return id;
		}
		const args = arguments as any as [ string, ...unknown[]];
		if (args.length <= 1) {
			return translated;
		}
		return format.apply( null, args );
	}

	static getInstance(): MessageSource {
		if( this.INSTANCE == null ) {
			this.INSTANCE = new MessageSource();
		}
		return this.INSTANCE;
	}
}

MessageSource.prototype.source = {};
