#!/bin/bash

# how to run: ./module-downloader release-sain.properties REPO_URL REPO_USERNAME REPO_PASSWORD EXTENSION_NEXUS_URL

mkdir SAIN-modules

SAIN_REPO_URL=$2
SAIN_FTP_USER=$3
SAIN_FTP_PASS=$4
SAIN_EXTENSION_NEXUS_URL=$5

IFS=','
array=($(grep -i "sain.modules.repo" $1 | cut -d '=' -f 2))
for item in ${array[@]}
do
	MODULE_NAME=$(echo $item | cut -d ':' -f 1)
	MODULE_VERSION=$(echo $item | cut -d ':' -f 2)

	# Download Tar file
	wget --user $3 --password $4 https://$2/repository/$5/$(echo $MODULE_NAME)/$(echo $MODULE_VERSION)/$(echo $MODULE_NAME)-$(echo $MODULE_VERSION).tar.gz || exit 1

	# Download MD5 hash
	wget --user $3 --password $4 https://$2/repository/$5/$(echo $MODULE_NAME)/$(echo $MODULE_VERSION)/$(echo $MODULE_NAME)-$(echo $MODULE_VERSION).tar.gz.md5 || exit 1

	# checksum MD5
	cat $(echo $MODULE_NAME)-$(echo $MODULE_VERSION).tar.gz.md5 | md5sum -c - || exit 1

	echo "checksum $MODULE_NAME:$MODULE_VERSION OK."

	# Uncompress file
	tar -xzf $(echo $MODULE_NAME)-$(echo $MODULE_VERSION).tar.gz --directory=SAIN-modules
done

# Download static contents

wget --user $3 --password $4 https://$2/repository/$5/static/fonts.tgz || exit 1
tar -xzf ./fonts.tgz --directory=./