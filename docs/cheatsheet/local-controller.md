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
Namely, `controller.field`, `controller.component` and `controller.component.field` are instances of [SInteger](../api/js/classes/controller_data.sinteger.html), [Component](../api/js/interfaces/controller.component.html) and [SList](../api/js/classes/controller_data.slist.html), respectively.

```javascript
controller.field.set( 1 );
controller.field.on( "value", ( e, value ) => {
	console.log( value ); // Prints 1
});

console.log( controller.component.field.size() ); // Prints 0
```

Controllers created by [Controllers#create][1] are referred to as local controllers.

[1]: ../api/js/classes/controller.controllers.html#create

### Local controller basics

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "SInteger",
	component: {
		field: "SList"
	}
});

controller.field.set( 1 );
controller.field.on( "value", ( e, value ) => {
	console.log( value ); // Prints 1
});

console.log( controller.component.field.size() ); // Prints 0
```

### Non-null local controller field

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "@NonNull SInteger"
});

controller.field.set( null ); // Throws `wcardinal.exception.NullArgumentException`
```

or

```javascript
const controller = Controllers.create({
	field: { type: "SInteger", nonnull: true }
});
```

### Uninitialized local controller field

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "@Uninitialized SInteger"
});

controller.field.on( "value", () => {
	// *Not fired* because the `field` is not initialized yet.
});
```

or

```javascript
const controller = Controllers.create({
	field: { type: "SInteger", uninitialized: true }
});
```

### Local controller fields with default values

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	field: "SInteger: 24"
});

console.log( controller.field.get() ); // Prints 24
```

or

```javascript
const controller = Controllers.create({
	field: { type: "SInteger", value: 24 }
});
```

Default values must be valid as JSON.

### @Callable methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	callable: "@Callable"
});

// Called when the `controller.callable` is called.
controller.callable.on( "call", ( e, arg ) => {
	return arg + ", World!";
});

controller.callable( "Hello" ).then(( result ) => {
	console.log( result ); // Prints "Hello, World!"
});
```

### Asynchronous @Callable methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	callable: "@Callable"
});

// Called when the `controller.callable` is called.
controller.callable.on( "call", ( e, arg ) => {
	// Returns a `thenable` or a promise.
	return new Thenable(( resolve ) => {
		setTimeout(() => {
			resolve( arg + ", World!" );
		}, 1000);
	});
});

controller.callable( "Hello" ).then(( result ) => {
	console.log( result ); // Prints "Hello, World!"
});
```

### Failing @Callable methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	callable: "@Callable"
});

// Called when the `controller.callable` is called.
controller.callable.on( "call", ( e, arg ) => {
	return Thenable.reject( "fail-reason" );
});

controller.callable( "Hello" ).catch(( reason ) => {
	console.log( reason ); // Prints "fail-reason"
});
```

### @Task methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const controller = Controllers.create({
	task: "@Task"
});

// Called when the `controller.task` is called.
controller.task.on( "call", ( e, arg ) => {
	return arg + ", World!";
});

controller.task.on( "success", ( e, result ) => {
	// Called when the task `task` succeeds.
	console.log( result ); // Prints "Hello, World!"
});

controller.task( "Hello" );
```

### Asynchronous @Task methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	task: "@Task"
});

// Called when the `controller.task` is called.
controller.task.on( "call", ( e, arg ) => {
	// Returns a `thenable` or a promise.
	return new Thenable(( resolve ) => {
		setTimeout(() => {
			resolve( arg + ", World!" );
		}, 1000);
	});
});

controller.task.on( "success", ( e, result ) => {
	// Called when the task `task` succeeds.
	console.log( result ); // Prints "Hello, World!"
});

controller.task( "Hello" );
```

### Failing @Task methods

```javascript
const Controllers = wcardinal.controller.Controllers;
const Thenable = wcardinal.util.Thenable;

const controller = Controllers.create({
	task: "@Task"
});

// Called when the `controller.task` is called.
controller.task.on( "call", ( e, arg ) => {
	return Thenable.reject( "fail-reason" );
});


controller.task.on( "fail", ( e, reason ) => {
	// Called when the task `task` fails.
	console.log( reason ); // Prints "fail-reason"
})

controller.task( "Hello" );
```
