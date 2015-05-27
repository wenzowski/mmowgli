#!/bin/bash

# Need to use --delete to remove old jar files after upgrading
/usr/bin/rsync -av --delete whitesheep.uc.nps.edu::tomcat7 /usr/java/tomcat7-puppet-dist &>/dev/null
