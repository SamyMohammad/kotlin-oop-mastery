"""Lint Arabic markdown so it renders correctly in left-to-right chat/IDE views.

Usage: python tools/check_bidi.py docs README.md
Exit code 1 when any violation is found.
"""
import re
import sys
from pathlib import Path

ARABIC = re.compile(r"[؀-ۿ]")
CONTROL = re.compile(r"[‎‏‪-‮⁦-⁩]")
ESCAPES = re.compile(r"\\u200[ef]|\\u202[a-e]|\\u206[6-9]|&rlm;|&lrm;", re.IGNORECASE)
ARROWS = ("->", "=>", "→")
LEADING = re.compile(r"^(\s*(#{1,6}|>|[-*+]|\d+\.|\[[ xX]\])\s+)*\s*")
HTML_TAGS = re.compile(r"^(<[^>]+>)+")


def first_strong_is_arabic(fragment: str) -> bool:
    s = LEADING.sub("", fragment, count=1)
    s = HTML_TAGS.sub("", s).lstrip()
    s = s.lstrip("*_ ").lstrip()
    return bool(s) and bool(ARABIC.match(s[0]))


def lint_text(text: str, name: str) -> list[str]:
    errors = []
    in_fence = False
    for n, line in enumerate(text.splitlines(), 1):
        where = f"{name}:{n}"
        if line.strip().startswith("```"):
            in_fence = not in_fence
            continue
        if CONTROL.search(line):
            errors.append(f"{where}: bidi control character")
        if ESCAPES.search(line):
            errors.append(f"{where}: bidi escape text")
        if in_fence or not ARABIC.search(line):
            continue
        if any(a in line for a in ARROWS):
            errors.append(f"{where}: arrow on Arabic line, use ←")
        if line.lstrip().startswith("|"):
            for cell in line.strip().strip("|").split("|"):
                if ARABIC.search(cell) and not first_strong_is_arabic(cell.strip()):
                    errors.append(f"{where}: table cell must start with Arabic: {cell.strip()[:30]}")
        elif not first_strong_is_arabic(line):
            errors.append(f"{where}: Arabic line must start with an Arabic word")
    return errors


def main(paths: list[str]) -> int:
    files = []
    for p in map(Path, paths):
        files += sorted(p.rglob("*.md")) if p.is_dir() else [p]
    files = [f for f in files if "superpowers" not in f.parts]
    errors = []
    for f in files:
        errors += lint_text(f.read_text(encoding="utf-8"), str(f))
    for e in errors:
        print(e)
    print(f"check_bidi: {len(files)} files, {len(errors)} errors")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.stdout.reconfigure(encoding="utf-8")  # Windows console defaults to cp1252
    sys.exit(main(sys.argv[1:] or ["docs", "README.md"]))
