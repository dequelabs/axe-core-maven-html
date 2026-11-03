bats_require_minimum_version 1.5.0
load test-helper

GROUP_PATH=com/deque/html/axe-core

setup() {
  STAGING="$(mktemp -d)"
  REMOTE="$(mktemp -d)"
  LOG="$(mktemp -d)"
  make_snapshot_staging "$STAGING"
  mkdir -p "$REMOTE/$GROUP_PATH/selenium"
  cat >"$REMOTE/$GROUP_PATH/selenium/maven-metadata.xml" <<'XML'
<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>com.deque.html.axe-core</groupId>
  <artifactId>selenium</artifactId>
  <versioning>
    <latest>1.2.2-SNAPSHOT</latest>
    <versions>
      <version>1.2.2-SNAPSHOT</version>
    </versions>
    <lastUpdated>20260101000000</lastUpdated>
  </versioning>
</metadata>
XML
  export CENTRAL_TOKEN_USERNAME=token-user CENTRAL_TOKEN_PASSWORD=token-pass
}

teardown() {
  [[ -n "${STUB_PID:-}" ]] && kill "$STUB_PID" 2>/dev/null
  rm -rf "$STAGING" "$REMOTE" "$LOG"
}

make_snapshot_staging() {
  local dir="$1/$GROUP_PATH/selenium"
  local version=1.2.3-SNAPSHOT stamp=1.2.3-20261103.101010-1
  mkdir -p "$dir/$version"
  printf 'jar bytes' >"$dir/$version/selenium-$stamp.jar"
  printf '<project/>' >"$dir/$version/selenium-$stamp.pom"
  cat >"$dir/$version/maven-metadata.xml" <<XML
<?xml version="1.0" encoding="UTF-8"?>
<metadata modelVersion="1.1.0">
  <groupId>com.deque.html.axe-core</groupId>
  <artifactId>selenium</artifactId>
  <version>$version</version>
  <versioning>
    <snapshot>
      <timestamp>20261103.101010</timestamp>
      <buildNumber>1</buildNumber>
    </snapshot>
    <lastUpdated>20261103101010</lastUpdated>
    <snapshotVersions>
      <snapshotVersion>
        <extension>jar</extension>
        <value>1.2.3-20261103.101010-1</value>
        <updated>20261103101010</updated>
      </snapshotVersion>
      <snapshotVersion>
        <extension>pom</extension>
        <value>1.2.3-20261103.101010-1</value>
        <updated>20261103101010</updated>
      </snapshotVersion>
    </snapshotVersions>
  </versioning>
</metadata>
XML
  cat >"$dir/maven-metadata.xml" <<XML
<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>com.deque.html.axe-core</groupId>
  <artifactId>selenium</artifactId>
  <versioning>
    <latest>$version</latest>
    <versions>
      <version>$version</version>
    </versions>
    <lastUpdated>20261103101010</lastUpdated>
  </versioning>
</metadata>
XML
  local file
  while IFS= read -r -d '' file; do
    printf 'signature' >"$file.asc"
  done < <(find "$1" -type f \( -name '*.jar' -o -name '*.pom' \) -print0)
  while IFS= read -r -d '' file; do
    md5sum_of "$file" >"$file.md5"
    sha1sum_of "$file" >"$file.sha1"
  done < <(find "$1" -type f ! -name '*.md5' ! -name '*.sha1' -print0)
}

start_repository() {
  python3 -I "$BATS_TEST_DIRNAME/repository-stub.py" "$REMOTE" "$LOG" token-user token-pass &
  STUB_PID=$!
  for _ in $(seq 50); do [[ -s "$LOG/port" ]] && break; sleep 0.1; done
  export SNAPSHOT_REPOSITORY_URL="http://127.0.0.1:$(cat "$LOG/port")/"
}

@test "uploads every artifact and signature into the snapshot repository" {
  start_repository
  run python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  [ "$status" -eq 0 ]
  for file in selenium-1.2.3-20261103.101010-1.jar selenium-1.2.3-20261103.101010-1.pom \
    selenium-1.2.3-20261103.101010-1.jar.asc selenium-1.2.3-20261103.101010-1.pom.asc; do
    cmp "$STAGING/$GROUP_PATH/selenium/1.2.3-SNAPSHOT/$file" "$REMOTE/$GROUP_PATH/selenium/1.2.3-SNAPSHOT/$file"
  done
}

@test "authenticates every upload with the user token" {
  start_repository
  python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  run grep '^PUT ' "$LOG/requests.log"
  [ "${#lines[@]}" -gt 0 ]
  [[ "$output" != *" False"* ]]
}

@test "keeps versions the repository already lists" {
  start_repository
  python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  run cat "$REMOTE/$GROUP_PATH/selenium/maven-metadata.xml"
  [[ "$output" == *"<version>1.2.2-SNAPSHOT</version>"* ]]
  [[ "$output" == *"<version>1.2.3-SNAPSHOT</version>"* ]]
}

@test "fails when the repository rejects the credentials" {
  start_repository
  CENTRAL_TOKEN_PASSWORD=wrong run python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  [ "$status" -ne 0 ]
  [ ! -e "$REMOTE/$GROUP_PATH/selenium/1.2.3-SNAPSHOT" ]
}

@test "refuses to deploy release versions" {
  start_repository
  make_staging_repo "$STAGING" 1.2.4
  run python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  [ "$status" -ne 0 ]
  ! grep -q '^PUT ' "$LOG/requests.log" 2>/dev/null
}

@test "merges into namespaced repository metadata" {
  cat >"$REMOTE/$GROUP_PATH/selenium/maven-metadata.xml" <<'XML'
<?xml version="1.0" encoding="UTF-8"?>
<metadata xmlns="http://maven.apache.org/METADATA/1.1.0" modelVersion="1.1.0">
  <groupId>com.deque.html.axe-core</groupId>
  <artifactId>selenium</artifactId>
  <versioning>
    <versions>
      <version>1.2.2-SNAPSHOT</version>
    </versions>
  </versioning>
</metadata>
XML
  start_repository
  run python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  [ "$status" -eq 0 ]
  run cat "$REMOTE/$GROUP_PATH/selenium/maven-metadata.xml"
  [[ "$output" == *"1.2.2-SNAPSHOT</"* ]]
  [[ "$output" == *"1.2.3-SNAPSHOT</"* ]]
}

@test "uploads checksums for the metadata it writes" {
  start_repository
  python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  local metadata="$REMOTE/$GROUP_PATH/selenium/maven-metadata.xml"
  [ "$(cat "$metadata.sha1")" = "$(sha1sum_of "$metadata")" ]
  [ "$(cat "$metadata.md5")" = "$(md5sum_of "$metadata")" ]
}

@test "does not follow redirects with the credentials" {
  start_repository
  printf '%selsewhere/maven-metadata.xml' "$SNAPSHOT_REPOSITORY_URL" >"$LOG/redirect"
  run python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  [ "$status" -ne 0 ]
  run grep '^GET /elsewhere' "$LOG/requests.log"
  [ "$status" -ne 0 ]
}

@test "refuses a repository that is not https" {
  SNAPSHOT_REPOSITORY_URL=http://repository.example.com/ run python3 -I "$SCRIPTS/deploy-snapshot.py" "$STAGING"
  [ "$status" -ne 0 ]
  [[ "$output" == *"https"* ]]
}
