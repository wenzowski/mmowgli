# This pattern is disfavored nowadays, but it works 
# for now.

class mmowgli_activemq::params {

  $activemq_tarball = hiera("java_tarball")
  $activemq_version = hiera("activemq_version") 
}
