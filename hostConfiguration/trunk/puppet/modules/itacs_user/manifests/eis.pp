class itacs_user::eis {

########################################
# Include the Base Class to setup JCFIRE
include itacs_user

#################
# Todd Wyatt
user { "wyatt":
  ensure => "absent",
  groups => $sudogroup,
  password_max_age => 60,
  password => '*',
  }

ssh_authorized_key { "wyatt" :
  ensure => "absent",
  key => "AAAAB3NzaC1yc2EAAAABJQAAAgB/wxeadyYrUby+6uJOE01R1fTaiDcpeprRRqmNz5EZkQ8jLQjQp4e8C9VQnNlzNQDLKAx4G0bN1arcnLLD3TmOznXz+2dNLZYe272NS2ba2rie8INNbyx1p5mpVYRQpWaVexWz7CNS7GLNjKGxQIb60w1CwlwRnDFZfKFPQKPTzLiRC81yJ3uTsZwErxfqzKaUGI3sAKnwao1+US7DLeOhCwvT2V5xBvHhXmQPvlJu6HWIjyRhjJGO81VoqwLSF4/vbiHlKJJ1YsfL0E8nwKmrsKdjH/XA97VSY3qTvj6mpVaxb+mjcc1N8nkCij1tVG3ecqq7cFcvGiA6xpLWsLI8F6d9cFgpISI5v9iX8p0xNofEYXLif2NFUzzVPAaMzOoNUrp1mAotrvagFaYm6HUzSWKHU05QY+JJTrBEg8EEr7uBpvGFzMGECRCdiHUjxxjjFQgkbBFOnxJJPsIMjP5jVPUfobqOsDLuXGkj1QbVlWP9qJxH54kP0GH2nrsAueh3T/uebdS5+7Q8EzZxuiNIKJSv8k6NTJZWSzK/xdTGyyY9EgKdlFKnUicFTYkNkJHxZpgSthy5xPRGQvxr7V4BjkCfqX6ckTF1b4iuUvd7JejGjnwDGSdb8QcNf3HU6EMV06cGVrj74TBIlFWFfHYce7jo/eEUGhiXADusJnXL0Q==",
  type => "ssh-rsa",
  name => "wyatt@itacs",
  user => "wyatt",
  }

ssh_authorized_key { "wyatt.cac" :
  ensure => "absent",
  key => "AAAAB3NzaC1yc2EAAAADAQABAAABAQC4Upd4ARim1b785o7DoHIpVEkdC8Ukga2nKUb3gInmecfEhyC0wyOoVidwQUvSTWitdGdTqsyDQ7NyMDrRHFXDLD/2vxkIliTPP8GQUT5o5tu2XiUULrWUPBCU29LB9ZNLc2RRP14V0G3jRKZlHBwW6WgBRu/dDcD/VWJ5aT1esNfk2HbuGJfQ8mtAFWNHanEhFmFFxrLO1BcJPOSRevY7hx5JWQH/Ws8ADVesYBL97GoG4vnW/Fu/IClIerXFLieuYvyPXFZzh5H7qAmo+OoqIN8xlsT4H1gtpkrWEKM1GQA5ZjdsHKE+qORs3TrDcn9tyNq147U74ZYc7Uue6+8D",
  type => "ssh-rsa",
  name => "wyatt.cac@itacs",
  user => "wyatt",
  }

##################
# Juana Wells
user { "juju":
  ensure => "present",
  managehome => true,
  groups => $sudogroup,
  password_max_age => 60,
  password => hiera('itacs_user::eis::juju_pw'),
  }

ssh_authorized_key { "juju" :
  ensure => "present",
  key => hiera('itacs_user::eis::juju_key'),
  type => "ssh-rsa",
  name => "juju@itacs",
  user => "juju",
  }

####################
# Craig Vershaw
user { "vershaw":
  ensure => "present",
  managehome => true,
  groups => $sudogroup,
  password_max_age => 60,
  password => hiera('itacs_user::eis::vershaw_pw'),
  }

ssh_authorized_key { "vershaw" :
  ensure => "present",
  key => hiera('itacs_user::eis::vershaw_key'),
  type => "ssh-rsa",
  name => "vershaw@itacs",
  user => "vershaw",
  }

##################
# Sarah Farley
user { "slfarley":
  ensure => "present",
  managehome => true,
  groups => $sudogroup,
  password_max_age => 60,
  password => hiera('itacs_user::eis::slfarley_pw'),
  }

ssh_authorized_key { "slfarley" :
  ensure => "present",
  key => hiera('itacs_user::eis::slfarley_key'),
  type => "ssh-dss",
  name => "slfarley@itacs",
  user => "slfarley",
  }

}
