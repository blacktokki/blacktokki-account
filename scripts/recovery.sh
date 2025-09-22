#!/bin/bash

# Usage:
# bash scripts/recovery.sh

sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 9000
sudo iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 9000
bash scripts/runserver.sh discovery gateway account
