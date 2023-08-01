#!/bin/sh

# Usage:
# bash scripts/gitconfig.sh param1 param2
# * param1: github email
# * param2: github name

git config user.email "$1"
git config user.name "$2"
git config credential.helper store