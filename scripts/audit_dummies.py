#!/usr/bin/env python3
"""
Find DUMMY cheat options in CatClient: options in CheatMods.java that have nothing behind them.

  python3 scripts/audit_dummies.py [--src path/to/CheatMods.java] [--libs libs]

An option is WORKING if either
  * cart/spear/mace section: CheatMods.setFieldSafe(..., "<field>", this.<option>.get()) exists AND
    that field exists in the real core config class inside libs/<core>.jar, or
  * combat/misc/render/client section: a Prestige module class with the same (normalised) name
    exists in libs/prestige.jar AND PrestigeBridge.set("<option>", ...) is called in the source.
Anything else is reported as DUMMY. Exit code 1 if any DUMMY / broken link is found.
"""
import argparse, os, re, sys, zipfile

CORE = {  # section -> (jar, config class, extras class)
    "cart":  ("cartcore.jar",  "thetruetrident/cartcore/cartConfig.class", None),
    "spear": ("spearcore.jar", "thetruetrident/spearcore/spearcoreConfig.class", None),
    "mace":  ("macecore.jar",  "thetruetrident/macecore/macecoreConfig.class",
                               "thetruetrident/macecore/macecoreConfig$MySection.class"),
}
PRESTIGE_SECTIONS = {"combat", "misc", "render", "client"}
SPECIAL = {"prestige": "clickgui"}          # CatClient option -> Prestige class name

def norm(s): return re.sub(r"[^a-z0-9]", "", s.lower())

def has_name(jar_path, cls, name):
    try:
        with zipfile.ZipFile(jar_path) as z:
            return name.encode() in z.read(cls)
    except Exception:
        return False

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--src"); ap.add_argument("--libs", default="libs")
    a = ap.parse_args()
    src = a.src
    if not src:
        for r, _, fs in os.walk("."):
            if "CheatMods.java" in fs:
                src = os.path.join(r, "CheatMods.java"); break
    if not src:
        sys.exit("CheatMods.java not found")
    text = open(src, encoding="utf-8").read()

    opts = re.findall(r'(?:public\s+)?(?:final\s+)?\w+Option\s+(\w+)\s*=\s*new\s+\w+Option\(\s*"cheat\.(\w+)\.', text)
    links = {}  # option -> (targetVar, coreField)
    for tgt, fld, opt in re.findall(r'setFieldSafe\(\s*(\w+)\s*,\s*"(\w+)"\s*,\s*this\.(\w+)\.get\(\)', text):
        links[opt] = (tgt, fld)
    bridged = set(re.findall(r'PrestigeBridge\.set\(\s*"(\w+)"', text))
    bridge_generic = "PrestigeBridge" in text and not bridged   # loop-style bridge

    p_classes = set()
    pj = os.path.join(a.libs, "prestige.jar")
    if os.path.exists(pj):
        with zipfile.ZipFile(pj) as z:
            for n in z.namelist():
                m = re.match(r"juxxl/prestige/module/modules/\w+/(\w+)\.class$", n)
                if m: p_classes.add(norm(m.group(1)))

    rows, dummy = [], 0
    for opt, sec in opts:
        status, why = "WORKING", ""
        if sec in CORE:
            jar, cfg, extras = CORE[sec]
            jp = os.path.join(a.libs, jar)
            if opt not in links:
                status, why = "DUMMY", "no setFieldSafe link to the core mod"
            else:
                tgt, fld = links[opt]
                cls = extras if (tgt == "extrasObj" and extras) else cfg
                if not os.path.exists(jp):
                    status, why = "BROKEN", "libs/%s missing" % jar
                elif not has_name(jp, cls, fld):
                    status, why = "BROKEN", "field '%s' not in %s" % (fld, os.path.basename(cls))
        elif sec in PRESTIGE_SECTIONS:
            want = SPECIAL.get(opt, norm(opt))
            if not p_classes:
                status, why = "BROKEN", "libs/prestige.jar missing"
            elif want not in p_classes:
                status, why = "DUMMY", "no Prestige module class named like it"
            elif not (opt in bridged or bridge_generic):
                status, why = "DUMMY", "PrestigeBridge.set(\"%s\", ...) never called" % opt
        else:
            status, why = "DUMMY", "unknown section " + sec
        if status != "WORKING": dummy += 1
        rows.append((sec, opt, status, why))

    for sec, opt, st, why in rows:
        print("%-7s %-30s %-8s %s" % (sec, opt, st, why))
    if "handleAutoCart" in text:
        print("\nNOTE: handleAutoCart() still exists - it duplicates Cartcore, remove it.")
    print("\n%d options, %d working, %d dummy/broken" % (len(rows), len(rows) - dummy, dummy))
    sys.exit(1 if dummy else 0)

if __name__ == "__main__":
    main()
