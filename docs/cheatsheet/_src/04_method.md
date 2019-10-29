## Method

### Calling methods from JavaScript

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;

@Controller
class MyController {
	@Callable
	String hello( String name ) {
		return "Hello, "+name;
	}
}
```

```javascript
myController.hello( 'John' )
.then(( result ) => {
	// Called when the invocation of the `hello` method is finished successfully.
	console.log( result ); // Prints "Hello, John"
})
.catch(( reason ) => {
	// Called when failed.
	// `reason` contains a reason as a string.
});
```

### Adjusting method timeout (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;

@Controller
class MyController {
	@Callable
	String hello( String name ) {
		return "Hello, "+name;
	}
}
```

```javascript
// Change the timeout of the `hello` method to 10 seconds
myController.hello.timeout( 10000 ).call( 'John' );
```

### Adjusting method timeout (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.annotation.Timeout;

@Controller
class MyController {
	// Change the timeout of the `hello` method to 10 seconds
	@Callable
	@Timeout( 10000 )
	String hello( String name ) {
		return "Hello, "+name;
	}
}
```

### Method exception handling

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;

@Controller
class MyController {
	@Callable
	String hello( String name ) {
		throw new RuntimeException();
	}

	@CallableExceptionHandler
	String handle( Exception e ){
		return "fail-reason";
	}
}
```

```javascript
myController.hello( 'John' )
.catch(( reason ) => {
	console.log( reason ); // Prints `fail-reason`
});
```

If there is more than one exception handler, most specific one is chosen
and executed based on types of raised exceptions and arguments of handlers:

```java
@Controller
class MyController {
	@Callable
	String hello( String name ) {
		throw new RuntimeException();
	}

	@CallableExceptionHandler
	String handle( Exception e ){
		// Never be called because the other one has the more specific signature.
		return "fail-reason-a";
	}

	@CallableExceptionHandler
	String handle( RuntimeException e ) {
		// Called because this is more specific.
		return "fail-reason-b";
	}
}
```

```javascript
myController.hello( 'John' )
.catch(( reason ) => {
	console.log( reason ); // Prints `fail-reason-b`
});
```

If there is no appropriate handler, one of the handlers on a parent is called:

```java
@Component
class MyComponent {
	@Callable
	String hello( String name ) {
		throw new RuntimeException();
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;

	@CallableExceptionHandler
	String handle( Exception e ){
		return "fail-reason-a";
	}

	@CallableExceptionHandler
	String handle( RuntimeException e ) {
		return "fail-reason-b";
	}
}
```

```javascript
myController.component.hello( 'John' )
.catch(( reason ) => {
	console.log( reason ); // Prints `fail-reason-b`
});
```
