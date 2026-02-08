#!/bin/bash

# how to run: ./module-downloader release-sain.properties
# Note: Files should be committed in ./downloaded-modules/ directory

mkdir -p SAIN-modules

# Path to where the downloaded files are stored in the repo
DOWNLOADED_MODULES_DIR="./downloaded-modules"

IFS=','
array=($(grep -i "sain.modules.repo" $1 | cut -d '=' -f 2))
for item in ${array[@]}
do
  MODULE_NAME=$(echo $item | cut -d ':' -f 1)
  MODULE_VERSION=$(echo $item | cut -d ':' -f 2)

  TAR_FILE="$(echo $MODULE_NAME)-$(echo $MODULE_VERSION).tar.gz"

  # Check if tar file exists
  if [ ! -f "$DOWNLOADED_MODULES_DIR/$TAR_FILE" ]; then
    echo "Error: $TAR_FILE not found in $DOWNLOADED_MODULES_DIR"
    exit 1
  fi

  # Uncompress file
  tar -xzf "$DOWNLOADED_MODULES_DIR/$TAR_FILE" --directory=SAIN-modules
done

# Extract static contents
if [ ! -f "$DOWNLOADED_MODULES_DIR/fonts.tgz" ]; then
  echo "Error: fonts.tgz not found in $DOWNLOADED_MODULES_DIR"
  exit 1
fi

tar -xzf "$DOWNLOADED_MODULES_DIR/fonts.tgz" --directory=./