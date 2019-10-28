## Threading

### Introduction

Each instance of controllers has a lock and is locked automatically
when `@OnChange` / `@Callable` methods are called by wcardinal and when fields of types in the package `org.wcardinal.controller.data` are changed.
Thus, basically, such methods and fields are thread safe.

```java
@Controller
class MyController {
	@Callable
	void foo(){
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
	void init(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				// Thread safe because the `SLong#set( Long )` is thread safe.
				field.set( 0 );
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
	void init(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				// Not thread safe because the `foo` is not thread safe.
				foo();
			}
		}).start();
	}

	// Non-thread safe
	void foo(){
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
	void init(){
	   timeout( "bar", 0 );
	}

	@OnTime
	void bar(){
		// Not thread safe because the `foo` is not thread safe.
		foo();
	}

	// Non-thread safe
	void foo(){
		...
	}
}
```

There are two ways to fix this: making `MyController#foo()` thread safe

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
	   timeout( "bar", 0 );
	}

	@OnTime
	void bar(){
		// Thread safe because the `foo` is thread safe.
		foo();
	}

	// Thread safe because the `foo` acquires a lock inside.
	void foo(){
		try( Unlocker unlocker = lock() ){
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
	void init(){
	   timeout( "bar", 0 );
	}

	@OnTime
	@Locked
	void bar(){
		// Thread safe because the `bar` is annotated with a `@Locked` annotation.
		// A thread that executes the `bar` acquires a lock before calling the `bar`.
		foo();
	}

	// Non-thread safe
	void foo(){
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
	void onCreate(){
	   // Called without a lock.
	}

	@OnTime
	@Locked
	void onTime(){
	   // Called with a lock.
	}

	@Callable
	@Unlocked
	void callable(){
		// Called without a lock.
	}

	@Task
	@Locked
	void task(){
		// Called with a lock.
	}

	@OnChange
	@Unlocked
	void onChange(){
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
	void foo(){
		// Lock by `AbstractController#lock()`
		try( Unlocker unlocker = lock() ){
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

	void foo(){
		// Lock by `SLong#lock()` of `field`
		try( Unlocker unlocker = field.lock() ){
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

	void foo(){
		// Lock by `MyComponent#lock()`
		try( Unlocker unlocker = component.lock() ){
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

	void foo(){
		// Lock by `SLong#lock()` of `MyComponent#field`
		try( Unlocker unlocker = component.field.lock() ){
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

	void foo(){
		// Lock by `ComponentFacade#lock()` of `MyComponent#facade`
		try( Unlocker unlocker = component.facade.lock() ){
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

	void foo(){
		// `MySharedComponent#lock()` and `MyController#lock()` are not equivalent.
		try( Unlocker unlocker = component.lock() ){
			...
		}
	}
}
```

### Locking controller

```java
@Controller
class MyController extends AbstractController {
	void foo(){
		try( Unlocker unlocker = lock() ){
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

	void foo(){
		try( Unlocker unlocker = facade.lock() ){
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

	void foo(){
		try( Unlocker unlocker = field.lock() ){
			...
		}
	}
}
```

### Periodic method calls

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime( "process" ) methods
		// with a 1000 milliseconds interval.
		interval( "process", 1000 );
	}

	@OnTime( "process" )
	void processA(){
		// Called with an interval of 1000 milliseconds *without a lock*
	}

	@OnTime( "process" )
	@Locked
	void processB(){
		// Called with an interval of 1000 milliseconds *with a lock*
	}
}
```

### Periodic method calls with parameters

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call @OnTime methods
		// with a 1000 milliseconds interval
		// starting after 50 milliseconds
		// with parameters "b" and 1
		interval( "process", 50, 1000, "b", 1 );
	}

	@OnTime
	void process( String b, int one ){
		System.out.println( b );		// Prints "b"
		System.out.println( "" + one ); // Prints 1
	}
}
```

### Periodic executions of runnables

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		// Requests to call the runnable
		// with a 1000 milliseconds interval.
		interval( new Runnable(){
			@Override
			void run(){
				// Called with a 1000 milliseconds interval *without a lock*.
			}
		}, 1000 );
	}
}
```

### Canceling periodic calls from the outside

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods
		// with a 1000 milliseconds interval.
		long id = interval( "process", 1000 );

		// And cancels the request.
		cancel( id );
	}

	@OnTime
	void process(){
		// Not called if canceled before being called.
	}
}
```

### Canceling periodic calls from the inside

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods
		// with a 1000 milliseconds interval.
		interval( "process", 1000 );
	}

	@OnTime
	void process(){
		// Cancels by itself.
		cancel();
	}
}
```

### One-time method calls with a delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods
		// after 1000 milliseconds.
		timeout( "process", 1000 );
	}

	@OnTime
	void process(){
		// Called once after 1000 milliseconds *without a lock*.
	}
}
```

### One-time method calls with a delay and parameters

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods
		// after 1000 milliseconds
		// with parameters "b" and 1.
		timeout( "process", 1000, "b", 1 );
	}

	@OnTime
	void process( String b, int one ){
		System.out.println( b );		// Prints "b"
		System.out.println( "" + one ); // Prints 1
	}
}
```

### One-time execution of runnables with a delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call the runnable
		// after 1000 milliseconds.
		timeout( new Runnable(){
			@Overrid
			void run(){
				// Called once after 1000 milliseconds *without a lock*.
			}
		}, 1000 );
	}
}
```

### One-time execution of callables with a delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call the callable
		// after 1000 milliseconds.
		Future<String> future = timeout( new Callable<String>(){
			@Overrid
			String call(){
				// Called once after 1000 milliseconds *without a lock*.
				return "a";
			}
		}, 1000 );

		System.out.println( future.get() ); // Prints "a"
	}
}
```

### Canceling one-time method calls

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods
		// after 1000 milliseconds.
		long id = timeout( "process", 1000);

		// And cancels the request.
		cancel( id );
	}

	@OnTime
	void process(){
		// Not called if canceled before being called.
	}
}
```

### One-time method calls without a delay

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods immediately.
		execute( "process" );
	}

	@OnTime
	void process(){
		// Called immediately *without a lock*
	}
}
```

### One-time method calls without a delay and parameters

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods immediately
		// with parameters "b" and 1.
		execute( "process", "b", 1 );
	}

	@OnTime
	void process(){
		System.out.println( b );		// Prints "b"
		System.out.println( "" + one ); // Prints 1
	}
}
```

### Canceling all concurrent requests

```java
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		// Requests to call @OnTime methods after 1000 milliseconds.
		timeout( "process", 1000 );

		// Cancels all concurrent requests issued by `execute`, `timeout`, or `interval`
		cancelAll();
	}

	@OnTime
	void process(){
		// Not called if canceled before being called.
	}
}
```

### Enabling WebWorker

The `wcardinal.worker.min.js` utilizes the WebWorker if available while the `wcardinal.min.js` does not.

```javascript
<script src="/webjars/wcardinal/wcardinal.worker.min.js"></script>
```
