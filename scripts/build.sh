#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

rm -rf build/classes
mkdir -p build/classes

mapfile -t sources < <(rg --files src/main/java -g '*.java' | sort)
if [[ "${#sources[@]}" -eq 0 ]]; then
  echo "No production Java sources found" >&2
  exit 1
fi

javac -Xlint:all -d build/classes "${sources[@]}"
echo "Compiled ${#sources[@]} production source files into build/classes"
