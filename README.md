#Phone utils
A convenient phone library helping to work with phone numbers. 
Built on top of [Google i18n Phone number lib](https://github.com/googlei18n/libphonenumber)

## Install Apache Maven
Download Maven [here](https://maven.apache.org/download.cgi)

## Build, package and test app
```
$ mvn package
$ mvn test
```

## Release
To release, first commit and push all changes, then run:
```
$ mvn release:prepare
$ mvn release:perform
```

## Test coverage Jacoco
Run test target. Output file in HTML.
```
$ mvn test
$ open /target/site/jacoco/index.html
```