# GEN 000290-1,2,3,4
# GEN  00000-LNX00320
class itacs_stig::delusers
{

    # STIG has 'ftp' also but we are keeping that one at TSI
    user { ['games', 'gopher', 'shutdown', 'halt', 'reboot', ]:
            ensure   => absent,
    }

       notify {"itacs_stig::delusers":  message => "itacs_stig::delusers" }

}
