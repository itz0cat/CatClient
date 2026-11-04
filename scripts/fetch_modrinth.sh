#!/usr/bin/env bash
# Download Modrinth dependencies for CatClient (MC 1.21.11, fabric) into libs/ with fixed names.
# Verifies sha512 and the mod id inside each jar. Usage: scripts/fetch_modrinth.sh [--with-fzzy]
set -euo pipefail
GV="1.21.11"; LOADER="fabric"; OUT="libs"; UA="User-Agent: itz0cat-CatClient-ci"
mkdir -p "$OUT"

fetch() {  # slug  output-file  expected-mod-id
  local slug="$1" out="$2" want="$3" json ver url sha id
  json=$(curl -fsSG "https://api.modrinth.com/v2/project/$slug/version" -H "$UA" \
         --data-urlencode "game_versions=[\"$GV\"]" --data-urlencode "loaders=[\"$LOADER\"]")
  ver=$(echo "$json" | jq -c '([.[] | select(.version_type=="release")][0]) // .[0]')
  if [ -z "$ver" ] || [ "$ver" = "null" ]; then echo "ERROR: no $GV/$LOADER version for $slug"; exit 1; fi
  url=$(echo "$ver" | jq -r '(.files | map(select(.primary==true))[0] // .files[0]).url')
  sha=$(echo "$ver" | jq -r '(.files | map(select(.primary==true))[0] // .files[0]).hashes.sha512')
  curl -fsSL -H "$UA" "$url" -o "$OUT/$out"
  echo "$sha  $OUT/$out" | sha512sum -c - >/dev/null || { echo "ERROR: sha512 mismatch for $slug"; exit 1; }
  id=$(unzip -p "$OUT/$out" fabric.mod.json | jq -r '.id')
  if [ "$id" != "$want" ] && [ "$id" != "oneconfig" ]; then echo "ERROR: $slug jar has mod id '$id', expected '$want'"; exit 1; fi
  echo "OK  $slug -> $OUT/$out  (id=$id, $(echo "$ver" | jq -r '.version_number'))"
}

fetch fabric-language-kotlin flk.jar        fabric-language-kotlin
fetch oneconfig              oneconfig.jar  oneconfigv1
if [ "${1:-}" = "--with-fzzy" ]; then fetch fzzy-config fzzy_config.jar fzzy_config; fi
