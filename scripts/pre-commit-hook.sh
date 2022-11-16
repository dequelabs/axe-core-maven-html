#!/usr/bin/env sh
mkdir -p .cache
cd .cache


# For reference: https://github.com/maltzj/google-style-precommit-hook
# For reference: https://github.com/google/google-java-format#google-java-format
# Ensure Google Java format JAR exists in .cache otherwise fetch and download it
if [ ! -f google-java-format-1.7-all-deps.jar ]
then
    curl -LJO "https://github.com/google/google-java-format/releases/download/google-java-format-1.7/google-java-format-1.7-all-deps.jar"
    chmod 755 google-java-format-1.7-all-deps.jar
fi
cd ..

# For reference: https://github.com/clibs/clib/blob/master/scripts/pre-commit-hook.sh#L3-L10
format_and_restage_file () {
  local file="$1"
  if [ -f "$file" ]; then
    java -jar .cache/google-java-format-1.7-all-deps.jar --replace $file
    git add "$file"
  fi
}

# Format each staged file ending in .java
for file in `git diff-index --cached --name-only HEAD | grep -iE '.*java$' ` ; do
  format_and_restage_file "$file"
done
