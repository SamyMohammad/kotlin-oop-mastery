"""Every `Symbol` cited in a doc's "In Compose" section must exist in the reference.

A citation is inline code inside the section that starts at a heading containing
"Compose" and ends at the next heading of the same or higher level.
Only PascalCase symbols, dotted symbols (RowScope.weight) and @Annotations are checked.
"""
import re
import sys
from pathlib import Path

REF = Path("docs/reference/compose_signatures.md")
CITED = re.compile(r"`(@?[A-Z][A-Za-z0-9]+(?:\.[A-Za-z][A-Za-z0-9]*)*)`")
HEADING = re.compile(r"^(#{1,6})\s+(.*)")

# Kotlin/stdlib/domain names allowed in Compose sections without a reference entry
ALLOW = {"String", "Int", "Long", "Boolean", "Unit", "List", "Any", "Nothing",
         "Map", "Set", "MutableList", "MutableMap", "Pair", "Float", "Double", "Char", "Sequence", "Lazy",
         "Product", "Cart", "Order", "Payment", "Price", "UiState", "ProductCard",
         "OrderStatus", "CartScreen", "ProductListScreen", "ShopViewModel",
         # classes defined in earlier course docs
         "DiscountBadgePainter", "TicketShape", "CartViewModel", "CartUiState"}


def known_symbols() -> set[str]:
    names = set()
    for line in REF.read_text(encoding="utf-8").splitlines():
        m = HEADING.match(line)
        if m and m.group(1) == "###":
            names.add(m.group(2).strip())
    return names


def cited_in_compose_sections(text: str) -> list[tuple[int, str]]:
    out, level, in_fence = [], None, False
    for n, line in enumerate(text.splitlines(), 1):
        if line.strip().startswith("```"):
            in_fence = not in_fence
            continue
        m = HEADING.match(line) if not in_fence else None
        if m:
            depth = len(m.group(1))
            if "Compose" in m.group(2):
                level = depth
            elif level is not None and depth <= level:
                level = None
            continue
        if level is not None and not in_fence:
            out += [(n, s) for s in CITED.findall(line)]
    return out


def main() -> int:
    known = known_symbols()
    errors = []
    for doc in sorted(Path("docs").glob("[0-9][0-9]_*.md")):
        for n, sym in cited_in_compose_sections(doc.read_text(encoding="utf-8")):
            if sym not in known and sym not in ALLOW and sym.split(".")[0] not in ALLOW:
                errors.append(f"{doc}:{n}: `{sym}` not in {REF}")
    for e in errors:
        print(e)
    print(f"check_compose_refs: {len(known)} reference symbols, {len(errors)} errors")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.stdout.reconfigure(encoding="utf-8")  # Windows console defaults to cp1252
    sys.exit(main())
