"""Render package contents and check names against the report and two-module source."""
from pathlib import Path
from html import escape
import argparse, hashlib, json, os, re, subprocess, xml.etree.ElementTree as ET, zipfile
os.environ.setdefault('QT_QPA_PLATFORM', 'offscreen')
from PySide6.QtCore import QMarginsF, QRectF, QSizeF
from PySide6.QtGui import QColor, QFontDatabase, QGuiApplication, QImage, QPainter, QPageSize, QPdfWriter
from PySide6.QtSvg import QSvgRenderer
from PySide6.QtPdf import QPdfDocument

HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[2]
MODEL=['Member4','Employee4','Manager4','WarehouseStaff4','SalesStaff4','Customer4','Technician4',
       'Supplier4','Car4','Appointment4','RepairIntake4','CustomerInvoice4','Service4','SparePart4',
       'SparePartCategory4','RepairService4','RepairPart4','PurchaseInvoice4','PurchaseInvoiceDetail4',
       'CustomerStatistics4','ServiceStatistics4','SparePartStatistics4','SupplierStatistics4']
DAO=['DAO','ServiceDAO4','SparePartDAO4','SupplierDAO4','WarehouseStaffDAO4','SparePartCategoryDAO4','PurchaseInvoiceDAO4']
CONTROLLER=['CatalogServlet4','SupplierServlet4','SparePartServlet4','PurchaseInvoiceServlet4']
CUSTOMER=['customerHome.jsp','catalogSearch.jsp','itemDetails.jsp']
WAREHOUSE=['warehouseHome.jsp','selectSupplier.jsp','newSupplier.jsp','purchaseInvoice.jsp','newSparePart.jsp','confirmedInvoice.jsp','printInvoice.jsp']
CONTENTS={'member':[],'manager':[],'salesStaff':[],'customer':CUSTOMER,'warehouseStaff':WAREHOUSE,
          'controller':CONTROLLER,'dao':DAO,'model':MODEL}
DEPENDENCIES=[('view','controller'),('controller','view'),('view','model'),
              ('controller','dao'),('controller','model'),('dao','model')]
POSITIONS={'view':(60,40,1820,470),'member':(90,110,180,80),'manager':(295,110,180,80),
           'salesStaff':(500,110,180,80),'customer':(705,110,340,300),'warehouseStaff':(1070,110,780,370),
           'controller':(60,640,480,350),'dao':(60,1080,480,610),'model':(1000,640,880,670)}
ROUTES={('view','controller'):[(150,510),(150,640)],('controller','view'):[(430,640),(430,510)],
        ('view','model'):[(1730,510),(1730,640)],('controller','dao'):[(300,990),(300,1080)],
        ('controller','model'):[(540,790),(1000,790)],('dao','model'):[(540,1250),(770,1250),(770,1030),(1000,1030)]}

def puml():
    out=['@startuml package-diagram','skinparam shadowing false',
         'skinparam roundcorner 0','skinparam defaultFontName Arial',
         'skinparam classBackgroundColor #86CFE9','skinparam classBorderColor black',
         'skinparam packageBackgroundColor white','skinparam packageBorderColor black',
         'skinparam classAttributeIconSize 0','hide circle','show empty members',
         'package "view" as view {']
    for group in ['member','manager','salesStaff','customer','warehouseStaff']:
        out.append(f'  package "{group}" as {group} {{')
        out.extend(f'    class "{name}" as {name[:-4]}' for name in CONTENTS[group])
        out.append('  }')
    out.append('}')
    for group in ['controller','dao','model']:
        out.append(f'package "{group}" as {group} {{')
        for name in CONTENTS[group]:
            kind='abstract class' if name in ['DAO','Member4','Employee4'] else 'class'
            out.append(f'  {kind} "{name}" as {name}')
        out.append('}')
    out.extend(f'{a} ..> {b}' for a,b in DEPENDENCIES)
    return '\n'.join(out+['@enduml'])+'\n'

