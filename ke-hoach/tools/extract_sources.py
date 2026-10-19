"""Read-only source extraction for this review; generated files go to .review/."""
from pathlib import Path
import hashlib
import json
import zipfile
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / '.review'
OUT.mkdir(exist_ok=True)
NS = {'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main',
      'a': 'http://schemas.openxmlformats.org/drawingml/2006/main',
      'r': 'http://schemas.openxmlformats.org/officeDocument/2006/relationships'}


def extract_docx(path, label):
    folder = OUT / label
    folder.mkdir(exist_ok=True)
    with zipfile.ZipFile(path) as archive:
        root = ET.fromstring(archive.read('word/document.xml'))
        rels = ET.fromstring(archive.read('word/_rels/document.xml.rels'))
        targets = {r.attrib['Id']: r.attrib['Target'] for r in rels}
        lines = []
        for i, p in enumerate(root.findall('.//w:body//w:p', NS), 1):
            text = ''.join(n.text or '' for n in p.findall('.//w:t', NS))
            images = [targets.get(n.attrib.get('{'+NS['r']+'}embed'), '?')
                      for n in p.findall('.//a:blip', NS)]
            if text or images:
                lines.append(f'[P{i:04d}] {text}' + (f' [IMAGES: {images}]' if images else ''))
        (folder / 'text.txt').write_text('\n'.join(lines), encoding='utf-8')
        for name in archive.namelist():
            if name.startswith('word/media/'):
                (folder / Path(name).name).write_bytes(archive.read(name))


manifest = []
for path in sorted(ROOT.iterdir()):
    if path.suffix.lower() not in ('.pdf', '.docx'):
        continue
    manifest.append({'file': path.name, 'sha256': hashlib.sha256(path.read_bytes()).hexdigest()})
    if path.suffix == '.docx':
        extract_docx(path, 'report' if path.name.startswith('26_') else 'lab1')
    else:
        from PySide6.QtGui import QGuiApplication
        from PySide6.QtPdf import QPdfDocument
        app = QGuiApplication.instance() or QGuiApplication([])
        doc = QPdfDocument()
        result = doc.load(str(path))
        if doc.pageCount() == 0:
            raise RuntimeError(f'Could not load {path}: {result}')
        label = 'lecture' if path.name.startswith('BG ') else 'questions'
        pages = [f'\n=== PDF PAGE {i+1}; LABEL {doc.pageLabel(i)} ===\n' + doc.getAllText(i).text()
                 for i in range(doc.pageCount())]
        (OUT / f'{label}.txt').write_text('\n'.join(pages), encoding='utf-8')
        manifest[-1]['pages'] = doc.pageCount()
        doc.close()
(OUT / 'manifest.json').write_text(json.dumps(manifest, indent=2, ensure_ascii=False), encoding='utf-8')
print(json.dumps(manifest, ensure_ascii=True))
