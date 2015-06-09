# baseline for all boxes. Install some basic security 
# (I'm not claiming it will fully stig the box) and
# configure some initial system stuff.

class mmowgli_base {

include mmowgli_base::params

# include yum repositories that should be on all boxes
include epel
include rpmforge

# Firewall set to a semi-plausible initial state. 
include stig_firewall

# Antivirus
include stig_clamav

# Ensure ssh is installed, reasonable security settings, banner
include stig_sshd

# Log rotation
include stig_csi

# sudoers--set to wheel
include stig_sudo

# Password aging, hash functions to use
include stig_passwords


# the base packages that should be installed
$base_packages = [ 'fail2ban' ]

package { $base_packages:
    ensure=>"installed"
   }

# NTP server installed. The local_ntp_server_list is configured in hiera in /etc/puppet
class { '::ntp':
  servers => [ $local_ntp_server_list ],
}


# mmowgli user, for shared filesystem. Should be present on all hosts,
# including front end (readonly-mounted samba dir), tomcats, and server.
# The uid should be the same on all the hosts.

user { "mmowgli":
  ensure => "present",
  name => "mmowgli",
  password => "${mmowgli_base::params::mmowgli_user_password}",
  groups => "mmowgli",
  shell => "/bin/bash",
  home => "/home/mmowgli",
  managehome => true,
  uid => 1004,
}

# mmowgli group

group {"mmowgli":
   ensure => "present",
   name => "mmowgli",
   members => "mmowgli",
   gid => 1004,
 }


}
