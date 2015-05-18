class itacs_user::met {

# make sure we get the base class and jcfire setup
include itacs_user


user { "dmb":
        ensure => "present",
        managehome => true,
	groups => "wheel",
	password_max_age => 60,
	password => hiera('itacs_user::met::dmb_pw'),
        }

ssh_authorized_key { "dmb" :
        ensure => "present",
        key => hiera('itacs_user::met::dmb_key'),
        type => "ssh-rsa",
        name => "burych@itacs",
        user => "dmb",
        }


}
