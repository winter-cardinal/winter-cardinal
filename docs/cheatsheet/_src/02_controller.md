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
