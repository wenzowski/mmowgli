
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
$smb_server_password = hiera("smb_server_password")
$mmowgli_user_password = hiera("mmowgli_user_password")
$mmowgli_samba_password = hiera("mmowgli_samba_password")
$mmowgli_database_root_password = hiera("mmowgli_database_root_password")
$mmowgli_database_application_password = hiera("mmowgli_database_application_password")


# operations done on all hosts

# assorted half-assed STIG operations
include mmowgli_base

# The front end web server
node 'mmowgli' {
  include mmowgli_java
  include mmowgli_apache
  include mmowgli_samba_client
}

# The mmowgliServer node includes database, activemq, samba, zookeeper, and any other
# centralized services.
node 'mmowgliServer' {
  include mmowgli_java
  include mmowgli_mysql
  include mmowgli_activemq
  include mmowgli_zookeeper
  include mmowgli_samba_client
  include mmowgli_samba_server
  host{"mmowgliServer":
    ensure => "present",
    comment => "MMOWGLI database server",
    ip => $ipaddress,
   } 
}

# Runs a tomcat instance. You can add more tomcat instances as needed.
# You should modify the tomcat_hosts variable in hiera if you do.

node 'tomcat1' {
  include mmowgli_java
  include mmowgli_tomcat
  include mmowgli_samba_client
  host{"tomcat1":
    ensure=>"present",
    comment => "tomcat server",
    ip => $ipaddress,
   }
}

