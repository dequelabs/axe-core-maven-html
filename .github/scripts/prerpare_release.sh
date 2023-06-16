#!/bin/bash

# Fail on first error.
set -e

releaseLevel="$1"

oldVersion="$(node -pe 'require("./package.json").version')"
npx standard-version --release-as "$releaseLevel" --skip.commit=true --skip.changelog=true --skip.tag=true

cd selenium
npx standard-version --release-as "$releaseLevel" --skip.commit=true --skip.changelog=true --skip.tag=true
cd ..
newVersion="$(node -pe 'require("./package.json").version')"


# xmlstarlet is used to edit xml files
sudo apt-get install -y xmlstarlet

updateXML() {
  xpath="$1"
  newValue="$2"
  file="$3"

  # Update file inplace (--inplace) and preserve formatting (-P)
  xmlstarlet edit -P --inplace --update "$xpath" --value "$newValue" "$file"
}

versionXpath=/_:project/_:version
parentVersionXpath=/_:project/_:parent/_:version
# Reads as: Select the "version" node of the "dependency" node that has a "groupId" node which matches "com.deque.html.axe-devtools"
dequeDepVersionXpath='/_:project/_:dependencies/_:dependency[_:groupId="com.deque.html.axe-core"]/_:version'
propertiesVersionXpath=/_:project/_:properties/_:version

updateXML "$versionXpath" "$newVersion" pom.xml

# Update version, the version of parent, and version of any ADT deps in our ADT packages
for package in utilities selenium playwright; do
  updateXML "$versionXpath" "$newVersion" "$package"/pom.xml
  updateXML "$parentVersionXpath" "$newVersion" "$package"/pom.xml
  # If no dep is found no change will be made
  updateXML "$dequeDepVersionXpath" "$newVersion" "$package"/pom.xml
done

npx conventional-changelog-cli -p angular -i CHANGELOG.md -s

