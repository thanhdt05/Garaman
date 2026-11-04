"""Check GaraMan's deployment model, then export Visual Paradigm style SVG/PDF/PNG."""
from pathlib import Path
from html import escape, unescape
from urllib.parse import unquote
import argparse
import hashlib
import json
import os
import re
import subprocess
import xml.etree.ElementTree as ET

os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")

from PySide6.QtCore import QMarginsF, QRectF, QSizeF
from PySide6.QtGui import QColor, QFontDatabase, QGuiApplication, QImage, QPainter, QPageSize, QPdfWriter
from PySide6.QtPdf import QPdfDocument
from PySide6.QtSvg import QSvgRenderer

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[2]
DOCS = HERE.parent
STEM = "deployment-diagram"

EXPECTED = {
    "WebClient": ("Web Client", "device", None),
    "Browser": ("Web Browser", "executionEnvironment", "WebClient"),
    "GaraManServer": ("GaraMan Server", "device", None),
    "JavaRuntime": ("JVM (Java 17)", "executionEnvironment", "GaraManServer"),
    "Tomcat": ("Apache Tomcat 9.0.x", "executionEnvironment", "JavaRuntime"),
    "WebApplication": ("garaman.war", "artifact", "Tomcat"),
    "MySQL": ("MySQL 8.4", "executionEnvironment", "GaraManServer"),
    "Database": ("garaman4", "artifact", "MySQL"),
    "Printer": ("Printer", "device", None),
}
EXPECTED_PATHS = [
    ("Browser", "Tomcat", "HTTP"),
    ("Tomcat", "MySQL", r"JDBC / MySQL protocol\nTCP 3306 (localhost)"),
    ("WebClient", "Printer", "Local printing"),
]
POSITIONS = {
    "WebClient": (40, 225, 390, 285, 16),
    "Browser": (80, 335, 305, 140, 10),
    "GaraManServer": (640, 50, 800, 950, 18),
    "JavaRuntime": (690, 165, 700, 480, 12),
    "Tomcat": (730, 270, 620, 320, 12),
    "WebApplication": (900, 355, 280, 70, 0),
    "MySQL": (830, 795, 500, 150, 12),
    "Database": (970, 865, 220, 60, 0),
    "Printer": (105, 650, 260, 95, 16),
}
DEPLOYED_PACKAGES = ["view", "controller", "dao", "model"]


def read_model(source):
    nodes, stack = {}, []
    for line in source.splitlines():
        node = re.fullmatch(r'\s*node "([^"]+)" as (\w+) <<(\w+)>> \{', line)
        artifact = re.fullmatch(r'\s*artifact "([^"]+)" as (\w+)', line)
        if node:
            name, alias, kind = node.groups()
            nodes[alias] = (name, kind, stack[-1] if stack else None)
            stack.append(alias)
        elif artifact:
            name, alias = artifact.groups()
            nodes[alias] = (name, "artifact", stack[-1])
        elif line.strip() == "}":
            stack.pop()
    assert not stack
    paths = re.findall(r"^(\w+) -- (\w+) : (.+)$", source, re.M)
    assert nodes == EXPECTED, nodes
    assert paths == EXPECTED_PATHS, paths
    return nodes, paths


def check_source():
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    pom = ET.parse(ROOT / "app/pom.xml").getroot()
    assert pom.findtext("m:packaging", namespaces=ns) == "war"
    assert pom.findtext("m:build/m:finalName", namespaces=ns) == "garaman"
    assert pom.findtext("m:properties/m:maven.compiler.release", namespaces=ns) == "17"
    dependencies = {d.findtext("m:artifactId", namespaces=ns):
                    (d.findtext("m:version", namespaces=ns), d.findtext("m:scope", namespaces=ns))
                    for d in pom.findall("m:dependencies/m:dependency", ns)}
    assert dependencies["javax.servlet-api"] == ("4.0.1", "provided")
    assert dependencies["mysql-connector-j"][0] == "8.4.0"
    props = (ROOT / "app/src/main/resources/db.properties").read_text(encoding="utf-8")
    # Inspect only the URL; do not include user/password properties in output.
    url = re.search(r"^db\.url=(.+)$", props, re.M).group(1)
    match = re.match(r"jdbc:mysql://([^:/]+):(\d+)/([^?\s]+)", url)
    assert match.groups() == ("localhost", "3306", "garaman4")
    dao = (ROOT / "app/src/main/java/dao/DAO.java").read_text(encoding="utf-8")
    assert 'System.getenv("GARAMAN_DB_URL")' in dao
    assert "com.mysql.cj.jdbc.Driver" in dao and "DriverManager.getConnection" in dao
    printer = (ROOT / "app/src/main/webapp/assets/js/print.js").read_text(encoding="utf-8")
    assert "window.print()" in printer
    schema = (ROOT / "app/database/01_schema.sql").read_text(encoding="utf-8")
    tables = re.findall(r"CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?`?(tbl\w+)", schema, re.I)
    assert len(tables) == 9, tables
    return dict(war="garaman.war", java_target="17", servlet_api="4.0.1",
                connector_j="8.4.0", database_host=match[1], database_port=int(match[2]),
                database_name=match[3], schema_tables=tables,
                mysql_server_target="8.4", tomcat_target="9.0.x",
                topology="target design; shared app/database host from default URL",
                printing="browser/OS printing; physical printer availability is a design assumption")


