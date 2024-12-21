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
	String handle(Exception e){
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
	String handle(Exception e){
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
	String handle(Exception e){
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
