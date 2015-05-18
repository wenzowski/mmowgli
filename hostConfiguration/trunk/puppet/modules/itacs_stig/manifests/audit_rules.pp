# GEN  002720 002740 002750 002751 002752 002753 002800 002820 002825
#      002720-2,3,4,5 002740-2 002760-2,3,4,5,6,7,8,9,10 002820-2,3,4,5,6,7,8,9,10,11,12,13 002825-3,4,5
class itacs_stig::audit_rules
{
       if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "itacs_stig::audit_rules": } }

       if ( $operatingsystem == debian ) or ( $operatingsystem == ubuntu ) {
			$audit_daemon_name = 'auditd'
			$acct_daemon_name = 'acct'
	}
	else {
		        $audit_daemon_name = 'audit'
			$acct_daemon_name = 'psacct'
        }

        package { 'audit':
                  name => $audit_daemon_name,
                  ensure => installed,
         }

        package { 'psacct':
                  name => $acct_daemon_name,
                  ensure => installed,
         }

	file { 'audit_rules':
		path	=> '/etc/audit/audit.rules',
		ensure  => present,
                owner => 'root',
                group => 'root',
	        mode	=> 0444,
                require => Package['audit'],
		source	=> "puppet:///modules/itacs_stig/audit.rules",
                notify => Service['auditd'],           # restart after the rules are updated
       }

       # control the audit service
       service { 'auditd':
             enable => true,
             ensure => running,
             require => Package['audit'],
       }
  

       service { 'psacct':
             name => $acct_daemon_name,
             enable => true,
             ensure => running,
             require => Package['psacct'],
       }
  
      

}
