#!/bin/bash
# Script to regenerate the cert on puppet agents.

# Stop the puppet service.
puppet resource service puppet ensure=stopped

# Identify the ssl directory on the client and clear it.
ssldir=$(puppet agent --configprint ssldir)
/bin/rm -r $ssldir/*

# Restart the puppet service after waiting random minutes (0-27).
sec=$(( $RANDOM/20 ))
sleep $sec
puppet resource service puppet ensure=running
