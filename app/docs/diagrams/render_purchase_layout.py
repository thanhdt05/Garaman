"""Render the canonical purchase UML with explicit ports and non-overlapping routes.

Run from any directory with Python + PySide6. Class contents and relationships
are read from purchase-design.puml; only presentation is specified here.
"""
from pathlib import Path
from html import escape
import os,re,json,math,sys
os.environ.setdefault('QT_QPA_PLATFORM','offscreen')
from PySide6.QtGui import QGuiApplication,QFontDatabase,QFont,QFontMetricsF,QImage,QPainter
from PySide6.QtSvg import QSvgRenderer
from PySide6.QtCore import QByteArray,Qt

HERE=Path(__file__).resolve().parent
CATALOG='--catalog' in sys.argv
STEM='catalog-design' if CATALOG else 'purchase-design'
SOURCE=(HERE/f'{STEM}.puml').read_text(encoding='utf-8')
app=QGuiApplication.instance() or QGuiApplication([])
for filename in ['times.ttf','timesbd.ttf']:
    path=Path('C:/Windows/Fonts')/filename
    if path.exists():QFontDatabase.addApplicationFont(str(path))
FONT='Times New Roman'
def width(text,size=22):
    f=QFont(FONT);f.setPixelSize(size)
    return QFontMetricsF(f).horizontalAdvance(text)
def wrap(text,limit):
    words=text.replace('(', '( ').split();lines=[];line=''
    for word in words:
        candidate=(line+' '+word).strip()
        if line and width(candidate)>limit:lines.append(line);line='    '+word
        else:line=candidate
    if line:lines.append(line)
    return lines or ['']
classes={}
for abstract,label,alias,stereo,body in re.findall(
    r'(?m)^(abstract )?class (?:"([^"]+)" as )?(\w+)(?: <<([^>]+)>>)?\s*\{([^}]*?)\}',SOURCE):
    classes[alias]={'name':label or alias,'stereo':stereo,'abstract':bool(abstract),
                    'members':[x.strip() for x in body.splitlines() if x.strip()]}
edges=[]
for line in SOURCE.splitlines():
    line=line.replace('-[dashed,norank]->','..>').replace('-[norank]->','-->')
    match=re.match(r'^(\w+)\s+(?:"([^"]+)"\s+)?(<\|--|\*--|-->|\.\.>)\s+(?:"([^"]+)"\s+)?(\w+)(?:\s*:\s*(.*))?$',line)
    if match:edges.append(dict(zip(['source','startMultiplicity','kind','endMultiplicity','target','label'],match.groups())))
assert (len(classes),len(edges))==((12,12) if CATALOG else (24,35)),(len(classes),len(edges))

positions={
 'WarehouseHomeView':(60,125,240),'SelectSupplierView':(355,125,340),
 'NewSupplierView':(750,125,340),'PurchaseInvoiceView':(1145,125,440),
 'ConfirmedInvoiceView':(1640,125,390),'PrintInvoiceView':(2085,125,355),
 'NewSparePartView':(2495,125,680),
 'SupplierServlet4':(350,920,750),'PurchaseInvoiceServlet4':(1340,920,940),
 'SparePartServlet4':(2420,920,810),
 'DAO':(1500,1220,300),'SupplierDAO4':(80,1580,620),
 'WarehouseStaffDAO4':(760,1580,380),'PurchaseInvoiceDAO4':(1210,1580,620),
 'SparePartDAO4':(1900,1580,620),'SparePartCategoryDAO4':(2590,1580,620),
 'Member4':(80,2370,340),'Employee4':(100,2790,300),'WarehouseStaff4':(100,2990,300),
 'Supplier4':(550,2480,350),'PurchaseInvoice4':(1050,2370,490),
 'PurchaseInvoiceDetail4':(1730,2480,350),'SparePart4':(2250,2370,440),
 'SparePartCategory4':(2870,2480,340)}
for key,(x,y,w) in list(positions.items()):
    if y>=1165:positions[key]=(x,y+650,w)
if CATALOG:
    positions={
        'CustomerHomeView':(60,125,400),'CatalogSearchView':(650,125,600),'ItemDetailsView':(1600,125,500),
        'CatalogServlet4':(600,650,1000),'DAO':(900,1145,240),
        'ServiceDAO4':(450,1430,600),'SparePartDAO4':(1230,1430,880),
        'Member4':(70,1900,360),'Customer4':(100,2420,300),
        'Service4':(520,1900,480),'SparePart4':(1170,1900,560),
        'SparePartCategory4':(1850,2100,300)}
