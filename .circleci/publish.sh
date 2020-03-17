#!/bin/bash

# This script will create a "global" settings file for Maven
# to use for auth, then publish the project.

# Fail on first error.
set -e

# Ensure directory exists.
mkdir -p ~/.m2

echo "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd\">" > ~/.m2/settings.xml
echo "  <localRepository/>" >> ~/.m2/settings.xml
echo "  <interactiveMode/>" >> ~/.m2/settings.xml
echo "  <usePluginRegistry/>" >> ~/.m2/settings.xml
echo "  <offline/>" >> ~/.m2/settings.xml
echo "  <pluginGroups/>" >> ~/.m2/settings.xml
echo "  <servers>" >> ~/.m2/settings.xml
echo "    <server>" >> ~/.m2/settings.xml
echo "      <id>deque</id>" >> ~/.m2/settings.xml
echo "      <username>$MVN_USERNAME</username>" >> ~/.m2/settings.xml
echo "      <password>$MVN_PASSWORD</password>" >> ~/.m2/settings.xml
echo "    </server>" >> ~/.m2/settings.xml
echo "    <server>" >> ~/.m2/settings.xml
echo "      <id>attest-java-releases</id>" >> ~/.m2/settings.xml
echo "      <username>$MVN_USERNAME</username>" >> ~/.m2/settings.xml
echo "      <password>$MVN_PASSWORD</password>" >> ~/.m2/settings.xml
echo "    </server>" >> ~/.m2/settings.xml
echo "    <server>" >> ~/.m2/settings.xml
echo "      <id>attest-java-snapshots</id>" >> ~/.m2/settings.xml
echo "      <username>$MVN_USERNAME</username>" >> ~/.m2/settings.xml
echo "      <password>$MVN_PASSWORD</password>" >> ~/.m2/settings.xml
echo "    </server>" >> ~/.m2/settings.xml
echo "  </servers>" >> ~/.m2/settings.xml
echo "  <mirrors/>" >> ~/.m2/settings.xml
echo "  <proxies/>" >> ~/.m2/settings.xml
echo "  <activeProfiles/>" >> ~/.m2/settings.xml
echo "</settings>" >> ~/.m2/settings.xml

chmod 0600 ~/.m2/settings.xml

mvn deploy -DskipTests
