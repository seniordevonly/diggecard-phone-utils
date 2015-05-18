# Git repo here:
git clone ssh://git@dev.realtap.com:2222/phone-utils

# Install Apache Maven
https://maven.apache.org/download.cgi

# Build, package and test app
mvn package

# To release, first commit and push all changes, then run:
mvn release:prepare
mvn release:perform