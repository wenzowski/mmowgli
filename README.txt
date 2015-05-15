This is the top level for the MMOWGLI project.

The application directory contains the source code for the mmowgli web application.
This code is compiled and creates the deployable war file.

The hostConfiguration directory contains various scripts to configure the
infrastructure for running mmowgli. Mmowgli must run on a linux host with a web
server, tomcat, activeMQ, zookeeper, and some other software running. The
scripts aid in this task.

The testing directory contains PhantomJS code for load testing from a headless
web browser.

The staticWebContent directory contains assorted content.
