class itacs_user {

# the group that grants sudo depends on O/S (and version for ubuntu)
case $operatingsystem {
  "Ubuntu":  { 
    case $lsbmajdistrelease {
      "10" : { $sudogroup = "admin" }
      "11" : { $sudogroup = "admin" }
      default : { $sudogroup = "sudo" }
      }
    }
  "Debian":  { $sudogroup = "sudo" }
  default :  { $sudogroup = "wheel" }
  }
  
#########################################
# retuser account used for RETINA scanning
# POC: Terry Welliver  -- no longer used EFC
  

user { "retuser":
  ensure => "absent",
  groups => $sudogroup,
  shell => "/bin/bash",
  system => true,
  managehome => true,
  password_max_age => -1,
  password => '$6$ZAzMyXuz$QP0F5twqNW2J2jK.7Q3lL1dKFpLIwrBet6PSkDmxyS1WcHweFnmlUanuNvCdvbUc7jmok1E.ZKyX6hKVbefxy1',
  }

file {'retuser_directory':
    ensure => absent,
    path => '/home/retuser',
    recurse => true,
    purge => true,
    force => true,
   }

#########################################
# retuser account used for ACAS scanning
# POC: Terry Welliver and Artie Gross
user { "enesuser":
  ensure => "present",
  groups => $sudogroup,
  shell => "/bin/bash",
  system => true,
  managehome => true,
  password_max_age => -1,
  password => hiera('itacs_user::enesuser_pw'),
  }

ssh_authorized_key { "enesuser" :
  ensure => "present",
  key => hiera('itacs_user::enesuser_key'),
  type => "ssh-dss",
  name => "enesuser@itacs",
  user => "enesuser",
  }
}
