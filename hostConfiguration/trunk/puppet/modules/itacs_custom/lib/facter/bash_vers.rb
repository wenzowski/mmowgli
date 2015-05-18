# Check the version of bash on the system
Facter.add(:bash_version) do
  os = Facter.value(:osfamily)
  case os
  when 'RedHat'
    setcode 'rpm -q bash'
  when 'Debian'
    setcode 'dpkg -s bash | grep Version'
  else
    setcode 'bash --version | grep version | grep -v GPL'
  end
end
