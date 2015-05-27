# Class: logrotate
#
# This module manages logrotate
# Not really STIG, but for the CSI we want this
#
class stig_csi::logrotate {

    package { "logrotate": }

    file {
        "/var/log/archive":
            ensure  => directory,
            owner => 'root',
            group => 'root',
            mode    => "750";
        "/etc/logrotate.conf":
            source  => "puppet:///modules/stig_csi/logrotate.conf",
            require => Package["logrotate"];
        "/etc/cron.daily/logrotate":
            mode    => "755",
            owner => 'root',
            group => 'root',
            source  => "puppet:///modules/stig_csi/logrotate.cron",
            require => Package["logrotate"];
    } # file

     if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "stig_csi::logrotate": } }


} # class logrotate
