#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

rm -rf build/test-classes
mkdir -p build/test-classes

mapfile -t sources < <(rg --files src/main/java src/test/java -g '*.java' | sort)
if [[ "${#sources[@]}" -eq 0 ]]; then
  echo "No Java sources found" >&2
  exit 1
fi

javac -Xlint:all -d build/test-classes "${sources[@]}"
java -cp build/test-classes com.cursor.ptt.PttSessionTest
