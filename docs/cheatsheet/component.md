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
	void init(){
		field.set( 1 );
	}

	@Callable
	int callble(){
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
console.log( myController.component.field.get() ); // Prints 1
myController.component.callable().then(( result ) => {
	console.log( result ); // Prints 2
});
```

### Component basics

```java
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.Controller;

@Component
class MyComponent {
	@Callable
	String hello( String name ){
		return "Hello, " + name;
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent component;
}
```

```javascript
myController.component.hello( 'John' ).then(( result ) => {
	console.log( result ); // Prints "Hello, John"
});
```

### Retrieving parent (JavaScript)

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
console.log( myController.getParent() ); // Prints null
console.log( myController.myComponent.getParent() === controller ); // Prints true
```

### Retrieving parent (Java)

```java
@Component
class MyComponent extends AbstractComponent {
	...
}

@Controller
class MyController {
	@Autowired
	MyComponent myComponent;

	void foo(){
		System.out.println( getParent() ); // Prints null
		System.out.println( myComponent.getParent() == this ); // Prints true
	}
}
```

or

```java
@Component
class MyComponent {
	@Autowired
	ComponentFacade facade;

	Object getParent(){
		return facade.getParent();
	}
}

@Controller
class MyController {
	@Autowired
	MyComponent myComponent;

	void foo(){
		System.out.println( getParent() ); // Prints null
		System.out.println( myComponent.getParent() == this ); // Prints true
	}
}
```

### Component lifecycle handling

```java
import org.wcardinal.controller.annotation.Component;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Component
class MyComponent {
	@OnCreate
	void init(){
		// Called after instantiated.
	}

	@OnDestroy
	void destroy(){
		// Called before getting destroyed.
	}
}
```

### Creating components dynamically (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set( 128 );
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;
}
```

```javascript
myController.factory.create().value.on( 'value', ( e, value ) => {
	console.log( value ); // Prints 128
});
```

### Creating components dynamically (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set( 128 );
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
myController.factory.on( 'create', ( e, newInstance ) => {
	// Called when a new instance is created.
	newInstance.value.on( 'value', ( e, value ) => {
		console.log( value ); // Prints 128
	});
});
```

### Creating components dynamically with parameters (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate( int parameter ) {
		value.set( parameter );
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;
}
```

```javascript
myController.factory.create( 128 ).value.on( 'value', ( e, value ) => {
	console.log( value ); // Prints 128
});
```

### Creating components dynamically with parameters (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;

class MyComponent {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate( int parameter ) {
		value.set( parameter );
	}
}

@Controller
class MyController {
	@Autowired
	ComponentFactory<MyComponent> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyComponent` instance with a integer of 128.
		factory.create( 128 );
	}
}
```

### Destroying dynamic components (JavaScript)

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
myController.factory.destroy( controller.factory.create() );
```

### Destroying dynamic components (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.ComponentFactory;
import org.wcardinal.controller.AbstractComponent;

class MyComponent extends AbstractComponent {
	@OnCreate
	void onCreate() {
		// Calls the `@OnTime` method after 10 seconds.
		timeout( "done", 10000 );
	}

	@OnTime
	void destroy() {
		// Destroys itself.
		getParentAsFactory().destroy( this );
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
myController.factory.on( 'destroy', ( e, newInstance ) => {
	// Called when instances are destroyed.
});
```

### Sharing components among browsers

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

### Shared component lifecycle handling

```java
@SharedComponent
class MySharedComponent {
	@OnCreate
	void init(){
		// Called after instantiated.
	}

	@OnDestroy
	void destroy(){
		// Called before getting destroyed.
	}
}
```
