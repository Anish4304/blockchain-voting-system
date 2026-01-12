@echo off
echo Downloading MongoDB Java Driver...
curl -L -o mongodb-driver-sync-4.11.1.jar https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.11.1/mongodb-driver-sync-4.11.1.jar
curl -L -o mongodb-driver-core-4.11.1.jar https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/4.11.1/mongodb-driver-core-4.11.1.jar
curl -L -o bson-4.11.1.jar https://repo1.maven.org/maven2/org/mongodb/bson/4.11.1/bson-4.11.1.jar
echo Done!
