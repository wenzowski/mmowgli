# GEN000000-LNX00580  GEN000020
# only works with classic inittab (RHEL5, Debian) and RHEL6 not Fedora
class itacs_stig::inittab
{

  if ( $verbose == "yes" ) or ( $verbose == true ) { notify {"itacs_stig::inittab":  message => "itacs_stig::inittab" } }


  if ( ( $operatingsystem == RedHat ) and ($operatingsystemmajrelease == 5) ) or ( $operatingsystem == Debian )
  {
  
   augeas { "ctr_alt_del":
           context => "/files/etc/inittab",
           changes => [
                       "set ca/runlevels nil",
                       "set ca/action ctrlaltdel",
                       "set ca/process \"/usr/bin/logger -p security.info 'Ctrl-Alt-Del was pressed'\"",
                       ],
          }

   augeas { "single_user":
           context => "/files/etc/inittab",
           changes => [
                       "set ~~/runlevels S",
                       "set ~~/action wait",
                       "set ~~/process \"/sbin/sulogin\"",
                       ],
          }
   }
   elsif ( $operatingsystem == RedHat ) and ($operatingsystemmajrelease == 6)
   {

   augeas { "single_user":
           context => "/files/etc/sysconfig/init",
           changes => [
                       "set SINGLE \"/sbin/sulogin\"",
                       "set PROMPT no",
                       ],
          }

   }

}
