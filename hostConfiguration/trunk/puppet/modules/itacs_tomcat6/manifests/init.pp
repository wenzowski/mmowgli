class itacs_tomcat6 {

# setup the folder to be used for the distributed tomcat6
file { "/usr/java/tomcat6-puppet-dist":
  ensure => directory,
  owner => 'root',
  group => 'root',
  mode => '0755',
  }

# distribute our shell script for updating the tomcat6 distro
file { "/etc/cron.daily/puppet-update-tomcat6.sh":
  source => "puppet:///modules/itacs_tomcat6/puppet-update-tomcat6.sh",
  owner => 'root',
  group => 'root',
  mode => '0755',
  require => File['/usr/java/tomcat6-puppet-dist'],
  }

# if this is the first run, go ahead and run the tomcat6 updater script now
exec { "puppet-update-tomcat6.sh":
  path        => ["/etc/cron.daily"],
  subscribe   => File["/etc/cron.daily/puppet-update-tomcat6.sh"],
  refreshonly => true
  }

}
