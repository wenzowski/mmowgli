# uses the thias/samba module. Sets up a server to 
# export the shared mmowgli filesystem for images
# and reports.

class mmowgli_samba_server {

notify{"Installing samba": }


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

}

