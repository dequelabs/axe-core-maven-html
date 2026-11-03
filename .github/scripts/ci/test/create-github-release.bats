bats_require_minimum_version 1.5.0
load test-helper

setup() {
  WORK="$(mktemp -d)"
  ASSETS="$(mktemp -d)"
  BIN="$(mktemp -d)"
  make_staging_repo "$ASSETS" 1.2.3
  while IFS= read -r -d '' file; do
    printf 'signature' >"$file.asc"
  done < <(find "$ASSETS" -type f \( -name '*.jar' -o -name '*.pom' \) -print0)
  cat >"$WORK/pom.xml" <<'XML'
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.deque.html.axe-core</groupId>
  <artifactId>virtual-superpackage</artifactId>
  <version>1.2.3</version>
  <dependencies>
    <dependency>
      <version>9.9.9</version>
    </dependency>
  </dependencies>
</project>
XML
  cat >"$WORK/CHANGELOG.md" <<'MD'
## [1.2.3](https://example.com/compare/v1.2.2...v1.2.3) (2026-11-03)


### Bug Fixes

* fix a thing

# [1.2.2](https://example.com/compare/v1.2.1...v1.2.2) (2026-10-01)

* older entry
MD
  cat >"$BIN/gh" <<'SH'
#!/usr/bin/env bash
if [[ "$1 $2" == "release view" ]]; then
  [[ -n "${GH_STUB_EXISTING:-}" ]] && exit 0
  exit 1
fi
printf '%s\n' "$@" >"$GH_STUB_LOG/args"
for ((i = 1; i <= $#; i++)); do
  if [[ "${!i}" == "--notes-file" ]]; then
    j=$((i + 1))
    cp "${!j}" "$GH_STUB_LOG/notes"
  fi
done
SH
  chmod +x "$BIN/gh"
  export PATH="$BIN:$PATH" GH_STUB_LOG="$WORK" GH_TOKEN=token TARGET_SHA=abc123
}

teardown() {
  rm -rf "$WORK" "$ASSETS" "$BIN"
}

release() {
  (cd "$WORK" && "$SCRIPTS/create-github-release.sh" "$ASSETS")
}

@test "creates the version's tag at the released commit" {
  run release
  [ "$status" -eq 0 ]
  run cat "$WORK/args"
  [ "${lines[0]}" = "release" ]
  [ "${lines[1]}" = "create" ]
  [ "${lines[2]}" = "v1.2.3" ]
  [[ "$output" == *$'--target\nabc123'* ]]
  [[ "$output" == *$'--title\nRelease 1.2.3'* ]]
}

@test "uses the version's changelog section as the description" {
  release
  run cat "$WORK/notes"
  [[ "$output" == "### Bug Fixes"* ]]
  [[ "$output" == *"* fix a thing" ]]
  [[ "$output" != *"older entry"* ]]
  [[ "$output" != *"[1.2.3]"* ]]
}

@test "attaches every published artifact and its signature" {
  release
  for file in selenium-1.2.3.jar selenium-1.2.3-sources.jar selenium-1.2.3-javadoc.jar \
    selenium-1.2.3.pom virtual-superpackage-1.2.3.pom; do
    grep -qx ".*/$file" "$WORK/args"
    grep -qx ".*/$file.asc" "$WORK/args"
  done
}

@test "does not attach checksums or repository metadata" {
  release
  run grep -E '\.(md5|sha1)$|maven-metadata' "$WORK/args"
  [ "$status" -ne 0 ]
}

@test "fails when the changelog's newest section is not this version" {
  sed -i.bak 's/\[1.2.3\]/[1.2.4]/' "$WORK/CHANGELOG.md"
  run release
  [ "$status" -ne 0 ]
  [ ! -e "$WORK/args" ]
}

@test "does nothing when the release already exists" {
  GH_STUB_EXISTING=1 run release
  [ "$status" -eq 0 ]
  [ ! -e "$WORK/args" ]
}
