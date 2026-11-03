setup() {
  WORK="$(mktemp -d)"
  REPO_ROOT="$(cd "$BATS_TEST_DIRNAME/../../../.." && pwd)"
  for dir in . selenium playwright utilities; do
    mkdir -p "$WORK/$dir"
    cp "$REPO_ROOT/$dir/pom.xml" "$WORK/$dir/pom.xml"
  done
  VERSION="$(python3 -I -c 'import xml.etree.ElementTree as E,sys;print(E.parse(sys.argv[1]).getroot().find("{http://maven.apache.org/POM/4.0.0}version").text)' "$WORK/pom.xml")"
}

teardown() {
  rm -rf "$WORK"
}

version_of() {
  python3 -I -c '
import sys, xml.etree.ElementTree as E
ns = "{http://maven.apache.org/POM/4.0.0}"
root = E.parse(sys.argv[1]).getroot()
print(root.find(sys.argv[2].replace("/", "/" + ns).replace("^", ns)).text)
' "$1" "$2"
}

@test "appends -SNAPSHOT to every module version" {
  run python3 -I "$REPO_ROOT/.github/scripts/ci/snapshot-version.py" "$WORK"
  [ "$status" -eq 0 ]
  for dir in . selenium playwright utilities; do
    [ "$(version_of "$WORK/$dir/pom.xml" '^version')" = "$VERSION-SNAPSHOT" ]
  done
}

@test "appends -SNAPSHOT to module parent versions" {
  python3 -I "$REPO_ROOT/.github/scripts/ci/snapshot-version.py" "$WORK"
  for dir in selenium playwright utilities; do
    [ "$(version_of "$WORK/$dir/pom.xml" '^parent/version')" = "$VERSION-SNAPSHOT" ]
  done
}

@test "appends -SNAPSHOT to dependencies on sibling modules only" {
  python3 -I "$REPO_ROOT/.github/scripts/ci/snapshot-version.py" "$WORK"
  for dir in selenium playwright; do
    run grep -A1 '<artifactId>dequeutilites</artifactId>' "$WORK/$dir/pom.xml"
    [[ "$output" == *"<version>$VERSION-SNAPSHOT</version>"* ]]
  done
  run grep -A1 '<artifactId>selenium-java</artifactId>' "$WORK/selenium/pom.xml"
  [[ "$output" != *"SNAPSHOT"* ]]
}

@test "leaves already-snapshot poms unchanged" {
  python3 -I "$REPO_ROOT/.github/scripts/ci/snapshot-version.py" "$WORK"
  cp "$WORK/selenium/pom.xml" "$WORK/before.xml"
  python3 -I "$REPO_ROOT/.github/scripts/ci/snapshot-version.py" "$WORK"
  cmp "$WORK/before.xml" "$WORK/selenium/pom.xml"
  [ "$(version_of "$WORK/pom.xml" '^version')" = "$VERSION-SNAPSHOT" ]
}
