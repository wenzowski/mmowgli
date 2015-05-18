# GEN008700 008710
# does not work with Debian because augeas does not understand the grub.cfg file
#
#
# requires:  puppet module install domcleal/augeasproviders   on the master
#
class itacs_stig::grub
{
   if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "itacs_stig::grub": } }

if ( ( $operatingsystem == RedHat ) and ($operatingsystemmajrelease == 6) )
{
   $grub_parameter = 'encrypted'
   $grub_password = '$6$5ePKFObBhQs4NVzM$R.ZwjlHk2x8s9CaeI2eza2fCDQ6mRNOc1TvQt6hg0qwx816NmAa78qbfzRJqKlEbUA6MzqHluQ2Gm0APe8dcI.'
}
else
{
   $grub_parameter = 'md5'
   $grub_password = '$1$MY1d2Uqc$kgOjTb8v8xmyzWycfFtvY.'
}
  

# augeas does not understand the Debian grub.cfg file
  if ( $operatingsystem == Fedora ) or ( $operatingsystem == RedHat )
  {
  
   augeas { "grub-create-password":
           context => "/files/boot/grub/menu.lst",
           changes => [
                       "ins password after timeout",
                       "set timeout 4",
                       "set password/$grub_parameter ''",
                        "set password $grub_password",
                       ],
           onlyif => "match password size == 0",
    }

#    augeas { "grub-set-password":
#              context => "/files/boot/grub/menu.lst",
#              changes => [ "set timeout 4", "set password $grub_password",],
#              require => Augeas["grub-create-password"],
#     }



  kernel_parameter {"audit":
         ensure => present,
         value => "1",
  }


  }
  else
  {
    notify { "WARNING itacs_stig::grub not applied for $operatingsystem": }
  }


}
