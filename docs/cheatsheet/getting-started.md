### Step 1: Add compile dependency

```gradle
repositories {
	mavenCentral()
}

dependencies {
    compile 'com.github.winter-cardinal:winter-cardinal:latest.release'
}
```

### Step 2: Create a controller class

```java
import org.springframework.beans.factory.annotation.Autowired;

import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;
import org.wcardinal.controller.data.SLong;

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

Please check whether Spring does scan your controller class.
Detecting Spring your controller class, working wcardinal successfully, in a log you find:

```text
2019-10-28 22:27:52.875  INFO 19517 --- [ost-startStop-1] d.c.internal.ControllerServletLoader     : Mapping controller: 'MyController' to [/my-controller] with roles [] as 'MyController'
```

### Step 3: Load scripts

```xml
<script src="webjars/wcardinal/wcardinal.min.js"></script>
```

If the WebWorker version is required, load `wcardinal.worker.min.js` instead.
The `wcardinal.worker.min.js` utilizes the WebWorker if available while the `wcardinal.min.js` does not.

```xml
<script src="webjars/wcardinal/wcardinal.worker.min.js"></script>
```

### Step 4: Load a controller script

```xml
<script src="my-controller"></script>
```

The default URL is a kebab-case of a controller name.

### Step 5: Run and check with browsers

```text
cd your/project/directory
./gradlew bootRun
```

Then open `http://localhost:8080/` with a browser.
Please find a `MyController` clone at `window.myController`.

```javascript
console.log( myController.field.get() ); // Prints 64

myController.hello( 'John' ).then(( result ) => {
    console.log( result ); // Prints 'Hello, John'
});
```
