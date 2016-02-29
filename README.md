#Phone utils
A convenient phone library helping to work with phone numbers. 
Built on top of [Google i18n Phone number lib](https://github.com/googlei18n/libphonenumber)

## Install Apache Maven
Download Maven [here](https://maven.apache.org/download.cgi)

## Build
```
$ mvn package
```

## Test
Run test target and Jacoco code coverage.
```
$ mvn test
$ open /target/site/jacoco/index.html
```

## Release and publish
To release, first commit and push all changes, then run:
```
$ mvn release:prepare
$ mvn release:perform
```
Will be published in Diggecard (Artifactory Repository)[http://home.realtap.com/artifactory/repo/]

