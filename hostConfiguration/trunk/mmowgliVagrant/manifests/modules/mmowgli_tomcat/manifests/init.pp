class mmowgli_tomcat {

# bleah, fix this somehow. Uses global variables, looked up from
# hiera in the site.pp file. Should look them up directly here.

file {"/usr/java/apache-tomcat":
  ensure => "link",
  target=> "$tomcat_version",
}

file {"/usr/java/$tomcat_version":
  recurse => true,
  group   => "tomcat",
  owner   => "tomcat",
}

user { "tomcat":
   name => "tomcat",
   ensure => "present",
   comment => "Tomcat User",
   shell => "/sbin/nologin",
}

# sysv style init.d script. Still works on systemd hosts, should migrate
# to systemd when more widespread.

file { "/etc/init.d/tomcat":
   ensure => "present",
   source => "puppet:///modules/mmowgli_tomcat/tomcat_sysv_script",
   owner => "root",
   group => "root",
   mode =>  "755",
}

# Sets the jvmroute for AJP
file { "/usr/java/apache-tomcat/conf/server.xml":
  ensure => "present",
  content => template("mmowgli_tomcat/server.xml.erb"),
  group => "tomcat",
  owner => "tomcat",
  mode => "0665",
  require => Exec["$tomcat_tarball"],
}

# Sets placeholder password for java melody
file { "/usr/java/apache-tomcat/conf/tomcat-users.xml":
  ensure => "present",
  content => template("mmowgli_tomcat/tomcat-users.xml.erb"),
  group => "tomcat",
  owner => "tomcat",
  mode => "0665",
  require => Exec["$tomcat_tarball"],
}


# SysV-style service. Works on RHEL7 boxes, which are dual systemd/SysV.

service {"tomcat":
   provider => "redhat",
   ensure => running,
   enable => true,
   require => File['/etc/init.d/tomcat'],
}


# unpack the tarball if not present
exec { "$tomcat_tarball":
    command => "/usr/bin/tar xvzf $tomcat_tarball -C /usr/java",
    cwd => "/usr/java",
    creates => "/usr/java/$tomcat_version",
    logoutput => on_failure,
    path => "/bin:/usr/sbin:/usr/bin",
  }

}
