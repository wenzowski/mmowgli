#
# GEN 003601 003603 003604 003608 003609 003610 003611 007860 007920
#
class itacs_stig::sysctl
{

       if ( $verbose == "yes" ) or ( $verbose == true ) { notify {"itacs_stig::sysctl":  message => "itacs_stig::sysctl" } }

       augeas { "sysctl.conf":
                 context => "/files/etc/sysctl.conf",
                 changes => [
                      "set kernel.exec-shield  1",
                      "set kernel.randomize_va_space  2",
                      "set net.ipv4.tcp_max_syn_backlog 1280",
                      "set net.ipv4.icmp_echo_ignore_broadcasts 1",
                      "set net.ipv4.conf.all.proxy_arp 0",
                      "set net.ipv4.conf.default.proxy_arp 0",
                      "set net.ipv4.conf.all.accept_redirects 0",
                      "set net.ipv4.conf.default.accept_redirects 0",
                      "set net.ipv4.conf.all.send_redirects 0",
                      "set net.ipv4.conf.default.send_redirects 0",
                      "set net.ipv4.conf.all.log_martians 1",
                      "set net.ipv4.conf.default.log_martians 1",
                      "set net.ipv6.conf.all.accept_redirects 0",
                      "set net.ipv6.conf.default.accept_redirects 0",
                      "set net.ipv6.conf.all.forwarding 0",
                      "set net.ipv6.conf.default.forwarding 0",
                   ],
                   notify  => Exec["sysctl"],
        }

       exec { "/sbin/sysctl -p":
               alias => "sysctl",
               refreshonly => true,
#               subscribe => File["sysctl.conf"],
        }
}
