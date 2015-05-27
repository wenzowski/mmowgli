class mmowgli_mysql {

#include mysql::server
include mysql::client

# Set up users and databases. 
mysql::db {"mmowgli":
  user => "mmowgli",
  password => "gwtservelet",
  host => "localhost",
  grant => "ALL",
  sql => "/InstallFiles/mmowgli_bootstrap.sql",
}


# Set up the server itself. The override_options is used to set the 
# values in the /etc/my.cnf file, or the "show variables" settings.

class {"mysql::server":
  root_password => "mmowgli",
  remove_default_accounts => true,
  override_options => {'mysqld' => {'max_connections' => '1024', 'default-storage-engine' => 'innodb', 'query_cache_size' => '20M', 'key_buffer_size' => '10M' } },
}


}