def svg():
    out=['<svg xmlns="http://www.w3.org/2000/svg" width="1940" height="1740" viewBox="0 0 1940 1740">',
         '<title>Biểu đồ thiết kế gói GaraMan</title>',
         '<desc>Package hình thư mục chứa Class UML ba ngăn cho JSP, Servlet, DAO và model. Các ngăn thuộc tính và phương thức không liệt kê thành viên ở mức biểu đồ gói.</desc>',
         '<rect width="1940" height="1740" fill="white"/>']
    for group,(x,y,w,h) in POSITIONS.items():
        out.append(f'<g id="package-{group}" data-uml-type="Package" fill="white" stroke="black" stroke-width="1.5">')
        tab=min(190,w-20)
        out.append(f'<path d="M {x} {y+32} V {y} H {x+tab} V {y+32} H {x+w} V {y+h} H {x} Z M {x} {y+32} H {x+w}"/>')
        out.append(f'<text x="{x+15}" y="{y+24}" font-family="Arial" font-size="22" fill="black" stroke="none">{group}</text></g>')
        for i,name in enumerate(CONTENTS.get(group,[])):
            columns=3 if group=='model' else 2 if group=='warehouseStaff' else 1
            col=i//8 if group=='model' else i//4 if group=='warehouseStaff' else 0
            row=i%8 if group=='model' else i%4 if group=='warehouseStaff' else i
            cellw=(w-40-(columns-1)*18)/columns
            cx=x+20+col*(cellw+18); cy=y+60+row*72
            assert cy+64<=y+h,(group,name)
            out.append(f'<g id="element-{name}" data-uml-type="Class" fill="#86CFE9" stroke="black" stroke-width="1.2">')
            out.append(f'<rect x="{cx}" y="{cy}" width="{cellw}" height="64"/>')
            out.append(f'<path d="M {cx} {cy+34} H {cx+cellw} M {cx} {cy+49} H {cx+cellw}" fill="none"/>')
            italic=' font-style="italic"' if name in ['DAO','Member4','Employee4'] else ''
            size=18 if group=='model' else 19
            out.append(f'<text x="{cx+cellw/2}" y="{cy+25}" text-anchor="middle" font-family="Arial" font-size="{size}" font-weight="bold"{italic} fill="black" stroke="none">{escape(name)}</text></g>')
    for a,b in DEPENDENCIES:
        points=ROUTES[a,b]; coords=' '.join(f'{x},{y}' for x,y in points)
        out.append(f'<polyline id="dependency-{a}-{b}" points="{coords}" fill="none" stroke="black" stroke-width="1.5" stroke-dasharray="8,6"/>')
        (px,py),(ex,ey)=points[-2:]; length=((ex-px)**2+(ey-py)**2)**.5; ux=(ex-px)/length; uy=(ey-py)/length
        bx=ex-14*ux; by=ey-14*uy
        out.append(f'<path d="M {bx-6*uy} {by+6*ux} L {ex} {ey} L {bx+6*uy} {by-6*ux}" fill="none" stroke="black" stroke-width="1.5"/>')
    return '\n'.join(out+['</svg>'])

