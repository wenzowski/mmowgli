class stig_passwords {

    # set our login.defs file, which sets the password aging controls
    # it also specifies SHA512 as the default hash, versus MD5 used on some older RHEL5 boxes
    file { 'logindefs':
        name => '/etc/login.defs',
        owner => root,
        group => root,
        mode => 644,
        source => 'puppet:///modules/stig_passwords/login.defs',
    }
}
