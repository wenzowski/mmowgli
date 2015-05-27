class stig_clamav {

# defaults to scanning / (everything) unless specified otherwised in a parameter
if ($clamav_scan_base) {
  $scanbasepath = $clamav_scan_base
  }
else {
  $scanbasepath = "/"
  }

# determine who to send the email alerts to, defaults to iacd-support@nps.edu
if ($clamav_contact_email) {
  $scanalertrecipient = $clamav_contact_email
  }
else {
  $scanalertrecipient = "iacd-support@nps.edu"
  }

# ensure that the clamav package is installed
package {'clamav':
  ensure => installed,
  }

# check for DLP scanning mode
if ($clamav_enable_DLP == 1) {
  $scanDLP = "--detect-structured"
  }
else {
  $scanDLP = ""
  }

# check for PUA scanning mode
if ($clamav_enable_PUA == 1) {
  $scanPUA = "--detect-pua"
  }
else {
  $scanPUA = ""
  }

# check for cross-fs disable
if ($clamav_disable_crossfs == 1) {
  $crossfs = "--cross-fs=no"
  }
else {
  $crossfs = ""
  }

# distribute a nightly script to update signatures
file { "/etc/cron.daily/puppet-update-clamavsigs.sh":
  source => "puppet:///modules/stig_clamav/puppet-update-clamavsigs.sh",
  owner => 'root',
  group => 'root',
  mode => '0700',
  require => Package['clamav'],
  }

# distribute a script to scan the filesystem 
file { "/etc/cron.daily/puppet-clamavscan-full.sh":
  content => template('stig_clamav/puppet-clamavscan-full.sh.erb'),
  owner => 'root',
  group => 'root',
  mode => '0700',
  require => Package['clamav'],
  }

# distribute a script to perform an hourly scan
#file { "/etc/cron.hourly/puppet-clamavscan-hourly.sh":
#  content => template('stig_clamav/puppet-clamavscan-hourly.sh.erb'),
#  owner => 'root',
#  group => 'root',
#  mode => '0700',
#  require => Package['clamav'],
#  }


}
