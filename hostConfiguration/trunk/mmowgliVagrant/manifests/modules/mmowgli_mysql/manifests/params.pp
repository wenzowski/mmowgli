# This pattern is out of style, but simple.

class mmowgli_mysql::params
{
    $mmowgli_database_root_password = hiera("mmowgli_database_root_password")
    $mmowgli_database_application_password = hiera("mmowgli_database_application_password")
}
