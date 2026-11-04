#!/usr/bin/env python3
"""
Bundle extra Fabric mods into CatClient (jar-in-jar). PERSONAL / PRIVATE use.

  python3 bundle_catclient.py [MODS_DIR] [--base CATCLIENT.jar] [--out OUT.jar] [--no-hide]

MODS_DIR  folder with the mod jars (default: .). A .zip that is a renamed jar is fine.
--base    CatClient jar to start from (default: newest catclient*.jar in MODS_DIR)
--out     output (default: <base dir>/CatClient-bundled.jar)
--no-hide keep bundled mods visible in Mod Menu (default: hidden via the "library" badge;
          needs hide_modmenu.py next to this script)

Picks up (case-insensitive): cartcore*, macecore*, spearcore*, prestige*, redaction*,
fabric-language-kotlin*|flk*, oneconfig*, fzzy*  -- missing ones are skipped with a warning.
"""
import argparse, fnmatch, json, os, re, sys, zipfile
try:
    from hide_modmenu import patch_jar        # same folder
except Exception:
    patch_jar = None

BASE_IDS = {"fabricloader", "minecraft", "java", "fabric-api", "fabric"}
WANTED = ["cartcore*", "macecore*", "spearcore*", "prestige*", "redaction*",
          "fabric-language-kotlin*", "flk*", "oneconfig*", "fzzy*"]

def find(folder, pattern):
    return sorted(f for f in os.listdir(folder)
                  if fnmatch.fnmatch(f.lower(), pattern.lower())
                  and f.lower().endswith((".jar", ".zip")))

def read_meta(path):
    with zipfile.ZipFile(path) as z:
        return json.loads(z.read("fabric.mod.json").decode("utf-8"))

def nested_ids(src, meta):
    ids = set()
    for j in (meta.get("jars") or []):
        try:
            with zipfile.ZipFile(src) as z:
                ids.add(json.loads(zipfile.ZipFile(z.open(j["file"])).read("fabric.mod.json"))["id"])
        except Exception:
            pass
    return ids

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("mods_dir", nargs="?", default=".")
    ap.add_argument("--base"); ap.add_argument("--out")
    ap.add_argument("--no-hide", action="store_true", help="keep bundled mods visible in Mod Menu")
    a = ap.parse_args()
    folder = os.path.expanduser(a.mods_dir)

    if a.base:
        base = a.base
    else:
        cands = [f for f in find(folder, "catclient*")
                 if not re.search(r"bundled|sources|dev", f, re.I)]
        if not cands:
            sys.exit("CatClient jar not found in " + folder)
        base = os.path.join(folder, cands[-1])
    out = a.out or os.path.join(os.path.dirname(os.path.abspath(base)), "CatClient-bundled.jar")
    print("Base:", base)

    nested, seen = [], set()
    for pat in WANTED:
        for f in find(folder, pat)[-1:]:
            if f in seen:
                continue
            seen.add(f)
            p = os.path.join(folder, f)
            try:
                meta = read_meta(p)
            except Exception as e:
                print("  [skip]", f, "- not a Fabric mod jar (%s)" % e); continue
            inner = re.sub(r"\.zip$", ".jar", f, flags=re.I).replace(" ", "_")
            nested.append((inner, p, meta))
            print("  [add ] %-48s id=%s v=%s" % (f, meta.get("id"), meta.get("version")))
        if not find(folder, pat):
            print("  [skip] nothing matching", pat)
    if not nested:
        sys.exit("Nothing to bundle.")

    with zipfile.ZipFile(base) as zin:
        mod = json.loads(zin.read("fabric.mod.json").decode("utf-8"))
        jars = mod.setdefault("jars", [])
        have = {j["file"] for j in jars}
        provided = {mod["id"]}
        for j in jars:
            try:
                provided.add(json.loads(zipfile.ZipFile(zin.open(j["file"])).read("fabric.mod.json"))["id"])
            except Exception:
                pass
        for inner, src, meta in nested:
            if "META-INF/jars/" + inner not in have:
                jars.append({"file": "META-INF/jars/" + inner})
            provided.add(meta["id"]); provided |= nested_ids(src, meta)
        mod_json = json.dumps(mod, separators=(",", ":")).encode("utf-8")
        with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                if item.filename != "fabric.mod.json":
                    zout.writestr(item, zin.read(item.filename))
            zout.writestr("fabric.mod.json", mod_json)
            for inner, src, _ in nested:
                data = open(src, "rb").read()
                if patch_jar and not a.no_hide:
                    data = patch_jar(data)            # tag as Mod Menu "library" (hidden by default)
                zi = zipfile.ZipInfo("META-INF/jars/" + inner, (1980, 1, 1, 0, 0, 0))
                zi.compress_type = zipfile.ZIP_STORED
                zout.writestr(zi, data)

    def with_children(src, meta):
        yield meta
        for j in (meta.get("jars") or []):
            try:
                with zipfile.ZipFile(src) as z:
                    yield json.loads(zipfile.ZipFile(z.open(j["file"])).read("fabric.mod.json"))
            except Exception:
                pass
    bad, reported = 0, set()
    for _, src, top in nested:
      for m in with_children(src, top):
        for dep in (m.get("depends") or {}):
            if dep in BASE_IDS or (dep.startswith("fabric-") and dep != "fabric-language-kotlin") or dep in provided:
                continue
            if (m["id"], dep) in reported:
                continue
            reported.add((m["id"], dep))
            print("  [WARN] %s needs '%s' - not bundled." % (m["id"], dep)); bad += 1
    print("\nWrote:", out)
    sys.exit(1 if bad else 0)

if __name__ == "__main__":
    main()
