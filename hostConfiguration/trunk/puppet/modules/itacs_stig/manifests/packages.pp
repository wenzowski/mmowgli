# File: $RCSfile$
#
# Revision: $Revision$
# Revision date: $Date$
# Revision Author: $Author$
# Author: Skip Carter
# Creation Date:   29 April 2013
#
# ====================================================================
#
# Copyright (c) $Date$,
# Taygeta Scientific Incorporated, all rights reserved.
# This copyright notice does not evidence any actual or intended
# publication.  This is unpublished computer software containing
# trade secrets and confidential information proprietary to Taygeta
# Scientific.
#
# ====================================================================
#
#
# Description: adds and removes packages the STIG specifies
#              GEN 003820 003825 003850 005080 005100 005140
#
#
# Requires:
#          
#
# Comments:
#
#
# ====================================================================
#
#
# $Log$
#
# ====================================================================
#
 
class itacs_stig::packages
{
       if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "itacs_stig::packages": } }


#       notify{ "itacs_stig::packages_test":  message => "ensure flag is: $installing" }

       # there SHOULD be a prettier way to do this, but evidently puppet is not exactly ruby
       if ( $ids == true )  or ( $ids == "yes" ) {
        package { ['ethereal','wireshark','tshark','nc','tcpdump','snoop']:
                      ensure => installed,
         }
       }
       else {
        package { ['ethereal','wireshark','tshark','nc','tcpdump','snoop']:
                      ensure => absent,
         }

       }


# everyone should have this
        package { 'aide':
                  ensure => installed,
         }


# (almost) no one needs these

  package { ['rsh-server', 'telnet-server', 'tftp-server', 'ypserv' ]:
     ensure => absent,
    }

 
  service { ['portmap', 'ypbind', ]:
     enable => false,
     ensure => stopped,
  }

#  service { 'aide':
#     enable => true,
#     ensure => running,
#  }

}
