#!/bin/bash

##
# Appends the current commit hash to the current value in the VERSION file. This is meant to be used in tandem with the
# distZip task.
#
# Logic:
#   If the value in the VERSION file does not match the regular expression, the script exists with an error.
#
# Example Usage:
#   $ cd path/to/repository
#
#   # Examine the current VERSION
#   $ head -1 VERSION
#   # => 1.2.3
#
#   # Examine the last commit hash
#   $ git log -1 --format=%H
#   # => 929452f62ce4dbc99f96b37e46bf786a4433db56
#
#   $ sh tools/dist.sh
#   # => 1.2.3-sha.929452f62ce4dbc99f96b37e46bf786a4433db56
#
#   $ gradle distZip
#   # or
#   $ grunt distZip
#   # etc.
##

##
# Name of the file with the project version.
##
readonly VERSION_FILE=VERSION

# Get the current project version from the version file.
read -r CURRENT_VERSION < $VERSION_FILE

##
# Regular expression for a pattern to capture everything that is not a - followed by any number of characters. This
# assumes a pattern like: 1.2.3-build.1+sha.929452f
# The build tag on the version is attached only if the BUILD_VERSION environment variable is set. Since this script may
# be called more than once, it is important to not keep appending the -sha.{hash} to the end repeatedly.
##
VERSION_REGEX="^([^\-]+).*$"

# Check to see if the value in the VERSION file matches.
if [[ "$CURRENT_VERSION" =~ $VERSION_REGEX ]]; then
  readonly baseVersion=${BASH_REMATCH[1]}
  newVersion=$baseVersion

  if [[ -n "$BUILD_NUMBER" ]]; then
    newVersion="${newVersion}-build.${BUILD_NUMBER}+"
  else
    newVersion="${newVersion}-"
  fi

  echo "${newVersion}sha.$(git log -1 --format=%h)" > VERSION
  read -r version < $VERSION_FILE
  echo $version
# Otherwise, exit with an error
else
  echo "fatal: VERSION ${CURRENT_VERSION} does not match the semver scheme 'X.Y.Z-sha.HASH'." 1>&2
fi
