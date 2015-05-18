class itacs_jdk7 {

# all our java stuff goes to /usr/java
file {'/usr/java':
  ensure => directory,
  owner => 'root',
  group => 'root',
  mode => '0755',
  }

# setup the folder to be used for the distributed jdk
file { "/usr/java/jdk7-puppet-dist":
  ensure => directory,
  owner => 'root',
  group => 'root',
  mode => '0755',
  require => File['/usr/java'],
  }

# distribute our shell script for updating the jdk distro
file { "/etc/cron.daily/puppet-update-jdk7.sh":
  source => "puppet:///modules/itacs_jdk7/puppet-update-jdk7.sh",
  owner => 'root',
  group => 'root',
  mode => '0700',
  require => File['/usr/java/jdk7-puppet-dist'],
  }

# maintain the symbolic link at /usr/java/latest 
file { '/usr/java/latest':
  ensure => 'link',
  target => '/usr/java/jdk7-puppet-dist',
  require => File['/usr/java'],
  }

# if this is the first run, go ahead and run the jdk updater script now
exec { "puppet-update-jdk7.sh":
  path        => ["/etc/cron.daily"],
  subscribe   => File["/etc/cron.daily/puppet-update-jdk7.sh"],
  refreshonly => true
  }

}
