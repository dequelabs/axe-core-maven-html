#!/usr/bin/env bash
set -euo pipefail

staging="${1:?usage: sign-artifacts.sh STAGING_DIR}"
: "${GPG_PRIVATE_KEY:?GPG_PRIVATE_KEY must be set}"
: "${GPG_PASSPHRASE:?GPG_PASSPHRASE must be set}"
: "${GPG_KEY_FINGERPRINT:?GPG_KEY_FINGERPRINT must be set}"

GNUPGHOME="$(mktemp -d)"
export GNUPGHOME
trap 'gpgconf --kill all >/dev/null 2>&1 || true; rm -rf "$GNUPGHOME"' EXIT

printf '%s' "$GPG_PRIVATE_KEY" | base64 --decode |
  gpg --batch --quiet --pinentry-mode loopback --passphrase-fd 3 --import 3< <(printf '%s' "$GPG_PASSPHRASE")

if ! gpg --batch --with-colons --list-secret-keys "$GPG_KEY_FINGERPRINT" 2>/dev/null |
  awk -F: -v want="$GPG_KEY_FINGERPRINT" '$1 == "fpr" && $10 == want { found = 1 } END { exit !found }'; then
  echo "::error::GPG_PRIVATE_KEY is not the key with fingerprint $GPG_KEY_FINGERPRINT" >&2
  exit 1
fi

mapfile -d '' artifacts < <(find "$staging" -type f \
  ! -name 'maven-metadata*' ! -name '*.md5' ! -name '*.sha1' \
  ! -name '*.sha256' ! -name '*.sha512' ! -name '*.asc' -print0 | sort -z)

if [[ ${#artifacts[@]} -eq 0 ]]; then
  echo "::error::no artifacts to sign under $staging" >&2
  exit 1
fi

for artifact in "${artifacts[@]}"; do
  gpg --batch --quiet --yes --pinentry-mode loopback --passphrase-fd 3 \
    --local-user "$GPG_KEY_FINGERPRINT" --armor --detach-sign \
    --output "$artifact.asc" "$artifact" 3< <(printf '%s' "$GPG_PASSPHRASE")
done

echo "Signed ${#artifacts[@]} artifacts"
