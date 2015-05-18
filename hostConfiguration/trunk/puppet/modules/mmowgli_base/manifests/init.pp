class mmowgli_base {

# include yum repositories that should be on all boxes
include epel
include rpmforge

# Antivirus
include stig_clamav

# Ensure ssh is installed, reasonable security settings, banner
include stig_sshd

# Log rotation
include stig_csi

# sudoers--set to wheel
include stig_sudo


# the base packages that should be installed
$base_packages = [ 'fail2ban' ]

package { $base_packages:
    ensure=>"installed"
   }

# NTP server installed
class { '::ntp':
  servers => [ $ntp_server_list ],
}

}
