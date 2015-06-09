
class mmowgli_samba_client {

include mmowgli_samba_client::params

package {"samba-client":
  ensure => "installed",
 }

# Grr. For some reason the mount.cifs command is not installed
# by samba-client package. This may be a problem for RHEL5
# systems, where this apparently does not exist.

package {"cifs-utils":
  ensure => "installed",
  before => File["/mmowgli/shared/images"],
}

# Should more logically be in "/imports" or "/mnt", but for
# historical reasons (aka inertia) it's here.

file { ["/mmowgli", "/mmowgli/shared", "/mmowgli/shared/images"]:
  ensure => "directory",
  owner => "mmowgli",
  group => "mmowgli",
  mode => 755,
  }

file { "/etc/smb-credentials.txt":
  content => template("mmowgli_samba_client/smb-credentials.txt.erb"),
  ensure => "present",
  owner  => "root",
  group => "root",
  mode => 600,
  require => File["/mmowgli/shared/images"],
  notify => Mount["shared"],
}

mount { "shared":
  name => "/mmowgli/shared/images", 
  atboot => "true",
  fstype => "cifs",
  device => "//mmowgliServer/images",
  options => "auto,_netdev,cred=/etc/smb-credentials.txt",
  ensure => "mounted",
  require => File["/mmowgli/shared/images"],
 } 
 

}
