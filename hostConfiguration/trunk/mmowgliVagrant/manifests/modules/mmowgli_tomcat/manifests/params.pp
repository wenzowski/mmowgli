# old pattern, but simple

class mmowgli_tomcat::params
{
 $tomcat_tarball = hiera("tomcat_tarball")
 $tomcat_version = hiera("tomcat_version")
 $java_melody_password = hiera("java_melody_password")
}
