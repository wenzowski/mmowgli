class itacs_puppet {


   if ( $operatingsystem == fedora ) and ( $operatingsystemrelease > 16) {
       $service_name = 'puppetagent'
   }
   else {
       $service_name = 'puppet'
   }

   if ( $ldap_managed == true ) {
        $server='bluemoon.ern.nps.edu'
   }
   else {
         $server='whitesheep.uc.nps.edu'
   }
     
# manage the puppet agent service
service { 'puppet':
  name => $service_name,
  ensure => running,
  enable => true,
}
 
# manage our puppet config file settings - restarts the puppet service if changes are made
augeas { "puppetconfig":
  context => "/files/etc/puppet/puppet.conf",
  changes => [
    "set agent/runinterval 3600",
    "set agent/server $server",
    "set agent/report true",
    ],
  notify => Service['puppet'],
  }

}
