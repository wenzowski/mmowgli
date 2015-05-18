class stig_sshd {

    # get the correct service name depending on O/S
    if $operatingsystem == "Ubuntu" {
   	$daemonname = "ssh"
      	} 
    else {
       	$daemonname = "sshd"
       	}

    # make sure the ssh package is installed obviously
    package { 'openssh-server':
        ensure => installed,
    }

    # keep it running
    service { 'sshd':
	name => $daemonname,
        require => Package['openssh-server'],
        ensure => running,
    }

    # set our options in the sshd_config file using augeas
    augeas { "sshdconfig":
        context => "/files/etc/ssh/sshd_config",
        changes => [
                "set PermitRootLogin no",
                "set LoginGraceTime 60",
                "set IgnoreRhosts yes",
                "set PermitEmptyPasswords no",
                "set PasswordAuthentication yes",
                "set ChallengeResponseAuthentication yes",
                "set UsePAM yes",
                "set AllowTcpForwarding no",
                "set ClientAliveInterval 5400",
                "set ClientAliveCountMax 0",
                "set MaxStartups 10",
                "set PermitTunnel no",
                "set Banner /etc/issue",
		"set PrintMotd no",
                ],
        notify => Service['sshd'],
        }
 
    # copy our issue file into place on all nodes
    file { 'issue' :
        name    => "/etc/issue",
        source  => "puppet:///modules/stig_sshd/issue",
        owner   => root,
        group   => root,
        mode    => 644,
        ensure => present
        }

}
