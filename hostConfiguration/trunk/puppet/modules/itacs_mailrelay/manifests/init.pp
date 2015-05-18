class itacs_mailrelay {

  # stop sendmail
  #service { 'sendmail':
  #  ensure => stopped,
  #  enable => false,
  #  }

  # SASL dependency package for redhat
  if ($osfamily == "RedHat") {
    package { 'cyrus-sasl-plain':
      ensure => installed,
      }
    } 
 
  # make sure the package is installed
  package { 'postfix':
    ensure => installed,
    }
 
  # make sure the service is running
  service { 'postfix':
    require => Package['postfix'],
    enable => true,
    ensure => running,
    }
 
  # set our options in the postfix file using augeas
  augeas { "main.cf":
    context => "/files/etc/postfix/main.cf",
    changes => [
      "set relayhost [smtp.nps.edu]:587",
      "set smtp_generic_maps hash:/etc/postfix/localdb/generic",
      "set smtp_sasl_auth_enable yes",
      "set smtp_use_tls yes",
      "set smtp_tls_security_level encrypt",
      "set smtp_sasl_mechanism_filter plain,login",
      "set smtp_sasl_password_maps hash:/etc/postfix/localdb/itacs_smtp_auth",
      "set smtp_sasl_security_options noanonymous,noplaintext",
      "set smtp_sasl_tls_security_options noanonymous",
      "set smtp_tls_loglevel 1",
      "set smtp_tls_CAfile /etc/postfix/localcerts/smtp.nps.edu.cer",
      "set disable_dns_lookups yes",
      ],
    require => Package['postfix'],
    notify => Service['postfix'],
    }
 
  # the folder where we store our local database files
  file {'/etc/postfix/localdb':
    ensure => directory,
    owner => 'root',
    group => 'root',
    mode => '0700',
    }

  # the folder for our ssl certificates
  file {'/etc/postfix/localcerts':
    ensure => directory,
    owner => 'root',
    group => 'root',
    mode => '0700',
    }
 
  # copy our pre-hashed sasl_passwd file
  # the master is on the puppetmaster in the module folder, and should be hashed there.
  file { '/etc/postfix/localdb/itacs_smtp_auth' :
    source => "puppet:///modules/itacs_mailrelay/itacs_smtp_auth",
    owner => root,
    group => root,
    mode => 400,
    ensure => present,
    require => [ Package['postfix'], File['/etc/postfix/localdb'] ],
    notify => Exec['postmapper'],
    }
 
  # copy our generic file into place on all nodes
  file { '/etc/postfix/localdb/generic' :
    content => template("itacs_mailrelay/generic.erb"),
    owner   => root,
    group   => root,
    mode    => 400,
    ensure => present,
    require => [ Package['postfix'], File['/etc/postfix/localdb'] ],
    notify => Exec['postmapper'],
    }

  # copy our public certificate for smtp.nps.edu
  file { '/etc/postfix/localcerts/smtp.nps.edu.cer' :
    source => "puppet:///modules/itacs_mailrelay/smtp.nps.edu.cer",
    owner => root,
    group => root,
    mode => 644,
    ensure => present,
    require => File['/etc/postfix/localcerts'],
    }
 
  # Hash the generics file if it changes
  exec { "postmapper":
    command => "/usr/sbin/postmap /etc/postfix/localdb/generic /etc/postfix/localdb/itacs_smtp_auth",
    notify => Service['postfix'],
    refreshonly => true,
    }

}
