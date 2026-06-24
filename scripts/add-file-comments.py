#!/usr/bin/env python3
"""Agrega comentarios de cabecera (JavaDoc/JSDoc) a archivos sin documentar."""
from __future__ import annotations

import re
from pathlib import Path

BACKEND_ROOT = Path(__file__).resolve().parents[1] / "src" / "main" / "java" / "com" / "novatech" / "store"
FRONTEND_ROOT = Path(__file__).resolve().parents[2] / "frontend" / "src" / "app"


def java_description(path: Path) -> str:
    name = path.stem
    rel = path.relative_to(BACKEND_ROOT)
    parts = rel.parts
    pkg = parts[0] if parts else ""

    base = name
    for suffix in (
        "Controller", "Service", "Repository", "Request", "Response",
        "Dto", "Config", "Seeder", "Util", "Handler", "Filter",
    ):
        if base.endswith(suffix):
            base = base[: -len(suffix)]
            break

    label = base or name
    if pkg == "controller":
        return (
            f"Controlador REST `{name}`: expone endpoints HTTP JSON para {label}. "
            f"Ruta base en `@RequestMapping` de la clase."
        )
    if pkg == "service":
        return (
            f"Servicio `{name}`: reglas de negocio, transacciones y orquestación de {label}. "
            f"Los controllers delegan aquí; no accede HTTP directamente."
        )
    if pkg == "repository":
        return f"Repositorio JPA `{name}`: consultas y persistencia de entidad {label} en MySQL."
    if pkg == "entity":
        return f"Entidad JPA `{name}`: tabla y relaciones ORM; se serializa a JSON en respuestas API."
    if pkg == "dto":
        return f"DTO `{name}`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla."
    if pkg == "config":
        return f"Configuración Spring `{name}`: beans, seguridad, seeders o ajustes de arranque."
    if pkg == "security":
        return f"Seguridad `{name}`: autenticación JWT, filtros o utilidades de rol/sesión."
    if pkg == "exception":
        return f"Manejo de excepciones `{name}`: respuestas HTTP uniformes ante errores."
    if pkg == "util":
        return f"Utilidad `{name}`: funciones puras reutilizables sin estado de negocio."
    if pkg == "validation":
        return f"Validación `{name}`: patrones y reglas compartidas para DTOs/entidades."
    return f"Componente backend `{name}` del paquete `{pkg}`."


def ts_description(path: Path) -> str:
    name = path.stem
    rel = path.relative_to(FRONTEND_ROOT)
    parts = rel.parts
    area = parts[0] if parts else ""

    if area == "services":
        resource = name.replace(".service", "").replace(".ts", "")
        if name == "api-base":
            return "Clase base HTTP CRUD: listar, obtener, crear, actualizar y eliminar por recurso REST."
        if name == "auth.service":
            return "Autenticación: login, registro, logout, restaurar sesión desde cookie HttpOnly."
        if name == "permiso.service":
            return "Permisos RBAC en UI: consulta matriz del backend y resuelve si el rol puede una acción."
        return f"Servicio Angular `{name}`: llama API `/ {resource}` y expone Observables al UI."
    if area == "guards":
        return f"Guard de ruta `{name}`: decide si el router permite entrar según sesión/rol/permiso."
    if area == "interceptors":
        return f"Interceptor HTTP `{name}`: modifica requests/responses globales (cookies, errores)."
    if area == "pages":
        return f"Página `{name}`: pantalla Angular (componente + template) del módulo {parts[1] if len(parts)>1 else 'app'}."
    if area == "components":
        return f"Componente reutilizable `{name}`: UI compartida entre varias pantallas."
    if area == "layouts":
        return f"Layout `{name}`: marco visual (header/sidebar/outlet) de una zona del sitio."
    if area == "utils":
        return f"Utilidad frontend `{name}`: helpers puros (validación, formato, exportación)."
    if area == "config":
        return f"Configuración estática `{name}`: constantes RBAC, roles y permisos del cliente."
    if area == "models":
        return "Modelos TypeScript: interfaces que reflejan entidades/DTOs del backend."
    if name == "app.routes":
        return "Definición de rutas: tienda, login, panel admin y guards por URL."
    if name == "app.config":
        return "Bootstrap Angular: router, HttpClient, interceptors e inicializador de sesión."
    return f"Módulo frontend `{name}` ({area})."


