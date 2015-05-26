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

# The mod_ssl is installed
apache::mod {"ssl":}

# mod_proxy, for tomcat loadbalancing
apache::mod{"proxy":}

# make sure there's a host files entry for mmowgli
host { "mmowgli":
  host_aliases => "mmowgliWeb",
}

}



