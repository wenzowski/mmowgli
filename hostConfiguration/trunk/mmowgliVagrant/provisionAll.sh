#!/bin/bash
#
# This could also be done within Vagrant, but for assorted reasons dealing
# with rapidity of dev cycles I'm doing it in a separate script.
#
# To set up a mmowgli cluster:
# vagrant up
# ./provisionAll.sh
#
# Note that you will need to change some paths in the ansible/hosts file
# where it points to private keys for the VMs created here.

vagrant up

cd ansible;

# Base line configuration. No attempt at STIG.
ansible-playbook -i hosts base.yml

# Configure the main mmowgli server. This runs activemq, mysql, zookeeper, samba, etc.
ansible-playbook -i hosts mmowgliServer.yml

# Configure 1..N tomcat servers. This installs Java, tomcat, mounts the shared filesystem, etc.
ansible-playbook -i hosts tomcat.yml

# Configure the front-end webserver to run apache, against a load-balanced set of tomcat servers.
ansible-playbook -i hosts webServer.yml

