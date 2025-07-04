/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * When {@link org.wcardinal.controller.annotation.Callable @Callable} /
 * {@link org.wcardinal.controller.annotation.Task @Task} methods need to
 * return large set of data, it is preferable to stream those data instead
 * of consuming large heap memory. This class is for that purpose.
 * If {@link org.wcardinal.controller.annotation.Callable @Callable} /
 * {@link org.wcardinal.controller.annotation.Task @Task} methods return
 * {@link StreamingResult StreamingResult}, {@link #serialize(JsonGenerator)}
 * is called to serialize their return values. Therefore, all the data don't
 * need to be stored in the heap memory.
 *
 * Please note that if the methods are not annotated with
 * {@link org.wcardinal.controller.annotation.Ajax @Ajax}, the serialized data
 * still consume the heap memory, although the data themselve don't. Because of
 * this, the annotating methods with {@link org.wcardinal.controller.annotation.Ajax @Ajax}
 * is highly recommended if the serialized data are considered to be large.
 *
 * <blockquote><pre>
 * import org.wcardinal.controller.StreamingResult;
 * import org.wcardinal.controller.annotation.Callable;
 * import org.wcardinal.controller.annotation.Controller;
 *
 * &#64;Controller
 * class MyController {
 *   &#64;Ajax
 *   &#64;Callable
 *   StreamingResult callable() {
 *     return (generator) -&gt; {
 *       generator.writeStartArray();
 *       for (int i = 0; i &lt; 3; ++i) {
 *         generator.writeNumber(i);
 *       }
 *       generator.writeEndArray();
 *     };
 *   }
 * }</pre></blockquote>
 *
 * <h2>Thread Safety</h2>
 *
 * In the case that {@link org.wcardinal.controller.annotation.Callable @Callable} /
 * {@link org.wcardinal.controller.annotation.Task @Task} methods have a lock,
 * {@link #serialize(JsonGenerator)} gets called before releasing that lock.
 *
 * When {@link org.wcardinal.controller.annotation.Callable @Callable} /
 * {@link org.wcardinal.controller.annotation.Task @Task} methods don't have a lock,
 * a lock isn't aquired automatically before calling {@link #serialize(JsonGenerator)}.
 *
 * @see org.wcardinal.controller.annotation.Ajax
 * @see org.wcardinal.controller.annotation.Callable
 * @see org.wcardinal.controller.annotation.Task
 * @since 2.2.0
 */
public interface StreamingResult {
	/**
	 * Called to serialize a {@link org.wcardinal.controller.annotation.Callable @Callable} / {@link org.wcardinal.controller.annotation.Task @Task} method result.
	 *
	 * @param generator a JSON generator
	 * @throws IOException
	 */
	void serialize(JsonGenerator generator) throws IOException;
}
