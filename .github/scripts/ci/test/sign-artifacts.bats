bats_require_minimum_version 1.5.0

load test-helper

setup() {
  make_signing_key
  STAGING="$(mktemp -d)"
  make_staging_repo "$STAGING" 1.2.3
  ARTIFACTS="$STAGING/com/deque/html/axe-core"
}

teardown() {
  rm -rf "$STAGING" "$KEY_HOME"
}

@test "signs every artifact with a signature that verifies against the key" {
  run "$SCRIPTS/sign-artifacts.sh" "$STAGING"
  [ "$status" -eq 0 ]
  for file in \
    selenium/1.2.3/selenium-1.2.3.jar \
    selenium/1.2.3/selenium-1.2.3-sources.jar \
    selenium/1.2.3/selenium-1.2.3-javadoc.jar \
    selenium/1.2.3/selenium-1.2.3.pom \
    virtual-superpackage/1.2.3/virtual-superpackage-1.2.3.pom; do
    run env GNUPGHOME="$KEY_HOME" gpg --batch --status-fd 1 --verify "$ARTIFACTS/$file.asc" "$ARTIFACTS/$file"
    [ "$status" -eq 0 ]
    [[ "$output" == *"VALIDSIG $GPG_KEY_FINGERPRINT"* ]]
  done
}

@test "does not sign checksums or repository metadata" {
  "$SCRIPTS/sign-artifacts.sh" "$STAGING"
  run find "$STAGING" -name '*.md5.asc' -o -name '*.sha1.asc' -o -name 'maven-metadata*.asc'
  [ -z "$output" ]
}

@test "leaves the artifacts themselves unchanged" {
  local jar="$ARTIFACTS/selenium/1.2.3/selenium-1.2.3.jar"
  cp "$jar" "$BATS_TEST_TMPDIR/before.jar"
  "$SCRIPTS/sign-artifacts.sh" "$STAGING"
  cmp "$BATS_TEST_TMPDIR/before.jar" "$jar"
}

@test "fails when the key does not have the expected fingerprint" {
  GPG_KEY_FINGERPRINT=F8AF74A4CDA6E2063C8985E21A4AC53EFF73AC0F run "$SCRIPTS/sign-artifacts.sh" "$STAGING"
  [ "$status" -ne 0 ]
  run find "$STAGING" -name '*.asc'
  [ -z "$output" ]
}

@test "fails when the staging directory has no artifacts" {
  run "$SCRIPTS/sign-artifacts.sh" "$BATS_TEST_TMPDIR"
  [ "$status" -ne 0 ]
}

@test "does not leave the key in the caller's keyring" {
  local home="$BATS_TEST_TMPDIR/home"
  mkdir -m 700 "$home"
  GNUPGHOME="$home" "$SCRIPTS/sign-artifacts.sh" "$STAGING"
  run --separate-stderr env GNUPGHOME="$home" gpg --batch --with-colons --list-secret-keys
  [[ "$output" != *"sec:"* ]]
}
