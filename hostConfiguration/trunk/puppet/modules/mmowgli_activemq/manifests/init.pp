class mmowgli_activemq  {

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
exec { "$activemq_tarball":
    command => "tar xvzf $activemq_tarball -C /usr/java",
    cwd => "/usr/java",
    creates => "/usr/java/$activemq_version",
    logoutput => on_failure,
    path => "/usr/bin:/bin",
    before => Service["activemq"]
  }


}
