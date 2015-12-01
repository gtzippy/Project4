Project 4 webapp and server README

Notes:

This application requires the use of the Gurobi software package version 6.0.5.  Installation and condifuration of Gurobi is considered OUT OF SCOPE for this document, beyond what will be discussed below.

This application is designed to be deployed as a web app in a standard JEE app server, such as Glassfish.  Installation and configuration of an app server is considered OUT OF SCOPE for this document.

This application requires the use of a MySQL database in a default and highly insecure manner, i.e. on the default port with no password on the "root" user.  Installation and configuration of MySQL is considered OUT OF SCOPE for this document.

The application is built with the Maven software project management and build comprehension tool.  Installation of the maven tool is system dependent and considered OUT OF SCOPE for this document.

There are configuration issues that should be addressed in making Gurobi available to the app server which are dependent on the choice of Operating System, App Server, and Gurobi version.  In brief, the system must be configured in such a way as to allow the app server to locate the Gurobi native libraries needed by the application.  For example, if using Gruobi 6.0.5, Linux, and Glassfish, it requires a two step process.  For example:

1. Add the path to the Gurobi native library files to the java.library.path parameter.
2. Add the path to the Gurobi native library files to the Linux /etc/ld.so.conf.d directory.

There is one piece of setup to correctly build the app beyond installing the required software: the Gurobi jar must be stored in the local Maven repository so that it can get packaged into the war file.  This is done by executing the maven command:

mvn install:install-file -Dfile=$GUROBI_HOME/lib/gurobi.jar -DgroupId=gurobi -DartifactId=gurobi -Dversion=6.5.0 -Dpackaging=jar


Application build:

The app is built with a single mave command.  From the MavenRest directory (the directory containing pom.xml) issue the command:

mvn war:war

This will build all of the source code and package it into a deployable war file located in the "target" directory of the project.

Deploy the resulting war file to the app server of your choice.  Note that a container such as Glassfish will automatically set the "context root" of the app to the name of the war file, which in this case would be MavenRest-0.0.1-SNAPSHOT.  Make sure that the context root is just "MavenRest", or the UI won't work.