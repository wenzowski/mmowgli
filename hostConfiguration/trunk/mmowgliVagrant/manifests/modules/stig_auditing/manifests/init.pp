class stig_auditing {
  
  # get the correct service name for psacct depending on O/S
  if $osfamily == "Debian" {
    $psacct_daemonname = "acct"
    $audit_packagename = "auditd"
    }
  else {
    $psacct_daemonname = "psacct"
    $audit_packagename = "audit"
    }

  # install/update the psacct package
  package { 'audit':
    name => $audit_packagename,
    ensure => installed,
    }
  
  # install/update the psacct package
  package { 'psacct':
    name => $psacct_daemonname,
    ensure => installed,
    }
  
  # control the audit service
  service { 'auditd':
    enable => true,
    ensure => running,
    require => Package['audit'],
    }
  
  # control the psacct service
  service { 'psacct':
    name => $psacct_daemonname,
    enable => true,
    ensure => stopped,
    require => Package['psacct'],
    }
  
  # control the permissions for the audit log folder
  file { '/var/log/audit' :
    owner   => root,
    group   => root,
    mode    => 0700,
    require => Package['audit'],
    }
 
  # control permissions for the audit log itself
  file { '/var/log/audit/audit.log' :
    owner   => root,
    group   => root,
    mode    => 0600,
    require => Package['audit'],
    }

  # copy our stig audit rules file into place
  file { '/etc/audit/audit.rules' :
    source  => "puppet:///modules/stig_auditing/stig.rules",
    owner => root,
    group => root,
    mode => 0400,
    ensure => present,
    notify => Service['auditd'],
    }
 
}
