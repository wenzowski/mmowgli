class itacs_yum {

  $dist = $operatingsystem ? {
    redhat  => "rhel${lsbmajdistrelease}s-$::architecture",
    centos  => "centos${lsbmajdistrelease}-$::architecture",
    default => undef,
  }
  $uri = "http://juju.uc.nps.edu/mrepo/$dist/RPMS"

  if $operatingsystem == redhat {
    yumrepo {
      'rhn-updates':
        descr    => 'Red Hat Enterprise Linux $releasever - $basearch',
        baseurl  => "$uri.updates",
        enabled  => 1,
        gpgcheck => 0,
        priority => 1,
    }
  }

  yumrepo {
    'epel':
      descr      => 'Extra Packages for Enterprise Linux $releasever - $basearch',
      baseurl    => "$uri.epel",
      mirrorlist => absent,
      enabled    => 1,
      gpgcheck   => 0,
      priority   => 10;
    'puppetlabs-deps':
      descr      => 'Puppet Labs Dependencies El $releasever - $basearch',
      baseurl    => "$uri.puppet-dependencies",
      mirrorlist => absent,
      enabled    => 1,
      gpgcheck   => 0;
    'puppetlabs-products':
      descr      => 'Puppet Labs Products El $releasever - $basearch',
      baseurl    => "$uri.puppet-products",
      mirrorlist => absent,
      enabled    => 1,
      gpgcheck   => 0;
    'rpmforge':
      descr      => 'RHEL $releasever - RPMforge.net - dag',
      baseurl    => "$uri.rpmforge",
      mirrorlist => absent,
      enabled    => 1,
      gpgcheck   => 0;
    'rpmforge-extras':
      descr      => 'RHEL $releasever - RPMforge.net - extras',
      baseurl    => "$uri.rpmforge-extras",
      mirrorlist => absent,
      enabled    => 0,
      gpgcheck   => 0;
  }

  file {
    '/etc/yum/pluginconf.d/rhnplugin.conf':
      ensure  => present,
      content => "[main]\nenabled=0";
  }
}