for key,node in classes.items():
    node['x'],node['y'],node['w']=positions[key]
    node['rows']=[];node['methodIndex']=None
    for member in node['members']:
        if '(' in member and node['methodIndex'] is None:node['methodIndex']=len(node['rows'])
        if node['stereo']=='Servlet':
            member=member.replace('request: HttpServletRequest','request').replace('response: HttpServletResponse','response')
        node['rows'].extend(wrap(member,node['w']-28))
    node['header']=65 if node['stereo'] else 45
    node['h']=max(88,node['header']+len(node['rows'])*28+14+(12 if node['methodIndex'] else 0))
    assert all(width(row)<=node['w']-20 for row in node['rows']),(key,node['rows'])

WIDTH,HEIGHT=(2200,3020) if CATALOG else (3300,4570)
svg=[];wire_svg=[];node_svg=[];header_svg=[];routes=[]
def add(s):svg.append(s)
def text(x,y,label,size=22,anchor='start',bold=False,target=None,italic=False):
    (target if target is not None else svg).append(f'<text x="{x:.2f}" y="{y:.2f}" font-family="{FONT}" font-size="{size}" text-anchor="{anchor}"'+(' font-weight="bold"' if bold else '')+(' font-style="italic"' if italic else '')+'>'+escape(label)+'</text>')
def frame(y,h,label):
    add(f'<rect x="30" y="{y}" width="{WIDTH-60}" height="{h}" rx="8" fill="#fff" stroke="#8a8a8a" stroke-width="2"/>')
    header_svg.append(f'<rect x="42" y="{y+8}" width="{width(label,27)+30}" height="35" fill="white"/>')
    text(48,y+32,label,size=27,bold=True,target=header_svg)
if CATALOG:
    frame(80,350,'1. Giao diện JSP')
    frame(590,365,'2. Điều phối Servlet')
    frame(1100,610,'3. Truy xuất dữ liệu')
    frame(1840,820,'4. Thực thể liên quan')
    title='Biểu đồ lớp thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng'
else:
    frame(80,580,'1. Giao diện JSP')
    frame(870,820,'2. Điều phối Servlet')
    frame(1815,850,'3. Truy xuất dữ liệu')
    frame(2955,930,'4. Thực thể liên quan')
    title='Biểu đồ lớp thiết kế modul nhân viên kho nhập phụ tùng'
text(WIDTH/2,42,title,size=34,anchor='middle',bold=True)
used_x=set();port_counts={}
def unique_x(x,low=-1e9,high=1e9):
    while round(x,2) in used_x:x+=2
    assert low<=x<=high,(x,low,high)
    used_x.add(round(x,2));return x
def bottom(key):
    node=classes[key];count=port_counts.get((key,'bottom'),0);port_counts[key,'bottom']=count+1
    x=unique_x(node['x']+22+count*27,node['x']+10,node['x']+node['w']-10)
    return (x,node['y']+node['h'])
def top(key):
    node=classes[key];count=port_counts.get((key,'top'),0);port_counts[key,'top']=count+1
    x=unique_x(node['x']+node['w']/2+count*29,node['x']+10,node['x']+node['w']-10)
    return (x,node['y'])
def side(key,which,offset=0):
    n=classes[key];return (n['x']+(n['w'] if which=='right' else 0),n['y']+90+offset)
def route(edge,points):
    assert points[0]!=points[-1]
    # Discard zero length segments, and merge successive straight segments.
    clean=[]
    for p in points:
        if clean and p==clean[-1]:continue
        if len(clean)>=2 and (clean[-2][0]==clean[-1][0]==p[0] or clean[-2][1]==clean[-1][1]==p[1]):clean[-1]=p
        else:clean.append(p)
    points=clean
    assert all(a[0]==b[0] or a[1]==b[1] for a,b in zip(points,points[1:]))
    edge['points']=points;routes.append(edge)
    coords=' '.join(f'{x:.2f},{y:.2f}' for x,y in points)
    wire_svg.append(f'<g data-source="{edge["source"]}" data-target="{edge["target"]}"><title>{edge["source"]} → {edge["target"]}</title>')
    # A white casing creates an unambiguous gap where independent wires cross.
    wire_svg.append(f'<polyline points="{coords}" fill="none" stroke="white" stroke-width="10" stroke-linejoin="round"/>')
    marker=' marker-start="url(#inheritance)"' if edge['kind']=='<|--' else ' marker-start="url(#composition)"' if edge['kind']=='*--' else ' marker-end="url(#dependency)"' if edge['kind']=='..>' else ' marker-end="url(#arrow)"'
    dash=' stroke-dasharray="9 7"' if edge['kind']=='..>' else ''
    wire_svg.append(f'<polyline points="{coords}" fill="none" stroke="#222" stroke-width="2.2" stroke-linejoin="round"{dash}{marker}/></g>')
