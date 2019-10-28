## Event

### Trigger events (server to browser)

```java
@Controller
class MyController extends AbstractController {
	void triggerReadyEvent() {
		trigger( "ready", 1, 2, 3 );
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
		facade.trigger( "ready", 1, 2, 3 );
	}
}
```

```javascript
myController.on( 'ready', ( e, p1, p2, p3 ) => {
	console.log( p1, p2, p3 ); // Prints 1, 2, 3
});
```

### Trigger events (server to server)

```java
@Component
class MyComponent extends AbstractComponent {
	void foo() {
		notify( "ready", 1, 2, 3 );
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;

	@OnNotice( "component.ready" )
	void bar( int p1, int p2, int p3 ) {
		// Called when `component` raises a "ready" event.
		System.out.println( String.format( "%d, %d, %d" ) ); // Prints "1, 2, 3"
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
		facade.notify( "ready", 1, 2, 3 );
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyComponent component;

	@OnNotice( "component.ready" )
	void bar( int p1, int p2, int p3 ) {
		// Called when `component` raises a "ready" event.
		System.out.println( String.format( "%d, %d, %d" ) ); // Prints "1, 2, 3"
	}
}
```

### Retrieving returned values of event handlers (server to browser)

```java
import java.util.List;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import com.fasterxml.jackson.databind.JsonNode;

import org.wcardinal.controller.TriggerErrors;

@Controller
class MyController extends AbstractController {
	void triggerReadyEvent() {
		triggerAndWait( "ready", 1000, 1, 2, 3 )
		.done(new DoneCallback<List<JsonNode>>(){
			@Override
			public void onDone( List<JsonNode> result ) {
				System.out.println( result ); // Prints [ 6 ]
			}
		});
	}
}
```

```javascript
myController.on( 'ready', ( e, p1, p2, p3 ) => {
	return p1 + p2 + p3;
});
```
