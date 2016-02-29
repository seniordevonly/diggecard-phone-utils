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