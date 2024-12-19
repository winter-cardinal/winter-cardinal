package org.wcardinal.controller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * When {@link org.wcardinal.controller.annotation.Callable @Callable} / {@link org.wcardinal.controller.annotation.Task @Task} methods need to return large set of data,
 * it is preferable to stream those data instead of consuming large heap memory. This class is for that purpose.
 * If {@link org.wcardinal.controller.annotation.Callable @Callable} / {@link org.wcardinal.controller.annotation.Task @Task} methods return {@link StreamingResult StreamingResult},
 * {@link #serialize(JsonGenerator)} is called to serialize their return value. Therefore, the data do not need to be stored in the heap memory.
 * Please note that the methods need to be annotated with {@link org.wcardinal.controller.annotation.Ajax @Ajax} as well.
 * Otherwise, the serialized data still consume the heap memory, although the data themselve don't.
 *
 * <pre>
 *    &#64;Component
 *    class MyComponent {
 *      &#64;Ajax
 *      &#64;Callable
 *      StreamingResult foo() {
 *        return (generator) -&gt; {
 *          generator.writeStartArray();
 *          for (int i = 0; i &lt; 10; ++i) {
 *            generator.writeNumber(i);
 *          }
 *          generator.writeEndArray();
 *        }
 *      }
 *    }
 * </pre>
 *
 * @see org.wcardinal.controller.annotation.Ajax
 * @see org.wcardinal.controller.annotation.Callable
 * @see org.wcardinal.controller.annotation.Task
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
