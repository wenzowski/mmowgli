class itacs_user::jcfire {

# make sure we get the base class and jcfire setup
include itacs_user

#remove jcfire account

user { "jcfire":
        ensure => "absent",
        managehome => true,
        }

}
