define itacs_stig::limits_conf (
  $domain = "root",
  $type = "soft",
  $item = "nofile",
  $value = "10000"
  ) {

    # guid of this entry
    $key = "$domain/$type/$item"

    # augtool> match /files/etc/security/limits.conf/domain[.="root"][./type="hard" and ./item="nofile" and ./value="10000"]

    $context = "/files/etc/security/limits.conf"

    $path_list  = "domain[.=\"$domain\"][./type=\"$type\" and ./item=\"$item\"]"
    $path_exact = "domain[.=\"$domain\"][./type=\"$type\" and ./item=\"$item\" and ./value=\"$value\"]"

    augeas { "limits_conf/$key":
       context => "$context",
       onlyif  => "match $path_exact size != 1",
       changes => [
         # remove all matching to the $domain, $type, $item, for any $value
         "rm $path_list", 
         # insert new node at the end of tree
         "set domain[last()+1] $domain",
         # assign values to the new node
         "set domain[last()]/type $type",
         "set domain[last()]/item $item",
         "set domain[last()]/value $value",
       ],
     }

}

# GEN 000450 003500
class itacs_stig::limits
{
     if ( $verbose == "yes" ) or ( $verbose == true ) { notify { "itacs_stig::limits": } }


   itacs_stig::limits_conf { 
    "maxlogins": domain => '*', type => '-', item => maxlogins, value =>  10;
    "core": domain => '*', type => 'hard', item => core, value =>  0;
   }


}
