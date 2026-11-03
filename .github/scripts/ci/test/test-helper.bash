REPO_ROOT="$(cd "$BATS_TEST_DIRNAME/../../../.." && pwd)"
SCRIPTS="$REPO_ROOT/.github/scripts/ci"

make_signing_key() {
  KEY_HOME="$(mktemp -d)"
  chmod 700 "$KEY_HOME"
  GNUPGHOME="$KEY_HOME" gpg --batch --quiet --passphrase 'test passphrase' \
    --quick-gen-key 'CI Test <ci@example.com>' default default never 2>/dev/null
  GPG_KEY_FINGERPRINT="$(GNUPGHOME="$KEY_HOME" gpg --batch --with-colons --list-secret-keys | awk -F: '/^fpr/ { print $10; exit }')"
  GPG_PRIVATE_KEY="$(GNUPGHOME="$KEY_HOME" gpg --batch --pinentry-mode loopback --passphrase 'test passphrase' --export-secret-keys --armor "$GPG_KEY_FINGERPRINT" | base64 | tr -d '\n')"
  GPG_PASSPHRASE='test passphrase'
  export GPG_KEY_FINGERPRINT GPG_PRIVATE_KEY GPG_PASSPHRASE
}

make_staging_repo() {
  local root="$1" version="$2"
  local base="$root/com/deque/html/axe-core"
  mkdir -p "$base/selenium/$version" "$base/virtual-superpackage/$version"
  printf 'jar bytes' >"$base/selenium/$version/selenium-$version.jar"
  printf 'sources bytes' >"$base/selenium/$version/selenium-$version-sources.jar"
  printf 'javadoc bytes' >"$base/selenium/$version/selenium-$version-javadoc.jar"
  printf '<project/>' >"$base/selenium/$version/selenium-$version.pom"
  printf '<project/>' >"$base/virtual-superpackage/$version/virtual-superpackage-$version.pom"
  printf '<metadata/>' >"$base/selenium/maven-metadata.xml"
  local file
  while IFS= read -r -d '' file; do
    md5sum_of "$file" >"$file.md5"
    sha1sum_of "$file" >"$file.sha1"
  done < <(find "$root" -type f -print0)
}

md5sum_of() {
  if command -v md5sum >/dev/null; then md5sum "$1" | cut -d' ' -f1; else md5 -q "$1"; fi
}

sha1sum_of() {
  shasum -a 1 "$1" | cut -d' ' -f1
}
