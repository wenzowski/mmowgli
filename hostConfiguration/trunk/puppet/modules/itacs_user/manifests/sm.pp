class itacs_user::sm {

# make sure we get the base class
include itacs_user


user { "magat":
        ensure => "present",
        managehome => true,
	groups => "wheel",
	password_max_age => 60,
	password => hiera('itacs_user::sm::magat_pw'),
        }

user { "gordon":
        ensure => "present",
        managehome => true,
        groups => "wheel",
        password_max_age => 60,
        password => hiera('itacs_user::sm::gordon_pw'),
        }

user { "mendoza":
        ensure => "present",
        managehome => true,
        groups => "wheel",
        password_max_age => 60,
        password => hiera('itacs_user::sm::mendoza_pw'),
        }

user { "sajameson":
        ensure => "present",
        managehome => true,
        groups => "wheel",
        password_max_age => 60,
        password => hiera('itacs_user::sm::sajameson_pw'),
        }

}