def label(x,y,value,size=20,anchor='middle'):
    if not value:return
    tw=width(value,size)+12
    wire_svg.append(f'<rect x="{x-tw/2}" y="{y-size}" width="{tw}" height="{size+5}" fill="white"/>')
    text(x,y,value,size=size,anchor=anchor,target=wire_svg)

ui=control=inherit=entity_dep=0
for edge in edges:
    a,b,k=edge['source'],edge['target'],edge['kind']
    if a.endswith('View'):
        p,q=bottom(a),top(b);lane=(460+ui*30) if CATALOG else (690+ui*20);ui+=1
        route(edge,[p,(p[0],lane),(q[0],lane),q])
    elif a.endswith('Servlet4'):
        p=bottom(a);n=classes[b]
        side_count=port_counts.get((b,'left'),0);port_counts[b,'left']=side_count+1
        q=side(b,'left',side_count*33)
        corridor=unique_x(n['x']-22-side_count*8);lane=(1010+control*30) if CATALOG else (1720+control*20);control+=1
        route(edge,[p,(p[0],lane),(corridor,lane),(corridor,q[1]),q])
    elif a=='DAO':
        p,q=bottom(a),top(b);lane=(1300+inherit*35) if CATALOG else (1990+inherit*35);inherit+=1
        route(edge,[p,(p[0],lane),(q[0],lane),q])
    elif a.endswith('DAO4') and b.endswith('DAO4'):
        p=side(a,'right',130);q=side(b,'left',130)
        route(edge,[p,q]);label((p[0]+q[0])/2,p[1]-15,'«use»',size=18)
    elif a.endswith('DAO4'):
        p=bottom(a);lane=(1770+entity_dep*25) if CATALOG else (2710+entity_dep*27);entity_dep+=1
        if b=='WarehouseStaff4':
            q=side(b,'left');x=unique_x(46)
            route(edge,[p,(p[0],lane),(x,lane),(x,q[1]),q])
        else:
            q=top(b);route(edge,[p,(p[0],lane),(q[0],lane),q])
    elif k=='<|--':
        n,m=classes[a],classes[b]
        x=n['x']+n['w']/2
        route(edge,[(x,n['y']+n['h']),(x,m['y'])])
    elif b=='WarehouseStaff4':
        p=bottom(a);q=side(b,'right');x=unique_x(465)
        route(edge,[p,(p[0],3820),(x,3820),(x,q[1]),q])
        label(850,3805,edge['label']);label(p[0]+22,p[1]+23,edge['startMultiplicity']);label(q[0]+20,q[1]-12,edge['endMultiplicity'])
    else:
        n,m=classes[a],classes[b]
        link_y=2200 if CATALOG else 3225
        p=(n['x'] if b=='Supplier4' else n['x']+n['w'],link_y)
        q=(m['x']+m['w'] if b=='Supplier4' else m['x'],link_y)
        route(edge,[p,q]);label((p[0]+q[0])/2,link_y-17,edge['label'])
        sign=1 if q[0]>p[0] else -1
        label(p[0]+sign*25,link_y+26,edge['startMultiplicity']);label(q[0]-sign*25,link_y+26,edge['endMultiplicity'])

# Assert no independent connectors share a segment and no connector traverses a class box.
segments=[]
for index,edge in enumerate(routes):
    for a,b in zip(edge['points'],edge['points'][1:]):
        vertical=a[0]==b[0];fixed=a[0] if vertical else a[1]
        low,high=sorted([a[1],b[1]] if vertical else [a[0],b[0]])
        for oi,ov,of,ol,oh in segments:
            assert not (oi!=index and vertical==ov and abs(fixed-of)<.1 and min(high,oh)-max(low,ol)>.1),('overlapping connectors',edge,routes[oi])
        segments.append((index,vertical,fixed,low,high))
        for key,n in classes.items():
            x,y,w,h=(n[k] for k in ['x','y','w','h'])
            intersects=(x+.2<fixed<x+w-.2 and min(high,y+h-.2)>max(low,y+.2)) if vertical else (y+.2<fixed<y+h-.2 and min(high,x+w-.2)>max(low,x+.2))
            assert not intersects,('wire crosses class',edge['source'],edge['target'],key,a,b)

