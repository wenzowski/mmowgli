# java_tarball and jdk_version are picked up from hiera in /etc/puppet

#$java_tarball = hiera("java_tarball")
#$jdk_version = hiera("jdk_version")

notify{"env vars are $::java_tarball and $::jdk_version": }

class mmowgli_java
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
   target => "/usr/java/$jdk_version",
}

file{ "/usr/java/latest":
   ensure => 'link',
   target => "/usr/java/$jdk_version",
}

file{"/usr/java/$jdk_version":
  recurse=> true,
  owner => 'root',
  group => 'root',
  require => File["/usr/java"],
}

# unpack the java jdk tarball
exec { "tar xvf $java_tarball":
    cwd => "/usr/java",
    creates => "/usr/java/$jdk_version",
    path => ["/usr/bin", "/usr/sbin"],
  #  require => File["/usr/java"],
  }

# The source of these are the jar files in the files directory
# of this puppet module. They implement better-than-US crypto export
# capabilities.

file {"/usr/java/$jdk_version/jre/lib/security/local_policy.jar":
  ensure => present,
  source => 'puppet:///modules/mmowgli_java/local_policy.jar',
  require => File["/usr/java/$jdk_version"],
}

file {"/usr/java/$jdk_version/jre/lib/security/US_export_policy.jar":
  ensure => present,
  source => 'puppet:///modules/mmowgli_java/US_export_policy.jar',
  require => File["/usr/java/$jdk_version"],
}

}   


