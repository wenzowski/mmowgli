class stig_tomcat7 {

# setup the folder to be used for the distributed tomcat7
file { "/usr/java/tomcat7-puppet-dist":
  ensure => directory,
  owner => 'root',
  group => 'root',
  mode => '0755',
  }

# distribute our shell script for updating the tomcat7 distro
file { "/etc/cron.daily/puppet-update-tomcat7.sh":
  source => "puppet:///modules/stig_tomcat7/puppet-update-tomcat7.sh",
  owner => 'root',
  group => 'root',
  mode => '0755',
  require => File['/usr/java/tomcat7-puppet-dist'],
  }

# if this is the first run, go ahead and run the tomcat7 updater script now
exec { "puppet-update-tomcat7.sh":
  path        => ["/etc/cron.daily"],
  subscribe   => File["/etc/cron.daily/puppet-update-tomcat7.sh"],
  refreshonly => true
  }

}
