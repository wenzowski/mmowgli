class mmowgli_java {
 file{ "/usr/java":
        ensure=>directory,
     }

  file{ "/InstallFiles/jdk-8u45-linux-x64.tar.gz":
    path => /InstallFiles

  exec { "untar":
   command => "/bin/tar xvzf /InstallFiles/jdk-8u45-linux-x64.tar.gz -C /usr/java",
               refreshonly => true,
               require => File["/InstallFiles/jdk-8u45-linux-x64.tar.gz", "/usr/java"],
       }
 
}
