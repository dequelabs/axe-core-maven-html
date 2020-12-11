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
echo "      <id>ossrh</id>" >> ~/.m2/settings.xml
echo "      <username>$OSSRH_USERNAME</username>" >> ~/.m2/settings.xml
echo "      <password><![CDATA[$OSSRH_PASSWORD]]></password>" >> ~/.m2/settings.xml
echo "    </server>" >> ~/.m2/settings.xml
echo "  </servers>" >> ~/.m2/settings.xml
echo "  <profiles>" >> ~/.m2/settings.xml
echo "    <profile>" >> ~/.m2/settings.xml
echo "      <id>ossrh</id>" >> ~/.m2/settings.xml
echo "      <activation>" >> ~/.m2/settings.xml
echo "        <activeByDefault>true</activeByDefault>" >> ~/.m2/settings.xml
echo "      </activation>" >> ~/.m2/settings.xml
echo "      <properties>" >> ~/.m2/settings.xml
# echo "        <gpg.passphrase>$GPG_PASSPHRASE</gpg.passphrase>" >> ~/.m2/settings.xml
echo "      </properties>" >> ~/.m2/settings.xml
echo "    </profile>" >> ~/.m2/settings.xml
echo "  </profiles>" >> ~/.m2/settings.xml
echo "  <mirrors/>" >> ~/.m2/settings.xml
echo "  <proxies/>" >> ~/.m2/settings.xml
echo "  <activeProfiles/>" >> ~/.m2/settings.xml
echo "</settings>" >> ~/.m2/settings.xml

chmod 0600 ~/.m2/settings.xml

mvn -P release -DskipTests clean install nexus-staging:deploy
