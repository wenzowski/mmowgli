class mmowgli_tomcat {

include mmowgli_tomcat::params

file {"/usr/java/apache-tomcat":
  ensure => "link",
  target=> "/usr/java/$tomcat_version",
}

# Lucene (indexing) tmp directory
file {"/tmp/mmowgliLucene":
  ensure => "directory",
  owner => "tomcat",
  group => "tomcat",
  mode => 775,
}

# We need this around to set environment variables
file {"/usr/java/${mmowgli_tomcat::params::tomcat_version}/.bash_profile":
  ensure => present,
  owner => "tomcat",
  group => "tomcat",
  mode => 644,
  source => "puppet:///modules/mmowgli_tomcat/bash_profile",
  require => Exec["${mmowgli_tomcat::params::tomcat_tarball}"],
}

file {"/usr/java/${mmowgli_tomcat::params::tomcat_version}":
  recurse => true,
  group   => "tomcat",
  owner   => "tomcat",
}

user { "tomcat":
   name => "tomcat",
   home => "/usr/java/apache-tomcat",
   ensure => "present",
   comment => "Tomcat User",
   shell => "/bin/bash",
}

# sysv style init.d script. Still works on systemd hosts, should migrate
# to systemd when more widespread.

file { "/etc/init.d/tomcat":
   ensure => "present",
   source => "puppet:///modules/mmowgli_tomcat/tomcat_sysv_script",
   owner => "root",
   group => "root",
   mode =>  755,
}

# Opens AJP port for load balancing from the front end
firewall{'130 AJP port for tomcat':
  port => "8009",
  proto => "tcp",
  action => "accept",
 }

# Sets the jvmroute for AJP
file { "/usr/java/apache-tomcat/conf/server.xml":
  ensure => "present",
  content => template("mmowgli_tomcat/server.xml.erb"),
  group => "tomcat",
  owner => "tomcat",
  mode => "0664",
  require => Exec["${mmowgli_tomcat::params::tomcat_tarball}"],
}

# Sets placeholder password for java melody
file { "/usr/java/apache-tomcat/conf/tomcat-users.xml":
  ensure => "present",
  content => template("mmowgli_tomcat/tomcat-users.xml.erb"),
  group => "tomcat",
  owner => "tomcat",
  mode => "0665",
  require => Exec["${mmowgli_tomcat::params::tomcat_tarball}"],
}


# SysV-style service. Works on RHEL7 boxes, which are dual systemd/SysV.

service {"tomcat":
   provider => "redhat",
   ensure => running,
   enable => true,
   require => [File['/etc/init.d/tomcat'],Exec["${mmowgli_tomcat::params::tomcat_tarball}"]],
}


# unpack the tarball if not present
exec { "${mmowgli_tomcat::params::tomcat_tarball}":
    command => "tar xzf ${mmowgli_tomcat::params::tomcat_tarball}",
    cwd => "/usr/java",
    creates => "/usr/java/${mmowgli_tomcat::params::tomcat_version}",
    logoutput => on_failure,
    path => "/bin:/usr/sbin:/usr/bin",
  }

}
