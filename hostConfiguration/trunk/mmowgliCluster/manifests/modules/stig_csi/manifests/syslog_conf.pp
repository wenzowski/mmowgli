# Class: syslog_conf
#
# This module manages syslog/rsyslog.conf
# setting up longboard or lichtenau as the remote syslog server
#
class stig_csi::syslog_conf {

   if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "stig_csi::syslog_conf": } }

   # RHEL 5 still uses syslog
   if ( $operatingsystem == RedHat ) and ( $operatingsystemmajrelease == 5 ) {
                  $conf_file='syslog.conf'
                  $service_name='syslog'
   }
   else        # everybody else is using rsyslog
   {
           $conf_file='rsyslog.conf'
           $service_name='rsyslog'
   }


#   which syslog server
   if ( $network_eth0 == '205.155.65.0' )
   {
       $syslog_server='205.155.65.80'
   }
   else
   {
       $syslog_server='172.20.40.42'
   }



    service { "syslog":
        name => $service_name,
        ensure  => "running",
        enable  => "true",
    }

    file {'syslog_conf':
      path => "/etc/${conf_file}",
      ensure  => file,
      owner => 'root',
      group => 'root',
      mode    => "640",
      content => template("stig_csi/${conf_file}.erb"),
      notify  => Service["${service_name}"],
    }

}
