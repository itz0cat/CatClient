#!/usr/bin/env python3
"""
Hide bundled mods from Mod Menu by tagging them with the 'library' badge
(custom.modmenu.badges = ["library"] in their fabric.mod.json). Mod Menu hides library
mods unless "Show libraries" is switched on in its filter. Works recursively on nested jars.
Only the metadata of the jar copy you point it at is changed; code and licenses are untouched.

  python3 scripts/hide_modmenu.py libs          # patches libs/*.jar in place (do this in CI, not in git)
"""
import io, json, os, re, sys, zipfile

def patch_jar(data):
    zin = zipfile.ZipFile(io.BytesIO(data))
    meta = json.loads(zin.read("fabric.mod.json").decode("utf-8"))
    custom = meta.get("custom")
    if not isinstance(custom, dict):
        custom = meta["custom"] = {}
    mm = custom.get("modmenu")
    if not isinstance(mm, dict):
        mm = custom["modmenu"] = {}
    badges = mm.get("badges")
    if not isinstance(badges, list):
        badges = mm["badges"] = []
    if "library" not in badges:
        badges.append("library")
    nested = {j.get("file") for j in (meta.get("jars") or []) if isinstance(j, dict)}
    out = io.BytesIO()
    with zipfile.ZipFile(out, "w") as zout:
        for item in zin.infolist():
            name = item.filename
            if re.match(r"META-INF/[^/]+\.(SF|RSA|DSA|EC)$", name, re.I):
                continue                       # signature files would no longer match
            buf = zin.read(name)
            if name == "fabric.mod.json":
                buf = json.dumps(meta, separators=(",", ":")).encode("utf-8")
            elif name in nested:
                try:
                    buf = patch_jar(buf)
                except Exception as e:
                    print("  [warn] could not patch nested", name, e)
            zout.writestr(item, buf)
    return out.getvalue()

def main():
    folder = sys.argv[1] if len(sys.argv) > 1 else "libs"
    for f in sorted(os.listdir(folder)):
        if not f.lower().endswith(".jar"):
            continue
        p = os.path.join(folder, f)
        try:
            data = open(p, "rb").read()
            new = patch_jar(data)
        except Exception as e:
            print("[skip] %s (%s)" % (f, e)); continue
        open(p, "wb").write(new)
        print("[hidden in Mod Menu] " + f)

if __name__ == "__main__":
    main()
