#!/usr/bin/env bash
set -euo pipefail

assets="${1:?usage: create-github-release.sh ASSETS_DIR}"
: "${GH_TOKEN:?GH_TOKEN must be set}"
: "${TARGET_SHA:?TARGET_SHA must be set}"

version="$(python3 -I -c '
import xml.etree.ElementTree as ET
print(ET.parse("pom.xml").getroot().find("{http://maven.apache.org/POM/4.0.0}version").text)
')"

heading="$(grep -m1 -E '^#+ \[[0-9]' CHANGELOG.md || true)"
if [[ "$heading" != *"[$version]"* ]]; then
  echo "::error::CHANGELOG.md's newest section is '$heading', not $version" >&2
  exit 1
fi

if gh release view "v$version" >/dev/null 2>&1; then
  echo "::notice::release v$version already exists"
  exit 0
fi

notes="$(mktemp)"
trap 'rm -f "$notes"' EXIT
awk '
  /^#+ \[[0-9]/ { if (in_section) exit; in_section = 1; next }
  in_section { print }
' CHANGELOG.md | awk 'NF { seen = 1 } seen' >"$notes"

mapfile -d '' files < <(find "$assets" -type f \( -name '*.jar' -o -name '*.pom' -o -name '*.jar.asc' -o -name '*.pom.asc' \) -print0 | sort -z)
if [[ ${#files[@]} -eq 0 ]]; then
  echo "::error::no artifacts to attach under $assets" >&2
  exit 1
fi

gh release create "v$version" \
  --target "$TARGET_SHA" \
  --title "Release $version" \
  --notes-file "$notes" \
  "${files[@]}"
