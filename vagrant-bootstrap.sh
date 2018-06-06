#!/usr/bin/env bash

#config vagrant user
echo "vagrant:vagrant" | sudo chpasswd
sed -i "s/PasswordAuthentication no/PasswordAuthentication yes/" /etc/ssh/sshd_config
sed -i "s/ChallengeResponseAuthentication no/ChallengeResponseAuthentication yes/" /etc/ssh/sshd_config
sed -i "s/UserPAM yes/UserPAM no/" /etc/ssh/sshd_config