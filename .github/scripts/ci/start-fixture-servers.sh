#!/usr/bin/env bash
set -euo pipefail

declare -A ports=([playwright]=1337 [selenium]=8001)
projects=("$@")
if [[ ${#projects[@]} -eq 0 ]]; then
  projects=(playwright selenium)
fi

for project in "${projects[@]}"; do
  port="${ports[$project]:?unknown project $project}"
  nohup npm --prefix="$project" start >"$RUNNER_TEMP/fixtures-$project.log" 2>&1 &
  if ! timeout 30 bash -c "until curl --silent --output /dev/null http://localhost:$port/; do sleep 0.5; done"; then
    echo "::error::$project fixture server did not start on port $port" >&2
    cat "$RUNNER_TEMP/fixtures-$project.log" >&2
    exit 1
  fi
  echo "$project fixtures served on port $port"
done
