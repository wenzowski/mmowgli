class mmowgli_activemq ($activemq_tarball = "/InstallFiles/apache-activemq-5.11.1-bin.tar.gz") {

# ensure activemq user present
user { "activemq":
  name => "activemq",
  ensure => present,
  shell => "/sbin/nologin", 
}

# Install service script
file {"/etc/init.d/activemq":
  source=>"puppet:///modules/mmowgli_activemq/activemq",
  before => Service["activemq"],
}

# ensure activemq is running
service {"activemq":
  name => "activemq",
  ensure => running,
  enable => true,
}


  
# unpack the tarball if not present
exec { "activemq_tarball":
    command => "/usr/bin/tar xvzf $activemq_tarball -C /usr/java",
    cwd => "/usr/java",
    creates => "/usr/java/apache-activemq-5.11.1",
    logoutput => on_failure,
    path => "/usr/bin",
  }


}
