# change the file ownerships and permissions according to STIG
#
# does not work for things like: /etc/cron.hourly/*
# which we want to apply to all the files inside that dir not the dir itself
#
class itacs_stig::file_perms
{
         if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "itacs_stig::file_perms": } }

# GEN 001362, 001363, 001364, 001365
# GEN 001366, 001367, 001364, 001368
# GEN 001371, 001372, 001373, 001374
# GEN 001378, 001379, 001380, 001390
# GEN 001391, 001392, 001393, 001394
# GEN 001800, 001810, 001820, 001830 
# GEN 001720,001730,001740,001760
# GEN 005740 005750 005760 005770
# GEN 006100 006120 006140 006150
# GEN 003760 003770 003780 003790
# GEN 004360 004370 004380 004390 
#$files=['/etc/resolv.conf', '/etc/hosts', '/etc/nsswitch.conf', '/etc/passwd', '/etc/group',
#        '/etc/bashrc', '/etc/csh.cshrc', '/etc/csh.login', '/etc/environment', '/etc/profile', '/etc/suid_profile',
#        '/etc/exports', '/etc/services', '/etc/samba/smb.conf', ]

  file { ['/etc/resolv.conf', '/etc/hosts', '/etc/nsswitch.conf', '/etc/passwd', '/etc/group',
          '/etc/bashrc', '/etc/csh.cshrc', '/etc/csh.login', '/etc/environment', '/etc/profile', '/etc/suid_profile',
          '/etc/exports', '/etc/services', '/etc/samba/smb.conf', '/etc/mail/aliases', '/etc/aliases' ]:
    owner => 'root',
    group => 'root',
    mode => 0644,
  }

  if ( $mailserver == "sendmail" )
  {
        $mailgroup = 'smmsp'
  }
  else
  {
        $mailgroup = 'root'
  }

  file { ['/etc/mail/aliases.db', '/etc/aliases.db' ]:
    owner => 'root',
    group => $mailgroup,
    mode => 0644,
  }

# GEN 000800 /etc/cron.deny
# GEN 0000-LNX00620 00640 00660
# GEN 00000-LNX00480 00500 00520
# GEN 008720 008740 008760 008780
# GEN 003200, 003210
# GEN 003240, 003245, 003250
  file { ['/etc/security/opasswd', '/etc/securetty', '/etc/sysctl.conf', '/boot/grub/grub.conf', '/etc/cron.allow', '/etc/cron.deny' ]:
    owner => 'root',
    group => 'root',
    mode => 0600,
  }

# GEN  00000-LNX00400 00420 00440 00450
# GEN 005390 005395 005400 005420
  file { '/etc/security/access.conf':
    owner => 'root',
    group => 'root',
    mode => 0640,
  }

# GEN 001400,001410 001420
# GEN 0000-LNX001431 001432 001433
if ( ( $operatingsystem == RedHat ) and ($operatingsystemmajrelease == 6) )
{
  file { [ '/etc/shadow', '/etc/gshadow' ]:
    owner => 'root',
    group => 'root',
    mode => 0000,
  }
}
else
{
  file { [ '/etc/shadow', '/etc/gshadow' ]:
    owner => 'root',
    group => 'root',
    mode => 0400,
  }
}


# GEN 003080-2
# NOT RIGHT:   WE WANT 0755 on the DIR and 0700 on the FILE         
  file { [ '/etc/cron.daily', '/etc/cron.hourly', '/etc/cron.monthly', '/etc/cron.weekly' ]:
    owner => 'root',
    group => 'root',
    mode => 0755,
    recurse => true,
    ignore => ['.placeholder', '*~']
  }


# GEN 002715,002716,002717,002718
#$auditlist=["/sbin/auditctl", "/sbin/auditd", "/sbin/ausearch", "/sbin/aureport", "/sbin/autrace", "/sbin/audispd", ]
 
  file { ['/sbin/auditctl', '/sbin/auditd', '/sbin/ausearch', '/sbin/aureport', '/sbin/autrace', '/sbin/audispd', ]:
    owner => 'root',
    group => 'root',
    mode => 0750,
  }



}