def write_svg(path, nodes):
    parts = ['<svg xmlns="http://www.w3.org/2000/svg" width="1500" height="1075" viewBox="0 0 1500 1075">',
             '<title>Biểu đồ triển khai hệ thống GaraMan</title>',
             '<desc>Một loại máy khách Web Client dùng trình duyệt cho các vai trò. Một máy chủ chứa JVM, Tomcat, garaman.war và MySQL với CSDL garaman4. Máy in nối máy khách thực hiện in.</desc>',
             '<rect width="1500" height="1075" fill="white"/>']

    def text(x, y, value, size=22, anchor="middle"):
        parts.append(f'<text x="{x}" y="{y}" text-anchor="{anchor}" font-family="Arial" '
                     f'font-size="{size}" font-weight="400" fill="black">{escape(value)}</text>')

    for alias, (name, kind, parent) in nodes.items():
        x, y, width, height, depth = POSITIONS[alias]
        if parent:
            px, py, pw, ph, _ = POSITIONS[parent]
            assert px < x and x + width + depth < px + pw
            assert py < y - depth and y + height < py + ph
        parts.append(f'<g id="{alias}" data-kind="{kind}" data-parent="{parent or ""}" '
                     'stroke="black" stroke-width="1.3" fill="white">')
        if kind == "artifact":
            parts.append(f'<rect x="{x}" y="{y}" width="{width}" height="{height}"/>')
            # Standard artifact glyph: a small document with a folded corner.
            ix, iy = x + width - 27, y + 8
            parts.append(f'<path d="M {ix} {iy} h 12 l 7 7 v 16 h -19 Z M {ix+12} {iy} v 7 h 7"/>')
        else:
            parts.append(f'<polygon points="{x},{y} {x+depth},{y-depth} '
                         f'{x+width+depth},{y-depth} {x+width},{y}"/>')
            parts.append(f'<polygon points="{x+width},{y} {x+width+depth},{y-depth} '
                         f'{x+width+depth},{y+height-depth} {x+width},{y+height}"/>')
            parts.append(f'<rect x="{x}" y="{y}" width="{width}" height="{height}"/>')
        parts.append('</g>')
        text(x + width / 2, y + (22 if kind == "artifact" else 29), f'«{kind}»',
             17 if kind == "artifact" else 18)
        text(x + width / 2, y + (50 if kind == "artifact" else 61), name,
             22 if kind == "artifact" else 24)

    text(232.5, 444, "HTML / CSS / JavaScript", 18)
    # UML note tied to the deployed WAR; package names are not separate devices.
    parts.append('<g id="war-package-note" fill="white" stroke="black" stroke-width="1.3">'
                 '<path d="M 780 450 H 1285 L 1300 465 V 555 H 780 Z M 1285 450 V 465 H 1300"/>'
                 '</g>')
    parts.append('<line x1="1040" y1="425" x2="1040" y2="450" stroke="black" stroke-dasharray="5,4"/>')
    text(1040, 477, "Packages in garaman.war", 20)
    text(1040, 507, ", ".join(DEPLOYED_PACKAGES[:4]), 20)

    # Association/Communication Path: no arrowheads, no numbered messages.
    for index, (src, dst, label) in enumerate(EXPECTED_PATHS):
        points = (
            "395,385 560,385 560,320 730,320",
            "1040,590 1040,783",
            "235,510 235,634",
        )[index]
        parts.append(f'<polyline id="path-{src}-{dst}" points="{points}" '
                     'fill="none" stroke="black" stroke-width="1.3"/>')
    text(475, 368, "HTTP", 20)
    text(1062, 696, "JDBC / MySQL protocol", 19, "start")
    text(1062, 723, "TCP 3306 (localhost)", 19, "start")
    text(257, 580, "Local printing", 20, "start")
    parts.append('</svg>')
    path.write_text("\n".join(parts) + "\n", encoding="utf-8")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--plantuml", type=Path, required=True)
    args = parser.parse_args()
    source = HERE / (STEM + ".puml")
    nodes, paths = read_model(source.read_text(encoding="utf-8"))
    source_text = source.read_text(encoding="utf-8")
    assert "Packages in garaman.war:" in source_text
    for line in (", ".join(DEPLOYED_PACKAGES[:4]), ", ".join(DEPLOYED_PACKAGES[4:])):
        assert line in source_text
    package_check = json.loads((HERE / "package-diagram-check.json").read_text(encoding="utf-8"))
    assert set(DEPLOYED_PACKAGES) == set(package_check["root_packages"])
    evidence = check_source()
    result = subprocess.run(
        ["java", "-Djava.awt.headless=true", "-jar", str(args.plantuml.resolve()),
         "-charset", "UTF-8", "-failfast2", "-tsvg", str(source)],
        capture_output=True, text=True, encoding="utf-8", check=False,
    )
    if result.returncode:
        raise RuntimeError(result.stdout + result.stderr)

    app = QGuiApplication.instance() or QGuiApplication([])
    for filename in ("arial.ttf", "times.ttf"):
        font = Path("C:/Windows/Fonts") / filename
        if font.exists():
            assert QFontDatabase.addApplicationFont(str(font)) >= 0
    svg_path = source.with_suffix(".svg")
    write_svg(svg_path, nodes)
    renderer = QSvgRenderer(str(svg_path))
    assert renderer.isValid()
    svg = svg_path.read_text(encoding="utf-8")
    visible = [unescape(label) for label in re.findall(r"<text\b[^>]*>(.*?)</text>", svg, re.S)]
    for name, kind, parent in nodes.values():
        assert name in visible and f'«{kind}»' in visible
    assert "marker-end" not in svg and "marker-start" not in svg
    size = renderer.defaultSize()

    image = QImage(size.width() * 3, size.height() * 3, QImage.Format.Format_RGB32)
    image.fill(QColor("white"))
    painter = QPainter(image)
    renderer.render(painter)
    painter.end()
    assert image.save(str(source.with_suffix(".png")))

    pdf_path = source.with_suffix(".pdf")
    writer = QPdfWriter(str(pdf_path))
    writer.setTitle("Biểu đồ triển khai hệ thống GaraMan")
    writer.setCreator("GaraMan — Visual Paradigm style deployment notation")
    writer.setResolution(96)
    writer.setPageSize(QPageSize(QSizeF((size.width()+48)*0.75, (size.height()+48)*0.75),
                                 QPageSize.Unit.Point, STEM))
    writer.setPageMargins(QMarginsF(0, 0, 0, 0))
    painter = QPainter(writer)
    painter.fillRect(QRectF(0, 0, writer.width(), writer.height()), QColor("white"))
    renderer.render(painter, QRectF(24, 24, size.width(), size.height()))
    painter.end()
    del writer
    pdf = QPdfDocument()
    pdf.load(str(pdf_path))
    assert pdf.pageCount() == 1
    pdf.close()

    hashes = {}
    for item in json.loads((ROOT / ".review/manifest.json").read_text(encoding="utf-8")):
        if item["file"] in ("26_B23DCCN764.docx", "BG HP TTTN 2 CNPM 2020 final.pdf"):
            actual = hashlib.sha256((ROOT / item["file"]).read_bytes()).hexdigest()
            assert actual == item["sha256"]
            hashes[item["file"]] = actual
    report = dict(status="PASS", scope="shared system target deployment; source verification covers implemented modules",
                  devices=3, execution_environments=4, artifacts=2, communication_paths=len(paths),
                  model={alias: dict(name=value[0], kind=value[1], parent=value[2])
                         for alias, value in nodes.items()},
                  paths=[dict(source=a, target=b, label=c) for a, b, c in paths],
                  source_evidence=evidence, svg=dict(width=size.width(), height=size.height()),
                  package_placement=dict(client="HTML/CSS/JavaScript in browser; no server JSP package",
                                         server_war=DEPLOYED_PACKAGES, database="garaman4 in MySQL",
                                         printer="no GaraMan packages"),
                  pdf_pages=1, png_scale=3, root_source_hashes_unchanged=hashes)
    (HERE / (STEM + "-check.json")).write_text(
        json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    links = 0
    for filename in ("THIET_KE_TRIEN_KHAI.md", "VE_DEPLOYMENT_VISUAL_PARADIGM.md"):
        path = DOCS / filename
        content = path.read_text(encoding="utf-8")
        assert "\ufffd" not in content and "áº" not in content
        for target in re.findall(r"\]\(([^)]+)\)", content):
            if "://" not in target:
                assert (path.parent / unquote(target.split("#")[0])).exists(), (filename, target)
                links += 1
    print(json.dumps(dict(status="PASS", devices=3, execution_environments=4, artifacts=2,
                          communication_paths=3, pdf_pages=1, checked_local_links=links,
                          root_sources_unchanged=True), ensure_ascii=False))


if __name__ == "__main__":
    main()
