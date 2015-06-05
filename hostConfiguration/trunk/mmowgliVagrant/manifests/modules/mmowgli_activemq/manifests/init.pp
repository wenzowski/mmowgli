class mmowgli_activemq  {

# ensure activemq user present
user { "activemq":
  name => "activemq",
  group => "activemq",
  ensure => present,
  shell => "/sbin/nologin", 
}

# Install service script
file {"/etc/init.d/activemq":
  source=>"puppet:///modules/mmowgli_activemq/activemq",
  before => Service["activemq"],
}

# ensure firewall is open for activemq. 61616 is the data port,
# and 8161 is the management port. The management port should
# be shut down if this is public facing.
firewall{'101 activemq data port':
  port => '61616',
  proto => 'tcp',
  action => accept,
}

firewall{'102 activemq management port':
  port => '8161',
  proto => 'tcp',
  action => accept,
}


# ensure activemq is running
service {"activemq":
  name => "activemq",
  ensure => running,
  enable => true,
  require =>Exec["untarJdk"],
  
}


# unpack the tarball if not present
exec { "$activemq_tarball":
    command => "tar xvzf $activemq_tarball -C /usr/java",
    cwd => "/usr/java",
    creates => "/usr/java/$activemq_version",
    logoutput => on_failure,
    path => "/usr/bin:/bin",
    before => Service["activemq"],
    require => Group["activemq"],
  }


}
