class mmowgli_tomcat ($tomcat_tarball = "/InstallFiles/apache-tomcat-7.0.61.tar.gz") {

# bleah, fix this somehow

file {"/usr/java/apache-tomcat":
  ensure => "link",
  target=> "apache-tomcat-7.0.61",
}

file {"/usr/java/apache-tomcat-7.0.61":
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
    creates => "/usr/java/apache-tomcat-7.0.61",
    logoutput => on_failure,
    path => "/usr/bin",
  }

}
