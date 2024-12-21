## Task

### Introduction

Let us think about an application which searches and displays alarms containing given words when a user pushes an search button.
The simplest implementation looks like this:

```java:Java
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

```javascript:JavaScript
const alarms = await myAlarmController.find(...);
// Render the `alarms`.
```

Simple and works fine. However, if the `find` does take few minutes, what happens?
`@Callable` methods own their locks when being called by default.
Therefore, the user who pushed the search button can not change the search words until the `find` finishes.
It may not be acceptable.
This lock behavior of `@Callable` methods can be overridden by using `@Unlocked`:

```java:Java
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

```javascript:JavaScript
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

```java:Java
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

```javascript:JavaScript
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

```java:Java
@Controller
class MyAlarmController extends AbstractController {
	@Task
	List<Alarm> find(String words) {
		// Searches and returns alarms
		// if the `AbstractController#isCanceled()` returns false.
	}
}
```

```javascript:JavaScript
myAlarmController.find.on("success", (e, alarms) => {
	// Render the `alarms`.
};

myAlarmController.find("A");
myAlarmController.find("B");
```

Please note that `@Task` methods **do not own their locks** when being called by default.
This behavior can be changed by `@Locked`:

```java:Java
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

```java:Java
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

```javascript:JavaScript
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

### Type Declaration for TypeScript

In the TypeScript projects, the type declaration of `MyController` shown in above will look like this.

```typescript:TypeScript
	import { controller } from "@wcardinal/wcardinal";

	interface MyController extends controller.Controller {
		hello: controller.Task<string, [name: string]>;
	}
```

> [!NOTE]
> In the versions prior to 2.2.0, the type declaration of `MyController` will look like the following.
> Otherwise, `myController.hello("Cardinal")` doesn't compile.
>
> ```typescript:TypeScript
> 	import { controller } from "@wcardinal/wcardinal";
>
>	interface MyController extends controller.Controller {
>		hello: controller.Task<string, [name: string]> & controller.TaskCall<[name: string], string>;
>	}
> ```

### Aborting a Task

```java:Java
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

```javascript:JavaScript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

### Failing a Task

```java:Java
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

```javascript:JavaScript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

### Retrieving Task Arguments

```javascript:JavaScript
myController.hello("Cardinal");
console.log(myController.hello.getArguments());	// Prints ["Cardinal"]
console.log(myController.hello.getArgument(0));	// Prints "Cardinal"
```

### Retrieving a Task Result

```java:Java
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

```javascript:JavaScript
myController.hello.on("success", () => {
	console.log(myController.hello.getResult()); // Prints "Hello, Cardinal!"
});

myController.hello("Cardinal");
```

### Retrieving a Reason Why Failed

```java:Java
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

```javascript:JavaScript
myController.hello.on("fail", () => {
	console.log(myController.hello.getReason()); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

### Check Whether a Task Is Finished

```javascript:JavaScript
console.log(myController.hello.isDone()); // Prints true if finished. Otherwise, prints false.
```

### Check Whether a Task Is Finished Successfully

```javascript:JavaScript
console.log(myController.hello.isSucceeded()); // Prints true if finished successfully. Otherwise, prints false.
```

### Check Whether a Task Is Finished Unsuccessfully

```javascript:JavaScript
console.log(myController.hello.isFailed()); // Prints true if finished unsuccessfully. Otherwise, prints false.
```

### Task Exception Handling

```java:Java
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

```javascript:JavaScript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason"
});

myController.hello("Cardinal");
```

If there is more than one exception handler, most specific one is chosen
and executed based on types of raised exceptions and arguments of handlers:

```java:Java
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

```javascript:JavaScript
myController.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason-b"
});

myController.hello("Cardinal");
```

If there is no appropriate handler, one of the handlers on a parent is called:

```java:Java
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

```javascript:JavaScript
myController.component.hello.on("fail", (e, reason) => {
	console.log(reason); // Prints "fail-reason-b"
});

myController.hello("Cardinal");
```
