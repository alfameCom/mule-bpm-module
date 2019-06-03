#!/bin/bash

ARG="$1"

HELP="Usage: ./create-extension.sh -DgroupId=<groupId> -DartifactId=<artifactId> -Dversion=<version> -Dpackage=<package> -DextensionName=<extensionName>"

case "$ARG" in
	""|"-h"|"--help")
		echo "$HELP"
		;;
	*)
		mvn archetype:generate -DarchetypeGroupId=org.mule.extensions -DarchetypeArtifactId=mule-extensions-archetype $@
		;;
esac
