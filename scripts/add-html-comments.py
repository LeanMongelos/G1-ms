#!/usr/bin/env python3
"""Agrega comentarios HTML de sección en plantillas de páginas admin/tienda."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2] / "frontend" / "src" / "app" / "pages"


def describe(path: Path) -> str:
    name = path.parent.name.replace("-", " ")
    return f"Pantalla: {name} — template HTML (estructura visual y bindings Angular)."


def process_html(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    if text.lstrip().startswith("<!-- Pantalla:"):
        return False
    header = f"<!-- {describe(path)} -->\n"
    path.write_text(header + text, encoding="utf-8", newline="\n")
    return True


if __name__ == "__main__":
    n = sum(process_html(p) for p in ROOT.rglob("*.html"))
    print(f"HTML templates commented: {n}")
