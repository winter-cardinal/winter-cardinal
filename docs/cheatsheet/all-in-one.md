# Cheatsheet

## Table of contents

* [Getting Started](#getting-started)
	* [Step 1: Add Compile Dependency](#step-1-add-compile-dependency)
	* [Step 2: Create a Controller Class](#step-2-create-a-controller-class)
	* [Step 3: Load Scripts](#step-3-load-scripts)
	* [Step 4: Load a Controller Script](#step-4-load-a-controller-script)
	* [Step 5: Run and Check With Browsers](#step-5-run-and-check-with-browsers)
* [Controller](#controller)
	* [Controller Basics](#controller-basics)
	* [Controller Inheritance](#controller-inheritance)
	* [Controller URL](#controller-url)
	* [Controller Name](#controller-name)
	* [Controller-Scoped Service](#controller-scoped-service)
	* [Controller Locales](#controller-locales)
	* [Controller Parameters](#controller-parameters)
	* [Controller Attributes](#controller-attributes)
	* [Controller Creation/Destruction Handling](#controller-creationdestruction-handling)
	* [Controller Post-Creation Handling](#controller-post-creation-handling)
	* [Network Protocols](#network-protocols)
	* [Network Reconnection](#network-reconnection)
	* [Keep Alive](#keep-alive)
	* [Title Separators](#title-separators)
	* [Title Separators (I18N)](#title-separators-i18n)
* [Field](#field)
	* [Accessing Fields From JavaScript](#accessing-fields-from-javascript)
	* [Detecting Field Changes (JavaScript)](#detecting-field-changes-javascript)
	* [Detecting Field Changes (Java)](#detecting-field-changes-java)
	* [Read-only fields](#read-only-fields)
	* [Non-null fields](#non-null-fields)
	* [Uninitialized fields](#uninitialized-fields)
	* [Freeing Fields When a Synchronization is Finished](#freeing-fields-when-a-synchronization-is-finished)
	* [Constants](#constants)
	* [Controlling field synchronization timing (JavaScript)](#controlling-field-synchronization-timing-javascript)
	* [Controlling Field Synchronization Timing (Java)](#controlling-field-synchronization-timing-java)
* [Method](#method)
	* [Calling Methods From JavaScript](#calling-methods-from-javascript)
	* [Type Declaration of Callable Methods for TypeScript](#type-declaration-of-callable-methods-for-typescript)
	* [Adjusting Method Timeout (Pattern 1)](#adjusting-method-timeout-pattern-1)
	* [Adjusting Method Timeout (Pattern 2)](#adjusting-method-timeout-pattern-2)
	* [Adjusting Method Timeout (Pattern 3)](#adjusting-method-timeout-pattern-3)
	* [Calling Methods From JavaScript via Ajax (Pattern 1)](#calling-methods-from-javascript-via-ajax-pattern-1)
	* [Calling Methods From JavaScript via Ajax (Pattern 2)](#calling-methods-from-javascript-via-ajax-pattern-2)
	* [Calling @Ajax-Annotated Methods From JavaScript via WebSocket](#calling-ajax-annotated-methods-from-javascript-via-websocket)
	* [Streaming Large Set of Data](#streaming-large-set-of-data)
	* [Method Exception Handling](#method-exception-handling)
* [Task](#task)
	* [Introduction](#introduction)
	* [Task Basics](#task-basics)
	* [Type Declaration of Task Methods for TypeScript](#type-declaration-of-task-methods-for-typescript)
	* [Aborting a Task](#aborting-a-task)
	* [Failing a Task](#failing-a-task)
	* [Retrieving Task Arguments](#retrieving-task-arguments)
	* [Retrieving a Task Result](#retrieving-a-task-result)
	* [Retrieving a Reason Why Failed](#retrieving-a-reason-why-failed)
	* [Check Whether a Task Is Finished](#check-whether-a-task-is-finished)
	* [Check Whether a Task Is Finished Successfully](#check-whether-a-task-is-finished-successfully)
	* [Check Whether a Task Is Finished Unsuccessfully](#check-whether-a-task-is-finished-unsuccessfully)
	* [Task Exception Handling](#task-exception-handling)
* [Event](#event)
	* [Trigger Events (Server to Browser)](#trigger-events-server-to-browser)
	* [Trigger Events (Server to Server)](#trigger-events-server-to-server)
	* [Retrieving Returned Values of Event Handlers (Server to Browser)](#retrieving-returned-values-of-event-handlers-server-to-browser)
* [Component](#component)
	* [Introduction](#introduction)
	* [Component Basics](#component-basics)
	* [Retrieving Parent (JavaScript)](#retrieving-parent-javascript)
	* [Retrieving Parent (Java)](#retrieving-parent-java)
	* [Component Lifecycle Handling](#component-lifecycle-handling)
	* [Creating Components Dynamically (JavaScript)](#creating-components-dynamically-javascript)
	* [Creating Components Dynamically (Java)](#creating-components-dynamically-java)
	* [Creating Components Dynamically With Parameters (JavaScript)](#creating-components-dynamically-with-parameters-javascript)
	* [Creating Components Dynamically With Parameters (Java)](#creating-components-dynamically-with-parameters-java)
	* [Destroying Dynamic Components (JavaScript)](#destroying-dynamic-components-javascript)
	* [Destroying Dynamic Components (Java)](#destroying-dynamic-components-java)
	* [Sharing Components Among Browsers](#sharing-components-among-browsers)
	* [Shared Component Lifecycle Handling](#shared-component-lifecycle-handling)
* [Page](#page)
	* [Introduction](#introduction)
	* [Page Basics](#page-basics)
	* [Showing/Hiding Pages (JavaScript)](#showinghiding-pages-javascript)
	* [Showing/Hiding Pages (Java)](#showinghiding-pages-java)
	* [Retrieving Active Page (JavaScript)](#retrieving-active-page-javascript)
	* [Retrieving Active Page (Java)](#retrieving-active-page-java)
	* [Checking Whether a Page is Shown/Hidden (JavaScript)](#checking-whether-a-page-is-shownhidden-javascript)
	* [Checking Whether a Page is Shown/Hidden (Java)](#checking-whether-a-page-is-shownhidden-java)
	* [Retrieving Parent (JavaScript)](#retrieving-parent-javascript)
	* [Retrieving Parent (Java)](#retrieving-parent-java)
	* [Primary Page](#primary-page)
	* [No Primary Page](#no-primary-page)
	* [Detecting Page Change (JavaScript)](#detecting-page-change-javascript)
	* [Detecting Page Change (Java)](#detecting-page-change-java)
	* [Page Lifecycle Handling](#page-lifecycle-handling)
	* [Page Name in Title](#page-name-in-title)
	* [Page Name in Title (I18N)](#page-name-in-title-i18n)
	* [Creating Pages Dynamically (JavaScript)](#creating-pages-dynamically-javascript)
	* [Creating Pages Dynamically (Java)](#creating-pages-dynamically-java)
	* [Creating Pages Dynamically With Parameters (JavaScript)](#creating-pages-dynamically-with-parameters-javascript)
	* [Creating Pages Dynamically With Parameters (Java)](#creating-pages-dynamically-with-parameters-java)
	* [Destroying Dynamic Pages (JavaScript)](#destroying-dynamic-pages-javascript)
	* [Destroying Dynamic Pages (Java)](#destroying-dynamic-pages-java)
* [Popup](#popup)
	* [Introduction](#introduction)
	* [Popup Basics](#popup-basics)
	* [Showing/Hiding Popups (JavaScript)](#showinghiding-popups-javascript)
	* [Showing/Hiding Popups (Java)](#showinghiding-popups-java)
	* [Checking Whether a Popup is Shown/Hidden (JavaScript)](#checking-whether-a-popup-is-shownhidden-javascript)
	* [Checking Whether a Popup is Shown/Hidden (Java)](#checking-whether-a-popup-is-shownhidden-java)
	* [Retrieving Parent (JavaScript)](#retrieving-parent-javascript)
	* [Retrieving Parent (Java)](#retrieving-parent-java)
	* [Primary Popup](#primary-popup)
	* [Detecting Popup Visibility Change (JavaScript)](#detecting-popup-visibility-change-javascript)
	* [Detecting Popup Visibility Change (Java)](#detecting-popup-visibility-change-java)
	* [Popup Lifecycle Handling](#popup-lifecycle-handling)
	* [Popup Name in Title](#popup-name-in-title)
	* [Popup Name in Title (I18N)](#popup-name-in-title-i18n)
	* [Creating Popups Dynamically (JavaScript)](#creating-popups-dynamically-javascript)
	* [Creating Popups Dynamically (Java)](#creating-popups-dynamically-java)
	* [Creating Popups Dynamically With Parameters (JavaScript)](#creating-popups-dynamically-with-parameters-javascript)
	* [Creating Popups Dynamically With Parameters (Java)](#creating-popups-dynamically-with-parameters-java)
	* [Destroying Dynamic Popups (JavaScript)](#destroying-dynamic-popups-javascript)
	* [Destroying Dynamic Popups (Java)](#destroying-dynamic-popups-java)
* [Local controller](#local-controller)
	* [Introduction](#introduction)
	* [Local Controller Basics](#local-controller-basics)
	* [Non-Null Local Controller Field](#non-null-local-controller-field)
	* [Uninitialized Local Controller Field](#uninitialized-local-controller-field)
	* [Local Controller Fields With Default Values](#local-controller-fields-with-default-values)
	* [@Callable Methods](#callable-methods)
	* [Asynchronous @Callable Methods](#asynchronous-callable-methods)
	* [Failing @Callable Methods](#failing-callable-methods)
	* [@Task Methods](#task-methods)
	* [Asynchronous @Task Methods](#asynchronous-task-methods)
	* [Failing @Task Methods](#failing-task-methods)
* [Threading](#threading)
	* [Introduction](#introduction)
	* [Locking Controller](#locking-controller)
	* [Periodic Method Calls](#periodic-method-calls)
	* [Periodic Method Calls With Parameters](#periodic-method-calls-with-parameters)
	* [Periodic Executions of Runnables](#periodic-executions-of-runnables)
	* [Canceling Periodic Calls From the Outside](#canceling-periodic-calls-from-the-outside)
	* [Canceling Periodic Calls From the Inside](#canceling-periodic-calls-from-the-inside)
	* [One-time Method Calls With a Delay](#one-time-method-calls-with-a-delay)
	* [One-time Method Calls With a Delay and Parameters](#one-time-method-calls-with-a-delay-and-parameters)
	* [One-time Execution of Runnables With a Delay](#one-time-execution-of-runnables-with-a-delay)
	* [One-time Execution of Callables With a Delay](#one-time-execution-of-callables-with-a-delay)
	* [Canceling One-Time Method Calls](#canceling-one-time-method-calls)
	* [One-time Method Calls Without a Delay](#one-time-method-calls-without-a-delay)
	* [One-time Method Calls Without a Delay and Parameters](#one-time-method-calls-without-a-delay-and-parameters)
	* [Canceling All Concurrent Requests](#canceling-all-concurrent-requests)
	* [Enabling WebWorker](#enabling-webworker)
* [Logging](#logging)
	* [Logging (JavaScript)](#logging-javascript)
	* [Logging (Java)](#logging-java)
	* [Log Level (JavaScript)](#log-level-javascript)
	* [Log Level (Java)](#log-level-java)
* [I18N](#i18n)
	* [I18N (JavaScript)](#i18n-javascript)
	* [I18N (Java)](#i18n-java)
* [Security](#security)
	* [Switching Controllers Based on Roles](#switching-controllers-based-on-roles)
	* [Switching Controllers by Custom Logics](#switching-controllers-by-custom-logics)
	* [Retrieving User Principal](#retrieving-user-principal)
	* [Retrieving Remote Address](#retrieving-remote-address)
	* [Retrieving HttpServletRequest](#retrieving-httpservletrequest)
	* [Retrieving HttpServletRequest and Customizing Controller Attributes](#retrieving-httpservletrequest-and-customizing-controller-attributes)
* [Configuration](#configuration)
	* [Boot-time Configuration](#boot-time-configuration)

## Getting Started

### Step 1: Add Compile Dependency

```gradle
repositories {
	mavenCentral()
}

dependencies {
	compile 'com.github.winter-cardinal:winter-cardinal:latest.release'
}
```

### Step 2: Create a Controller Class

```java
import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.data.SLong;

@Controller
class MyController {
	@Autowired
	SLong field;

	@Callable
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}
```

Please check whether Spring does scan your controller class.
Detecting Spring your controller class, working wcardinal successfully, in a log you find:

```text
2019-10-28 22:27:52.875  INFO 19517 --- [ost-startStop-1] d.c.internal.ControllerServletLoader     : Mapping controller: 'MyController' to [/my-controller] with roles [] as 'MyController'
```

### Step 3: Load Scripts

```html
<script src="webjars/wcardinal/wcardinal.worker.min.js"></script>
```

There is an non-worker version called `webjars/wcardinal/wcardinal.min.js`.
The worker version `wcardinal.worker.min.js` is strongly recommended over this non-worker version as the `setTimeout` is not reliable in some cases.

### Step 4: Load a Controller Script

```html
<script src="my-controller"></script>
```

The default URL is a kebab-case of a controller name.

### Step 5: Run and Check With Browsers

```shell
cd your/project/directory
./gradlew bootRun
```

Then open `http://localhost:8080/` with a browser.
Please find a `MyController` clone at `window.myController`.

```javascript
console.log(myController.field.get()); // Prints 64
console.log(await myController.hello("Cardinal")); // Prints "Hello, Cardinal!"
```

## Controller

### Controller Basics

```java
@Controller
class MyController {

}
```

```html
<script src="my-controller"></script>
<script>
	// The MyController instance is available at window.myController.
	console.log( window.myController );
</script>
```

Please note that the default URL is the kebab-case of the class name, `my-controller`.

### Controller Inheritance

```java
class MySuperController {
	@Callable
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}

@Controller
class MyController extends MySuperController {

}
```

```javascript
console.log(await myController.hello("Cardinal")); // Prints "Hello, Cardinal!"
```

### Controller URL

```java
@Controller("/my-controller-url")
class MyController {

}
```

or

```java
@Controller(urls="/my-controller-url")
class MyController {

}
```

```html
<script src="my-controller-url"></script>
```

Please refer to [org.wcardinal.controller.annotation.Controller](../api/java/org/wcardinal/controller/annotation/Controller.html).

### Controller Name

```java
@Controller(name="MyControllerName")
class MyController {

}
```

```html
<script src="my-controller-name"></script>
```

### Controller-Scoped Service

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ControllerScopeService;

@ControllerScopeService
class MyControllerScopeService {

}

@Controller
class MyController {
	@Autowired
	MyControllerScopeService service;
}
```

Controller-scoped services are instantiated per controller instances.
Thus, the `service` and `component.service` are the same instance of `MyControllerScopeService`:

```java
@Component
class MyComponent {
	@Autowired
	MyControllerScopeService service;
}

@Controller
class MyController {
   @Autowired
   MyControllerScopeService service;

   @Autorired
   MyComponent component;
}
```

### Controller Locales

```java
@Controller
class MyController extends AbstractController {
	void foo() {
		System.out.println(getLocale());
	}
}
```

or

```java
@Controller
class MyController {
	@Autowired
	ControllerFacade facade;

	void foo() {
		System.out.println(facade.getLocale());
	}
}
```

### Controller Parameters

```html
<script src="my-controller?name=Cardinal"></script>
```

```Java
import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		log.info(getParameter("name")); // Prints "Cardinal"
	}
}
```

or

```Java
import org.springframework.beans.factory.annotation.Autowired;
import org.wcardinal.controller.ControllerFacade;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
class MyController {
	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init() {
		log.info(facade.getParameter("name")); // Prints "Cardinal"
	}
}
```

Please refer to [org.wcardinal.controller.ControllerFacade](../api/java/org/wcardinal/controller/ControllerFacade.html).

### Controller Attributes

```java
@Controller
class MyController extends AbstractController {
	@OnRequest
	static void onRequest(HttpServletRequest request, ControllerAttributes attributes){
		attributes.put("name", "Cardinal");
	}

	void something() {
		System.out.println(getAttributes().get("name")); // Prints "Cardinal"
	}
}
```

or

```java
@Controller
class MyController {
	@Autowired
	ControllerFacade facade;

	@OnRequest
	static void onRequest(HttpServletRequest request, ControllerAttributes attributes) {
		attributes.put("name", "Cardinal");
	}

	void something() {
		System.out.println(facade.getAttributes().get("name")); // Prints "Cardinal"
	}
}
```

### Controller Creation/Destruction Handling

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Controller
class MyController {
	@OnCreate
	void init() {
		// Called after instantiated.
	}

	@OnDestroy
	void destroy() {
		// Called before getting destroyed.
	}
}
```

Field modifications made in the `@OnCreate` methods are sent to browsers as a part of HTTP responses.
If this behavior is not desirable, please use the `@OnPostCreate` instead.

### Controller Post-Creation Handling

Methods annotated with `@OnPostCreate` are called immediately after `@OnCreate` methods.
In contrast to the `@OnCreate`, no changes made in `@OnPostCreate` methods are sent as a part of HTTP responses.

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnPostCreate;

@Controller
class MyController {
	@OnPostCreate
	void init() {
		// Called after @OnCreate methods.
	}
}
```

### Network Protocols

```java
// Allows `WebSocket` and `Long polling`
@Controller(protocols={ "websocket", "polling" })
class MyController {

}

// Allows `WebSocket` only
@Controller(protocols="websocket")
class MyController {

}
```

### Network Reconnection

```java
@Controller(retry = @Retry(delay = 5000, interval = 15000))
class MyController {

}
```

In the above settings, wcardinal trys to reconnect every 15 seconds, starting after 5 seconds, when a connection is lost.

### Keep Alive

```java
@Controller(keepAlive = @KeepAlive(
	// Sets a HTTP session keep-alive interval to 240 seconds.
	interval=240000,

	// Sets a ping interval to 15 seconds.
	ping=15000
))
class MyController {

}
```

* `KeepAlive#interval`

   An interval of a **HTTP** keep-alive message to keep **a HTTP session**.

* `KeepAlive#ping`

   An interval of a **non-HTTP** (i.e., WebSocket) keep-alive message to keep **a sub session**.
The sub session is quite similar to the familiar session.
However, the sub session is not shared across browser tabs of the same URL.

### Title Separators

```java
@Controller(separators={" - ", " / "})
class MyController {

}
```

### Title Separators (I18N)

```java
@Controller(separatorMessages={"title.separator.root", "title.separator.leaf"})
class MyController {

}
```

Add the fllowings to your `messages_en.properties`.

```
title.separator.root= -
title.separator.leaf= /
```

## Field

### Accessing Fields From JavaScript

```java
@Controller
class MyController{
	@Autowired
	SLong time;
}
```

```javascript
// Retrieves `time` value.
myController.time.get();

// Changes `time` value.
myController.time.set( 128 );
```

See JavaScript API Reference for available methods on a browser side.
For instance, for `SLong` class, refer to [controller/data/SLong](../api/js/classes/controller_data.slong.html).

Please note that browsers can see the fields of types in the package `controller/data` only:

```java
class MyController{
	@Autowired
	SLong visible;	// Visible from JavaScript

	Long invisible;	// Invisible from JavaScript
}
```

`List` and `Map` types are also available:

```java
class MyController{
	@Autowired
	SList<Long> times;
}
```

* `SList<T>`
* `SMovableList<T>`
* `SMap<T>`
* `SNavigableMap<T>`
* `SQueue<T>`
* `SROQueue<T>`
* `SArrayNode`
* `SJsonNode`
* `SObjectNode`

Any classes can be used as a generic parameter:

```java
class Series {
	...
}

class MyController {
	@Autowired
	SList<Series> series;
}
```
However, the `Series` class must be serializable to/deserializable from JSON by Jackson.
Namely, all fields in the `Series` class must have getter/setter methods:

```java
class Series {
	private long time;

	long getTime(){
		return time;
	}

	void setTime(long time) {
		this.time = time;
	}
}
```

or be public:

```java
class Series {
	public long time;
}
```

Please refer to [Jackson document](http://wiki.fasterxml.com/JacksonHome) for details.

The key type of `SMap<T>`、`SNavigableMap<T>`, `SObjectNode` is `String`.
This limitation comes from the JavaScript's `Object` and the JSON specifications.

### Detecting Field Changes (JavaScript)

```java
@Controller
class MyController{
	@Autowired
	SLong time;
}
```

```javascript
myController.time.on("value", (e, newValue, oldValue) => {
	// Variables `newValue` and `oldValue` are
	// a new value and an old value of the `time`, respectively.
});

// Or use `value` event on the controller to detect the change of the `time` field.
myController.on("value", () => {
	console.log(controller.time.get());
});
```

Refer to [controller/data/SLong#value](../api/js/classes/controller_data.slong.html#value),
and [controller/Controller#value](../api/js/interfaces/controller.controller-1.html#value).

The arguments of the `value` event varies by types:

* `SBoolean`
	+ `wcardinal.event.Event` event: Event object
	+ `Boolean` newValue: New value
	+ `Boolean` oldValue: Old value
* `SInteger`, `SLong`, `SFloat`, or `SDouble`
	+ `wcardinal.event.Event` event: Event object
	+ `Number` newValue: New value
	+ `Number` oldValue: Old value
* `SString`
	+ `wcardinal.event.Event` event: Event object
	+ `String` newValue: New value
	+ `String` oldValue: Old value
* `SClass<T>`
	+ `wcardinal.event.Event` event: Event object
	+ `T` newValue: New value
	+ `T` oldValue: Old value
* `SArrayNode`
	+ `wcardinal.event.Event` event: Event object
	+ `Array` newValue: New value
	+ `Array` oldValue: Old value
* `SObjectNode`
	+ `wcardinal.event.Event` event: Event object
	+ `Object` newValue: New value
	+ `Object` oldValue: Old value
* `SJsonNode`
	+ `wcardinal.event.Event` event: Event object
	+ `Object|Array|String|Number` newValue: New value
	+ `Object|Array|String|Number` oldValue: Old value
* `SList<T>`
	+ `wcardinal.event.Event` event: Event object
	+ `Array.<SList.Item>` addedItems: Added items sorted by their indices
	+ `Array.<SList.Item>` removedItems: Removed items sorted by their indices
	+ `Array.<SList.Update>` updatedItems: Updated items sorted by their indices
* `SMovableList<T>`
	+ `wcardinal.event.Event` event: Event object
	+ `Array.<SList.Item>` addedItems: Added items sorted by their indices
	+ `Array.<SList.Item>` removedItems: Removed items sorted by their indices
	+ `Array.<SList.Update>` updatedItems: Updated items sorted by their indices
	+ `Array.<SMovableList.Move>` newMovedItems: Moved items sorted by their new indices
	+ `Array.<SMovableList.Move>` oldMovedItems: Moved items sorted by their old indices
* `SMap<T>`
	+ `wcardinal.event.Event` event: Event object
	+ `Object.<String, T>` addedItems: Added items
	+ `Object.<String, T>` removedItems: Removed items
	+ `Object.<String, SMap.Update>` updatedItems: Updated items
* `SNavigableMap<T>`
	+ `wcardinal.event.Event` event: Event object
	+ `Object.<String, T>` addedItems: Added items
	+ `Object.<String, T>` removedItems: Removed items
	+ `Object.<String, SMap.Update>` updatedItems: Updated items
* `SQueue<T>`
	+ `wcardinal.event.Event` event: Event object
	+ `Array.<T>` addedItems: Added items sorted by their indices
	+ `Array.<T>` removedItems: Removed items sorted by their indices
* `SROQueue<T>`
	+ `wcardinal.event.Event` event: Event object
	+ `Array.<T>` addedItems: Added items sorted by their indices
	+ `Array.<T>` removedItems: Removed items sorted by their indices

### Detecting Field Changes (Java)

```java
@Controller
class MyController {
	@Autowired
	SLong time;

	@OnChange("time")
	void handler(Long newValue, Long oldValue){
		// Called immediately after the `time` field changes.
		// Arguments `newValue` and `oldValue` are a new value and
		// an old value of the `time` field, respectively.
	}
}
```

The arguments of the `@OnChange` method varies by types:

* `SBoolean`
	+ `Boolean` newValue: New value
	+ `Boolean` oldValue: Old value
* `SInteger`
	+ `Integer` newValue: New value
	+ `Integer` oldValue: Old value
* `SLong`
	+ `Long` newValue: New value
	+ `Long` oldValue: Old value
* `SFloat`
	+ `Float` newValue: New value
	+ `Float` oldValue: Old value
* `SDouble`
	+ `Double` newValue: New value
	+ `Double` oldValue: Old value
* `SString`
	+ `String` newValue: New value
	+ `String` oldValue: Old value
* `SClass<T>`
	+ `T` newValue: New value
	+ `T` oldValue: Old value
* `SArrayNode`
	+ `ArrayNode` newValue: New value
	+ `ArrayNode` oldValue: Old value
* `SObjectNode`
	+ `ObjectNode` newValue: New value
	+ `ObjectNode` oldValue: Old value
* `SJsonNode`
	+ `JsonNode` newValue: New value
	+ `JsonNode` oldValue: Old value
* `SList<T>`
	+ `SortedMap<Integer, T>` added: Added items sorted by their indices in ascending order
	+ `SortedMap<Integer, T>` removed: Removed items sorted by their indices in ascending order
	+ `SortedMap<Integer, SList.Update<T>>` updatedItems: Updated items sorted by their indices
* `SMovableList<T>`
	+ `SortedMap<Integer, T>` addd: Added items sorted by their indices in ascending order
	+ `SortedMap<Integer, T>` removed: Removed items sorted by their indices in ascending order
	+ `SortedMap<Integer, SList.Update<T>>` updated: Updated items sorted by their indices in ascending order
	+ `List<SMovableList.Move<T>>` newMoved: Moved items sorted by their new indices in ascending order
	+ `List<SMovableList.Move<T>>` oldMoved: Moved items sorted by their old indices in ascending order
* `SMap<T>`
	+ `Map<String, T>` added: Added items
	+ `Map<String, T>` removed: Removed items
	+ `Map<String, SMap.Update<T>>` updatedItems: Updated items
* `SNavigableMap<T>`
	+ `SortedMap<String, T>` added: Added items sorted by their keys in ascending order
	+ `SortedMap<String, T>` removed: Removed items sorted by their keys in ascending order
	+ `SortedMap<String, SMap.Update<T>>` updatedItems: Updated items sorted by their keys in ascending order
* `SQueue<T>`
	+ `List<T>` addedItems: Added items sorted by their indices
	+ `List<T>` removedItems: Removed items sorted by their indices
* `SROQueue<T>`
	+ No events occurs because `SROQueue<T>` is a read-only type.

Detecting the changes of fields in components/pages/popups is also possible:

```java
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SLong;

@Component
class MyComponent {
	@Autowired
	SLong time;
}

@Controller
class MyController {
	@Autowired
	MyComponent component;

	@OnChange("component.time")
	void handler(Long newValue, Long oldValue){
		// Called immediately after the `component.time` field changes.
	}
}
```

### Read-only fields

Read-only fields are fields which are not modifiable for browsers.
Still servers can change read-only fields.

```java
import org.wcardinal.controller.Controller;
import org.wcardinal.controller.ReadOnly;
import org.wcardinal.controller.data.SLong;

@Controller
class MyController {
	// This read-only field `time` is not modifiable for browsers
	// while servers can change this field.
	@Autowired
	@ReadOnly
	SLong time;
}
```

```javascript
myController.time.set(0); // Throws `wcardinal.exception.UnsupportedOperationException`
```

### Non-null fields

Making fields non-null are sometimes beneficial.
It protects fields from being null/undefined, frees us from the null checking.

```java
import org.wcardinal.controller.Controller;
import org.wcardinal.controller.data.NonNull;
import org.wcardinal.controller.data.SLong;

@Controller
class MyController {
	// This `time` field is initialized to 0 because it is a non-null field.
	// Setting this `time` to null raises `wcardinal.exception.NullArgumentException`.
	@Autowired
	@NonNull
	SLong time;
}
```

Initial values of non-null fields are:

| Field type           | Initial value |
|----------------------|---------------|
| `SBoolean`           | false         |
| `SInteger`           | 0             |
| `SLong`              | 0L            |
| `SFloat`             | 0.0F          |
| `SDouble`            | 0.0           |
| `SString`            | Empty string  |
| `SClass<T>`          | N/A           |
| `SArrayNode`         | Empty array   |
| `SObjectNode`        | Empty object  |
| `SJsonNode`          | N/A           |
| `SList<T>`           | Empty list    |
| `SMovableList<T>`    | Empty list    |
| `SMap<T>`            | Empty map     |
| `SNavigableMap<T>`   | Empty map     |
| `SQueue<T>`          | Empty queue   |
| `SROQueue<T>`        | Empty queue   |

<br/>Please note that `SClass<T>` and `SJsonNode` have no initial values.
This is because they have no appropriate initial values except null.
Therefore, non-null fields of these types are stay uninitialized.

### Uninitialized fields

Fields are initialized with null by defaults.
Thus, browsers may see null values unless they are set to appropriate values in `@OnCreate` methods.
However, at the time `@OnCreate` methods are called, a server may not be ready for initializing its fields.
Tuning off this null initialization allows servers taking a time to initializing their fields.

```java
import org.wcardinal.controller.Controller;
import org.wcardinal.controller.AbstractController;

import org.wcardinal.controller.data.Uninitialized;
import org.wcardinal.controller.data.SLong;

@Controller
class MyController extends AbstractController {
	// This `time` field will *not* be initialized to null
	// because it is annotated with @Uninitialized.
	@Autowired
	@Uninitialized
	SLong time;

	@OnCreate
	void onCreate() {
		timeout("init", 1000);


	@OnTime
	void init() {
		// Time-consuming tasks
		time.set(42);
	}
}
```

```javascript
myController.time.on("value", (e, time) => {
	console.log(time); // Prints 42 after 1 second.
});
```

### Freeing Fields When a Synchronization is Finished

If some of fields holds large data, a server may experience a memory starvation since JVM can not free heap memories for those data, .
One of the solutions to prevent this situation is setting such fields to null when the synchronization between a server and a browser is finished.
`@Soft` is for this purpose.
Fields annotated with `@Soft` are automatically set to null when the synchronization of data they have is finished so that JVM can free heap memories for data.

```java
import org.wcardinal.controller.Controller;

import org.wcardinal.controller.data.Soft;
import org.wcardinal.controller.data.SLong;

@Controller
class MyController {
	// This `time` field on a server side will be automatically set to null
	// when the synchronization between a server and a browser is finished
	// event if annotated with `@NonNull`.
	//
	// Please note that the `time` field on a browser side will *not* be set to null.
	@Autowired
	@Soft
	SLong time;
}
```

### Constants

```java
enum MyEnum {
	ENUM0,
	ENUM1,
	ENUM2
}

@Controller
@Constant(MyEnum.class)
class MyController {
	@Constant
	static final int STATIC_CONSTANT = 1;

	@Constant
	final int CONSTANT = 2;

	@Constant
	int NON_FINAL_CONSTANT = 0;

	@OnCreate
	void init() {
		NON_FINAL_CONSTANT = 3;
	}
}
```

```javascript
console.log(myController.MyEnum.ENUM0);       // Prints "ENUM0"
console.log(myController.STATIC_CONSTANT);    // Prints 1
console.log(myController.CONSTANT);           // Prints 2
console.log(myController.NON_FINAL_CONSTANT); // Prints 3
```

For non-static constants, values at the time all the locked `@OnCreate` method invocations are finished are sent to browsers.
Values after `@OnCreate` methods will not be sent to browsers:

```java
@Controller
class MyController extends AbstractController {
	@Constant
	int CONSTANT; // Initialized to 0.

	@OnCreate
	void init() {
		timeout("init", 1000);
	}

	@OnTime
	void init() {
		CONSTANT = 1; // Browsers never see this value.
	}
}
```

```javascript
console.log(myController.CONSTANT); // Prints 0
```

### Controlling field synchronization timing (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnChange;
import org.wcardinal.controller.data.SLong;

@Controller
class MyController {
	@Autowired
	SLong field0;

	@Autowired
	SLong field1;

	@OnChange("field0")
	void foo() {
		System.out.println(field1.get() == 2); // Always prints true
	}
}
```

```javascript
myController.lock();
try {
	myController.field0.set(1);
	myController.field1.set(2);
} finally {
	myController.unlock();
}
```

Updated fields within the same lock is synchronized atomically.
Therefore, it is guaranteed that `field1` has `2` when `field0` is changed to `1` in servers.

### Controlling Field Synchronization Timing (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.AbstractController;
import org.wcardinal.controller.data.SLong;

@Controller
class MyController extends AbstractController {
	@Autowired
	SLong field0;

	@Autowired
	SLong field1;

	void foo() {
		try(Unlocker unlocker = lock()) {
			field0.set(1);
			field1.set(2);
		}
	}
}
```

```javascript
myController.field0.on("value", (e, value) => {
	if (value === 1) {
		console.log(myController.field1.get() === 2); // Always prints true
	}
});
```

Updated fields within the same lock is synchronized atomically.
Therefore, it is guaranteed that `field1` has `2` when `field0` is changed to `1` in browsers.

## Method

### Calling Methods From JavaScript

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Callable
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}
```

```javascript
console.log(await myController.hello("Cardinal")); // Prints "Hello, Cardinal!"
```

### Type Declaration of Callable Methods for TypeScript

In the TypeScript projects, the type declaration of `MyController` shown in above will look like this.

```typescript:TypeScript
import { controller } from "@wcardinal/wcardinal";

interface MyController extends controller.Controller {
	hello: controller.Callable<string, [name: string]>;
}
```

If methods like `controller.Controller#on(string, function): this` and `controller.Callable#timeout(number)`
aren't mandatory, the declaration can be simplified to:

```typescript
interface MyController {
	hello(name: string): Promise<string>;
}
```

> **NOTE**
>
> In the versions prior to 2.2.0, the type declaration of `MyController` will look like the following.
> Otherwise, `myController.hello("Cardinal")` doesn't compile.
>
> ```typescript
> import { controller } from "@wcardinal/wcardinal";
>
> interface MyController extends controller.Controller {
> 	hello: controller.Callable<string, [name: string]> & controller.CallableCall<string, [name: string]>;
> }
> ```

### Adjusting Method Timeout (Pattern 1)

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Timeout;

@Controller
class MyController {
	// Change the timeout of the `hello` method to 10 seconds
	@Callable
	@Timeout(10000)
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}
```

### Adjusting Method Timeout (Pattern 2)

We can use Spring properties instead.

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Callable
	@Timeout(string="timeout.property.name")
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}
```

### Adjusting Method Timeout (Pattern 3)

The timeout values can be overridden in browsers by the `.timeout(number)` method.

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Callable
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}
```

```javascript
// Overrides the timeout value of the `hello` method to 10 seconds
console.log(await myController.hello.timeout(10000).call("Cardinal")); // Prints "Hello, Cardinal!"
```

### Calling Methods From JavaScript via Ajax (Pattern 1)

When `@Callable` / `@Task` methods need to return large data, it is
preferable to send them via the Ajax to avoid consuming large heap memory.

```java
import org.wcardinal.controller.annotation.Ajax;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Ajax
	@Callable
	String large() {
		return "Large Data";
	}
}
```

```javascript
console.log(await myController.large()); // Prints "Large Data"
```

### Calling Methods From JavaScript via Ajax (Pattern 2)

Wa can use the Ajax even when the callable methods are not annotated with `@Ajax` as follows:

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Callable
	String large() {
		return "Large Data";
	}
}
```

```javascript
console.log(await myController.large.ajax().call()); // Prints "Large Data"
```

### Calling @Ajax-Annotated Methods From JavaScript via WebSocket

```java
import org.wcardinal.controller.annotation.Ajax;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Ajax
	@Callable
	String large() {
		return "Large Data";
	}
}
```

```javascript
console.log(await myController.large.unajax.call()); // Prints "Large Data"
```

### Streaming Large Set of Data

When `@Callable` / `@Task` methods need to return large set of data, it is
preferable to stream those data instead of consuming large heap memory.
`StreamingResult` is for that purpose. If `@Callable` / `@Task` methods return
`StreamingResult`, `StreamingResult#serialize(JsonGenerator)` is called to
serialize their return values. Therefore, all the data don't need to be
stored in the heap memory.

Please note that if the methods are not annotated with `@Ajax`, the
serialized data still consume the heap memory, although the data themselve
don't. Because of this, it's highly recommended to annotate methods with
`@Ajax` if the serialized data are considered to be large.

```java
import org.wcardinal.controller.StreamingResult;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Ajax
	@Callable
	StreamingResult large() {
		return (generator) -> {
			generator.writeStartArray();
			for (int i = 0; i < 3; ++i) {
				generator.writeNumber(i);
			}
			generator.writeEndArray();
		};
	}
}
```

```javascript
console.log(await myController.large()) // Prints [0, 1, 2]
```

### Method Exception Handling

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.CallableExceptionHandler;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Callable
	String hello(String name) {
		throw new RuntimeException();
	}

	@CallableExceptionHandler
	String handle(Exception e) {
		return "fail-reason";
	}
}
```

```javascript
myController.hello("Cardinal").catch((reason) => {
	console.log(reason); // Prints "fail-reason"
});
```

If there is more than one exception handler, most specific one is chosen
and executed based on types of raised exceptions and arguments of handlers:

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.CallableExceptionHandler;
import org.wcardinal.controller.annotation.Controller;

@Controller
class MyController {
	@Callable
	String hello(String name) {
		throw new RuntimeException();
	}

	@CallableExceptionHandler
	String handle(Exception e) {
		// Never be called because the other one has the more specific signature.
		return "fail-reason-a";
	}

	@CallableExceptionHandler
	String handle(RuntimeException e) {
		// Called because this is more specific.
		return "fail-reason-b";
	}
}
```

```javascript
myController.hello("Cardinal").catch((reason) => {
	console.log(reason); // Prints "fail-reason-b"
});
```

If there is no appropriate handler, one of the handlers on a parent is called:

```java
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.CallableExceptionHandler;
import org.wcardinal.controller.annotation.Controller;

@Component
class MyComponent {
	@Callable
	String hello(String name) {
		throw new RuntimeException();
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;

	@CallableExceptionHandler
	String handle(Exception e) {
		return "fail-reason-a";
	}

	@CallableExceptionHandler
	String handle(RuntimeException e) {
		return "fail-reason-b";
	}
}
```

```javascript
myController.component.hello("Cardinal").catch((reason) => {
	console.log(reason); // Prints "fail-reason-b"
});
```

## Task

### Introduction

Let us think about an application which searches and displays alarms containing given words when a user pushes an search button.
The simplest implementation looks like this:

```java
class Alarm {

}

@Controller
class MyAlarmController {
	@Callable
	List<Alarm> find(String words) {
		// Search and returns alarms containing `words`.
	}
}
```

```javascript
const alarms = await myAlarmController.find(...);
// Render the `alarms`.
```

Simple and works fine. However, if the `find` does take few minutes, what happens?
`@Callable` methods own their locks when being called by default.
Therefore, the user who pushed the search button can not change the search words until the `find` finishes.
It may not be acceptable.
This lock behavior of `@Callable` methods can be overridden by using `@Unlocked`:

```java
@Controller
class MyAlarmController {
	@Callable
	@Unlocked
	List<Alarm> find(String words) {
		// Search and returns alarms containing `words`.
	}
}
```

Since this new `find` method does not require a lock, users can request an another search while a previous search is running.
But let's think about the following situation:

1. Executed `find("A")`.
2. Executed `find("B")` after 10 seconds.
3. `find("B")` finished.
4. `find("A")` finished.

Obviously, the `find("B")`'s result is supposed to be displayed.
But, because `find("B")` finished before `find("A")` did,
the application may override the `find("B")`'s result with the `find("A")`'s result.
To fix this issue, a version control on a browser side is required:

```javascript
let currentVersion = 0;

const renderer = (expectedVersion, alarms) => {
	if (expectedVersion === currentVersion) {
		// Render the `alarms`.
	}
};

const search = (words) => {
	currentVersion += 1;
	myAlarmController.find(words, currentVersion)
	.then(renderer.bind(null, currntVersion));
};

search("A");
search("B");
```

Things getting complicated.
Still we haven't done yet.
We may need to implement features:

* Canceling the `find("A")` when the `find("B")`,
* Auto retry when a connection is lost.

```java
@Controller
class MyAlarmController{
	long currentVersion;

	synchronized void checkVersion(long expectedVersion) throws IllegalStateException {
		if (currentVersion != expectedVersion) throw new IllegalStateException();
	}

	synchronized void updateVersion(long version) throws IllegalStateException {
		if (version <= currentVersion) throw new IllegalStateException();
		currentVersion = version;
	}

	@Callable
	@Unlocked
	List<Alarm> find(String words, long version) throws IllegalStateException {
		updateVersion(version);
		// Searches and returns alarms
		// if the `checkVersion(version)` does not throw an exception.
	}
}
```

```javascript
let currentVersion = 0;

const renderer = (expectedVersion, alarms) => {
	if (expectedVersion === currentVersion) {
		// Render the `alarms`.
	}
};

const retry = (expectedVersion, words, reason) => {
	if (expectedVersion == currentVersion && reason !== "exception") {
		search(words);
	}
};

const search = (words) => {
	currentVersion += 1;
	myAlarmController.find(words)
	.then(renderer.bind(null, currntVersion))
	.catch(retry.bind(null, currentVersion, words));
};

search("A");
search("B");
```

The `@Task` is for implementing this kind of time-consuming tasks.
With `@Task`, the above codes can be simplified as follows:

```java
@Controller
class MyAlarmController extends AbstractController {
	@Task
	List<Alarm> find(String words) {
		// Searches and returns alarms
		// if the `AbstractController#isCanceled()` returns false.
	}
}
```

```javascript
myAlarmController.find.on("success", (e, alarms) => {
	// Render the `alarms`.
};

myAlarmController.find("A");
myAlarmController.find("B");
```

Please note that `@Task` methods **do not own their locks** when being called by default.
This behavior can be changed by `@Locked`:

```java
@Controller
class MyAlarmController extends AbstractController {
	@Task
	@Locked
	List<Alarm> find(String words) {
		// Called with a lock.
	}
}
```

### Task Basics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}
```

```javascript
myController.hello
.on("success", (e, result) => {
	// Called when the task `hello` succeeds.
	console.log(result); // Prints "Hello, Cardinal!"
})
.on("fail", (e, reason) => {
	// Called when the task `hello` fails.
});

controller.hello("Cardinal");
```

### Type Declaration of Task Methods for TypeScript

In the TypeScript projects, the type declaration of `MyController` shown in above will look like this.

```typescript
import { controller } from "@wcardinal/wcardinal";

interface MyController extends controller.Controller {
	hello: controller.Task<string, [name: string]>;
}
```

> **NOTE**
>
> In the versions prior to 2.2.0, the type declaration of `MyController` will look like the following.
> Otherwise, `myController.hello("Cardinal")` doesn't compile.
>
> ```typescript
> import { controller } from "@wcardinal/wcardinal";
>
> interface MyController extends controller.Controller {
> 	hello: controller.Task<string, [name: string]> & controller.TaskCall<[name: string], string>;
> }
> ```

### Aborting a Task

```java
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.TaskAbortException;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello(String name) {
		throw new TaskAbortException("fail-reason");
	}
}
```

```javascript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

### Failing a Task

```java
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	TaskResult<String> hello(String name) {
		return TaskResults.fail("fail-reason");
	}
}
```

```javascript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

### Retrieving Task Arguments

```javascript
myController.hello("Cardinal");
console.log(myController.hello.getArguments());	// Prints ["Cardinal"]
console.log(myController.hello.getArgument(0));	// Prints "Cardinal"
```

### Retrieving a Task Result

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello(String name) {
		return "Hello, " + name + "!";
	}
}
```

```javascript
myController.hello.on("success", () => {
	console.log(myController.hello.getResult()); // Prints "Hello, Cardinal!"
});

myController.hello("Cardinal");
```

### Retrieving a Reason Why Failed

```java
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	TaskResult<String> hello(String name) {
		return TaskResults.fail("fail-reason");
	}
}
```

```javascript
myController.hello.on("fail", () => {
	console.log(myController.hello.getReason()); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

### Check Whether a Task Is Finished

```javascript
console.log(myController.hello.isDone()); // Prints true if finished. Otherwise, prints false.
```

### Check Whether a Task Is Finished Successfully

```javascript
console.log(myController.hello.isSucceeded()); // Prints true if finished successfully. Otherwise, prints false.
```

### Check Whether a Task Is Finished Unsuccessfully

```javascript
console.log(myController.hello.isFailed()); // Prints true if finished unsuccessfully. Otherwise, prints false.
```

### Task Exception Handling

```java
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello(String name) {
		throw new RuntimeException();
	}

	@TaskExceptionHandler
	String handle(Exception e) {
		return "fail-reason";
	}
}
```

```javascript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

If there is more than one exception handler, most specific one is chosen
and executed based on types of raised exceptions and arguments of handlers:

```java
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello(String name) {
		throw new RuntimeException();
	}

	@TaskExceptionHandler
	String handle(Exception e) {
		// Never be called because the other one has the more specific signature.
		return "fail-reason-a";
	}

	@TaskExceptionHandler
	String handle(RuntimeException e) {
		// Called because this is more specific.
		return "fail-reason-b";
	}
}
```

```javascript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason-b"
});

myController.hello("Cardinal");
```

If there is no appropriate handler, one of the handlers on a parent is called:

```java
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Component
class MyComponent {
	@Task
	String hello(String name) {
		throw new RuntimeException();
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;

	@TaskExceptionHandler
	String handle(Exception e) {
		// Never be called because the other one has the more specific signature.
		return "fail-reason-a";
	}

	@TaskExceptionHandler
	String handle(RuntimeException e) {
		// Called because this is more specific.
		return "fail-reason-b";
	}
}
```

```javascript
myController.component.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason-b"
});

myController.hello("Cardinal");
```

## Event

### Trigger Events (Server to Browser)

```java
@Controller
class MyController extends AbstractController {
	void triggerReadyEvent() {
		trigger("ready", 1, 2, 3);
	}
}
```

or

```java
@Controller
class MyController extends AbstractController {
	@Autowired
	ControllerFacade facade;

	void triggerReadyEvent() {
		facade.trigger("ready", 1, 2, 3);
	}
}
```

```javascript
myController.on("ready", (e, p1, p2, p3) => {
	console.log( p1, p2, p3 ); // Prints 1, 2, 3
});
```

### Trigger Events (Server to Server)

```java
@Component
class MyComponent extends AbstractComponent {
	void foo() {
		notify("ready", 1, 2, 3);
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;

	@OnNotice("component.ready")
	void bar(int p1, int p2, int p3) {
		// Called when `component` raises a "ready" event.
		System.out.println(String.format("%d, %d, %d")); // Prints "1, 2, 3"
	}
}
```

or

```java
@Component
class MyComponent {
	@Autowired
	ComponentFacade facade;

	void foo() {
		facade.notify("ready", 1, 2, 3);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyComponent component;

	@OnNotice("component.ready")
	void bar(int p1, int p2, int p3) {
		// Called when `component` raises a "ready" event.
		System.out.println(String.format("%d, %d, %d")); // Prints "1, 2, 3"
	}
}
```

### Retrieving Returned Values of Event Handlers (Server to Browser)

```java
import java.util.List;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.TriggerErrors;

@Controller
class MyController extends AbstractController {
	void triggerReadyEvent() {
		triggerAndWait("ready", 1000, 1, 2, 3)
		.done(new DoneCallback<List<JsonNode>>() {
			@Override
			public void onDone(List<JsonNode> result) {
				System.out.println( result ); // Prints [ 6 ]
			}
		});
	}
}
```

```javascript
myController.on("ready", (e, p1, p2, p3) => {
	return p1 + p2 + p3;
});
```

## Component

### Introduction

A component is for structurising a controller.
In a component, we can do almost anything that we can do in a controller:

```java
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Controller;

@Component
class MyComponent {
	@Autowired
	SLong field;

	@OnCreate
	void init() {
		field.set(1);
	}

	@Callable
	int callble() {
		return 2;
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;
}
```

```javascript
console.log(myController.component.field.get()); // Prints 1
console.log(await myController.component.callable()); // Prints 2
```

### Component Basics

```java
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Controller;

@Component
class MyComponent {
	@Callable
	String hello(String name){
		return "Hello, " + name + "!";
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;
}
```

```javascript
console.log(await myController.component.hello("Cardinal")); // Prints "Hello, Cardinal!"
```

### Retrieving Parent (JavaScript)

```java
@Component
class MyComponent {
	...
}

@Controller
class MyController {
	@Autowired
	MyComponent myComponent;
}
```

```javascript
console.log(myController.getParent()); // Prints null
console.log(myController.myComponent.getParent() === controller); // Prints true
```

### Retrieving Parent (Java)

```java
@Component
class MyComponent extends AbstractComponent {
	...
}

@Controller
class MyController {
	@Autowired
	MyComponent myComponent;

	void foo() {
		System.out.println(getParent()); // Prints null
		System.out.println(myComponent.getParent() == this); // Prints true
	}
}
```

or

```java
@Component
class MyComponent {
	@Autowired
	ComponentFacade facade;

	Object getParent() {
		return facade.getParent();
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent myComponent;

	void foo() {
		System.out.println(getParent()); // Prints null
		System.out.println(myComponent.getParent() == this); // Prints true
	}
}
```

### Component Lifecycle Handling

```java
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Component
class MyComponent {
	@OnCreate
	void init() {
		// Called after instantiated.
	}

	@OnDestroy
	void destroy() {
		// Called before getting destroyed.
	}
}
```

### Creating Components Dynamically (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set(128);
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;
}
```

```javascript
myController.factory.create().value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Components Dynamically (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set(128);
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyComponent` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("create", (e, newInstance) => {
	// Called when a new instance is created.
	newInstance.value.on("value", (e, value) => {
		console.log(value); // Prints 128
	});
});
```

### Creating Components Dynamically With Parameters (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;
}
```

```javascript
myController.factory.create(128).value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Components Dynamically With Parameters (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyComponent` instance with a integer of 128.
		factory.create(128);
	}
}
```

### Destroying Dynamic Components (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	...
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;
}
```

```javascript
myController.factory.destroy(controller.factory.create());
```

### Destroying Dynamic Components (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.AbstractComponent;

class MyComponent extends AbstractComponent {
	@OnCreate
	void onCreate() {
		// Calls the `@OnTime` method after 10 seconds.
		timeout("done", 10000);
	}

	@OnTime
	void destroy() {
		// Destroys itself.
		getParentAsFactory().destroy(this);
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyComponent` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("destroy", (e, newInstance) => {
	// Called when instances are destroyed.
});
```

### Sharing Components Among Browsers

```java
import org.wcardinal.controller.Controller;
import org.wcardinal.controller.SharedComponent;
import org.wcardinal.controller.data.SLong;

// This `MySharedComponent` is shared among users
// because it's annotated with the `SharedComponent` annotation.
@SharedComponent
class MySharedComponent {
	@Autowired
	SLong time;
}

@Controller
class MyController {
	@Autowired
	MySharedComponent shared;
}
```

Please note that the `MySharedComponent` class is annotated with the `SharedComponent` annotation.

### Shared Component Lifecycle Handling

```java
@SharedComponent
class MySharedComponent {
	@OnCreate
	void init() {
		// Called after instantiated.
	}

	@OnDestroy
	void destroy() {
		// Called before getting destroyed.
	}
}
```

## Page

### Introduction

A page is a component with visibility control and browser history binding methods.
In contrast to the popup, among pages belonging to the same class, at most one page can be visible.
For instance, let's consider the case:

```java
@Page
class MyPage {
	...
}

@Component
class MyComponent {
	@Autowired
	MyPage myPage3;
}

@Controller
class MyController {
	@Autowired
	MyPage myPage1;

	@Autowired
	MyPage myPage2;

	@Autowired
	MyComponent component;
}
```

In this case, if `myPage1` is shown, `myPage2` never be shown.
On the contrary, if `myPage2` is shown, `myPage1` never be shown.
However, `myPage1` and `myPage3` can be shown simultaneously because these two belongs to different classes.

### Page Basics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Page;

@Page
class MyPage1 {
	...
}

@Page
class MyPage2 {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage1 myPage1;

	@Autowired
	MyPage2 myPage2;
}
```

### Showing/Hiding Pages (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
myController.myPage.show();
myController.myPage.hide();
```

### Showing/Hiding Pages (Java)

```java
import org.wcardinal.controller.AbstractPage;

@Page
class MyPage extends AbstractPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void showMyPage() {
		myPage.show();
	}

	void hideMyPage() {
		myPage.hide();
	}
}
```

or

```java
import org.wcardinal.controller.PageFacade;

@Page
class MyPage {
	@Autowired
	PageFacade facade;

	public void show() {
		return facade.show();
	}

	public void hide() {
		return facade.hide();
	}
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void showMyPage() {
		myPage.show();
	}

	void hideMyPage() {
		myPage.hide();
	}
}
```

### Retrieving Active Page (JavaScript)

```java
import org.wcardinal.controller.AbstractController;

@Page
class MyPage {
	...
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyPage myPage;
}
```

```javascript
console.log(myController.getActivePage() === myController.myPage); // Prints true
```

### Retrieving Active Page (Java)

```java
import org.wcardinal.controller.AbstractController;

@Page
class MyPage {
	...
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyPage myPage;

	void foo(){
		System.out.println(getActivePage() == myPage); // Prints true
	}
}
```

### Checking Whether a Page is Shown/Hidden (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
console.log(myController.myPage.isShown()); // Prints true
console.log(myController.myPage.isHidden()); // Prints false
```

### Checking Whether a Page is Shown/Hidden (Java)

```java
import org.wcardinal.controller.AbstractPage;

@Page
class MyPage extends AbstractPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void foo() {
		System.out.println(myPage.isShown()); // Prints true
		System.out.println(myPage.isHidden()); // Prints false
	}
}
```

### Retrieving Parent (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
console.log(myController.getParent()); // Prints null
console.log(myController.myPage.getParent() === myController); // Prints true
```

### Retrieving Parent (Java)

```java
@Page
class MyPage extends AbstractPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void foo(){
		System.out.println(getParent()); // Prints null
		System.out.println(myPage.getParent() == this); // Prints true
	}
}
```

or

```java
@Page
class MyPage {
	@Autowired
	PageFacade facade;

	Object getParent() {
		return facade.getParent();
	}
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void foo() {
		System.out.println(getParent()); // Prints null
		System.out.println(myPage.getParent() == this); // Prints true
	}
}
```

### Primary Page

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage1; // Hidden by default

	@Autowired
	@Primary
	MyPage myPage2; // Shown by default
}
```

If the `@Primary` is missing, the first page in a class becomes a primary page.

```java
@Controller
class MyController {
	// Shown by default because there is no `@Primary`
	// and this is the first page in the `MyController`.
	@Autowired
	MyPage myPage1;

	@Autowired
	MyPage myPage2; // Hidden by default
}
```

### No Primary Page

```java
import org.wcardinal.controller.annotation.NoPrimaryPage;

@Page
class MyPage {
	...
}

@Controller
@NoPrimaryPage
class MyController {
	@Autowired
	MyPage myPage1; // Hidden by default

	@Autowired
	MyPage myPage2; // Hidden by default
}
```

### Detecting Page Change (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
myController.myPage.on("show", () => {
	// Called when event handlers are set if `myPage` is shown or after `myPage` gets to be shown.
});

myController.myPage.on("hide", () => {
	// Called when event handlers are set if `myPage` is hidden or after `myPage` gets to be hidden.
});

myController.on("page", (e, newPageName, oldPageName) => {
	// Called when event handlers are set or when the active page of this controller is changed.
});
```

### Detecting Page Change (Java)

```java
@Page
class MyPage {
	@OnShow
	void onShow() {
		// Called after being shown.
	}

	@OnHide
	void onHide() {
		// Called after being hidden.
	}
}
```

or

```java
@Page
class MyPage {
	...
}

class MyController {
	@Autowired
	MyPage myPage;

	@OnShow("myPage")
	void onShowMyPage(){
		// Called after `myPage` gets to be shown.
	}

	@OnHide("equipmentPage")
	void onShowMyPage(){
		// Called after `myPage` gets to be hidden.
	}

	@OnChange("page")
	void onChangePage(String newPageName, String oldPageName){
		// Called when the active page of this controller is changes.
	}
}
```

### Page Lifecycle Handling

```java
import org.wcardinal.controller.annotation.Page;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Page
class MyPage {
	@OnCreate
	void init() {
		// Called after instantiated.
	}

	@OnDestroy
	void destroy() {
		// Called before getting destroyed.
	}
}
```

### Page Name in Title

```java
import org.wcardinal.controller.annotation.DisplayName;

@Page
@DisplayName("Page name in title")
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayName;

@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayName("Page name in title")
	MyPage myPage;
}
```

### Page Name in Title (I18N)

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Page
@DisplayNameMessage("my-page.name")
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayNameMessage("my-page.name")
	MyPage myPage;
}
```

And add the followings to your `messages_en.properties`:

```text
my-page.name=Page name in title
```

### Creating Pages Dynamically (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		// Called when instantiated.
		value.set(128);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;
}
```

```javascript
myController.factory.create().value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Pages Dynamically (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		// Called when instantiated.
		value.set(128);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPage` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("create", (e, newInstance) => {
	// Called when a new instance is created.
	newInstance.value.on("value", (e, value) => {
		console.log(value); // Prints 128
	});
});
```

### Creating Pages Dynamically With Parameters (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;
}
```

```javascript
myController.factory.create(128).value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Pages Dynamically With Parameters (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PageFactory<MyPage> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPage` instance with an integer of 128.
		factory.create(128);
	}
}
```

### Destroying Dynamic Pages (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;
}
```

```javascript
myController.factory.destroy(myController.factory.create());
```

### Destroying Dynamic Pages (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.AbstractPage;

class MyPage extends AbstractPage {
	@OnCreate
	void onCreate() {
		// Calls the `@OnTime` method after 10 seconds.
		timeout("destroy", 10000);
	}

	@OnTime
	void destroy() {
		// Destroys itself.
		getParentAsFactory().destroy(this);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPage` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("destroy", (e, newInstance) => {
	// Called when instances are destroyed.
});
```

## Popup

### Introduction

A popup is a component with visibility control and browser history binding methods.
In contrast to the page, any number of popups can be visible simultaneously.
For instance, let's consider the case:

```java
@Popup
class MyPopup {
	...
}

@Component
class MyComponent {
	@Autowired
	MyPopup myPopup3;
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup1;

	@Autowired
	MyPopup myPopup2;

	@Autowired
	MyComponent component;
}
```

In this case, `myPopup1`, `myPopup2` and `myPopup3` can be visible simultaneously.

### Popup Basics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Popup;

@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

### Showing/Hiding Popups (JavaScript)

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

```javascript
myController.myPopup.show();
myController.myPopup.hide();
```

### Showing/Hiding Popups (Java)

```java
@Popup
class MyPopup extends AbstractPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	@Callable
	void onCalled() {
		myPopup.show();
	}
}
```

or

```java
@Popup
class MyPopup {
	@Autowired
	PopupFacade facade;

	public void show() {
		facade.show();
	}

	public void hide() {
		facade.hide();
	}
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	@Callable
	void onCalled() {
		myPopup.show();
	}
}
```

### Checking Whether a Popup is Shown/Hidden (JavaScript)

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

```javascript
console.log(myController.myPopup.isShown()); // Prints true
console.log(myController.myPopup.isHidden()); // Prints false
```

### Checking Whether a Popup is Shown/Hidden (Java)

```java
import org.wcardinal.controller.AbstractPopup;

@Popup
class MyPopup extends AbstractPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo() {
		System.out.println(myPopup.isShown()); // Prints true
		System.out.println(myPopup.isHidden()); // Prints false
	}
}
```

or

```java
import org.wcardinal.controller.PopupFacade;

@Popup
class MyPopup {
	@Autowired
	PopupFacade facade;

	public boolean isShown() {
		return facade.isShown();
	}

	public boolean isHidden() {
		return facade.isHidden();
	}
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo() {
		System.out.println(myPopup.isShown()); // Prints true
		System.out.println(myPopup.isHidden()); // Prints false
	}
}
```

### Retrieving Parent (JavaScript)

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

```javascript
console.log(myController.getParent()); // Prints null
console.log(myController.myPopup.getParent() === myController); // Prints true
```

### Retrieving Parent (Java)

```java
@Popup
class MyPopup extends AbstractPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo(){
		System.out.println(getParent()); // Prints null
		System.out.println(myPopup.getParent() == this); // Prints true
	}
}
```

or

```java
@Popup
class MyPopup {
	@Autowired
	PopupFacade facade;

	Object getParent() {
		return facade.getParent();
	}
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo() {
		System.out.println(getParent()); // Prints null
		System.out.println(myPopup.getParent() == this); // Prints true
	}
}
```

### Primary Popup

```java
@Popup
class MyPopup{
	...
}

@Controller
class MyController {
	@Autowired
	@Primary
	MyPopup myPopup; // Shown by default
}
```

If the `@Primary` is not present, `myPopup` is hidden by default.

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup; // Hidden by default because not annotated with `@Primary`.
}
```

### Detecting Popup Visibility Change (JavaScript)

```javascript
myController.myPopup.on("show", () => {
	// Called when event handlers are set if `myPopup` is shown, or after `myPopup` gets to be shown.
});

myController.myPopup.on("hide", () => {
	// Called when event handlers are set if `myPopup` is hidden, or after `myPopup` gets to be hidden.
});
```

### Detecting Popup Visibility Change (Java)

```java
@Popup
class MyPopup{
	@OnShow
	void onShow() {
		// Called after being shown.
	}

	@OnHide
	void onHide() {
		// Called after being hidden.
	}
}
```

or

```java
@Popup
class MyPopup {
	...
}

class MyController {
	@Autowired
	MyPopup myPopup;

	@OnShow("myPopup")
	void onShowMyPopup() {
		// Called after `myPopup` gets to be shown.
	}

	@OnHide("myPopup")
	void onHideMyPopup() {
		// Called after `myPopup` gets to be hidden.
	}
}
```

### Popup Lifecycle Handling

```java
import org.wcardinal.controller.annotation.Popup;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Popup
class MyPopup {
	@OnCreate
	void init() {
		// Called after instantiated.
	}

	@OnDestroy
	void destroy() {
		// Called before getting destroyed.
	}
}
```

### Popup Name in Title

```java
import org.wcardinal.controller.annotation.DisplayName;

@Popup
@DisplayName("Popup name in title")
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayName;

@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayName("Popup name in title")
	MyPopup myPopup;
}
```

### Popup Name in Title (I18N)

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Popup
@DisplayNameMessage("my-popup.name")
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayNameMessage("my-popup.name")
	MyPopup myPopup;
}
```

And add the followings to your `messages_en.properties`:

```text
my-popup.name=Popup name in title
```

### Creating Popups Dynamically (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;

class MyPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set(128);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;
}
```

```javascript
myController.factory.create().value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Popups Dynamically (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;

class MyPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set(128);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPopup` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("create", (e, newInstance) => {
	// Called when a new instance is created.
	newInstance.value.on("value", (e, value) => {
		console.log(value); // Prints 128
	});
});
```

### Creating Popups Dynamically With Parameters (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.AbstractPopup;

class MyPopup extends AbstractPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;
}
```

```javascript
myController.factory.create(128).value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Popups Dynamically With Parameters (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.AbstractPopup;

class MyPopup extends AbstractPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPopup` instance with an integer of 128.
		factory.create(128);
	}
}
```

### Destroying Dynamic Popups (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;

class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	PopupFactory<MyPopup> factory;
}
```

```javascript
myController.factory.destroy(myController.factory.create());
```

### Destroying Dynamic Popups (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.AbstractPopup;

class MyPopup extends AbstractPopup {
	@OnCreate
	void onCreate() {
		// Calls the `OnTime` method after 10 seconds.
		timeout("destroy", 10000);
	}

	@OnTime
	void onDone() {
		// Destroys itself.
		getParentAsFactory().destroy(this);
	}
}

@Controller
class MyController {
	@Autowired
	PopupFactory<MyPopup> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPopup` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("destroy", (e, newInstance) => {
	// Called when instances are destroyed.
});
```

## Local controller

### Introduction

Fields on controllers are synchronized between servers and browsers.
However, in some situations, the view layer requires its own data which are **NOT** synchronized.
In addition to that, it might be helpful if such data have same APIs as fields of controllers.
This is what [wcardinal.controller.Controllers#create][1] is intended for:

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "SInteger",
	component: {
		field: "SList"
	}
});
```

The fields of controllers created by [Controllers#create][1] have exactly the same APIs as fields of controllers defined by Java.
Namely, `controller.field`, `controller.component` and `controller.component.field` are instances of [SInteger](../api/js/classes/controller_data.sinteger.html),
[Component](../api/js/interfaces/controller.component.html) and [SList](../api/js/classes/controller_data.slist.html), respectively.

```javascript
controller.field.set(1);
controller.field.on("value", (e, value) => {
	console.log(value); // Prints 1
});

console.log(controller.component.field.size()); // Prints 0
```

Controllers created by [Controllers#create][1] are referred to as local controllers.

[1]: ../api/js/classes/controller.controllers.html#create

### Local Controller Basics

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "SInteger",
	component: {
		field: "SList"
	}
});

controller.field.set(1);
controller.field.on("value", (e, value) => {
	console.log(value); // Prints 1
});

console.log(controller.component.field.size()); // Prints 0
```

### Non-Null Local Controller Field

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "@NonNull SInteger"
});

controller.field.set(null); // Throws `wcardinal.exception.NullArgumentException`
```

or

```javascript
const controller = Controllers.create({
	field: {type: "SInteger", nonnull: true}
});
```

### Uninitialized Local Controller Field

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "@Uninitialized SInteger"
});

controller.field.on("value", () => {
	// *Not fired* because the `field` is not initialized yet.
});
```

or

```javascript
const controller = Controllers.create({
	field: {type: "SInteger", uninitialized: true}
});
```

### Local Controller Fields With Default Values

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "SInteger: 24"
});

console.log(controller.field.get()); // Prints 24
```

or

```javascript
const controller = Controllers.create({
	field: {type: "SInteger", value: 24}
});
```

Default values must be valid as JSON.

### @Callable Methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	callable: "@Callable"
});

// Called when the `controller.callable` is called.
controller.callable.on("call", (e, arg) => {
	return arg + ", World!";
});

controller.callable("Hello").then((result) => {
	console.log(result); // Prints "Hello, World!"
});
```

### Asynchronous @Callable Methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	callable: "@Callable"
});

// Called when the `controller.callable` is called.
controller.callable.on("call", (e, arg) => {
	// Returns a promise.
	return new Promise((resolve) => {
		setTimeout(() => {
			resolve(arg + ", World!");
		}, 1000);
	});
});

controller.callable("Hello").then((result) => {
	console.log(result); // Prints "Hello, World!"
});
```

### Failing @Callable Methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	callable: "@Callable"
});

// Called when the `controller.callable` is called.
controller.callable.on("call", (e, arg) => {
	return Thenable.reject("fail-reason");
});

controller.callable("Hello").catch((reason) => {
	console.log(reason); // Prints "fail-reason"
});
```

### @Task Methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	task: "@Task"
});

// Called when the `controller.task` is called.
controller.task.on("call", (e, arg) => {
	return arg + ", World!";
});

controller.task.on("success", (e, result) => {
	// Called when the task `task` succeeds.
	console.log( result ); // Prints "Hello, World!"
});

controller.task("Hello");
```

### Asynchronous @Task Methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	task: "@Task"
});

// Called when the `controller.task` is called.
controller.task.on("call", (e, arg) => {
	// Returns a `thenable` or a promise.
	return new Thenable((resolve) => {
		setTimeout(() => {
			resolve(arg + ", World!");
		}, 1000);
	});
});

controller.task.on("success", (e, result) => {
	// Called when the task `task` succeeds.
	console.log( result ); // Prints "Hello, World!"
});

controller.task("Hello");
```

### Failing @Task Methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	task: "@Task"
});

// Called when the `controller.task` is called.
controller.task.on("call", (e, arg) => {
	return Thenable.reject( "fail-reason" );
});


controller.task.on("fail", (e, reason) => {
	// Called when the task `task` fails.
	console.log( reason ); // Prints "fail-reason"
})

controller.task("Hello");
```

## Threading

### Introduction

Each instance of controllers has a lock and is locked automatically
when `@OnChange` / `@Callable` methods are called by wcardinal and when fields of types in the package `org.wcardinal.controller.data` are changed.
Thus, basically, such methods and fields are thread safe.

```java
@Controller
class MyController {
	@Callable
	void foo() {
	   // When called by wcardinal, a lock this instance has is locked.
	   // Thus, in that case, this method is thread safe.
	}
}
```

```java
@Controller
class MyController {
	@Autowired
	SLong field;

	@OnCreate
	void init() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				// Thread safe because the `SLong#set( Long )` is thread safe.
				field.set(0);
			}
		}).start();
	}
}
```

However, calling the non-thread safe `MyController#foo()` from a thread which does not have a lock is not thread safe.

```java
@Controller
class MyController {
	@OnCreate
	void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Not thread safe because the `foo` is not thread safe.
				foo();
			}
		}).start();
	}

	// Non-thread safe
	void foo() {
		...
	}
}
```

Because a thread executing `@OnTime` or `@Task` methods without a `@Locked` annotation do not own a lock,
calling the `MyController#foo()` from such methods is not thread safe either.

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		timeout("bar", 0);
	}

	@OnTime
	void bar() {
		// Not thread safe because the `foo` is not thread safe.
		foo();
	}

	// Non-thread safe
	void foo() {
		...
	}
}
```

There are two ways to fix this: making `MyController#foo()` thread safe

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		timeout("bar", 0);
	}

	@OnTime
	void bar() {
		// Thread safe because the `foo` is thread safe.
		foo();
	}

	// Thread safe because the `foo` acquires a lock inside.
	void foo() {
		try(Unlocker unlocker = lock()){
			...
		}
	}
}
```

and acquiring a lock before calling the non-thread safe `MyController#foo()`.

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		timeout("bar", 0);
	}

	@OnTime
	@Locked
	void bar() {
		// Thread safe because the `bar` is annotated with a `@Locked` annotation.
		// A thread that executes the `bar` acquires a lock before calling the `bar`.
		foo();
	}

	// Non-thread safe
	void foo() {
		...
	}
}
```

Every method annotated with annotations other than `@OnTime`, `@Task`, `@OnRequest` or `@OnCheck` own a lock when called from wcardinal by default.
Sometimes, it is useful to change this default behavior as shown above.
`@Locked` and `@Unlocked` annotations are for this purpose.

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	@Unlocked
	void onCreate() {
	   // Called without a lock.
	}

	@OnTime
	@Locked
	void onTime() {
	   // Called with a lock.
	}

	@Callable
	@Unlocked
	void callable() {
		// Called without a lock.
	}

	@Task
	@Locked
	void task() {
		// Called with a lock.
	}

	@OnChange
	@Unlocked
	void onChange() {
		// Called without a lock
	}
}
```

Because `@OnRequest` / `@OnCheck` methods are called before instantiating controllers,
the lock behavior of their methods can not be changed.
All fields including components have the same lock their controller instance has.
Thus, the followings are equivalent:

```java
@Controller
class MyController extends AbstractController {
	void foo() {
		// Lock by `AbstractController#lock()`
		try (Unlocker unlocker = lock()) {
			...
		}
	}
}
```

```java
@Controller
class MyController {
	@Autowired
	SLong field;

	void foo() {
		// Lock by `SLong#lock()` of `field`
		try (Unlocker unlocker = field.lock()) {
			...
		}
	}
}
```

```java
@Componnt
class MyComponent extends AbstractComponent {
	...
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyComponent component;

	void foo() {
		// Lock by `MyComponent#lock()`
		try (Unlocker unlocker = component.lock()) {
			...
		}
	}
}
```

```java
@Componnt
class MyComponent extends AbstractComponent {
	@Autowired
	SLong field;
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyComponent component;

	void foo() {
		// Lock by `SLong#lock()` of `MyComponent#field`
		try (Unlocker unlocker = component.field.lock()) {
			...
		}
	}
}
```

```java
@Componnt
class MyComponent {
	@Autowired
	ComponentFacade facade;
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyComponent component;

	void foo() {
		// Lock by `ComponentFacade#lock()` of `MyComponent#facade`
		try (Unlocker unlocker = component.facade.lock()) {
			...
		}
	}
}
```

Only exception to this is `@SharedComponent` instances.
Because `@SharedComponent` instances are shared among controllers,
`@SharedComponent` instances and their fields have their own locks.

```java
@SharedComponent
class MySharedComponent extends AbstractComponent {
	...
}

@Controller
class MyController {
	@Autowired
	MySharedComponent component;

	void foo() {
		// `MySharedComponent#lock()` and `MyController#lock()` are not equivalent.
		try (Unlocker unlocker = component.lock()) {
			...
		}
	}
}
```

### Locking Controller

```java
@Controller
class MyController extends AbstractController {
	void foo() {
		try (Unlocker unlocker = lock()) {
			...
		}
	}
}
```

```java
@Controller
class MyController {
	@Autowired
	ControllerFacad facade;

	void foo() {
		try (Unlocker unlocker = facade.lock()) {
			...
		}
	}
}
```

or

```java
@Controller
class MyController {
	@Autowired
	SLong field;

	void foo() {
		try (Unlocker unlocker = field.lock()) {
			...
		}
	}
}
```

### Periodic Method Calls

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime( "process" ) methods
		// with a 1000 milliseconds interval.
		interval("process", 1000);
	}

	@OnTime("process")
	void processA() {
		// Called with an interval of 1000 milliseconds *without a lock*
	}

	@OnTime("process")
	@Locked
	void processB() {
		// Called with an interval of 1000 milliseconds *with a lock*
	}
}
```

### Periodic Method Calls With Parameters

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods
		// with a 1000 milliseconds interval
		// starting after 50 milliseconds
		// with parameters "b" and 1
		interval("process", 50, 1000, "b", 1);
	}

	@OnTime
	void process(String b, int one) {
		System.out.println(b);		// Prints "b"
		System.out.println("" + one); // Prints 1
	}
}
```

### Periodic Executions of Runnables

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call the runnable
		// with a 1000 milliseconds interval.
		interval(new Runnable() {
			@Override
			void run() {
				// Called with a 1000 milliseconds interval *without a lock*.
			}
		}, 1000);
	}
}
```

### Canceling Periodic Calls From the Outside

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods
		// with a 1000 milliseconds interval.
		long id = interval("process", 1000);

		// And cancels the request.
		cancel(id);
	}

	@OnTime
	void process() {
		// Not called if canceled before being called.
	}
}
```

### Canceling Periodic Calls From the Inside

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods
		// with a 1000 milliseconds interval.
		interval("process", 1000);
	}

	@OnTime
	void process() {
		// Cancels by itself.
		cancel();
	}
}
```

### One-time Method Calls With a Delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods
		// after 1000 milliseconds.
		timeout("process", 1000);
	}

	@OnTime
	void process() {
		// Called once after 1000 milliseconds *without a lock*.
	}
}
```

### One-time Method Calls With a Delay and Parameters

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods
		// after 1000 milliseconds
		// with parameters "b" and 1.
		timeout("process", 1000, "b", 1);
	}

	@OnTime
	void process(String b, int one) {
		System.out.println(b);		// Prints "b"
		System.out.println("" + one); // Prints 1
	}
}
```

### One-time Execution of Runnables With a Delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call the runnable
		// after 1000 milliseconds.
		timeout(new Runnable() {
			@Overrid
			void run() {
				// Called once after 1000 milliseconds *without a lock*.
			}
		}, 1000);
	}
}
```

### One-time Execution of Callables With a Delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call the callable
		// after 1000 milliseconds.
		Future<String> future = timeout(new Callable<String>() {
			@Overrid
			String call() {
				// Called once after 1000 milliseconds *without a lock*.
				return "a";
			}
		}, 1000);

		System.out.println(future.get()); // Prints "a"
	}
}
```

### Canceling One-Time Method Calls

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods
		// after 1000 milliseconds.
		long id = timeout("process", 1000);

		// And cancels the request.
		cancel( id );
	}

	@OnTime
	void process() {
		// Not called if canceled before being called.
	}
}
```

### One-time Method Calls Without a Delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods immediately.
		execute("process");
	}

	@OnTime
	void process() {
		// Called immediately *without a lock*
	}
}
```

### One-time Method Calls Without a Delay and Parameters

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods immediately
		// with parameters "b" and 1.
		execute("process", "b", 1);
	}

	@OnTime
	void process() {
		System.out.println(b);		// Prints "b"
		System.out.println("" + one); // Prints 1
	}
}
```

### Canceling All Concurrent Requests

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods after 1000 milliseconds.
		timeout("process", 1000);

		// Cancels all concurrent requests issued by `execute`, `timeout`, or `interval`
		cancelAll();
	}

	@OnTime
	void process() {
		// Not called if canceled before being called.
	}
}
```

### Enabling WebWorker

The `wcardinal.worker.min.js` utilizes the WebWorker if available while the `wcardinal.min.js` does not.

```javascript
<script src="/webjars/wcardinal/wcardinal.worker.min.js"></script>
```

## Logging

### Logging (JavaScript)

```javascript
wcardinal.util.logger.info("Information");
```

Please refer to [wcardinal.util.Logger](../api/js/classes/util.logger.html).

### Logging (Java)

```java
import org.wcardinal.controller.annotation.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Controller
class MyController {
	public MyController() {
		log.info("Information");
	}
}
```

Please refer to [Spring Boot: Logging manual](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html) for details.

### Log Level (JavaScript)

```javascript
wcardinal.util.logger.setLevel("DEBUG");
```

Please refer to [wcardinal.util.Logger](../api/js/classes/util.logger.html).

### Log Level (Java)

Add the following line to your `application.properties`:

```text
logging.level.package.path=DEBUG
```

Replace the `package.path` part in the above example with the path of the package that you want to change.

Please refer to [Spring Boot: Logging manual](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html) for details.

## I18N

### I18N (JavaScript)

wcardinal provides the [util/MessageSource](../api/js/classes/util.messagesource.html) for the I18N.
This class is almost identical to the Spring's MessageSource.

The `MessageSource` in your server must implement the interface `org.wcardinal.util.message.ExposableMessageSource`.
wcardinal provides the two out-of-the-box implementations of this interface:
[org.wcardinal.util.message.ExposableReloadableResourceBundleMessageSource](../api/java/org/wcardinal/util/message/ExposableReloadableResourceBundleMessageSource.html) and
[org.wcardinal.util.message.ExposableResourceBundleMessageSource](../api/java/org/wcardinal/util/message/ExposableReloadableResourceBundleMessageSource.html).

```java
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.wcardinal.util.message.ExposableReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
	@Bean
	public MessageSource messageSource(){
		final ExposableReloadableResourceBundleMessageSource result
			= new ExposableReloadableResourceBundleMessageSource();
		result.setBasename("classpath:/i18n/messages");
		result.setDefaultEncoding("UTF-8");
		return result;
	}
}
```

And then embed a message script obtained by ExposableMessages#getScript as follows:

```java
import org.wcardinal.util.message.ExposableMessages;

@Controller
public class MessageMvcController {
	@Autowired
	ExposableMessages messages;

	@RequestMapping( "/" )
	ModelAndView en( final HttpServletRequest req ) {
		final ModelAndView mav = new ModelAndView();
		mav.addObject( "messageScript", messages.getScript( Locale.ENGLISH ) );
		mav.setViewName("sample-view");
		return mav;
	}
}
```

```html
<!-- HTML Template (sample-view.html) -->
<!-- Must be placed after the wcardinal script -->
<script th:utext="${ messageScript }"></script>
```

Translated messages can be obtained by calling `MessageSource#get` with message IDs.

```javascript
wcardinal.util.messageSource.get( 'message.id' );
```

For parameterized messages, pass parameters to `MessageSource#get`.

```javascript
wcardinal.util.messageSource.get( 'message.id', 1, '2' );
```

### I18N (Java)

```java
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.wcardinal.util.message.ExposableReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
	@Bean
	public MessageSource messageSource(){
		final ExposableReloadableResourceBundleMessageSource result
			= new ExposableReloadableResourceBundleMessageSource();
		result.setBasename("classpath:/i18n/messages");
		result.setDefaultEncoding("UTF-8");
		return result;
	}
}
```

Create the `src/main/resources/i18n/messages_en.properties` as follows:

```
title=Translated title
```

In your `@Controller` class,

```java
@Controller
public class MyController {
	@Autowired
	MessageSource messageSource;

	@OnCreate
	void init() {
		System.out.println(messageSource.get("title")); // Prints "Translated title"
	}
}
```

## Security

### Switching Controllers Based on Roles

```java
// For users with an "ADMIN" role
@Controller(name="MyController", roles="ADMIN")
class MyControllerForAdmin {

}

// For others
@Controller(name="MyController")
class MyControllerForOthers {

}
```

```html
<script src="my-controller"></script>
```

Users who have the `ADMIN` role always get a `MyControllerForAdmin` instance
because controllers are chosen by the longest match principle.

### Switching Controllers by Custom Logics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;

// For users with an "ADMIN" role
@Controller(name="MyController")
class MyControllerForAdmin {
	@OnCheck
	static boolean check(HttpServletRequest request) {
		return request.isUserInRole("ADMIN");
	}
}

// For others
@Controller(name="MyController")
class MyControllerForOthers {
	@OnCheck
	static boolean check(HttpServletRequest request) {
		return true;
	}
}
```

```html
<script src="my-controller"></script>
```

### Retrieving User Principal

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		log.info(getPrincipal());
	}
}
```

or

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.ControllerFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Controller
class MyController {
	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init(){
		log.info(facade.getPrincipal());
	}
}
```

Please refer to [org.wcardinal.controller.ControllerFacade](../api/java/org/wcardinal/controller/ControllerFacade.html).

### Retrieving Remote Address

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		log.info(getRemoteAddress());
	}
}
```

or

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.ControllerFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Controller
class MyController {
	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init() {
		log.info(facade.getRemoteAddress());
	}
}
```

Please refer to [org.wcardinal.controller.ControllerFacade](../api/java/org/wcardinal/controller/ControllerFacade.html).

### Retrieving HttpServletRequest

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnRequest;

import javax.servlet.http.HttpServletRequest;

@Controller
class MyController {
	@OnRequest
	static void onRequest(HttpServletRequest request) {
		// DO SOMETHING
	}
}
```

Note that the `onRequest(HttpServletRequest)` is a static method.
Please refer to [org.wcardinal.controller.annotation.OnRequest](../api/java/org/wcardinal/controller/annotation/OnRequest.html).

### Retrieving HttpServletRequest and Customizing Controller Attributes

```java
@Controller
class MyController extends AbstractController {
	@OnRequest
	static void onRequest(HttpServletRequest request, ControllerAttributes attributes) {
		attributes.put("name", "Cardinal");
	}

	void something() {
		System.out.println(getAttributes().get("name")); // Prints "Cardinal"
	}
}
```

Note that the `onRequest(HttpServletRequest)` is a static method.
Please refer to [org.wcardinal.controller.annotation.OnRequest](../api/java/org/wcardinal/controller/annotation/OnRequest.html).

## Configuration

### Boot-time Configuration

The following configurations are configurable in the same way as Spring Boot.
For instance, `wcardinal.message.binary.size.max=1000000` in your `application.properties` sets the maximum size of binary messages to 1MB.
Please refer to [Spring Boot: External config](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config) for details.

* `wcardinal.message.binary.size.max=500000`

  Maximum size of binary messages in bytes.

* `wcardinal.message.text.size.max=62500`

  Maximum length of UTF-8 text messages.

* `wcardinal.message.pool.size=30`

  Message pool size.

* `wcardinal.message.partial=true`

  True to allow partial messages.

* `wcardinal.message.partial.size=10000`

  Partial message size.

* `wcardinal.idle.max=15000`

  Criteria to determine whether browsers are inactive or not.
  The controllers, the instances of classes annotated with `@Controller`, that are assigned to inactive browsers are destroyed.

    * If `wcardinal.disconnection.max` is negative, the browsers that don't send any messages or heartbeats for longer than   `wcardinal.idle.max` are considered inactive.

    * If `wcardinal.disconnection.max` is not negative, the browsers that had network connections before and don't have network connections for longer than `wcardinal.disconnection.max` now are considered inactive.
      The browsers that have network connections now, or never had network connections before, and don't send heartbeats longer than `max(wcardinal.idle.max, wcardinal.disconnection.max)` are also considered inactive.

    * If the controllers have methods annotated with `@OnIdleCheck`, the browsers that those methods return negative numbers are considered inactive. `wcardinal.idle.max` and `wcardinal.disconnection.max` are not taken into the consideration in this case.

* `wcardinal.disconnection.max=-1`

  Criteria to determine whether browsers are inactive or not.
  The controllers, the instances of classes annotated with `@Controller`, that are assigned to inactive browsers are destroyed.
  Please refer to `wcardinal.idle.max` for details.

* `wcardinal.allowed-origins=`

  Comma-separated allowed origins or empty string to disallow all origins except the one a server is running at.

* `wcardinal.thread.pool.size=30`

  Thread pool size.

* `wcardinal.controller.http=true`

  True to allow HTTP requests of controllers.

* `wcardinal.controller.variable.embedding=true`

  True to allow embedding values of controller fields.

* `wcardinal.controller.variable.embedding.encoding=REPLACE`

  Encoding of embedding controller fields.
  Please refer to [org.wcardinal.configuration.ControllerVariableEncoding](../api/java/org/wcardinal/configuration/ControllerVariableEncoding.html).

* `wcardinal.io.shared=false`

  True to allow sharing a network connection among controllers on the same tab.

* `wcardinal.io.protocol.defaults=web-socket,polling-100`

  Comma-separated default protocols.

* `wcardinal.websocket.path=**/wcardinal-web-socket`

  WebSocket endpoint URL pattern.

* `wcardinal.polling.path=**/wcardinal-polling`

  Long polling endpoint URL pattern.

* `wcardinal.polling.timeout=10000`

  Long polling timeout.

* `wcardinal.sync.connect.timeout=5000`

  Server-side connect request timeout in a synchronization process.

* `wcardinal.sync.update.timeout=5000`

  Server-side update request timeout in a synchronization process.

* `wcardinal.sync.update.interval=10000`

  Server-side update request interval in a synchronization process.

* `wcardinal.sync.client.connect.timeout=5000`

  Browser-side connect request timeout in a synchronization process.

* `wcardinal.sync.client.update.timeout=5000`

  Browser-side update request timeout in a synchronization process.

* `wcardinal.sync.client.update.interval=10000`

  Browser-side update request interval in a synchronization process.

Java configuration is also supported:

```java
@Configuration
public class MyConfigurer implements WCardinalConfigurer {
	@Override
	public void configure(final WCardinalConfiguration configuration) {
		configuration.setMaximumBinaryMessageSize(1000000);
	}
}
```

Please refer to [org.wcardinal.configuration.WCardinalConfiguration](../api/java/org/wcardinal/configuration/WCardinalConfiguration.html).
