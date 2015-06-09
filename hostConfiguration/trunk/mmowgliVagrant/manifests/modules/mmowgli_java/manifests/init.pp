# java_tarball and jdk_version are picked up from hiera in /etc/puppet
# via the mmowgli_java::params class

class mmowgli_java
{

include mmowgli_java::params

file {"/usr/java":
  ensure => "directory",
  mode => 755,
  owner => "root",
  group => "root",
  before=> File["/usr/java/default", "/usr/java/latest"],
}

file{"/usr/java/default":
   ensure => 'link',
   target => "/usr/java/${mmowgli_java::params::jdk_version}",
}

file{ "/usr/java/latest":
   ensure => 'link',
   target => "/usr/java/${mmowgli_java::params::jdk_version}",
}

# unpack the java jdk tarball. Careful, the tar command is
# stored in several different places in different unices
# and versions.

exec { "untarJdk":
    command => "tar xf ${mmowgli_java::params::java_tarball}; chown -R root:root jdk*",
    cwd => "/usr/java",
    creates => "/usr/java/${mmowgli_java::params::jdk_version}",
    path => "/bin:/usr/bin:/usr/sbin",
    require => File["/usr/java"],
  }

# The source of these are the jar files in the files directory
# of this puppet module. They implement better-than-US crypto export
# capabilities.

file {"/usr/java/${mmowgli_java::params::jdk_version}/jre/lib/security/local_policy.jar":
  ensure => present,
  source => 'puppet:///modules/mmowgli_java/local_policy.jar',
  require => Exec["untarJdk"],
}

file {"/usr/java/${mmowgli_java::params::jdk_version}/jre/lib/security/US_export_policy.jar":
  ensure => present,
  source => 'puppet:///modules/mmowgli_java/US_export_policy.jar',
  require => Exec["untarJdk"],
}

}   


