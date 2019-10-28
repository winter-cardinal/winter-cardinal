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
	List<Alarm> find( String words ){
		// Search and returns alarms containing `words`.
	}
}
```

```javascript
myAlarmController.find( ... ).then(( alarms ) => {
	// Render the `alarms`.
});
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
	List<Alarm> find( String words ){
		// Search and returns alarms containing `words`.
	}
}
```

Since this new `find` method does not require a lock, users can request an another search while a previous search is running.
But let's think about the following situation:

1. Executed `find( 'A' )`.
2. Executed `find( 'B' )` after 10 seconds.
3. `find( 'B' )` finished.
4. `find( 'A' )` finished.

Obviously, the `find( 'B' )`'s result is supposed to be displayed.
But, because `find( 'B' )` finished before `find( 'A' )` did,
the application may override the `find( 'B' )`'s result with the `find( 'A' )`'s result.
To fix this issue, a version control on a browser side is required:

```javascript
let currentVersion = 0;

const renderer = ( expectedVersion, alarms ) => {
	if( expectedVersion == currentVersion ) {
		// Render the `alarms`.
	}
};

const search = ( words ) => {
	currentVersion += 1;
	myAlarmController.find( words, currentVersion )
	.then(renderer.bind( null, currntVersion ));
};

search( 'A' );
search( 'B' );
```

Things getting complicated.
Still we have not done yet.
We may need to implement features:

* Canceling the `find( 'A' )` when the `find( 'B' )`,
* Auto retry when a connection is lost.

```java
@Controller
class MyAlarmController{
	long currentVersion;

	synchronized void checkVersion( long expectedVersion ) throws IllegalStateException {
		if( currentVersion != expectedVersion ) throw new IllegalStateException();
	}

	synchronized void updateVersion( long version ) throws IllegalStateException {
		if( version <= currentVersion ) throw new IllegalStateException();
		currentVersion = version;
	}

	@Callable
	@Unlocked
	List<Alarm> find( String words, long version ) throws IllegalStateException {
		updateVersion( version );
		// Searches and returns alarms
		// if the `checkVersion( version )` does not throw an exception.
	}
}
```

```javascript
let currentVersion = 0;

const renderer = ( expectedVersion, alarms ) => {
	if( expectedVersion == currentVersion ) {
		// Render the `alarms`.
	}
};

const retry = ( expectedVersion, words, reason ) => {
	if( expectedVersion == currentVersion && reason !== 'exception' ) {
		search( words );
	}
};

const search = ( words ) => {
	currentVersion += 1;
	myAlarmController.find( words )
	.then(renderer.bind( null, currntVersion ))
	.catch(retry.bind( null, currentVersion, words ));
};

search( 'A' );
search( 'B' );
```

The `@Task` is for implementing this kind of time-consuming tasks.
With `@Task`, the above codes can be simplified as follows:

```java
@Controller
class MyAlarmController extends AbstractController {
	@Task
	List<Alarm> find( String words ) {
		// Searches and returns alarms
		// if the `AbstractController#isCanceled()` returns false.
	}
}
```

```javascript
myAlarmController.find.on( 'success', ( e, alarms ) => {
	// Render the `alarms`.
};

myAlarmController.find( 'A' );
myAlarmController.find( 'B' );
```

Please note that `@Task` methods **do not own their locks** when being called by default.
This behavior can be changed by using `@Locked`:

```java
@Controller
class MyAlarmController extends AbstractController {
	@Task
	@Locked
	List<Alarm> find( String words ) {
		// Called with a lock.
	}
}
```

### Task basics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello( String name ) {
		return "Hello, " + name;
	}
}
```

```javascript
myController.hello
.on( 'success', ( e, result ) => {
	// Called when the task `hello` succeeds.
	console.log( result ); // Prints 'Hello, John'
})
.on( 'fail', ( e, reason ) => {
	// Called when the task `hello` fails.
});

controller.hello( 'John' );
```

### Aborting a task

```java
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.TaskAbortException;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello( String name ) {
		throw new TaskAbortException( "fail-reason" );
	}
}
```

```javascript
myController.hello
.on( 'fail', ( e, reason ) => {
	console.log( reason ); // Prints "fail-reason"
});

myController.hello( 'John' );
```

### Failing a task

```java
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.TaskResults;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	TaskResult<String> hello( String name ) {
		return TaskResults.fail( "fail-reason" );
	}
}
```

```javascript
myController.hello
.on( 'fail', ( e, reason ) => {
	console.log( reason ); // Prints "fail-reason"
});

myController.hello( 'John' );
```

### Retrieving task arguments

```javascript
myController.hello( 'John' );
console.log( myController.hello.getArguments() );	// Prints [ 'John' ]
console.log( myController.hello.getArgument( 0 ) );	// Prints 'John'
```

### Retrieving a task result

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello( String name ) {
		return "Hello, " + name;
	}
}
```

```javascript
myController.hello
.on( 'success', () => {
	console.log( myController.hello.getResult() ); // Prints 'Hello, John'
});

myController.hello( 'John' );
```

### Retrieving a reason why failed

```java
import org.wcardinal.controller.TaskResult;
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	TaskResult<String> hello( String name ) {
		return TaskResults.fail( "fail-reason" );
	}
}
```

```javascript
myController.hello
.on( 'fail', () => {
	console.log( myController.hello.getReason() ); // Prints "fail-reason"
});

myController.hello( 'John' );
```

### Check whether a task is finished

```javascript
console.log( myController.hello.isDone() ); // Prints true if finished. Otherwise, prints false.
```

### Check whether a task is finished successfully

```javascript
console.log( myController.hello.isSucceeded() ); // Prints true if finished successfully. Otherwise, prints false.
```

### Check whether a task is finished unsuccessfully

```javascript
console.log( myController.hello.isFailed() ); // Prints true if finished unsuccessfully. Otherwise, prints false.
```

### Task exception handling

```java
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello( String name ) {
		throw new RuntimeException();
	}

	@TaskExceptionHandler
	String handle( Exception e ) {
		return "fail-reason";
	}
}
```

```javascript
myController.hello
.on( 'fail', ( e, reason ) => {
	console.log( reason ); // Prints "fail-reason"
});

myController.hello( 'John' );
```

If there is more than one exception handler, most specific one is chosen and executed based on types of raised exceptions and arguments of handlers:

```java
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Controller
class MyController {
	@Task
	String hello( String name ) {
		throw new RuntimeException();
	}

	@TaskExceptionHandler
	String handle( Exception e ) {
		// Never be called because the other one has the more specific signature.
		return "fail-reason-a";
	}

	@TaskExceptionHandler
	String handle( RuntimeException e ) {
		// Called because this is more specific.
		return "fail-reason-b";
	}
}
```

```javascript
myController.hello
.on( 'fail', ( e, reason ) => {
	console.log( reason ); // Prints "fail-reason-b"
});

myController.hello( 'John' );
```

If there is no appropriate handler, one of the handlers on a parent is called:

```java
import org.wcardinal.controller.TaskResults;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Task;

@Component
class MyComponent {
	@Task
	String hello( String name ) {
		throw new RuntimeException();
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;

	@TaskExceptionHandler
	String handle( Exception e ) {
		// Never be called because the other one has the more specific signature.
		return "fail-reason-a";
	}

	@TaskExceptionHandler
	String handle( RuntimeException e ) {
		// Called because this is more specific.
		return "fail-reason-b";
	}
}
```

```javascript
myController.component.hello
.on( 'fail', ( e, reason ) => {
	console.log( reason ); // Prints "fail-reason-b"
});

myController.hello( 'John' );
```
