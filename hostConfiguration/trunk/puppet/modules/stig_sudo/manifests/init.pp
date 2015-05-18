class stig_sudo {

# make sure the sudo package is installed
# (never assume anything!)
package { 'sudo':
        ensure => installed,
        }

# make sure the wheel group exists
group {'wheel':
	ensure => present,
	}

# make sure the wheel group can use all sudo commands
augeas { 'sudowheel':
        context => '/files/etc/sudoers',
        changes => [
                'set spec[user = "%wheel"]/user %wheel',
                'set spec[user = "%wheel"]/host_group/host ALL',
                'set spec[user = "%wheel"]/host_group/command ALL',
                'set spec[user = "%wheel"]/host_group/command/runas_user ALL',
                ]
        }
}
