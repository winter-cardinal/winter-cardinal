## Security

### Switching controllers based on roles

```java
// For users with an "ADMIN" role
@Controller( name="MyController", roles="ADMIN" )
class MyControllerForAdmin {

}

// For others
@Controller( name="MyController" )
class MyControllerForOthers {

}
```

```html
<script src="my-controller"></script>
```

Users who have the `ADMIN` role always get a `MyControllerForAdmin` instance
because controllers are chosen by the longest match principle.

### Switching controllers by custom logics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;

// For users with an "ADMIN" role
@Controller( name="MyController" )
class MyControllerForAdmin {
	@OnCheck
	static boolean check( HttpServletRequest request ){
		return request.isUserInRole( "ADMIN" );
	}
}

// For others
@Controller( name="MyController" )
class MyControllerForOthers {
	@OnCheck
	static boolean check( HttpServletRequest request ){
		return true;
	}
}
```

```html
<script src="my-controller"></script>
```

### Retrieving user principal

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
class MyController extends AbstractController {
	final Logger logger = LoggerFactory.getLogger(MyController.class);

	@OnCreate
	void init(){
		logger.info( getPrincipal() );
	}
}
```

or

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.ControllerFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
class MyController {
	final Logger logger = LoggerFactory.getLogger(MyController.class);

	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init(){
		logger.info( facade.getPrincipal() );
	}
}
```

Please refer to [org.wcardinal.controller.ControllerFacade](../api/java/org/wcardinal/controller/ControllerFacade.html).

### Retrieving remote address

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
class MyController extends AbstractController {
	final Logger logger = LoggerFactory.getLogger(MyController.class);

	@OnCreate
	void init(){
		logger.info( getRemoteAddress() );
	}
}
```

or

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.ControllerFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
class MyController {
	final Logger logger = LoggerFactory.getLogger(MyController.class);

	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init(){
		logger.info( facade.getRemoteAddress() );
	}
}
```

Please refer to [org.wcardinal.controller.ControllerFacade](../api/java/org/wcardinal/controller/ControllerFacade.html).

### Retrieving HttpServletRequest

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnRequest;

import javax.servlet.http.HttpServletRequest;

@Controller
class MyController {
	@OnRequest
	static void onRequest( HttpServletRequest request ){
		// DO SOMETHING
	}
}
```

Note that the `onRequest(HttpServletRequest)` is a static method.
Please refer to [org.wcardinal.controller.annotation.OnRequest](../api/java/org/wcardinal/controller/annotation/OnRequest.html).

### Retrieving HttpServletRequest and customizing controller attributes

```java
@Controller
class MyController extends AbstractController {
	@OnRequest
	static void onRequest( HttpServletRequest request, ControllerAttributes attributes ){
		attributes.put( "name", "John" );
	}

	void something(){
		System.out.println( getAttributes().get( "name" ) ); // Prints "John"
	}
}
```

Note that the `onRequest(HttpServletRequest)` is a static method.
Please refer to [org.wcardinal.controller.annotation.OnRequest](../api/java/org/wcardinal/controller/annotation/OnRequest.html).
