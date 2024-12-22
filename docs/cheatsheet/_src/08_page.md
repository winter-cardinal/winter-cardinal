## Page

### Introduction

A page is a component with visibility control and browser history binding methods.
In contrast to the popup, among pages belonging to the same class, at most one page can be visible.
For instance, let's consider the case:

```java
@Page
class MyPage {
	...
}

@Component
class MyComponent {
	@Autowired
	MyPage myPage3;
}

@Controller
class MyController {
	@Autowired
	MyPage myPage1;

	@Autowired
	MyPage myPage2;

	@Autowired
	MyComponent component;
}
```

In this case, if `myPage1` is shown, `myPage2` never be shown.
On the contrary, if `myPage2` is shown, `myPage1` never be shown.
However, `myPage1` and `myPage3` can be shown simultaneously because these two belongs to different classes.

### Page Basics

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Page;

@Page
class MyPage1 {
	...
}

@Page
class MyPage2 {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage1 myPage1;

	@Autowired
	MyPage2 myPage2;
}
```

### Showing/Hiding Pages (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
myController.myPage.show();
myController.myPage.hide();
```

### Showing/Hiding Pages (Java)

```java
import org.wcardinal.controller.AbstractPage;

@Page
class MyPage extends AbstractPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void showMyPage() {
		myPage.show();
	}

	void hideMyPage() {
		myPage.hide();
	}
}
```

or

```java
import org.wcardinal.controller.PageFacade;

@Page
class MyPage {
	@Autowired
	PageFacade facade;

	public void show() {
		return facade.show();
	}

	public void hide() {
		return facade.hide();
	}
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void showMyPage() {
		myPage.show();
	}

	void hideMyPage() {
		myPage.hide();
	}
}
```

### Retrieving Active Page (JavaScript)

```java
import org.wcardinal.controller.AbstractController;

@Page
class MyPage {
	...
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyPage myPage;
}
```

```javascript
console.log(myController.getActivePage() === myController.myPage); // Prints true
```

### Retrieving Active Page (Java)

```java
import org.wcardinal.controller.AbstractController;

@Page
class MyPage {
	...
}

@Controller
class MyController extends AbstractController {
	@Autowired
	MyPage myPage;

	void foo(){
		System.out.println(getActivePage() == myPage); // Prints true
	}
}
```

### Checking Whether a Page is Shown/Hidden (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
console.log(myController.myPage.isShown()); // Prints true
console.log(myController.myPage.isHidden()); // Prints false
```

### Checking Whether a Page is Shown/Hidden (Java)

```java
import org.wcardinal.controller.AbstractPage;

@Page
class MyPage extends AbstractPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void foo() {
		System.out.println(myPage.isShown()); // Prints true
		System.out.println(myPage.isHidden()); // Prints false
	}
}
```

### Retrieving Parent (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
console.log(myController.getParent()); // Prints null
console.log(myController.myPage.getParent() === myController); // Prints true
```

### Retrieving Parent (Java)

```java
@Page
class MyPage extends AbstractPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void foo(){
		System.out.println(getParent()); // Prints null
		System.out.println(myPage.getParent() == this); // Prints true
	}
}
```

or

```java
@Page
class MyPage {
	@Autowired
	PageFacade facade;

	Object getParent() {
		return facade.getParent();
	}
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;

	void foo() {
		System.out.println(getParent()); // Prints null
		System.out.println(myPage.getParent() == this); // Prints true
	}
}
```

### Primary Page

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage1; // Hidden by default

	@Autowired
	@Primary
	MyPage myPage2; // Shown by default
}
```

If the `@Primary` is missing, the first page in a class becomes a primary page.

```java
@Controller
class MyController {
	// Shown by default because there is no `@Primary`
	// and this is the first page in the `MyController`.
	@Autowired
	MyPage myPage1;

	@Autowired
	MyPage myPage2; // Hidden by default
}
```

### No Primary Page

```java
import org.wcardinal.controller.annotation.NoPrimaryPage;

@Page
class MyPage {
	...
}

@Controller
@NoPrimaryPage
class MyController {
	@Autowired
	MyPage myPage1; // Hidden by default

	@Autowired
	MyPage myPage2; // Hidden by default
}
```

### Detecting Page Change (JavaScript)

```java
@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

```javascript
myController.myPage.on("show", () => {
	// Called when event handlers are set if `myPage` is shown or after `myPage` gets to be shown.
});

myController.myPage.on("hide", () => {
	// Called when event handlers are set if `myPage` is hidden or after `myPage` gets to be hidden.
});

myController.on("page", (e, newPageName, oldPageName) => {
	// Called when event handlers are set or when the active page of this controller is changed.
});
```

### Detecting Page Change (Java)

```java
@Page
class MyPage {
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
@Page
class MyPage {
	...
}

class MyController {
	@Autowired
	MyPage myPage;

	@OnShow("myPage")
	void onShowMyPage(){
		// Called after `myPage` gets to be shown.
	}

	@OnHide("equipmentPage")
	void onShowMyPage(){
		// Called after `myPage` gets to be hidden.
	}

	@OnChange("page")
	void onChangePage(String newPageName, String oldPageName){
		// Called when the active page of this controller is changes.
	}
}
```

### Page Lifecycle Handling

```java
import org.wcardinal.controller.annotation.Page;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Page
class MyPage {
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

### Page Name in Title

```java
import org.wcardinal.controller.annotation.DisplayName;

@Page
@DisplayName("Page name in title")
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayName;

@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayName("Page name in title")
	MyPage myPage;
}
```

### Page Name in Title (I18N)

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Page
@DisplayNameMessage("my-page.name")
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	MyPage myPage;
}
```

or

```java
import org.wcardinal.controller.annotation.DisplayNameMessage;

@Page
class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	@DisplayNameMessage("my-page.name")
	MyPage myPage;
}
```

And add the followings to your `messages_en.properties`:

```text
my-page.name=Page name in title
```

### Creating Pages Dynamically (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		// Called when instantiated.
		value.set(128);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;
}
```

```javascript
myController.factory.create().value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Pages Dynamically (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate() {
		// Called when instantiated.
		value.set(128);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPage` instance.
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

### Creating Pages Dynamically With Parameters (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	@Autowired
	SLong value;

	@OnCreate
	void onCreate(int parameter) {
		value.set(parameter);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;
}
```

```javascript
myController.factory.create(128).value.on("value", (e, value) => {
	console.log(value); // Prints 128
});
```

### Creating Pages Dynamically With Parameters (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
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
	PageFactory<MyPage> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPage` instance with an integer of 128.
		factory.create(128);
	}
}
```

### Destroying Dynamic Pages (JavaScript)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;

class MyPage {
	...
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;
}
```

```javascript
myController.factory.destroy(myController.factory.create());
```

### Destroying Dynamic Pages (Java)

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.PageFactory;
import org.wcardinal.controller.AbstractPage;

class MyPage extends AbstractPage {
	@OnCreate
	void onCreate() {
		// Calls the `@OnTime` method after 10 seconds.
		timeout("destroy", 10000);
	}

	@OnTime
	void destroy() {
		// Destroys itself.
		getParentAsFactory().destroy(this);
	}
}

@Controller
class MyController {
	@Autowired
	PageFactory<MyPage> factory;

	@OnCreate
	void onCreate() {
		// Creates a `MyPage` instance.
		factory.create();
	}
}
```

```javascript
myController.factory.on("destroy", (e, newInstance) => {
	// Called when instances are destroyed.
});
```
