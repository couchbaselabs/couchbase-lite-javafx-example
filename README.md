# Couchbase Lite JavaFX Example

This project will demonstrate data synchronization between a JavaFX desktop application and Couchbase Server.

## Build Instructions

This project can be built using the command line or an IDE.  Essentially anywhere that Maven is supported.  To build an executable JAR file, run the following from a Command Prompt (Windows) or Terminal (Mac and Linux):

```sh
mvn jfx:jar
```

The above command will output an executable JAR at **target/couchbase-javafx-example-1.0-jar-with-dependencies.jar**.  If you only wish to run the project, a similar command can be used as follows:

```sh
mvn jfx:run
```

A Couchbase Sync Gateway must be configured and running to have replication support.  Otherwise all data will be saved locally on the computer where it is being run.

## Resources

Couchbase Developer Portal - [http://developer.couchbase.com](http://developer.couchbase.com)
