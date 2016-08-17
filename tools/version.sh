#!/bin/bash

##
# This script iterates through the list of commits on the current branch and parses the Jira labels for semver
# including:
#   semver:major
#   semver:minor
#   semver:patch
#
# Logic:
#   If the semver:major label is set, the major value is incremented by one and the minor and patch values are set to 0.
#   If the semver:minor label is set, the minor value is incremented by one and the patch value is set to 0.
#   If the semver:patch label is set, the patch value is incremented by one.
#   If the number of hashes does not match the number of semver references, the script exists with an error.
#   If the value in the VERSION file does not match X.Y.Z, the script exists with an error.
#
# Example Usage:
#   $ cd path/to/repository
#   $ head -1 VERSION
#   # => 1.2.3
#
#   # Branch contains commits with semver:major label(s)
#   $ sh tools/version.sh
#   # => 2.0.0
#
#   # Branch contains commits with semver:minor label(s)
#   $ sh tools/version.sh
#   # => 1.3.0
#
#   # Branch contains commits with semver:patch label(s)
#   $ sh tools/version.sh
#   # => 1.2.4
#
#   # Using it to set the version
#   $ sh tools/version.sh > VERSION
##

##
# Array of commit hashes.
##
HASHES=()

##
# Regular expression for a limited semver including only X.Y.Z.
##
readonly SEMVER_REGEX="^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$"

##
# Array of semver references.
##
SEMVER=()

##
# Name of the file with the project version.
##
readonly VERSION_FILE=VERSION

# Get the commit hashes that appear in the current branch but not in origin/master.
index=0
for hash in $(git log --format=%H HEAD ^origin/master --no-merges); do
  HASHES[$index]=$hash
  index=$(($index + 1))
done

# Collect the semver references from the commit messages.
index=0
for hash in "${HASHES[@]}"; do
  SEMVER[$index]=$(git log -1 --format=%b $hash | grep -i semver)
  index=$(($index + 1))
done

# Get the current project version from the version file.
read -r VERSION < $VERSION_FILE

# Check if the semver reference count matches the commit has count.
if [[ "${#SEMVER[@]}" -eq "${#HASHES[@]}" ]]; then
  # Check if the value in the VERSION file matches the expected pattern.
  if [[ "$VERSION" =~ $SEMVER_REGEX ]]; then
    update=1
    major=${BASH_REMATCH[1]}
    minor=${BASH_REMATCH[2]}
    patch=${BASH_REMATCH[3]}

    # If semver:major is referenced, update major and reset minor and patch.
    if [[ -n $(echo ${SEMVER[@]} | grep -i major) ]]; then
      major=$(($major + 1))
      minor=0
      patch=0
    # Else if semver:minor is referenced, update minor and reset patch.
    elif [[ -n $(echo ${SEMVER[@]} | grep -i minor) ]]; then
      minor=$(($minor + 1))
      patch=0
    # Else if semver:patch is referenced, update patch.
    elif [[ -n $(echo ${SEMVER[@]} | grep -i patch) ]]; then
      patch=$(($patch + 1))
    # Otherwise, there is no update and the value in the VERSION file remains the same.
    else
      update=0;
    fi

    # If there is an update, return the value.
    if [[ $update ]]; then
      echo "$major.$minor.$patch"
      exit 0
    fi
  # Otherwise, exit with an error.
  else
    echo "fatal: VERSION $version does not match the semver scheme 'X.Y.Z'." 1>&2
  fi
# Otherwise, exit with an error.
else
  echo "fatal: the number of commits does not match the number of semver references; ${#HASHES[@]} commit(s) vs" \
    "${#SEMVER[@]} semver reference(s)" 1>&2
fi
