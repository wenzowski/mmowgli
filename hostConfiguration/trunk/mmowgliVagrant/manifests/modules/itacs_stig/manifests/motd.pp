# GEN 000400
class itacs_stig::motd
{
   if ( $verbose == 'yes' ) or ( $verbose == true ) { notify { 'itacs_stig::motd GEN00400': } }

   if ( $operatingsystem == OpenBSD ) or ( $operatingsystem == FreeBSD ) {
       $group_name = 'wheel'
   }
   else
   {
       $group_name = 'root'
   }

   file { 'motd':
           ensure => present,
           path   => '/etc/motd',
           owner  => 'root',
           group  => $group_name,
           mode   => '0644',
           source => 'puppet:///modules/itacs_stig/motd',
       }


   if ( $operatingsystem != OpenBSD ) and ( $operatingsystem != FreeBSD ) {

      file { 'issue':
           ensure => present,
           path   => '/etc/issue',
           owner  => 'root',
           group  => 'root',
           mode   => '0644',
           source => 'puppet:///modules/itacs_stig/motd',
       }

      file { 'issue.net':
           ensure => present,
           path   => '/etc/issue.net',
           owner  => 'root',
           group  => 'root',
           mode   => '0644',
           source => 'puppet:///modules/itacs_stig/motd',
       }

   }

}
