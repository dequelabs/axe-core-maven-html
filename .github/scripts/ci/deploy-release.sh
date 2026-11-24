#!/usr/bin/env bash
set -euo pipefail

staging="${1:?usage: deploy-release.sh STAGING_DIR}"
: "${CENTRAL_TOKEN_USERNAME:?CENTRAL_TOKEN_USERNAME must be set}"
: "${CENTRAL_TOKEN_PASSWORD:?CENTRAL_TOKEN_PASSWORD must be set}"
portal="${PORTAL_URL:-https://central.sonatype.com}"
interval="${POLL_INTERVAL_SECONDS:-15}"
timeout="${POLL_TIMEOUT_SECONDS:-3600}"

staging="$(cd "$staging" && pwd)"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

mapfile -d '' artifacts < <(cd "$staging" && find . -type f \( -name '*.jar' -o -name '*.pom' \) -print0 | sort -z)
if [[ ${#artifacts[@]} -eq 0 ]]; then
  echo "::error::no artifacts to deploy under $staging" >&2
  exit 1
fi

for artifact in "${artifacts[@]}"; do
  if [[ "$artifact" == *-SNAPSHOT* ]]; then
    echo "::error::$artifact is a snapshot; snapshots are not deployed to the Portal" >&2
    exit 1
  fi
  for suffix in asc md5 sha1; do
    if [[ ! -f "$staging/$artifact.$suffix" ]]; then
      echo "::error::$artifact has no .$suffix" >&2
      exit 1
    fi
  done
done

bundle="$work/central-bundle.zip"
(cd "$staging" && find . -type f ! -name 'maven-metadata*' | sed 's|^\./||' | sort | zip -q -X "$bundle" -@)

auth="Authorization: Bearer $(printf '%s:%s' "$CENTRAL_TOKEN_USERNAME" "$CENTRAL_TOKEN_PASSWORD" | base64 | tr -d '\n')"
name="$(basename "$(dirname "${artifacts[0]}")")"

deployment="$(curl --silent --show-error --fail-with-body \
  --header @<(printf '%s\n' "$auth") \
  --form "bundle=@$bundle;type=application/octet-stream" \
  "$portal/api/v1/publisher/upload?name=axe-core-maven-html-$name&publishingType=AUTOMATIC")"
echo "Uploaded deployment $deployment"

deadline=$((SECONDS + ${timeout%.*}))
while :; do
  status="$(curl --silent --show-error --fail-with-body --request POST \
    --header @<(printf '%s\n' "$auth") \
    "$portal/api/v1/publisher/status?id=$deployment")"
  state="$(jq -r '.deploymentState' <<<"$status")"
  echo "Deployment $deployment is $state"
  case "$state" in
    PUBLISHED)
      exit 0
      ;;
    FAILED)
      echo "::error::the Portal rejected deployment $deployment" >&2
      jq '.errors' <<<"$status" >&2
      exit 1
      ;;
  esac
  if ((SECONDS >= deadline)); then
    echo "::error::deployment $deployment was still $state after ${timeout}s" >&2
    exit 1
  fi
  sleep "$interval"
done
