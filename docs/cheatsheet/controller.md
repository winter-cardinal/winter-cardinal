### Controller basics

```java
@Controller
class MyController {

}
```

```html
<script src="my-controller"></script>
<script>
    // `MyController` instance is available at `window.myController`.
	console.log( window.myController );
</script>
```

Please note that the default URL is the kebab-case of the class name, `my-controller`.

### Controller inheritance

```java
class MySuperController {
    @Callable
    String hello( String name ) {
        return "Hello, " + name;
    }
}

@Controller
class MyController extends MySuperController {

}
```

```javascript
console.log( myController.hello( 'John' ) ); // Prints 'Hello, John'
```

### Controller URL

```java
@Controller( "/my-controller-url" )
class MyController {

}
```

or

```java
@Controller( urls="/my-controller-url" )
class MyController {

}
```

```xml
<script src="my-controller-url"></script>
```

See the [org.wcardinal.controller.annotation.Controller](../api/java/org/wcardinal/controller/annotation/Controller.html) of the JavaDoc.

### Controller name

```java
@Controller( name="MyControllerName" )
class MyController {

}
```

```xml
<script src="my-controller-name"></script>
```

### Controller-scoped service

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.ControllerScopedService;

@ControllerScopedService
class MyControllerScopedService {

}

@Controller
class MyController {
    @Autowired
    MyControllerScopedService service;
}
```

Controller-scoped services are instantiated per controller instances.
Thus, the `service` and `component.service` are the same instance of `MyControllerScopedService`:

```java
@Component
class MyComponent {
    @Autowired
    MyControllerScopedService service;
}

@Controller
class MyController {
   @Autowired
   MyControllerScopedService service;

   @Autorired
   MyComponent component;
}
```

### Controller locales

```java
@Controller
class MyController extends AbstractController {
   void foo(){
       System.out.println( getLocale() );
   }
}
```

or

```java
@Controller
class MyController {
   @Autowired
   ControllerFacade facade;

   void foo(){
       System.out.println( facade.getLocale() );
   }
}
```

### Controller parameters

```xml
<script src="my-controller?name=John"></script>
```

```Java
import org.wcardinal.controller.AbstractController;

@Controller
class MyController extends AbstractController {
    final Log logger = LogFactory.getLog(MyController.class);

    @OnCreate
    void init(){
        logger.info( getParameter( "name" ) ); // Prints "John"
    }
}
```

or

```Java
import org.wcardinal.controller.ControllerFacade;

@Controller
class MyController {
    final Log logger = LogFactory.getLog(MyController.class);

    @Autowired
    ControllerFacade facade;

    @OnCreate
    void init(){
        logger.info( facade.getParameter( "name" ) ); // Prints "John"
    }
}
```

See the [org.wcardinal.controller.ControllerFacade](../api/java/org/wcardinal/controller/ControllerFacade.html) of the JavaDoc.

### Controller attributes

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

or

```java
@Controller
class MyController {
    @Autowired
    ControllerFacade facade;

    @OnRequest
    static void onRequest( HttpServletRequest request, ControllerAttributes attributes ){
        attributes.put( "name", "John" );
    }

    void something(){
        System.out.println( facade.getAttributes().get( "name" ) ); // Prints "John"
    }
}
```

### Controller creation/destruction handling

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnCreate;
import org.wcardinal.controller.annotation.OnDestroy;

@Controller
class MyController {
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

Field modifications made in the `@OnCreate` methods are sent to browsers as a part of HTTP responses.
If this behavior is not desirable, please use the `@OnPostCreate` instead.

### Controller post-creation handling

Methods annotated with `@OnPostCreate` are called immediately after `@OnCreate` methods.
In contrast to the `@OnCreate`, no changes made in `@OnPostCreate` methods are sent as a part of HTTP responses.

```java
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.OnPostCreate;

@Controller
class MyController {
    @OnPostCreate
    void init(){
        // Called after @OnCreate methods.
    }
}
```

### Network protocols

```java
// Allows `WebSocket` and `Long polling`
@Controller( protocols={ "websocket", "polling" } )
class MyController {

}

// Allows `WebSocket` only
@Controller( protocols="websocket" )
class MyController {

}
```

### Network reconnection

```java
@Controller( retry=@Retry( delay=5000, interval=15000 ) )
class MyController {

}
```

In the above settings, wcardinal trys to reconnect every 15 seconds, starting after 5 seconds, when a connection is lost.

### Keep alive

```java
@Controller( keepAlive=@KeepAlive(
    // Sets a HTTP session keep-alive interval to 240 seconds.
    interval=240000,

    // Sets a ping interval to 15 seconds.
    ping=15000
) )
class MyController {

}
```

* `KeepAlive#interval`

   An interval of a **HTTP** keep-alive message to keep **a HTTP session**.

* `KeepAlive#ping`

   An interval of a **non-HTTP** (i.e., WebSocket) keep-alive message to keep **a sub session**.
The sub session is quite similar to the familiar session.
However, the sub session is not shared across browser tabs of the same URL.

### Title separators

```java
@Controller( separators={ " - ", " / " } )
class MyController {

}
```

### Title separators (I18N)

```java
@Controller( separatorMessages={ "title.separator.root", "title.separator.leaf" } )
class MyController {

}
```

Add the fllowings to your `messages_en.properties`.

```
title.separator.root= -
title.separator.leaf= /
```
