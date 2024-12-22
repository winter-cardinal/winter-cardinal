## Security

### Switching Controllers Based on Roles

```java
// For users with an "ADMIN" role
@Controller(name="MyController", roles="ADMIN")
class MyControllerForAdmin {

}

// For others
@Controller(name="MyController")
class MyControllerForOthers {

}
```

```html
<script src="my-controller"></script>
```

Users who have the `ADMIN` role always get a `MyControllerForAdmin` instance
because controllers are chosen by the longest match principle.

### Switching Controllers by Custom Logics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCheck;

// For users with an "ADMIN" role
@Controller(name="MyController")
class MyControllerForAdmin {
	@OnCheck
	static boolean check(HttpServletRequest request) {
		return request.isUserInRole("ADMIN");
	}
}

// For others
@Controller(name="MyController")
class MyControllerForOthers {
	@OnCheck
	static boolean check(HttpServletRequest request) {
		return true;
	}
}
```

```html
<script src="my-controller"></script>
```

### Retrieving User Principal

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init() {
		log.info(getPrincipal());
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

@Slf4j
@Controller
class MyController {
	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init(){
		log.info(facade.getPrincipal());
	}
}
```

Please refer to [org.wcardinal.controller.ControllerFacade](../api/java/org/wcardinal/controller/ControllerFacade.html).

### Retrieving Remote Address

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Controller
class MyController extends AbstractController {
	@OnCreate
	void init(){
		log.info(getRemoteAddress());
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

@Slf4j
@Controller
class MyController {
	@Autowired
	ControllerFacade facade;

	@OnCreate
	void init() {
		log.info(facade.getRemoteAddress());
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
	static void onRequest(HttpServletRequest request) {
		// DO SOMETHING
	}
}
```

Note that the `onRequest(HttpServletRequest)` is a static method.
Please refer to [org.wcardinal.controller.annotation.OnRequest](../api/java/org/wcardinal/controller/annotation/OnRequest.html).

### Retrieving HttpServletRequest and Customizing Controller Attributes

```java
@Controller
class MyController extends AbstractController {
	@OnRequest
	static void onRequest(HttpServletRequest request, ControllerAttributes attributes) {
		attributes.put("name", "Cardinal");
	}

	void something() {
		System.out.println(getAttributes().get("name")); // Prints "Cardinal"
	}
}
```

Note that the `onRequest(HttpServletRequest)` is a static method.
Please refer to [org.wcardinal.controller.annotation.OnRequest](../api/java/org/wcardinal/controller/annotation/OnRequest.html).
