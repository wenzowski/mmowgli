# Handles STIG GEN000920 000930
class itacs_stig::root_home {

         notify { "ITACS_STIG::root_home":
                     message => "GEN000920 930 /root",
                 }

   if ( $operatingsystem == OpenBSD ) {
       $group_name = 'wheel'
   }
   else
   {
       $group_name = 'root'
   }

        file { 'roothome':
                ensure => directory,
                path   => '/root',
                mode   => '0700',
                owner  => 'root',
                group  => $group_name,
        }

                # move this to a utilities file for reuse
        exec { "/usr/bin/setfacl --remove-all /root":
                        onlyif => ["/usr/bin/test -x /usr/bin/setfacl", "/usr/bin/test -d /root"],
             }



}
