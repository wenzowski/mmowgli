# Check to see if this server is using sendmail or postfix
# defaults to sendmail if postfix is not found
Facter.add(:mailserver) do
  has_weight 100
  setcode do
    if File.exist? "/usr/sbin/postfix"
      "postfix"
    else
      "sendmail"
    end
  end
end


#  Check to see if this server has been marked as managed by Cybersecurity
Facter.add(:mailserver) do
  has_weight 50
  setcode do
    if File.exist? "/usr/sbin/sendmail.postfix"
      "postfix"
    else
       "sendmail"
    end
  end
end


