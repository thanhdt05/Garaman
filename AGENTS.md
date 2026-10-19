# AGENTS.md

## Repository state
- This is a Vietnamese capstone package for GaraMan (đề 26), not a finished app. There is no `pom.xml`, Java source, SQL schema, or backend yet — only planning documents and a static JSP prototype. Do not invent Maven/backend commands or infer backend support from prototype test passes.
- Authoritative requirements are in `26_B23DCCN764-full.docx`. Start plans at `ke-hoach/README.md`; it defines the reading order and ownership. `03_HOP_DONG.md` is the schema/route/HTTP contract, `02_KE_HOACH.md` is the file/class/implementation contract, `09`/`10` cover UI reuse and technical gaps.
- Treat the root PDF/DOCX files as read-only sources. `E:\java\Garaman1` is an external reference copy; edit only inside this workspace.
- To inspect those sources, `python ke-hoach/tools/extract_sources.py` extracts text/images and a hash manifest into gitignored `.review/`; PDF extraction additionally requires PySide6.
- Docs and UI text are Vietnamese with UTF-8 accents. Preserve them; `ui-check.cjs` fails on mojibake.

## Runnable prototype — `giao-dien-tham-chieu/`
- Needs Java 17+ on `PATH` and an extracted Tomcat 10.1 install. No Maven or MySQL is required.
- Start/stop from that directory:
  ```powershell
  .\Start-GaraMan.ps1 -TomcatHome 'C:\Tools\apache-tomcat-10.1.x' -Port 8096
  .\Stop-GaraMan.ps1
  ```
  Open `http://localhost:8096/GaraMan/`. Logs go to `.runtime/tomcat/logs/`; `.runtime/` is generated and gitignored. Do not open JSP files directly — Tomcat must compile them.
- The prototype is intentionally static: sample data in JSP/HTML and `sessionStorage` in `web/assets/app.js`; no Servlet, DAO, DB, or API. Do not reuse its `web.xml` (Jakarta 5.0) or client-side state model as the backend design.
- `gdChonPhuTung4.jsp` only forwards to `gdNhapPhuTung4.jsp`. `gdXacNhanHoaDon4.jsp` is the already-confirmed, read-only invoice, not a pre-confirmation review screen.
- Shared fragments live in `web/WEB-INF/jspf/`. Invoice line markup exists both in `invoice-lines.jspf` and in JS rendering in `app.js`; keep the headers/cells aligned with the 6-column check.

## UI verification
- With the prototype running, run from the repo root:
  ```powershell
  $env:PLAYWRIGHT_MODULE = 'C:\path\to\node_modules\playwright'
  $env:GARAMAN_URL = 'http://localhost:8096/GaraMan/'
  node giao-dien-tham-chieu/checks/ui-check.cjs
  ```
- Playwright is not vendored (the script's default `.tools` path is absent), so always set `PLAYWRIGHT_MODULE`. The script launches installed Microsoft Edge (`channel: 'msedge'`), so Edge must be available.
- It writes screenshots and an A4 PDF to `giao-dien-tham-chieu/docs/anh-giao-dien/`, which is gitignored. Those images are absent on a fresh clone; regenerate them rather than committing them.
- A PASS covers only prototype rendering/interactions. It does not validate auth, MySQL, transactions, stock, or snapshot persistence.

## Planned backend (not implemented)
- Target per `02`: Java 17, Maven WAR, Tomcat 9.0.x with Servlet 4.0 `javax.servlet.*`/JSP 2.3/JSTL 1.2, MySQL 8.4 `utf8mb4`, and JUnit 5. Dependency versions are fixed there; do not use `LATEST` or `+`.
- Never mix APIs: the prototype's Tomcat 10.1 uses `jakarta.*` and must not be copied into the backend. The planned Maven artifact/context is `garaman`; the prototype URL is `/GaraMan`.
- Keep table/column names in camelCase with their existing `4` suffixes exactly as specified in `03`; MySQL on Linux is case-sensitive.
