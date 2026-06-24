#!/usr/bin/env python3
"""Mueve JSDoc antes de decoradores @Injectable/@Component."""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2] / "frontend" / "src" / "app"

pat = re.compile(
    r"(@(?:Injectable|Component|Directive)\([^)]*\)\s*\n)(/\*\*[\s\S]*?\*/\s*\n)(export\s+)",
    re.M,
)


def fix(content: str) -> str:
    return pat.sub(r"\2\1\3", content)


if __name__ == "__main__":
    n = 0
    for path in ROOT.rglob("*.ts"):
        text = path.read_text(encoding="utf-8")
        new = fix(text)
        if new != text:
            path.write_text(new, encoding="utf-8", newline="\n")
            n += 1
    print(f"Fixed decorator order: {n}")
