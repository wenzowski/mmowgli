class mmowgli_java ($tarball = "/InstallFiles/jdk-8u45-linux-x64.tar.gz",
                    $jdk_dir = "jdk1.8.0_45")
{

file {"/usr/java":
  ensure => "directory",
  mode => 755,
  owner => "root",
  group => "root",
  before=> File["/usr/java/default", "/usr/java/latest"],
}

file{"/usr/java/default":
   ensure => 'link',
   target => $jdk_dir,
}

file{ "/usr/java/latest":
   ensure => 'link',
   target => $jdk_dir,
}

file{"/usr/java/$jdk_dir":
  recurse=> true,
  owner => 'root',
  group => 'root',
  require => File["/usr/java"],
}


exec { "tar xvf $tarball":
    #logoutput => on_failure,
    cwd => "/usr/java",
    creates => "/usr/java/jdk1.8.0_45/COPYRIGHT",
    onlyif => "test ! -f /usr/java/jdk1.8.0_45",
    path => ["/usr/bin", "/usr/sbin"],
    require => File["/usr/java"],
  }

file {"/usr/java/default/jre/lib/security/local_policy.jar":
  ensure => present,
  source => 'puppet:///modules/mmowgli_java/local_policy.jar',
  require => File["/usr/java/$jdk_dir"],
}

file {"/usr/java/default/jre/lib/security/US_export_policy.jar":
  ensure => present,
  source => 'puppet:///modules/mmowgli_java/US_export_policy.jar',
}

}   


