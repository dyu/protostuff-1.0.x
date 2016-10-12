#!/bin/sh

set -e

VERSION=$1

[ ! -n "$VERSION" ] && echo "Enter release version: " && read VERSION

echo "Releasing $VERSION - are you sure? (y/n):" && read CONFIRM && [ "$CONFIRM" != "y" ] && exit 0

mvn versions:set -DnewVersion=$VERSION && \
git clean -f && git add -u && git commit -m "$VERSION" && \
mvn -Prelease deploy && mvn scm:tag && \
mvn versions:set -DnewVersion=1.0.x-SNAPSHOT && \
git clean -f && git add -u && git commit -m "1.0.x-SNAPSHOT"
