
# Global variables. This is crap, I'll fix it later. It's
# halfway there--look up the variables in hiera, except the
# lookup variables should not be global and should be in
# the modules.

$local_ntp_server_list = hiera("local_ntp_server_list")
$jdk_version = hiera("jdk_version")
$java_tarball = hiera("java_tarball")
$activemq_tarball = hiera("activemq_tarball")
$activemq_version = hiera("activemq_version")
$zookeeper_tarball = hiera("zookeeper_tarball")
$zookeeper_version = hiera("zookeeper_version")
$tomcat_tarball = hiera("tomcat_tarball")
$tomcat_version = hiera("tomcat_version")
$java_melody_password = hiera("java_melody_password")
$tomcat_hosts = hiera("tomcat_hosts")

# operations done on all hosts

# assorted half-assed STIG operations
include mmowgli_base

# The default operation if the host doesn't match any nodes

node 'default' {

include mmowgli_java
include mmowgli_tomcat 
include mmowgli_mysql

include mmowgli_apache
include mmowgli_activemq
include mmowgli_zookeeper

}


