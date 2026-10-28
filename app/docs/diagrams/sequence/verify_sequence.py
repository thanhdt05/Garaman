"""Validate exactly two success diagrams and matching VP drawing tables."""
from pathlib import Path
from urllib.parse import unquote
import csv
import hashlib
import json
import re

from PySide6.QtGui import QGuiApplication
from PySide6.QtPdf import QPdfDocument

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[3]
DOCS = HERE.parents[1]


def main():
    manifest = json.loads((HERE / "sequence-manifest.json").read_text(encoding="utf-8"))
    assert len(manifest) == 2
    assert len(list(HERE.glob("*.pdf"))) == 2
    assert not list(HERE.glob("*.puml"))
    java = {p.stem: p.read_text(encoding="utf-8") for p in (ROOT / "app/src/main/java").rglob("*.java")}
    jsp = {p.name for p in (ROOT / "app/src/main/webapp").rglob("*.jsp")}
    guide = (DOCS / "VE_SEQUENCE_VISUAL_PARADIGM.md").read_text(encoding="utf-8")
    prose = (DOCS / "SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md").read_text(encoding="utf-8")
    calls = set()
    for item in manifest:
        path = HERE.parent / (item["stem"]+".puml")
        text = path.read_text(encoding="utf-8")
        assert text.count("@startuml") == 1 and text.count("@enduml") == 1
        assert not re.search(r"^(?:alt|else|opt|ref|newpage)\b", text, re.M)
        assert "CP1" not in text and "CP2" not in text
        omitted = ("DraftStore4", "saveLineInputsToDraft", "getTotalAmount(",
                   "calculateTotalAmount(", "calculateLineAmount(", "insertInvoice(",
                   "insertInvoiceDetails(", "beginConfirmation(", "draftVersion")
        assert not any(name in text for name in omitted), "Implementation helpers leaked into report diagram"
        messages = re.findall(r"^(\w+) (->|-->) (\w+) : (\d+): (.*)$", text, re.M)
        assert [int(m[3]) for m in messages] == list(range(1, len(item["steps"])+1))
        aliases = {a: data[1] for a, data in item["participants"].items()}
        active = {a: 0 for a in aliases}
        for line in text.splitlines():
            action = re.match(r"(activate|deactivate) (\w+)$", line)
            if action:
                active[action[2]] += 1 if action[1] == "activate" else -1
                assert active[action[2]] >= 0, (path, line)
        assert not any(active.values()), (path, active)
        for step, msg in zip(item["steps"], messages):
            src, arrow, dst, number, label = msg
            assert src == step["src"] and dst == step["dst"] and label == step["label"]
            assert f'{number}. {step["text"]}' in prose
            assert f'| {number} | {aliases[src]} | {aliases[dst]} |' in guide
            target = aliases[dst]
            match = re.match(r"(\w+)\(", label)
            if target in java and match:
                method = match[1]
                assert re.search(r"\b"+method+r"\s*\(", java[target]), (target, method)
                if method != target:
                    calls.add((target, method))
        for kind, name in item["participants"].values():
            assert name in jsp if kind == "boundary" else kind == "actor" or name in java
        for loop in item["loops"]:
            assert 1 <= loop["first"] <= loop["last"] <= len(item["steps"])
    csv_rows = list(csv.DictReader((DOCS / "TEN_CU_TEN_MOI.csv").open(encoding="utf-8-sig", newline="")))
    confirmed = {(r["Lớp/trang mới"], r["Tên mới"].split("(")[0]) for r in csv_rows if r["Loại"] == "Method"}
    business_calls = {(c, m) for c, m in calls if c.endswith("DAO4") and not m.startswith("insert")}
    assert business_calls <= confirmed, business_calls-confirmed
    links = 0
    for name in ("SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md", "VE_SEQUENCE_VISUAL_PARADIGM.md", "SCENARIO_V3_DOI_CHIEU.md"):
        path = DOCS / name
        text = path.read_text(encoding="utf-8")
        assert "\ufffd" not in text and "áº" not in text
        for target in re.findall(r"\]\(([^)]+)\)", text):
            if "://" not in target:
                assert (path.parent / unquote(target.split("#")[0])).exists(), (name, target)
                links += 1
    roots = json.loads((ROOT / ".review/manifest.json").read_text(encoding="utf-8"))
    hashes = {}
    for item in roots:
        if item["file"] in ("26_B23DCCN764.docx", "BG HP TTTN 2 CNPM 2020 final.pdf"):
            actual = hashlib.sha256((ROOT/item["file"]).read_bytes()).hexdigest()
            assert actual == item["sha256"]
            hashes[item["file"]] = actual
    app = QGuiApplication.instance() or QGuiApplication([])
    pdfs = {}
    for filename in ("catalog-sequence.pdf", "purchase-sequence.pdf"):
        doc = QPdfDocument()
        doc.load(str(HERE / filename))
        assert doc.pageCount() == 1
        pdfs[filename] = doc.pageCount()
        doc.close()
    result = dict(status="PASS", diagrams=2, scope="success only", pdf_pages=pdfs,
                  messages={m["stem"]: len(m["steps"]) for m in manifest},
                  methods_found_in_source=len(calls), checked_local_links=links,
                  root_source_hashes_unchanged=hashes)
    (HERE / "verification.json").write_text(json.dumps(result, ensure_ascii=False, indent=2)+"\n", encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
