class itacs_newcert {

  # distribute the script to clear the cert and generate a new request
  file { "/puppet-newcert.sh":
    ensure => present,
    source => "puppet:///modules/itacs_newcert/puppet-newcert.sh",
    owner => 'root',
    group => 'root',
    mode => '0755',
  }

  # distribute a file to /etc/cron.d to execute the script
  file { "/etc/cron.d/puppet-newcert":
    ensure => present,
    source => "puppet:///modules/itacs_newcert/newcert-cron",
    owner => 'root',
    group => 'root',
    mode => '0644',
   }
}
