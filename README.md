### WinterCardinal

WinterCardinal is a library for real-time web applications build on top of the Spring framework.
It is designed for making single-page applications stable against unintended network/server failures.
The controller class and its fields defined on a server will be synchronized with clones on browsers in real time.
Also enables us to call methods defined on the controller class from browsers.

```java
import org.wcardinal.controller.annotation.AbstractController;
import org.wcardinal.controller.annotation.Controller;
import org.wcardinal.controller.annotation.Callable;

@Controller
class MyController extends AbstractController {
	@Autowired
	SLong field;

	@OnCreate
	void init() {
		field.set(42);
	}

	@Callable
	String hello(String name) {
		return "Hello, " + name + "!";
	}

	void method() {
		trigger("eventname", 42);
	}
}
```

```html
<script src="./my-controller"></script>
<script>
	// Accessing Fields
	console.log(myController.field.get()); // Prints 42
	myController.field.set(84);

	// Calling Methods
	console.log(await myController.hello("Cardinal")); // Prints "Hello, Cardinal!"

	// Catching Events
	myController.on("eventname", (e, value) => {
		console.log(value); // Prints 42
	});
</script>
```

### Installation

#### Gradle

```groovy
dependencies {
	compile 'com.github.winter-cardinal:winter-cardinal:latest.release'
}
```

Client-side libraries are packed in this JAR as a WebJars.
Please find the `wcardinal.worker.min.js` in the directory `META-INF/resources/webjars/wcardinal/${version}/`.
WebJars can be loaded from browsers as follows:

```html
<script src="webjars/wcardinal/wcardinal.worker.min.js"></script>
```

Please note that the `${version}` part is omitted. The worker version `wcardinal.worker.min.js` is strongly recommended over the non-worker version `wcardinal.min.js` as the `setTimeout` is not reliable in some cases.

#### NPM

The client-side libraries are also available as a NPM package.

```shell
$> npm i @wcardinal/wcardinal
```

Please note that the NPM package has no default exports.

```javascript:JavaScript
import * as wcardinal from "@wcardinal/wcardinal";
```

#### CDN

```html
<script src="https://cdn.jsdelivr.net/npm/@wcardinal/wcardinal/dist/wcardinal.worker.min.js"></script>
<script src="./my-controller"></script>
```

All the classes are in `window.wcardinal` in this case.
Note that the `wcardinal.worker.min.js` must be loaded before loading your controller as shown above.

### Documentation

* API document
	* [Java](https://winter-cardinal.github.io/winter-cardinal/api/java/)
	* [JS](https://winter-cardinal.github.io/winter-cardinal/api/js/)
* [Cheatsheet](https://winter-cardinal.github.io/winter-cardinal/cheatsheet/all-in-one.html)
* [Starter](https://github.com/winter-cardinal/winter-cardinal-starter)

### Compatibility Matrix

|WinterCardinal Version|JDK Version        |Spring Boot Version |
|--                    |--                 |--                  |
|1.0.x                 |8                  |2.2.4.RELEASE       |
|1.1.0                 |8                  |2.7.18              |
|2.0.0 to 2.2.0        |17                 |3.3.1               |

### How to Build

#### JS for Release

```shell
npm run build
```

#### JS for Development

```shell
npm run watch:ts
```

and then in an another terminal

```shell
npm run watch:rollup
```

#### Java API Document

```shell
./gradlew compileJavaApiDocument
```

#### JS API Document

```shell
npm run build:api
```

#### Cheatsheet

```shell
./gradlew compileCheatsheet
```

### Publishing

#### JARs to Sonatype

In `~/.gradle/gradle.properties`, add

```shell
signing.keyId=<SIGNING-KEYID>
signing.password=<SIGNING-PASSWORD>
signing.secretKeyRingFile=<SIGNING-SECRETRINGFILE>

ossrhUsername=<OSSRH-USERNAME>
ossrhPassword=<OSSRH-PASSWORD>
ossrhName=<OSSRH-NAME>
ossrhEMail=<OSSRH-EMAIL>

ossrhToken=<OSSRH-USER-TOKEN>
ossrhTokenPassword=<OSSRH-USER-TOKEN-PASSWORD>
```

and then execute

```shell
./gradlew publishToSonatype
./gradlew closeAndReleaseRepository
```

Or close and release via [Nexus repository manager](https://oss.sonatype.org/).

#### NPM

```shell
npm publish
```

### License

Apache License Version 2.0
