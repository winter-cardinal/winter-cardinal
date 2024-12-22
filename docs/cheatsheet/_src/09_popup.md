## Popup

### Introduction

A popup is a component with visibility control and browser history binding methods.
In contrast to the page, any number of popups can be visible simultaneously.
For instance, let's consider the case:

```java
@Popup
class MyPopup {
	...
}

@Component
class MyComponent {
	@Autowired
	MyPopup myPopup3;
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup1;

	@Autowired
	MyPopup myPopup2;

	@Autowired
	MyComponent component;
}
```

In this case, `myPopup1`, `myPopup2` and `myPopup3` can be visible simultaneously.

### Popup Basics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Popup;

@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

### Showing/Hiding Popups (JavaScript)

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

```javascript
myController.myPopup.show();
myController.myPopup.hide();
```

### Showing/Hiding Popups (Java)

```java
@Popup
class MyPopup extends AbstractPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	@Callable
	void onCalled() {
		myPopup.show();
	}
}
```

or

```java
@Popup
class MyPopup {
	@Autowired
	PopupFacade facade;

	public void show() {
		facade.show();
	}

	public void hide() {
		facade.hide();
	}
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	@Callable
	void onCalled() {
		myPopup.show();
	}
}
```

### Checking Whether a Popup is Shown/Hidden (JavaScript)

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

```javascript
console.log(myController.myPopup.isShown()); // Prints true
console.log(myController.myPopup.isHidden()); // Prints false
```

### Checking Whether a Popup is Shown/Hidden (Java)

```java
import org.wcardinal.controller.AbstractPopup;

@Popup
class MyPopup extends AbstractPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo() {
		System.out.println(myPopup.isShown()); // Prints true
		System.out.println(myPopup.isHidden()); // Prints false
	}
}
```

or

```java
import org.wcardinal.controller.PopupFacade;

@Popup
class MyPopup {
	@Autowired
	PopupFacade facade;

	public boolean isShown() {
		return facade.isShown();
	}

	public boolean isHidden() {
		return facade.isHidden();
	}
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo() {
		System.out.println(myPopup.isShown()); // Prints true
		System.out.println(myPopup.isHidden()); // Prints false
	}
}
```

### Retrieving Parent (JavaScript)

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

```javascript
console.log(myController.getParent()); // Prints null
console.log(myController.myPopup.getParent() === myController); // Prints true
```

### Retrieving Parent (Java)

```java
@Popup
class MyPopup extends AbstractPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo(){
		System.out.println(getParent()); // Prints null
		System.out.println(myPopup.getParent() == this); // Prints true
	}
}
```

or

```java
@Popup
class MyPopup {
	@Autowired
	PopupFacade facade;

	Object getParent() {
		return facade.getParent();
	}
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;

	void foo() {
		System.out.println(getParent()); // Prints null
		System.out.println(myPopup.getParent() == this); // Prints true
	}
}
```

### Primary Popup

```java
@Popup
class MyPopup{
	...
}

@Controller
class MyController {
	@Autowired
	@Primary
	MyPopup myPopup; // Shown by default
}
```

If the `@Primary` is not present, `myPopup` is hidden by default.

```java
@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup; // Hidden by default because not annotated with `@Primary`.
}
```

### Detecting Popup Visibility Change (JavaScript)

```javascript
myController.myPopup.on("show", () => {
	// Called when event handlers are set if `myPopup` is shown, or after `myPopup` gets to be shown.
});

myController.myPopup.on("hide", () => {
	// Called when event handlers are set if `myPopup` is hidden, or after `myPopup` gets to be hidden.
});
```

### Detecting Popup Visibility Change (Java)

```java
@Popup
class MyPopup{
	@OnShow
	void onShow() {
		// Called after being shown.
	}

	@OnHide
	void onHide() {
		// Called after being hidden.
	}
}
```

or

```java
@Popup
class MyPopup {
	...
}

class MyController {
	@Autowired
	MyPopup myPopup;

	@OnShow("myPopup")
	void onShowMyPopup() {
		// Called after `myPopup` gets to be shown.
	}

	@OnHide("myPopup")
	void onHideMyPopup() {
		// Called after `myPopup` gets to be hidden.
	}
}
```

### Popup Lifecycle Handling

```java
import org.wcardinal.controller.annotation.Popup;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Popup
class MyPopup {
	@OnCreate
	void init() {
		// Called after instantiated.
	}

	@OnDestroy
	void destroy() {
		// Called before getting destroyed.
	}
}
```

### Popup Name in Title

```java
import org.wcardinal.controller.annotation.DisplayName;

@Popup
@DisplayName("Popup name in title")
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayName;

@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayName("Popup name in title")
	MyPopup myPopup;
}
```

### Popup Name in Title (I18N)

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Popup
@DisplayNameMessage("my-popup.name")
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	MyPopup myPopup;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Popup
class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayNameMessage("my-popup.name")
	MyPopup myPopup;
}
```

And add the followings to your `messages_en.properties`:

```text
my-popup.name=Popup name in title
```

### Creating Popups Dynamically (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;

class MyPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set(128);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;
}
```

```javascript
myController.factory.create().value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Popups Dynamically (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;

class MyPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		value.set(128);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPopup` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("create", (e, newInstance) => {
	// Called when a new instance is created.
	newInstance.value.on("value", (e, value) => {
		console.log(value); // Prints 128
	});
});
```

### Creating Popups Dynamically With Parameters (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.AbstractPopup;

class MyPopup extends AbstractPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;
}
```

```javascript
myController.factory.create(128).value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Popups Dynamically With Parameters (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.AbstractPopup;

class MyPopup extends AbstractPopup {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController extends AbstractController {
	@Autowired
	PopupFactory<MyPopup> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPopup` instance with an integer of 128.
		factory.create(128);
	}
}
```

### Destroying Dynamic Popups (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;

class MyPopup {
	...
}

@Controller
class MyController {
	@Autowired
	PopupFactory<MyPopup> factory;
}
```

```javascript
myController.factory.destroy(myController.factory.create());
```

### Destroying Dynamic Popups (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PopupFactory;
import org.wcardinal.controller.AbstractPopup;

class MyPopup extends AbstractPopup {
	@OnCreate
	void onCreate() {
		// Calls the `OnTime` method after 10 seconds.
		timeout("destroy", 10000);
	}

	@OnTime
	void onDone() {
		// Destroys itself.
		getParentAsFactory().destroy(this);
	}
}

@Controller
class MyController {
	@Autowired
	PopupFactory<MyPopup> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPopup` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("destroy", (e, newInstance) => {
	// Called when instances are destroyed.
});
```
