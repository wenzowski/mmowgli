class itacs_npsrepo {

# for RHEL5
if ($operatingsystem == "RedHat" and $lsbmajdistrelease == "5") {
  package {'python-hashlib' :
    ensure => installed,
  }

  # copies our nps.repo metadata file into place
  file { 'nps.repo' :
    name    => "/etc/yum.repos.d/nps.repo",
    source  => "puppet:///modules/itacs_npsrepo/nps.repo",
    owner   => root,
    group   => root,
    mode    => 644,
    ensure => present,
    require => Package['python-hashlib'],
  }
}
 
# Redhat6
elsif ($operatingsystem == "RedHat" and $lsbmajdistrelease == "6") {

  file { 'nps.repo' :
    name    => "/etc/yum.repos.d/nps.repo",
    source  => "puppet:///modules/itacs_npsrepo/nps.repo",
    owner   => root,
    group   => root,
    mode    => 644,
    ensure => present,
  }
}

}
