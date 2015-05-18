# Class: managed
#
# This module sets a file to announce who is the manager of the  system
# By default sets the system to ITACS managed, if the global variable
# manager is set to 'cs' or 'cybersecurity' then that is set as the manager
#
#
class stig_csi::managed_by {

     if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "stig_csi::managed": } }

   if ( $operatingsystem == OpenBSD ) or ( $operatingsystem == FreeBSD ) {
       $group_name = 'wheel'
   }
   else
   {
       $group_name = 'root'
   }

     if ( $manager == 'CS' ) or ( $manager == 'cybersecurity')
     {
            $thefile='/etc/.cs_managed'
            $otherfile='/etc/.stig_managed'
     }
     else
     {
            $thefile='/etc/.stig_managed'
            $otherfile='/etc/.cs_managed'
     }

    file { "$thefile":
              path   => $thefile,
              ensure  => present,
              owner => 'root',
              group => $group_name,
              mode    => "644";
            "$otherfile":
              path   => $otherfile,
              ensure  => absent,
    }



}
