## Field

### Accessing fields from JavaScript

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
For instance, for `SLong` class, refer to the `controller/data/SLong` of the JavaScript API Reference.

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

class MyController{
	@Autowired
	SList<Series> series;
}
```
However, the `Series` class must be serializable to/deserializable from JSON by Jackson.
Namely, all fields in the `Series` class must have getter/setter methods:

```java
class Series {
	private long time;

	long getTime(){ return time; }
	void setTime( long time ) { this.time = time; }
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

### Detecting field changes (JavaScript)

```java
@Controller
class MyController{
	@Autowired
	SLong time;
}
```

```javascript
myController.time.on('value', ( e, newValue, oldValue ) => {
	// Variables `newValue` and `oldValue` are
	// a new value and an old value of the `time`, respectively.
});

// Or use `value` event on the controller to detect the change of the `time` field.
myController.on('value', () => {
	console.log( controller.time.get() );
});
```

Refer to the `controller/data/SLong#value`,
and the `controller/Controller#event` of the JavaScript API document.

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

### Detecting field changes (Java)

```java
@Controller
class MyController {
	@Autowired
	SLong time;

	@OnChange( "time" )
	void handler( Long newValue, Long oldValue ){
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

	@OnChange( "component.time" )
	void handler( Long newValue, Long oldValue ){
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
myController.time.set( 0 ); // Throws `wcardinal.exception.UnsupportedOperationException`
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
	void onCreate(){
		timeout( "init", 1000 );


	@OnTime
	void init(){
		// Time-consuming tasks

		time.set( 42 );
	}
}
```

```javascript
myController.time.on( 'value', ( e, time ) => {
	console.log( time ); // Prints 42 after 1 second.
});
```

### Freeing fields when a synchronization is finished

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
	void init(){
		NON_FINAL_CONSTANT = 3;
	}
}
```

```javascript
console.log( myController.MyEnum.ENUM0 );       // Prints "ENUM0"
console.log( myController.STATIC_CONSTANT );    // Prints 1
console.log( myController.CONSTANT );           // Prints 2
console.log( myController.NON_FINAL_CONSTANT ); // Prints 3
```

For non-static constants, values at the time all the locked `@OnCreate` method invocations are finished are sent to browsers.
Values after `@OnCreate` methods will not be sent to browsers:

```java
@Controller
class MyController extends AbstractController {
	@Constant
	int CONSTANT; // Initialized to 0.

	@OnCreate
	void init(){
		timeout( "init", 1000 );
	}

	@OnTime
	void init(){
		CONSTANT = 1; // Browsers never see this value.
	}
}
```

```javascript
console.log( myController.CONSTANT ); // Prints 0
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

	@OnChange( "field0" )
	void foo(){
		System.out.println( field1.get() == 2 ); // Always prints true
	}
}
```

```javascript
myController.lock();
try {
	myController.field0.set( 1 );
	myController.field1.set( 2 );
} finally {
	myController.unlock();
}
```

Updated fields within the same lock is synchronized atomically.
Therefore, it is guaranteed that `field1` has `2` when `field0` is changed to `1` in servers.

### Controlling field synchronization timing (Java)

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

	void foo(){
		try( Unlocker unlocker = lock() ) {
			field0.set( 1 );
			field1.set( 2 );
		}
	}
}
```

```javascript
myController.field0.on( 'value', ( e, value ) => {
	if( value === 1 ) {
		console.log( myController.field1.get() === 2 ); // Always prints true
	}
});
```

Updated fields within the same lock is synchronized atomically.
Therefore, it is guaranteed that `field1` has `2` when `field0` is changed to `1` in browsers.
