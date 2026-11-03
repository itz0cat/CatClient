#!/usr/bin/env python3
"""
Prestige Module Analyzer & Dummy Module Detector

Checks modules within Prestige.jar (or any specified client JAR) to verify
whether modules have functional logic, event listeners, or mixin hooks,
or if they are dummy/empty placeholders.
"""

import os
import sys
import re
import argparse
import tempfile
import zipfile
import subprocess

DEFAULT_JAR_PATHS = [
    "/storage/emulated/0/Download/Prestige.jar",
    "/sdcard/Download/Prestige.jar",
]

def find_jar():
    for p in DEFAULT_JAR_PATHS:
        if os.path.exists(p):
            return p
    return None

def analyze_jar(jar_path):
    if not os.path.isfile(jar_path):
        print(f"Error: JAR file not found at '{jar_path}'", file=sys.stderr)
        sys.exit(1)

    print(f"Analyzing JAR: {jar_path}")
    print("=" * 80)

    with tempfile.TemporaryDirectory() as tmpdir:
        with zipfile.ZipFile(jar_path, 'r') as z:
            for item in z.namelist():
                if item.endswith('.class'):
                    z.extract(item, tmpdir)

        # Locate ModuleManager
        mm_path = os.path.join(tmpdir, 'juxxl/prestige/module/ModuleManager.class')
        if not os.path.exists(mm_path):
            print("Error: Could not locate ModuleManager.class in archive.", file=sys.stderr)
            sys.exit(1)

        p_mm = subprocess.run(['javap', '-p', '-c', mm_path], capture_output=True, text=True)
        registered_classes = re.findall(r'class\s+(juxxl/prestige/module/modules/[a-zA-Z0-9/]+)', p_mm.stdout)
        registered_classes = list(dict.fromkeys(registered_classes))

        # Scan class references across archive
        class_refs = {}
        for root, _, files in os.walk(tmpdir):
            for file in files:
                if file.endswith('.class'):
                    full_p = os.path.join(root, file)
                    rel = os.path.relpath(full_p, tmpdir)
                    with open(full_p, 'rb') as fp:
                        content = fp.read()
                    for mod_cls in registered_classes:
                        base = mod_cls.split('/')[-1]
                        if base.encode() in content:
                            if rel != mod_cls + '.class' and rel != 'juxxl/prestige/module/ModuleManager.class':
                                class_refs.setdefault(base, set()).add(rel)

        results = []
        for mod_cls in registered_classes:
            mod_path = os.path.join(tmpdir, mod_cls + '.class')
            p = subprocess.run(['javap', '-p', '-c', mod_path], capture_output=True, text=True)
            out = p.stdout
            base = mod_cls.split('/')[-1]

            strings = re.findall(r'ldc\s+#\d+\s+//\s+String\s+(.*)', out)
            category_match = re.search(r'Field juxxl/prestige/module/Category\.([A-Z]+)', out)
            cat = category_match.group(1) if category_match else 'UNKNOWN'
            mod_name = strings[0] if len(strings) > 0 else base
            mod_desc = strings[1] if len(strings) > 1 else ''

            implements_match = re.findall(r'implements\s+([^{]+)', out.split('{')[0])
            interfaces = implements_match[0].strip().replace('\n', ' ') if implements_match else 'None'

            methods = re.findall(r'public [^;]+;', out)
            custom_methods = [m for m in methods if '<init>' not in m]

            # Check for non-empty instruction bodies
            method_blocks = re.split(r'\n\s*public\s+', out)
            non_empty_methods = 0
            for block in method_blocks[1:]:
                if '<init>' in block:
                    continue
                instrs = re.findall(r'^\s*\d+:\s+(\w+)', block, re.MULTILINE)
                if len(instrs) > 1 or (len(instrs) == 1 and instrs[0] != 'return'):
                    non_empty_methods += 1

            refs = class_refs.get(base, set())

            is_dummy = False
            dummy_reasons = []
            if non_empty_methods == 0 and interfaces == 'None' and not refs:
                is_dummy = True
                dummy_reasons.append("No event interfaces, no operational methods, and no mixin/external references")

            results.append({
                'class': base,
                'name': mod_name,
                'category': cat,
                'desc': mod_desc,
                'interfaces': interfaces,
                'custom_methods_count': len(custom_methods),
                'non_empty_methods_count': non_empty_methods,
                'refs_count': len(refs),
                'refs': sorted(list(refs)),
                'is_dummy': is_dummy,
                'dummy_reasons': dummy_reasons
            })

    # Group results by category
    categories = ['COMBAT', 'MISC', 'RENDER', 'CLIENT']
    print(f"\nDiscovered {len(results)} registered modules across {len(categories)} categories.\n")

    for cat in categories:
        cat_mods = [m for m in results if m['category'] == cat]
        print(f"--- [{cat}] ({len(cat_mods)} modules) ---")
        for m in cat_mods:
            status = "DUMMY" if m['is_dummy'] else "FUNCTIONAL"
            status_tag = f"[{status}]"
            details = []
            if m['interfaces'] != 'None':
                short_ifaces = [i.split('.')[-1] for i in m['interfaces'].split(',')]
                details.append(f"events: {', '.join(short_ifaces)}")
            if m['non_empty_methods_count'] > 0:
                details.append(f"{m['non_empty_methods_count']} methods")
            if m['refs_count'] > 0:
                details.append(f"{m['refs_count']} external refs")
            detail_str = f" ({'; '.join(details)})" if details else ""
            print(f"  {status_tag:14} {m['name']:24} [{m['class']:20}]{detail_str}")
            if m['is_dummy']:
                for r in m['dummy_reasons']:
                    print(f"                 Reason: {r}")
        print()

    functional_count = sum(1 for m in results if not m['is_dummy'])
    dummy_count = sum(1 for m in results if m['is_dummy'])

    print("=" * 80)
    print("ANALYSIS SUMMARY")
    print(f"  Total Modules Tested  : {len(results)}")
    print(f"  Functional Modules    : {functional_count}")
    print(f"  Dummy / Empty Modules : {dummy_count}")
    print("=" * 80)

    if dummy_count == 0:
        print("RESULT: All modules in Prestige.jar contain functional logic, event bindings, or mixin hooks.")
    else:
        print(f"RESULT: Found {dummy_count} dummy module(s).")

    return results

def main():
    parser = argparse.ArgumentParser(description="Check Prestige.jar modules for dummy implementations")
    parser.add_argument("--jar", help="Path to Prestige.jar", default=None)
    args = parser.parse_args()

    jar = args.jar or find_jar()
    if not jar:
        print("Error: Could not locate Prestige.jar in standard download paths.", file=sys.stderr)
        print("Please specify path using --jar <path_to_jar>", file=sys.stderr)
        sys.exit(1)

    analyze_jar(jar)

if __name__ == '__main__':
    main()
