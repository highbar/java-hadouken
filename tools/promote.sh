#!/bin/bash

##
# Promotes the current state of the development branch to a release branch by examining the commit history difference
# between the development and master branches. Internally, this file uses the companion version.sh script to determine
# the semantic version change that should occur from the commits in difference (see version.sh). This script implements
# the following actions:
#
# Assumptions:
#   - Must be on the development branch.
#
# Workflow:
#   1. Verify the current branch is the development branch.
#   2. Determine the new version for the release.
#   3. Create the release branch with the new version.
#   4. Write the new version to the VERSION file.
#   5. Stage the VERSION file.
#   6. Commit the staged changes with the message "Version bump to {{version}}."
#   7. Push the release branch to the origin remote.
#   8. Return to the development branch.
#
# Logic:
#   If the current branch is not the development branch, the script exit with an error.
#
# Example Usage:
#   # Assume the commit history only contain a semver:patch change.
#   $ cd path/to/repository
#   $ git checkout dev
#
#   # Examine the current VERSION
#   $ head -1 VERSION
#   # => 0.1.1
#
#   $ sh tools/promote.sh
#   # =>
#   # Switched to a new branch 'release/0.1.2'
#   # [release/0.1.2 3e50649] Version bump to 0.1.2.
#   #  2 files changed, 2 insertions(+), 2 deletions(-)
#   # Counting objects: 10, done.
#   # Delta compression using up to 8 threads.
#   # Compressing objects: 100% (9/9), done.
#   # Writing objects: 100% (10/10), 3.37 KiB | 0 bytes/s, done.
#   # Total 10 (delta 3), reused 0 (delta 0)
#   # To https://repos.example.com/path/to/repository-example.git
#   #  * [new branch]      release/0.1.2 -> release/0.1.2
#   # Switched to branch 'dev'
##

##
# Current branch in the repository.
##
readonly CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

##
# Name of the development branch.
##
readonly DEV_BRANCH=dev

##
# Tools path.
##
readonly TOOLS_PATH=$(dirname $0)

# Check if the current branch matches the development branch.
if [[ "$CURRENT_BRANCH" = "$DEV_BRANCH" ]]; then
  readonly version=$(sh $TOOLS_PATH/version.sh 2>/dev/null)
  readonly localExists=$(git rev-parse --verify release/$version 2>/dev/null)
  readonly remoteExists=$(git rev-parse --verify origin/release/$version 2>/dev/null)

  if [[ -z "$version" ]]; then
    echo "fatal: version is empty for some strange reason..."
    exit 1
  fi

  # Check if the local branch already exists and, if so, delete it.
  if [[ -n "$localExists" ]]; then
    read -n 2 -p "Remove local release branch? (Y|n) " removeLocal

    case $removeLocal in
      [nN]* )
        echo "A local release branch exists; 'promote' requires a clean local release branch..."
        exit 1
      ;;
      * )
        git branch -D release/$version
        break
      ;;
    esac
  fi

  # Check if the remote branch already exists and, if so, delete it.
  if [[ -n "$remoteExists" ]]; then
    read -n 1 -p "Remove remote branch? (Y|n) " removeRemote

    case $removeRemote in
      [nN]* )
        echo "A remote release branch exists; 'promote' requires a clean remote release branch..."
        exit 1
      ;;
      * )
        git push origin :release/$version
        break
      ;;
    esac
  fi

  git checkout -b release/$version
  echo "$version" > VERSION
  git add VERSION
  git commit -m "Version bump to $version."
  git push origin release/$version
  git checkout dev
# Otherwise, exit with an error
else
  echo "fatal: promote may only occur from the dev branch" 1>&2
fi