for key,n in classes.items():
    x,y,w,h=(n[k] for k in ['x','y','w','h'])
    node_svg.append(f'<g id="class-{key}"><title>{escape(n["name"])}</title><rect x="{x}" y="{y}" width="{w}" height="{h}" fill="white" stroke="#222" stroke-width="2"/>')
    header_y=y+28
    if n['stereo']:text(x+w/2,y+22,'«'+n['stereo']+'»',size=18,anchor='middle',target=node_svg);header_y=y+49
    text(x+w/2,header_y,n['name'],size=25,anchor='middle',bold=True,target=node_svg,italic=n['abstract'])
    node_svg.append(f'<path d="M{x},{y+n["header"]} H{x+w}" stroke="#222" fill="none"/>')
    yy=y+n['header']+25
    for row_index,row in enumerate(n['rows']):
        if row_index==n['methodIndex'] and row_index>0:
            node_svg.append(f'<path d="M{x},{yy-20} H{x+w}" stroke="#222" fill="none"/>');yy+=12
        text(x+12,yy,row,target=node_svg);yy+=28
    node_svg.append('</g>')

notes=re.findall(r'note (?:bottom|right) of (\w+)\n(.*?)\nend note',SOURCE,re.S)
for index,(owner,body) in enumerate(notes):
    x=45+index*(1060 if CATALOG else 810);y=2710 if CATALOG else 3950;w=1020 if CATALOG else 780
    rows=[r for line in body.strip().splitlines() for r in wrap(line.strip(),w-24)]
    h=55+len(rows)*28
    add(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" fill="#f8f8f8" stroke="#aaa"/>')
    text(x+12,y+30,'Ghi chú — '+owner,size=23,bold=True)
    for j,row in enumerate(rows):text(x+12,y+61+j*28,row,size=22)
footer=2850 if CATALOG else 4270
for offset,content in enumerate([
    'Servlet: các hàm trên đều nhận request: HttpServletRequest, response: HttpServletResponse.',
    'Hai kiểu tham số HTTP được ghi chung tại đây; tên hàm, tham số và kiểu trả về giữ đúng code.',
    'Lược init(), doGet(), doPost(), serialVersionUID, throws và hàm kỹ thuật; không liệt kê biến cục bộ.',
    'Nét đứt: phụ thuộc; tam giác rỗng: kế thừa; hình thoi đặc: sở hữu dòng. Giao điểm có khoảng hở không phải điểm nối.'
]):text(45,footer+offset*38,content,size=24)
definitions='''<defs>
<marker id="arrow" markerUnits="userSpaceOnUse" markerWidth="18" markerHeight="18" refX="16" refY="9" orient="auto"><path d="M1,1 L16,9 L1,17" fill="none" stroke="#222" stroke-width="2"/></marker>
<marker id="dependency" markerUnits="userSpaceOnUse" markerWidth="18" markerHeight="18" refX="16" refY="9" orient="auto"><path d="M1,1 L16,9 L1,17" fill="none" stroke="#222" stroke-width="2"/></marker>
<marker id="inheritance" markerUnits="userSpaceOnUse" markerWidth="23" markerHeight="23" refX="22" refY="11" orient="auto-start-reverse"><path d="M22,11 L1,1 L1,21 Z" fill="white" stroke="#222" stroke-width="2"/></marker>
<marker id="composition" markerUnits="userSpaceOnUse" markerWidth="27" markerHeight="20" refX="26" refY="10" orient="auto-start-reverse"><path d="M1,10 L13,1 L26,10 L13,19 Z" fill="#222"/></marker>
</defs>'''
document=f'<svg xmlns="http://www.w3.org/2000/svg" width="{WIDTH}px" height="{HEIGHT}px" viewBox="0 0 {WIDTH} {HEIGHT}"><rect width="100%" height="100%" fill="white"/>{definitions}'+''.join(svg+wire_svg+node_svg+header_svg)+'</svg>'
(HERE/f'{STEM}.svg').write_text(document,encoding='utf-8')
renderer=QSvgRenderer(QByteArray(document.encode()))
assert renderer.isValid()
picture=QImage(WIDTH,HEIGHT,QImage.Format_ARGB32);picture.fill(Qt.white)
painter=QPainter(picture);renderer.render(painter);painter.end();assert picture.save(str(HERE/f'{STEM}.png'))
result={'classes':len(classes),'relationships':len(routes),'collinear_overlaps':0,'wires_through_classes':0,'size':[WIDTH,HEIGHT]}
(HERE/f'{"catalog" if CATALOG else "purchase"}-layout-check.json').write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
print(json.dumps(result))
