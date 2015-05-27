#	ntp-class1.pp
# Handles STIG GEN000250-253 
class itacs_stig::ntp	{

                if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "STIG":
                     message => "itacs_stig::ntp GEN000250 251 252 253",
                 } }

		case	$operatingsystem	{
				centos,	redhat, fedora:	{	
						$service_name	=	'ntpd'
						$conf_template	=	'ntp.conf.el.erb'
                                                $conf_file	=	'ntp.conf.el'
                                              $default_servers = [ "tick.usno.navy.mil", "tock.usno.navy.mil", ]
				}
				debian,	ubuntu:	{	
						$service_name	=	'ntp'
						$conf_template	=	'ntp.conf.debian.erb'
						$conf_file	=	'ntp.conf.debian'
                                                $default_servers = [ "tock.usno.navy.mil iburst", "tick.usno.navy.mil iburst", ]
				}
		}
						
                if $servers == undef {
					$servers_real	= $default_servers
				     }
		else {
		             $servers_real = $servers
																	                  }

		package	{ 'ntp':
			  ensure	=>	installed,
		}
						
		service	{ 'ntp':
				name		=>	$service_name,
				ensure		=>	stopped,
				enable		=>	false,
		}
						
		file { 'ntp.conf':
				path		=> '/etc/ntp.conf',
				ensure		=> file,
                                mode		=> 0640,
                                owner  => "root",
                                group  => "root",
		}

                exec { "/usr/bin/setfacl --remove-all /etc/ntp.conf":
                        onlyif => ["/usr/bin/test -x /usr/bin/setfacl", "/usr/bin/test -f /etc/ntp.conf"],
                }


                 # remove legacy files in /etc/cron.*

		 file { 'ntp.cron':
		      path	=> '/etc/cron.daily/ntp',
		      ensure	=> absent,
	         }

		 file { 'ntpdate.cron':
		      path	=> '/etc/cron.hourly/puppet-ntpdate.sh',
		      ensure	=> absent,
	         }



        cron { ntp:
             command => "/usr/sbin/ntpdate matrix.nps.edu > /dev/null",
             user    => root,
             hour    => [ 4, 16 ],
             minute  => 37,
        }





}

