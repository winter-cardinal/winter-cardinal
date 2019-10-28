### Logging (JavaScript)

```javascript
wcardinal.util.logger.info( "Information" );
```

See [wcardinal.util.Logger](../api/js/classes/util.logger.html).

### Logging (Java)

```java
import org.wcardinal.controller.annotation.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
class MyController {
    final Logger logger = LoggerFactory.getLogger(MyController.class);

    public MyController(){
        logger.info( "Information" );
    }
}
```

See [Spring Boot: Logging manual](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html).

### Log level (JavaScript)

```javascript
wcardinal.util.logger.setLevel( 'DEBUG' );
```

See [wcardinal.util.Logger](../api/js/classes/util.logger.html).

### Log level (Java)

Add the following line to your `application.properties`:

```text
logging.level.package.path=DEBUG
```

Replace the `package.path` part in the above example with the path of the package that you want to change.

See [Spring Boot: Logging manual](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html).