def main():
    parser=argparse.ArgumentParser(); parser.add_argument('--plantuml',type=Path,required=True); args=parser.parse_args()
    with zipfile.ZipFile(ROOT/'26_B23DCCN764.docx') as archive:
        doc=ET.fromstring(archive.read('word/document.xml'))
    report=' '.join(t.text or '' for t in doc.iter('{http://schemas.openxmlformats.org/wordprocessingml/2006/main}t'))
    for name in MODEL+DAO+CONTROLLER+CUSTOMER+WAREHOUSE: assert name in report,name
    entities=re.findall(r'Lớp\s+(\w+4)\s*→\s*bảng',report)
    assert set(entities)==set(MODEL),entities
    java={}
    for path in (ROOT/'app/src/main/java').rglob('*.java'): java.setdefault(path.parent.name,[]).append(path.stem)
    assert set(java)=={'controller','dao','model'}
    assert set(MODEL)>=set(java['model']) and len(java['model'])==10
    assert set(java['dao'])==set(DAO+['MemberDAO4'])
    assert set(java['controller'])==set(CONTROLLER+['AuthServlet4','HomeServlet4'])
    for group,names in [('customer',CUSTOMER),('warehouseStaff',WAREHOUSE)]:
        assert set(names)=={p.name for p in (ROOT/'app/src/main/webapp/WEB-INF/view'/group).glob('*.jsp')}
    source=HERE/'package-diagram.puml'; source.write_text(puml(),encoding='utf-8')
    subprocess.run(['java','-Djava.awt.headless=true','-jar',str(args.plantuml.resolve()),'-charset','UTF-8','-failfast2','-tsvg',str(source)],check=True,capture_output=True)
    path=source.with_suffix('.svg'); path.write_text(svg(),encoding='utf-8')
    assert 'artifact ' not in source.read_text(encoding='utf-8')
    assert path.read_text(encoding='utf-8').count('data-uml-type="Class"')==44
    app=QGuiApplication.instance() or QGuiApplication([])
    for name in ['arial.ttf','times.ttf']: QFontDatabase.addApplicationFont('C:/Windows/Fonts/'+name)
    renderer=QSvgRenderer(str(path)); assert renderer.isValid()
    size=renderer.defaultSize(); preview=QImage(size.width()*2,size.height()*2,QImage.Format.Format_RGB32); preview.fill(QColor('white'))
    painter=QPainter(preview); renderer.render(painter); painter.end(); assert preview.save(str(source.with_suffix('.png')))
    writer=QPdfWriter(str(source.with_suffix('.pdf'))); writer.setResolution(96); writer.setTitle('Biểu đồ thiết kế gói GaraMan')
    writer.setPageSize(QPageSize(QSizeF(size.width()*.75,size.height()*.75),QPageSize.Unit.Point,'GaraMan Package'))
    writer.setPageMargins(QMarginsF(0,0,0,0)); painter=QPainter(writer); renderer.render(painter,QRectF(0,0,size.width(),size.height())); painter.end(); del writer
    document=QPdfDocument(); document.load(str(source.with_suffix('.pdf'))); assert document.pageCount()==1; document.close()
    hashes={}
    for entry in json.loads((ROOT/'.review/manifest.json').read_text(encoding='utf-8')):
        if entry['file'] in ['26_B23DCCN764.docx','BG HP TTTN 2 CNPM 2020 final.pdf']:
            digest=hashlib.sha256((ROOT/entry['file']).read_bytes()).hexdigest(); assert digest==entry['sha256']; hashes[entry['file']]=digest
    result=dict(status='PASS',root_packages=['view','controller','dao','model'],diagram_packages=list(POSITIONS),contents=CONTENTS,
                dependencies=[dict(source=a,target=b) for a,b in DEPENDENCIES],java_packages_on_diagram=java,
                scope='23 full-report entities; DAO/Servlet/JSP only explicitly named in the two module designs',
                existing_login_support_outside_report_figure=['AuthServlet4','HomeServlet4','MemberDAO4','member/login.jsp'],
                unimplemented_view_groups=['manager','salesStaff'],pdf_pages=1,root_source_hashes_unchanged=hashes)
    result['notation']=dict(package='UML Package folder',jsp='UML Class, same element type as the report design',
                            java='UML Class',class_compartments=3,attributes_and_operations='not listed in package view; original members retained',
                            connectors='UML Dependency',class_elements=44,artifact_elements=0)
    (HERE/'package-diagram-check.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(json.dumps(dict(status='PASS',entities=len(MODEL),dao=len(DAO),servlets=len(CONTROLLER),jsp=len(CUSTOMER+WAREHOUSE),dependencies=len(DEPENDENCIES)),ensure_ascii=False))

if __name__=='__main__': main()
