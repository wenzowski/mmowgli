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

class stig_csi {

     if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "stig_csi::": } }
  
     include stig_csi::logrotate


}
