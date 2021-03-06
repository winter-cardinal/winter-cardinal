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

### Installation

#### Gradle

```groovy
dependencies {
	compile 'com.github.winter-cardinal:winter-cardinal:latest.release'
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

#### CDN

```html
<script src="https://cdn.jsdelivr.net/npm/@wcardinal/wcardinal/dist/wcardinal.min.js"></script>
<script src="./my-controller"></script>
```

All the classes are in `window.wcardinal` in this case.
Note that the `wcardinal.min.js` must be loaded before loading your controller as shown above.

### Documentation

* API document
	* [Java](https://winter-cardinal.github.io/winter-cardinal/api/java/)
	* [JS](https://winter-cardinal.github.io/winter-cardinal/api/js/)
* [Cheatsheet](https://winter-cardinal.github.io/winter-cardinal/cheatsheet/all-in-one.html)

### Browser support

Supports the latest version of Chrome, Firefox, Edge and Safari.
IE9 and later are supported on Windows.

### How to build

The following commands are for building Winter Cardinal itself.
For most users, you do not need to do this.

#### JS for release

```shell
npm run build
```

#### JS for development

```shell
npm run watch:ts
```

and then in an another terminal

```shell
npm run watch:rollup
```

#### Java API document

```shell
./gradlew compileJavaApiDocument
```

#### JS API document

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
