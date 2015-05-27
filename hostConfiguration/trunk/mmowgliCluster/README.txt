README

Early stages, not operational yet.

This is a vagrant directory that configures a small mmowgli
cluster, consisting of the hosts:

mmowgli: a front end web server

mmowgliServer: a server that runs mysql, ActiveMQ JMS server, a Samba
               fileserver, an Apache zookeeper instance, and perhaps
               some other cats and dogs

tomcat1: Runs Apache Tomcat. Configure more tomcats for a load balanced
         cluster.

Vagrant (www.vagrantup.com) is a utility that quickly creates
virtual machines for development purposes. It must be installed on
your machine. It can create virtual machines for either VirtualBox
or VMWare.

Virtual Box (www.virtualbox.org) is a free virtualizer for running
virtual machines from Oracle. When Vagrant runs it creates and
provisions the virtual machines above.

Puppet (puppetlabs.com) is provisioning software. It installs and 
configures software applications such as apache, mysql, host configuration
files, and so on. 

When you type "vagrant up" in this directory vagrant will run by 
downloading a virtual machine from the network--in this case a
CentOS 6 image--then starting and provisioning the three VMs above
on a private network on your laptop. IPs will be dynamically assigned,
eg 10.0.2.15, on a private network. Look at the Vagrantfile file
in this directory for details. 

The puppet manifests are duplicated between here and another tree in the
config code. I'll fix that somehow later.



