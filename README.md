### About

Winter Cardinal is a library for real-time web applications build on top of the Spring framework.
It is designed for making single-page applications stable against unintended network/server failures.
The controller class and its fields defined on a server will be synchronized with clones on browsers in real time.
Also enables us to call methods defined on the controller class from browsers.

```java
// Java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;

@Controller
class MyController {
	@Autowired
	SLong field;

	@Callable
	String hello( String name ){
		return "Hello, "+name;
	}
}
```

```html
<!-- HTML -->
<script src="./my-controller"></script>
<script>
myController.field.get();      // Gets a field value
myController.field.set( 128 ); // Changes a field value

// Calls `hello( String )` method
myController.hello( 'John' ).then(( result ) => {
	console.log( result ); // Prints 'Hello, John'
});
</script>
```

### Installing

#### Gradle

```groovy
dependencies {
	compile 'org.wcardinal:wcardinal:latest.release'
}
```

Client-side libraries are packed in this JAR as a WebJars.
Please find the `wcardinal.min.js` in the directory `META-INF/resources/webjars/wcardinal/${version}/`.
WebJars can be loaded from browsers as follows:

```html
<script src="webjars/wcardinal/wcardinal.min.js"></script>
```

Please note that the `${version}` part is omitted.

#### NPM

The client-side libraries are also available as a NPM package.

```shell
npm i @wcardinal/wcardinal
```

Please note that the NPM package has no default exports.

```javascript
import * as wcardinal from '@wcardinal/wcardinal';
```

### Browser support

Supports the latest version of Chrome, Firefox, Edge and Safari. IE9 and later are supported on Windows.

### License

Apache License Version 2.0
