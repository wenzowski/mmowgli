README

This is a vagrant directory that configures a small mmowgli
cluster, consisting of the hosts:

mmowgli: a front end web server that fronts a load-balanced pool
         of tomcat servers. It mounts (read-only) the shared
         filesystem with the game's images and reports.

mmowgliServer: a server that runs mysql, ActiveMQ JMS server, a Samba
               fileserver, an Apache zookeeper instance, and perhaps
               some other cats and dogs

tomcat1: Runs Apache Tomcat. Configure more tomcats for a load balanced
         cluster.

tomcat2: another VM that runs apache tomcat.

Vagrant (www.vagrantup.com) is a utility that quickly creates
virtual machines for development purposes. It must be installed on
your machine. It can create virtual machines for either VirtualBox
or VMWare. This example uses virtual box.

Virtual Box (www.virtualbox.org) is a free virtualizer for running
virtual machines from Oracle. When Vagrant runs it creates and
provisions the virtual machines above.

Ansible (ansible.com) is a framework for provisioning hosts in the cloud. 
It is similar to puppet or chef, but simpler. 

When you type "vagrant up" in this directory vagrant will run by 
downloading a virtual machine from the network--in this case a
CentOS 6 image--then starting and provisioning the VMs above
on a private network on your laptop. IPs will be dynamically assigned,
eg 10.1.100.2, on a private network. Look at the Vagrantfile file
in this directory for details. 

You can configure a complete cluster by running the script "provisionAll.sh",
in this directory. (There are better ways to do this but it's a little faster
to do it this way during dev cycles.) It will take aobut 15 minutes or so
to download, configure, and install everything in the cluster, assuming
a decently fast network connection.


