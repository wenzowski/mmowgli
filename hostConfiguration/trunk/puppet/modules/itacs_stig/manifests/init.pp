# File: $RCSfile$
#
# Revision: $Revision$
# Revision date: $Date$
# Revision Author: $Author$
# Author: <ORIGINAL AUTHOR>
# Creation Date:   <CREATION DATE>
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
# Description: Invoke this in order to automatically get all 
#              the submodules
#
#
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

# if you invoke this one, ALL the subclasses get applied

class itacs_stig {

     if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "itacs_stig::": } }

     include itacs_stig::delusers

     include itacs_stig::file_perms

     include itacs_stig::grub

     include itacs_stig::inittab

     include itacs_stig::motd

     include itacs_stig::ntp

     include itacs_stig::sshd

     include itacs_stig::root_home

     include itacs_stig::sysctl

     #include itacs_stig::audit_rules

     include itacs_stig::packages

     include itacs_stig::limits

}
