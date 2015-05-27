# sets SSHD config according to STIG, relies on the augeas
# GEN0001120
class itacs_stig::sshd {

    if ( $verbose == "yes" ) or ( $verbose == true ) { notify {"itacs_stig::sshd":  message => "itacs_stig::sshd" } }

   if ( $operatingsystem == debian ) or ( $operatingsystem == ubuntu ) {
       $service_name = 'ssh'
   }
   else {
       $service_name = 'sshd'
   }

   service { 'sshd':
           name => $service_name,
           ensure => running,
           enable => true,
           hasrestart => true,
           hasstatus => true,
   }

   augeas { "sshd_config":
         context => "/files/etc/ssh/sshd_config",
         changes => ["set Port 22",
                     "set Protocol 2",
                     "set PrintMotd no",
                     "set PermitRootLogin no",
                     "set SyslogFacility AUTHPRIV",
                     "set LogLevel INFO",
                     "set Banner /etc/issue.net",
                     "set IgnoreRhosts yes",
                     "set PermitEmptyPasswords no",
                     "set UsePAM yes",
                     "set LoginGraceTime 60",
                     "set ChallengeResponseAuthentication yes",
                     "set AllowTcpForwarding no",
                     "set ClientAliveInterval 5400",
                     "set ClientAliveCountMax 0",
                     "set MaxStartups 10:30:100",
                     "set PermitTunnel no",
                    ],
         notify => Service['sshd'],
   }

}
