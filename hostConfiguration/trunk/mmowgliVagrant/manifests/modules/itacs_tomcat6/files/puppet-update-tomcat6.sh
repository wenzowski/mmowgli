#!/bin/bash

# Need to use --delete to remove old jar files after upgrading
/usr/bin/rsync -av --delete whitesheep.uc.nps.edu::tomcat6 /usr/java/tomcat6-puppet-dist &>/dev/null
