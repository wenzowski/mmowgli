class mmowgli_apache {

class {'apache': }

apache::vhost{"mmowgli":
 docroot=>"/var/www/html",
 priority=>20,
 custom_fragment => template("mmowgli_apache/mmowgli.conf.erb"),
}


# A configuration fragment to set up the load balancing

file {"/etc/httpd/conf.d/mmowgli.conf.frag":
  ensure=>"present",
  content => template("mmowgli_apache/mmowgli.conf.erb"),
}


# Install modssl
apache::mod {"ssl":}

# mod_proxy, for tomcat loadbalancing
apache::mod{"proxy":}
apache::mod{"proxy_ajp":}
apache::mod{"proxy_balancer":}

# make sure there's a host files entry for mmowgli
host { "mmowgli":
  host_aliases => "mmowgliWeb",
}

# Firewall ports for apache
firewall {"201 apache":
  port => "80",
  proto => "tcp",
  action => "accept",
 }

# Firewall ports for the samba server
firewall {"202 apache":
  port => "443",
  proto => "tcp",
  action => "accept",
 }

}



