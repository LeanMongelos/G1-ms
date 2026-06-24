#!/usr/bin/env python3
"""Genera índice CODEMAP.md con cada archivo y su propósito."""
from pathlib import Path

BACKEND = Path(__file__).resolve().parents[1] / "src" / "main" / "java" / "com" / "novatech" / "store"
FRONTEND = Path(__file__).resolve().parents[2] / "frontend" / "src" / "app"
OUT = Path(__file__).resolve().parents[2] / "docs" / "CODEMAP.md"


def extract_doc(content: str) -> str:
    m = __import__("re").search(r"/\*\*\s*\n\s*\*\s*(.+?)\n", content)
    if m:
        return m.group(1).strip()
    m2 = __import__("re").search(r"^// (.+)$", content, __import__("re").M)
    return m2.group(1).strip() if m2 else "—"


def main() -> None:
    lines = [
        "# CODEMAP — índice del código NovaTech ERP",
        "",
        "Referencia rápida: **qué hace cada archivo**. Los comentarios `/** ... */` en el código amplían detalle.",
        "",
        "## Backend (`com.novatech.store`)",
        "",
        "| Archivo | Qué hace |",
        "|---------|----------|",
    ]
    for path in sorted(BACKEND.rglob("*.java")):
        rel = path.relative_to(BACKEND)
        doc = extract_doc(path.read_text(encoding="utf-8"))
        lines.append(f"| `{rel.as_posix()}` | {doc} |")

    lines += ["", "## Frontend (`src/app`)", "", "| Archivo | Qué hace |", "|---------|----------|"]
    for path in sorted(FRONTEND.rglob("*.ts")):
        if path.name.endswith(".spec.ts"):
            continue
        rel = path.relative_to(FRONTEND)
        doc = extract_doc(path.read_text(encoding="utf-8"))
        lines.append(f"| `{rel.as_posix()}` | {doc} |")

    OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote {OUT}")


if __name__ == "__main__":
    main()
