class itacs_denyhosts {

# ensure that all linux nodes have the latest denyhosts package installed
package { 'denyhosts':
 ensure => installed,
}

# make sure denyhosts is running on all linux nodes
service { 'denyhosts':
  ensure => running,
  require => Package[denyhosts],
}

# distribute our master copy of the allowed-hosts file, which contains ITACS vuln scanners (as well as whitesheep)
file { "/var/lib/denyhosts/allowed-hosts":
  source => "puppet:///modules/itacs_denyhosts/allowed-hosts",
  owner => 'root',
  group => 'root',
  mode => '0600',
  require => Package[denyhosts],
  }


}
