class mmowgli_zookeeper ($zookeeper_tarball = "/InstallFiles/zookeeper-3.4.6.tar.gz") {

# ensure activemq user present
user { "zookeeper":
  name => "zookeeper",
  ensure => present,
  shell => "/sbin/nologin",
}

# init script for zookeeper
file {"/etc/init.d/zookeeper":
  name=>"/etc/init.d/zookeeper",
  ensure => present,
  owner => "root",
  group => "root",
  mode => "755",
  source => "puppet:///modules/mmowgli_zookeeper/zookeeper",
  before => Service["zookeeper"],
}

file {"/usr/java/zookeeper-3.4.6/conf/zoo.cfg":
  ensure => present,
  owner => "zookeeper",
  group => "zookeeper",
  source => "puppet:///modules/mmowgli_zookeeper/zoo.cfg",
  before => Service["zookeeper"],
}


file{"/usr/java/zookeeper-3.4.6":
  owner => "zookeeper",
  group => "zookeeper",
  ensure => directory,
  recurse => true,
  before => File["/usr/java/zookeeper"],
}


file{"/usr/java/zookeeper":
  ensure => "link",
  target => "/usr/java/zookeeper-3.4.6",
}

# Service for zookeeper
service{ "zookeeper":
  ensure => running,
}

# unpack the tarball if not present
exec { "zookeeper_tarball":
    command => "/usr/bin/tar xvzf $zookeeper_tarball -C /usr/java",
    cwd => "/usr/java",
    creates => "/usr/java/zookeeper-3.4.6",
    logoutput => on_failure,
    path => "/usr/bin",
    before => File["/usr/java/zookeeper-3.4.6"],
  }


}

