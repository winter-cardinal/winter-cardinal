## Configuration

### Boot-time Configuration

The following configurations are configurable in the same way as Spring Boot.
For instance, `wcardinal.message.binary.size.max=1000000` in your `application.properties` sets the maximum size of binary messages to 1MB.
Please refer to [Spring Boot: External config](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config) for details.

* `wcardinal.message.binary.size.max=500000`

  Maximum size of binary messages in bytes.

* `wcardinal.message.text.size.max=62500`

  Maximum length of UTF-8 text messages.

* `wcardinal.message.pool.size=30`

  Message pool size.

* `wcardinal.message.partial=true`

  True to allow partial messages.

* `wcardinal.message.partial.size=10000`

  Partial message size.

* `wcardinal.idle.max=15000`

  Criteria to determine whether browsers are inactive or not.
  The controllers, the instances of classes annotated with `@Controller`, that are assigned to inactive browsers are destroyed.

    * If `wcardinal.disconnection.max` is negative, the browsers that don't send any messages or heartbeats for longer than   `wcardinal.idle.max` are considered inactive.

    * If `wcardinal.disconnection.max` is not negative, the browsers that had network connections before and don't have network connections for longer than `wcardinal.disconnection.max` now are considered inactive.
      The browsers that have network connections now, or never had network connections before, and don't send heartbeats longer than `max(wcardinal.idle.max, wcardinal.disconnection.max)` are also considered inactive.

    * If the controllers have methods annotated with `@OnIdleCheck`, the browsers that those methods return negative numbers are considered inactive. `wcardinal.idle.max` and `wcardinal.disconnection.max` are not taken into the consideration in this case.

* `wcardinal.disconnection.max=4000`

  Criteria to determine whether browsers are inactive or not.
  The controllers, the instances of classes annotated with `@Controller`, that are assigned to inactive browsers are destroyed.
  Please refer to `wcardinal.idle.max` for details.

* `wcardinal.allowed-origins=`

  Comma-separated allowed origins or empty string to disallow all origins except the one a server is running at.

* `wcardinal.thread.pool.size=30`

  Thread pool size.

* `wcardinal.controller.http=true`

  True to allow HTTP requests of controllers.

* `wcardinal.controller.variable.embedding=true`

  True to allow embedding values of controller fields.

* `wcardinal.controller.variable.embedding.encoding=REPLACE`

  Encoding of embedding controller fields.
  Please refer to [org.wcardinal.configuration.ControllerVariableEncoding](../api/java/org/wcardinal/configuration/ControllerVariableEncoding.html).

* `wcardinal.io.shared=false`

  True to allow sharing a network connection among controllers on the same tab.

* `wcardinal.io.protocol.defaults=web-socket,polling-100`

  Comma-separated default protocols.

* `wcardinal.websocket.path=**/wcardinal-web-socket`

  WebSocket endpoint URL pattern.

* `wcardinal.polling.path=**/wcardinal-polling`

  Long polling endpoint URL pattern.

* `wcardinal.polling.timeout=10000`

  Long polling timeout.

* `wcardinal.sync.connect.timeout=5000`

  Server-side connect request timeout in a synchronization process.

* `wcardinal.sync.update.timeout=5000`

  Server-side update request timeout in a synchronization process.

* `wcardinal.sync.update.interval=10000`

  Server-side update request interval in a synchronization process.

* `wcardinal.sync.client.connect.timeout=5000`

  Browser-side connect request timeout in a synchronization process.

* `wcardinal.sync.client.update.timeout=5000`

  Browser-side update request timeout in a synchronization process.

* `wcardinal.sync.client.update.interval=10000`

  Browser-side update request interval in a synchronization process.

Java configuration is also supported:

```java
@Configuration
public class MyConfigurer implements WCardinalConfigurer {
	@Override
	public void configure(final WCardinalConfiguration configuration) {
		configuration.setMaximumBinaryMessageSize(1000000);
	}
}
```

Please refer to [org.wcardinal.configuration.WCardinalConfiguration](../api/java/org/wcardinal/configuration/WCardinalConfiguration.html).
