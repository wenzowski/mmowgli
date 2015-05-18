# experimenting with custom facts
# Check to see if this server has been marked as managed by ITACS
Facter.add(:managed_by) do
  has_weight 100
  setcode do
    if File.exist? "/etc/.itacs_managed"
      "ITACS managed"
    end
  end
end


#  Check to see if this server has been marked as managed by Cybersecurity
Facter.add(:managed_by) do
  has_weight 50
  setcode do
    if File.exist? "/etc/.cs_managed"
      "cybersecurity managed"
    end
  end
end


