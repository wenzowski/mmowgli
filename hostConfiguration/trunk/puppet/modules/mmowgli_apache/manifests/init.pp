class mmowgli_apache {

class {'apache': }

apache::vhost{"mmowgli":
 docroot=>"/var/www/html",
 priority=>20,
}

apache::mod {"ssl":}

host { "mmowgli":
  ip=> "127.0.0.1",
  host_aliases => "mmowgli",
}

}



