
# Global variables. These would be better placed in hiera.

$ntp_server_list = ["matrix.nps.edu", "pool.ntp.org" ]

# The default operation if the host doesn't match any nodes

#module {"puppetlabs/apache":
#   ensure => present,
#}

#module {"puppetlabs/stdlib":
#   ensure => present,
#}

node 'default' {

  include mmowgli_base

include mmowgli_java
include mmowgli_tomcat 
include mmowgli_mysql

include mmowgli_apache
include mmowgli_activemq
include mmowgli_zookeeper

}


