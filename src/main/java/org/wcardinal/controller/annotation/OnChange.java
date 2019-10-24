/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wcardinal.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated methods are called when the specified fields get changed by browsers.
 * The types of the specified fields must be the ones in the package org.wcardinal.controller.data.
 *
 * <pre><code> {@literal @}Autowired
 * SString name;
 *
 * {@literal @}OnChange("name")
 * void onChange( String newValue, String oldValue ){
 *   // Invoked when the name field is changed by browsers.
 * }
 * </code></pre>
 *
 * <p>Events propagate to parents. To catch an event triggered by a child, use a name prefixed with a child name.
 *
 * <pre><code> {@literal @}Component
 * class MyComponent extends AbstractComponent {
 *   {@literal @}Autowired
 *   SString bar;
 * }
 *
 * {@literal @}Controller
 * class MyController {
 *   {@literal @}Autowired
 *   MyComponent foo;
 *
 *   {@literal @}OnChange("foo.bar")
 *   void onChange(){
 *     // Called when the 'bar' field of the child 'foo' is changed
 *   }
 * }
 * </code></pre>
 *
 * <p>The methods are invoked with two optional arguments.
 * Argument types and their meanings are different among field types as follows:
 *
 * <table>
 * <caption>OnChange argument types</caption>
 * <tr><th>Field type</th><th>Argument type</th><th>First argument</th><th>Second argument</th></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SBoolean}</td><td>{@code Boolean}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SInteger}</td><td>{@code Integer}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SLong}</td><td>{@code Long}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SFloat}</td><td>{@code Float}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SDouble}</td><td>{@code Double}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SString}</td><td>{@code String}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SClass}</td><td>{@code T}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SArrayNode}</td><td>{@code ArrayNode}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SObjectNode}</td><td>{@code ObjectNode}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SJsonNode}</td><td>{@code JsonNode}</td><td>new value</td><td>old value</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SList}</td><td>{@code SortedMap<Integer, T>} (sorted in ascending order of keys)</td><td>sorted map of inserted values and their index</td><td>sorted map of removed values and their index</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SMap}</td><td>{@code Map<String, T>}</td><td>map of inserted values and their keys</td><td>map of removed values and their keys</td></tr>
 * <tr><td>{@link org.wcardinal.controller.data.SROQueue}</td><td colspan="3">does not emit a change event</td></tr>
 * </table>
 *
 * <p>In case of {@link org.wcardinal.controller.data.SString}, arguments are, for instance, two strings
 * representing a new value and an old value as shown above.
 *
 * <p>Arguments are optional. All of the following are, thus, valid.
 *
 * <pre><code> {@literal @}OnChange("name")
 * static void onChange(){}
 *
 * {@literal @}OnChange("name")
 * static void onChange( String newValue ){}
 * </code></pre>
 *
 * <p>When a method is called, a controller owing the method is locked for preventing simultaneous modifications.
 * For instance, in the above example, when the 'onChange' method is called, the 'MyController' controller is locked.
 * The only exception to this is the case that a modified field belongs to a component annotated with {@link SharedComponent}
 * and an event handler method does not belong to the same component, because shared components and the other controllers do not share a lock.
 */
@Documented
@Decoratable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnChange {
	String[] value();
}
