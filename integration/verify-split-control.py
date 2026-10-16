"""Verify that the packaging control has no changed payload or injected DEX."""
import argparse
import json
from pathlib import Path
from zipfile import ZipFile

parser = argparse.ArgumentParser()
parser.add_argument("original", type=Path)
parser.add_argument("control", type=Path)
args = parser.parse_args()
checked = 0
errors = []

def payload(name):
    # Only Android metadata and signing records may differ in this control.
    return (name not in {"AndroidManifest.xml", "stamp-cert-sha256"}
            and not name.upper().startswith("META-INF/"))

original_names = {p.name for p in args.original.glob("*.apk")}
control_names = {p.name for p in args.control.glob("*.apk")}
if original_names != control_names:
    errors.append("APK set differs")
for name in sorted(original_names & control_names):
    with ZipFile(args.original / name) as original, ZipFile(args.control / name) as control:
        before = {e.filename: (e.file_size, e.CRC) for e in original.infolist() if payload(e.filename)}
        after = {e.filename: (e.file_size, e.CRC) for e in control.infolist() if payload(e.filename)}
        for entry in sorted(before.keys() | after.keys()):
            checked += 1
            if before.get(entry) != after.get(entry):
                errors.append(f"{name}:{entry}")
print(json.dumps({"apks": len(original_names), "payload_entries_checked": checked, "errors": errors}))
raise SystemExit(bool(errors))
