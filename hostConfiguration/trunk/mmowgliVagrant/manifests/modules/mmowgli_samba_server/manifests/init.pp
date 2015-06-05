# uses the thias/samba module. Sets up a server to 
# export the shared mmowgli filesystem for images
# and reports.

class mmowgli_samba_server {

file{"/etc/samba/smbusers":
  mode => 644,
  owner => "root",
  group => "root",
  ensure => "present",
  content => template("mmowgli_samba_server/smbusers.erb"),
}

# exports directory
file{"/exports":
  ensure => "directory",
  owner => "root",
  group => "root",
  mode => 755,
}

# the mmowlgi shared filesystem directory
file{"/exports/mmowgli":
  ensure => "directory",
  owner => "mmowgli",
  group => "mmowgli",
  mode => 755,
  subscribe => File["/exports"],
}


# configure the samba server

class {"::samba::server":
  workgroup => "mmowgli",
  server_string => "MMOWGLI Shared Server",
  shares => {
   'images' => [
                'comment=mmowlgi images and reports',
                'path=/exports/mmowgli',
                'browseable=yes',
                'writeable = yes',
                'valid users= mmowgli',
               ],
             },
    }

# This sets the smb password in /var. It uses tdb (trivial database) format
# file format. 

notify{"^^^^^^^^^^^^maybe doing samba password ${mmowgli_user_password}":}

exec{ "mmowgliSambaUser":
  command=>"/bin/echo -ne '${mmowgli_samba_password}\n${mmowgli_samba_password}\n' | /usr/bin/smbpasswd -a -s mmowgli",
  path=> "/bin:/usr/bin",
  user => "root",
}


}

