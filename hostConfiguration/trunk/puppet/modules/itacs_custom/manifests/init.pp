class itacs_custom{


 augeas { "custom_puppetconfig":
  context => "/files/etc/puppet/puppet.conf",
  changes => [
    "set main/pluginsync true",
    ],
  notify => Service['puppet'],
  }

}
