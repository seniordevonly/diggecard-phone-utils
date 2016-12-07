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
Will be published in SeniorDev [Artifactory Repository](http://home.realtap.com/artifactory/repo).

[Log in here](http://home.realtap.com/artifactory) with your account

## Definitions

Definitions of vocabulary used in project 

* Country code - Integer. Examples: 380, 47, 46, etc
* National number - String. (+47)45037118, (+380)985777268
* Full phone number: + Country code and national number
* Valid phone number: means that Google library says that this is real number according to Telecom rules
* Possible number: semantically incorrect, but syntactically correct numbers. 

## Examples

### Check if number is valid
```
$ isValidPhoneNumber("+47", "45 45 45 45") => true
$ isValidPhoneNumber("+47", "85 45 45 45") => false
```

### Check if possible number
```
$ isPossibleFullPhoneNumber("+4736985214") => true
$ isPossibleFullPhoneNumber("+473698521fdsd4") => false
```

### Normalize number
```
$ normalizePhoneNumber("+1 (650) - 713 (9923)") => +16507139923
```

## Check if number has given country code (for example: Norway)
```
$ hasCountryCode(47, "+4736985214") => true
$ hasCountryCode(46, "+4736985214") => false
$ hasCountryCode(47, "+478587845454545") => false (not possible number)
```