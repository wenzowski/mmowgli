# change the file ownerships and permissions according to STIG for syslog.conf or rsyslog.conf
#
# This is separate from itacs_stig::file_perms so that it can be excluded
# and allow itacs_csi::syslog_conf to be used instead
#
class itacs_stig::syslog_conf
{
         if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "itacs_stig::syslog_conf": } }


# GEN  00000-LNX00400 00420 00440 00450
# GEN 005390 005395 005400 005420
  file { ['/etc/rsyslog.conf', '/etc/syslog.conf', ]:
    owner => 'root',
    group => 'root',
    mode => 0640,
  }



}