def has_header(content: str, lang: str) -> bool:
    head = content[:800]
    if lang == "java":
        return "/**" in head or content.lstrip().startswith("// Esta clase") or content.lstrip().startswith("// Controller")
    return "/**" in head[:400] or "// Página" in head or "// Servicio" in head


def inject_java(content: str, desc: str) -> str:
    block = f"/**\n * {desc}\n */\n"
    # After package and imports
    m = re.search(r"^((?:package[^\n]*\n)(?:\n|import[^\n]*\n)*)", content, re.M)
    if not m:
        return content
    insert_at = m.end()
    # Before public class/interface/enum/@RestController block
    rest = content[insert_at:]
    cm = re.search(r"^(@[\w.]+\s*\n)*((public|final)\s+)?(class|interface|enum|record)\s", rest, re.M)
    if not cm:
        return content
    pos = insert_at + cm.start()
    if content[max(0, pos - 10):pos].strip().endswith("*/"):
        return content
    return content[:pos] + block + content[pos:]


def inject_ts(content: str, desc: str) -> str:
    block = f"/**\n * {desc}\n */\n"
    if content.startswith(block.strip()):
        return content
    # Top of file after leading comments
    m = re.match(r"^(\s*(?:\/\/[^\n]*\n|\/\*[\s\S]*?\*\/\s*)*)", content)
    prefix = m.group(1) if m else ""
    rest = content[len(prefix):]
    em = re.search(r"^export\s+(default\s+)?(class|function|const|interface|type)\s", rest, re.M)
    if em:
        pos = len(prefix) + em.start()
        if "/**" in content[max(0, pos - 80):pos]:
            return content
        return content[:pos] + block + content[pos:]
    return block + content


def process_java_files() -> int:
    count = 0
    for path in sorted(BACKEND_ROOT.rglob("*.java")):
        text = path.read_text(encoding="utf-8")
        if has_header(text, "java"):
            continue
        new = inject_java(text, java_description(path))
        if new != text:
            path.write_text(new, encoding="utf-8", newline="\n")
            count += 1
    return count


def process_ts_files() -> int:
    count = 0
    for path in sorted(FRONTEND_ROOT.rglob("*.ts")):
        if path.name.endswith(".spec.ts"):
            continue
        text = path.read_text(encoding="utf-8")
        if has_header(text, "ts"):
            continue
        new = inject_ts(text, ts_description(path))
        if new != text:
            path.write_text(new, encoding="utf-8", newline="\n")
            count += 1
    return count


def write_package_infos() -> None:
    packages = {
        "controller": "Capa REST: recibe HTTP, valida entrada y delega en services. Un controller por dominio (productos, pedidos, facturas…).",
        "service": "Capa de negocio: transacciones, reglas, KPIs y orquestación entre repositorios.",
        "repository": "Capa de datos JPA: acceso a MySQL vía Spring Data.",
        "entity": "Modelo relacional JPA + serialización JSON (cuidado con @JsonIgnore en ciclos).",
        "dto": "Objetos de entrada/salida desacoplados de entidades.",
        "config": "Beans Spring, seeders demo, CORS, plantillas y seguridad.",
        "security": "JWT en cookie, filtros y utilidades de usuario autenticado.",
        "exception": "Errores HTTP uniformes (400/404/500).",
        "util": "Helpers estáticos compartidos.",
        "validation": "Patrones Bean Validation reutilizables.",
    }
    for pkg, desc in packages.items():
        folder = BACKEND_ROOT / pkg
        if not folder.is_dir():
            continue
        info = folder / "package-info.java"
        content = f"/**\n * {desc}\n */\npackage com.novatech.store.{pkg};\n"
        info.write_text(content, encoding="utf-8", newline="\n")


if __name__ == "__main__":
    write_package_infos()
    j = process_java_files()
    t = process_ts_files()
    print(f"Java files commented: {j}")
    print(f"TypeScript files commented: {t}")
