"""Render two continuous success diagrams, each to one SVG/PNG/PDF page."""
from pathlib import Path
import argparse
import json
import os
import subprocess
import xml.etree.ElementTree as ET

from PySide6.QtCore import QRectF, QSizeF, QMarginsF
from PySide6.QtGui import QGuiApplication, QImage, QPainter, QPdfWriter, QPageSize, QColor
from PySide6.QtSvg import QSvgRenderer

HERE = Path(__file__).resolve().parent


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--plantuml", type=Path, required=True)
    args = parser.parse_args()
    manifest = json.loads((HERE / "sequence-manifest.json").read_text(encoding="utf-8"))
    assert len(manifest) == 2
    sources = [HERE.parent / (item["stem"] + ".puml") for item in manifest]
    result = subprocess.run(["java", "-Djava.awt.headless=true", "-jar", str(args.plantuml.resolve()),
                             "-charset", "UTF-8", "-failfast2", "-tsvg", *map(str, sources)],
                            env=dict(os.environ, PLANTUML_LIMIT_SIZE="32000"),
                            capture_output=True, text=True, encoding="utf-8")
    if result.returncode:
        raise RuntimeError(result.stdout + result.stderr)
    app = QGuiApplication.instance() or QGuiApplication([])
    report = []
    for item, source in zip(manifest, sources):
        svg = source.with_suffix(".svg")
        root = ET.parse(svg).getroot()
        renderer = QSvgRenderer(str(svg))
        assert renderer.isValid() and "Syntax Error" not in svg.read_text(encoding="utf-8")
        size = renderer.defaultSize()
        # One custom vector page at natural diagram scale, with white margins.
        filename = "catalog-sequence" if item["stem"] == "tuan_tu_tra_cuu" else "purchase-sequence"
        pdf = HERE / (filename + ".pdf")
        writer = QPdfWriter(str(pdf))
        writer.setTitle(item["title"])
        writer.setCreator("GaraMan — one continuous success sequence per module")
        writer.setResolution(96)
        writer.setPageSize(QPageSize(QSizeF((size.width()+48)*0.75, (size.height()+48)*0.75),
                                     QPageSize.Unit.Point, item["stem"]))
        writer.setPageMargins(QMarginsF(0, 0, 0, 0))
        painter = QPainter(writer)
        painter.fillRect(QRectF(0, 0, writer.width(), writer.height()), QColor("white"))
        renderer.render(painter, QRectF(24, 24, size.width(), size.height()))
        painter.end()
        preview = QImage(size.width(), size.height(), QImage.Format.Format_RGB32)
        preview.fill(QColor("white"))
        painter = QPainter(preview)
        renderer.render(painter)
        painter.end()
        assert preview.save(str(source.with_suffix(".png")))
        report.append(dict(file=svg.name, width=size.width(), height=size.height(),
                           messages=len(item["steps"]), pdf=pdf.name, pdf_pages=1))
    (HERE / "render-check.json").write_text(json.dumps(report, ensure_ascii=False, indent=2)+"\n", encoding="utf-8")
    print(json.dumps(report, ensure_ascii=False))


if __name__ == "__main__":
    main()
