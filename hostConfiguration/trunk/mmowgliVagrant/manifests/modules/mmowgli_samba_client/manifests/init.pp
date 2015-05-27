
class mmowgli_samba_client {

package {"samba-client":
  ensure => "installed",
 }

file { ["/mmowgli", "/mmowgli/shared"]:
  ensure => "directory",
  owner => "mmowgli",
  group => "mmowgli",
  }

file { "/etc/smb-credentials.txt":
  content => template("mmowgli_samba_client/smb-credentials.txt.erb"),
  ensure => "present",
  owner  => "root",
  group => "root",
  mode => 600,
  require => File["/mmowgli/shared"],
}

#mount { "shared":
#  name => "/imports/mmowgli", 
#  atboot => "true",
#  fstype => "cifs",
#  device => "//mmowgliserver/images",
#  options => "auto,_netdev,cred=/etc/smb-credentials.txt",
#  ensure => "mounted",
#  require => File["/mmowgli/shared"],
# } 
 

}
