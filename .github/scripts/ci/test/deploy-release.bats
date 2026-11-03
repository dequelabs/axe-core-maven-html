bats_require_minimum_version 1.5.0
load test-helper

setup() {
  STAGING="$(mktemp -d)"
  LOG="$(mktemp -d)"
  make_staging_repo "$STAGING" 1.2.3
  while IFS= read -r -d '' file; do
    printf 'signature' >"$file.asc"
  done < <(find "$STAGING" -type f \( -name '*.jar' -o -name '*.pom' \) -print0)
  export CENTRAL_TOKEN_USERNAME=token-user CENTRAL_TOKEN_PASSWORD=token-pass
  export POLL_INTERVAL_SECONDS=0 POLL_TIMEOUT_SECONDS=5
}

teardown() {
  [[ -n "${STUB_PID:-}" ]] && kill "$STUB_PID" 2>/dev/null
  rm -rf "$STAGING" "$LOG"
}

start_portal() {
  python3 -I "$BATS_TEST_DIRNAME/portal-stub.py" "$LOG" "$1" &
  STUB_PID=$!
  for _ in $(seq 50); do [[ -s "$LOG/port" ]] && break; sleep 0.1; done
  export PORTAL_URL="http://127.0.0.1:$(cat "$LOG/port")"
}

bundle_entries() {
  python3 -I - "$LOG/upload.body" <<'PY'
import io, sys, zipfile
body = open(sys.argv[1], 'rb').read()
start = body.index(b'PK\x03\x04')
end = body.rindex(b'\r\n--')
for name in sorted(zipfile.ZipFile(io.BytesIO(body[start:end])).namelist()):
    print(name)
PY
}

@test "uploads a bundle of artifacts, signatures and checksums in repository layout" {
  start_portal PUBLISHED
  run "$SCRIPTS/deploy-release.sh" "$STAGING"
  [ "$status" -eq 0 ]
  run bundle_entries
  [[ "$output" == *"com/deque/html/axe-core/selenium/1.2.3/selenium-1.2.3.jar"$'\n'* ]]
  [[ "$output" == *"com/deque/html/axe-core/selenium/1.2.3/selenium-1.2.3.jar.asc"* ]]
  [[ "$output" == *"com/deque/html/axe-core/selenium/1.2.3/selenium-1.2.3.jar.md5"* ]]
  [[ "$output" == *"com/deque/html/axe-core/selenium/1.2.3/selenium-1.2.3.jar.sha1"* ]]
  [[ "$output" == *"com/deque/html/axe-core/virtual-superpackage/1.2.3/virtual-superpackage-1.2.3.pom.asc"* ]]
  [[ "$output" != *"maven-metadata"* ]]
}

@test "authenticates with the user token and asks for automatic publishing" {
  start_portal PUBLISHED
  "$SCRIPTS/deploy-release.sh" "$STAGING"
  run python3 -I -c 'import json,sys; d=json.load(open(sys.argv[1])); print(d["authorization"]); print(d["query"]["publishingType"][0]); print(d["content_type"])' "$LOG/upload.json"
  [ "${lines[0]}" = "Bearer $(printf 'token-user:token-pass' | base64)" ]
  [ "${lines[1]}" = "AUTOMATIC" ]
  [[ "${lines[2]}" == multipart/form-data* ]]
}

@test "waits until the deployment is published" {
  start_portal PENDING,VALIDATING,VALIDATED,PUBLISHING,PUBLISHED
  run "$SCRIPTS/deploy-release.sh" "$STAGING"
  [ "$status" -eq 0 ]
  [ "$(wc -l <"$LOG/status.log")" -eq 5 ]
  [ "$(sort -u "$LOG/status.log")" = "deployment-123" ]
}

@test "fails and reports the errors when the Portal rejects the deployment" {
  start_portal VALIDATING,FAILED
  run "$SCRIPTS/deploy-release.sh" "$STAGING"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid signature"* ]]
}

@test "fails when the deployment is not published before the timeout" {
  start_portal PUBLISHING
  POLL_TIMEOUT_SECONDS=1 POLL_INTERVAL_SECONDS=0.2 run "$SCRIPTS/deploy-release.sh" "$STAGING"
  [ "$status" -ne 0 ]
}

@test "refuses to upload an artifact without a signature" {
  start_portal PUBLISHED
  rm "$STAGING/com/deque/html/axe-core/selenium/1.2.3/selenium-1.2.3.jar.asc"
  run "$SCRIPTS/deploy-release.sh" "$STAGING"
  [ "$status" -ne 0 ]
  [ ! -e "$LOG/upload.body" ]
}

@test "refuses to upload snapshot versions" {
  start_portal PUBLISHED
  rm -rf "$STAGING"/*
  make_staging_repo "$STAGING" 1.2.3-SNAPSHOT
  run "$SCRIPTS/deploy-release.sh" "$STAGING"
  [ "$status" -ne 0 ]
  [ ! -e "$LOG/upload.body" ]
}
