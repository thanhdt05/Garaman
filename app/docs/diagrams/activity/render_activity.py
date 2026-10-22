"""Rebuild the two lecture-style SVG diagrams and editable draw.io sources.
Python 3, standard library only. Run from any directory.
"""
from pathlib import Path
from html import escape
import json
import xml.etree.ElementTree as ET

OUT=Path(__file__).resolve().parent
class Diagram:
    def __init__(self,stem,w,h,title):
        self.stem,self.w,self.h,self.title=stem,w,h,title
        self.groups=[];self.nodes={};self.edges=[];self.notes=[]
    def group(self,id,x,y,w,h,title,owner=''):
        self.groups.append(dict(id=id,x=x,y=y,w=w,h=h,title=title,owner=owner))
    def node(self,id,x,y,w,h,label,method='',kind='action'):
        self.nodes[id]=dict(id=id,x=x,y=y,w=w,h=h,label=label,method=method,kind=kind)
    def edge(self,a,b,label='',side='bt',via=(),pos=None,call=False):
        def port(id,s):
            n=self.nodes[id];x,y,w,h=(n[k] for k in ('x','y','w','h'))
            return {'t':(x+w/2,y),'b':(x+w/2,y+h),'l':(x,y+h/2),'r':(x+w,y+h/2)}[s]
        p=[port(a,side[0]),*via,port(b,side[1])]
        self.edges.append(dict(a=a,b=b,label=label,points=p,pos=pos,call=call))
    def note(self,x,y,lines):self.notes.append((x,y,lines))
    def save(self):
        from layout_activity import arrange
        arrange(self)
        s=[f'<svg xmlns="http://www.w3.org/2000/svg" width="{self.w}" height="{self.h}" viewBox="0 0 {self.w} {self.h}">', '<defs><marker id="arrow" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto-start-reverse"><path d="M1 1 L9 4 L1 7" fill="none" stroke="black" stroke-width="1.2"/></marker></defs>', '<rect width="100%" height="100%" fill="white"/>']
        def txt(x,y,lines,size=17,bold=False,anchor='middle',halo=False):
            if isinstance(lines,str):lines=lines.split('\n')
            for i,line in enumerate(lines):
                s.append(f'<text x="{x}" y="{y+i*(size+5)}" font-family="Times New Roman, Liberation Serif, serif" font-size="{size}" text-anchor="{anchor}"'+(' font-weight="bold"' if bold else '')+(' stroke="white" stroke-width="5" paint-order="stroke" stroke-linejoin="round"' if halo else '')+'>'+escape(line)+'</text>')
        txt(self.w/2,38,self.title,25,True)
        for g in self.groups:
            s.append(f'<rect x="{g["x"]}" y="{g["y"]}" width="{g["w"]}" height="{g["h"]}" rx="18" fill="white" stroke="black" stroke-width="1.4"/>')
            txt(g['x']+14,g['y']+25,g['title'],19,True,'start')
            if g['owner']:txt(g['x']+14,g['y']+49,'Điều phối: '+g['owner'],15,False,'start')
        for e in self.edges:
            coords=' '.join(f'{x},{y}' for x,y in e['points'])
            s.append(f'<polyline points="{coords}" fill="none" stroke="white" stroke-width="5" stroke-linejoin="round"/>')
            s.append(f'<polyline points="{coords}" fill="none" stroke="black" stroke-width="1.25" marker-end="url(#arrow)"'+(' marker-start="url(#arrow)"' if e['call'] else '')+'/>')
        for n in self.nodes.values():
            x,y,w,h=(n[k] for k in ('x','y','w','h'))
            if n['kind'] in ('start','end'):
                s.append(f'<circle cx="{x+w/2}" cy="{y+h/2}" r="{w/2}" fill="'+('black' if n['kind']=='start' else 'white')+'" stroke="black" stroke-width="1.5"/>')
                if n['kind']=='end':s.append(f'<circle cx="{x+w/2}" cy="{y+h/2}" r="{w/2-5}" fill="black"/>')
                txt(x+w/2,y-9,n['label'],17)
            else:
                s.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="16" fill="white" stroke="black" stroke-width="1.3"'+'/>')
                lines=n['label'].split('\n'); count=len(lines)+(len(n['method'].split('\n')) if n['method'] else 0)
                yy=y+(h-count*22)/2+17
                txt(x+w/2,yy,lines,17)
                if n['method']:txt(x+w/2,yy+len(lines)*22+2,n['method'],16)
        for e in self.edges:
            if e['label']:
                x,y=e['pos'] if e['pos'] else ((e['points'][0][0]+e['points'][-1][0])/2+8,(e['points'][0][1]+e['points'][-1][1])/2-8)
                txt(x,y,e['label'],15,False,'start',True)
        for x,y,lines in self.notes:txt(x,y,lines,17,False,'start')
        s.append('</svg>');(OUT/(self.stem+'.svg')).write_text('\n'.join(s),encoding='utf-8')
        (OUT/(self.stem+'.json')).write_text(json.dumps(dict(title=self.title,width=self.w,height=self.h,groups=self.groups,nodes=list(self.nodes.values()),edges=self.edges,notes=self.notes),ensure_ascii=False,indent=2),encoding='utf-8')
        # Flat editable draw.io graph; frames are placed behind action vertices.
        mx=ET.Element('mxfile',host='app.diagrams.net');d=ET.SubElement(mx,'diagram',name=self.title)
        model=ET.SubElement(d,'mxGraphModel',page='0',pageWidth=str(self.w),pageHeight=str(self.h));root=ET.SubElement(model,'root')
        ET.SubElement(root,'mxCell',id='0');ET.SubElement(root,'mxCell',id='1',parent='0')
        def vertex(id,x,y,w,h,value,style):
            c=ET.SubElement(root,'mxCell',id=id,value=value,style=style,vertex='1',parent='1')
            ET.SubElement(c,'mxGeometry',x=str(x),y=str(y),width=str(w),height=str(h),attrib={'as':'geometry'})
        base='fontFamily=Times New Roman;fontSize=17;whiteSpace=wrap;html=0;fillColor=#ffffff;strokeColor=#000000;'
        vertex('title',20,5,self.w-40,45,self.title,base+'strokeColor=none;fontSize=25;fontStyle=1;')
        for g in self.groups:vertex(g['id'],g['x'],g['y'],g['w'],g['h'],g['title']+'\n'+('Điều phối: '+g['owner'] if g['owner'] else ''),base+'rounded=1;arcSize=8;verticalAlign=top;align=left;spacing=12;')
        for n in self.nodes.values():
            style=base+'rounded=1;arcSize=30;'
            if n['kind'] in ('start','end'):style=base+'ellipse;'+('fillColor=#000000;' if n['kind']=='start' else 'shape=doubleEllipse;fillColor=#000000;')
            if n['kind']=='callout':style+='fontSize=16;'
            if n['kind'] in ('start','end'):vertex(n['id']+'_label',n['x']-65,n['y']-32,150,25,n['label'],base+'strokeColor=none;')
            vertex(n['id'],n['x'],n['y'],n['w'],n['h'],n['label']+('\n'+n['method'] if n['method'] else '') if n['kind'] not in ('start','end') else '',style)
        for i,e in enumerate(self.edges):
            c=ET.SubElement(root,'mxCell',id=f'edge{i}',value=e['label'],style='endArrow=open;html=0;fontFamily=Times New Roman;fontSize=15;labelBackgroundColor=#ffffff;'+('startArrow=open;' if e['call'] else ''),edge='1',parent='1')
            geom=ET.SubElement(c,'mxGeometry',relative='1',attrib={'as':'geometry'})
            c.set('source',e['a']);c.set('target',e['b'])
            for id,point,prefix in [(e['a'],e['points'][0],'exit'),(e['b'],e['points'][-1],'entry')]:
                n=self.nodes[id];c.set('style',c.get('style')+f'{prefix}X={(point[0]-n["x"])/n["w"]};{prefix}Y={(point[1]-n["y"])/n["h"]};{prefix}Perimeter=0;')
            for p,key in [(e['points'][0],'sourcePoint'),(e['points'][-1],'targetPoint')]:ET.SubElement(geom,'mxPoint',x=str(p[0]),y=str(p[1]),attrib={'as':key})
            if e['label'] and e['pos']:
                lengths=[abs(a[0]-b[0])+abs(a[1]-b[1]) for a,b in zip(e['points'],e['points'][1:])]
                remaining=sum(lengths)/2
                for a,b,length in zip(e['points'],e['points'][1:],lengths):
                    if remaining<=length and length:
                        midpoint=(a[0]+(b[0]-a[0])*remaining/length,a[1]+(b[1]-a[1])*remaining/length);break
                    remaining-=length
                desired=(e['pos'][0]+(len(e['label'])*7.2+8)/2,e['pos'][1]-7)
                ET.SubElement(geom,'mxPoint',x=str(round(desired[0]-midpoint[0],1)),y=str(round(desired[1]-midpoint[1],1)),attrib={'as':'offset'})
            arr=ET.SubElement(geom,'Array',attrib={'as':'points'})
            for x,y in e['points'][1:-1]:ET.SubElement(arr,'mxPoint',x=str(x),y=str(y))
        for i,(x,y,lines) in enumerate(self.notes):vertex(f'note{i}',x,y-20,self.w-x-20,len(lines)*24,'\n'.join(lines),base+'strokeColor=none;align=left;verticalAlign=top;')
        ET.indent(mx);ET.ElementTree(mx).write(OUT/(self.stem+'.drawio'),encoding='utf-8',xml_declaration=True)

def catalog():
    d=Diagram('catalog-activity',1420,1020,'Biểu đồ thiết kế hoạt động modul tìm kiếm thông tin dịch vụ/phụ tùng')
    d.group('home',40,140,250,290,'Xử lí tại customerHome.jsp')
    d.group('search',350,140,570,560,'Xử lí tại catalogSearch.jsp')
    d.group('detail',980,140,400,560,'Xử lí tại itemDetails.jsp')
    d.node('start',156,85,18,18,'Bắt đầu',kind='start')
    d.node('h1',65,220,200,60,'Nhận thông tin KH')
    d.node('hshow',90,340,150,55,'Hiển thị GD',kind='display')
    d.node('s1',380,220,200,60,'Nhận thông tin KH')
    d.node('s2',380,360,200,60,'Nhận từ khóa')
    d.node('s3',380,520,200,60,'Lấy thông tin\nDV/PT')
    d.node('sshow',700,520,170,60,'Hiển thị GD',kind='display')
    d.node('t1',1010,220,340,60,'Nhận thông tin DV/PT')
    d.node('t2',1005,360,165,65,'Lấy chi tiết DV')
    d.node('t3',1190,360,165,65,'Lấy chi tiết PT')
    d.node('tshow',1100,520,160,60,'Hiển thị GD',kind='display')
    d.node('q1',350,770,270,80,'ServiceDAO4:','findServicesByName()',kind='callout')
    d.node('q2',650,770,270,80,'SparePartDAO4:','findSparePartsByName()',kind='callout')
    d.node('q3',980,770,190,80,'ServiceDAO4:','getServiceDetails()',kind='callout')
    d.node('q4',1190,770,190,80,'SparePartDAO4:','getSparePartDetails()',kind='callout')
    d.node('end',155,800,22,22,'Kết thúc',kind='end')
    d.edge('start','h1');d.edge('h1','hshow')
    d.edge('hshow','s1','[click tìm thông tin]','rt',[(320,367.5),(320,120),(480,120)],(355,108))
    d.edge('s1','sshow','[chưa tìm]','rt',[(785,250)],(700,240))
    d.edge('sshow','s2','[click tìm]','lt',[(650,550),(650,320),(480,320)],(500,310))
    d.edge('s2','s3','[hợp lệ]',pos=(490,475))
    d.edge('s2','sshow','[từ khóa sai]','rt',[(785,390)],(650,380))
    d.edge('s3','sshow','[lấy xong / lỗi]','rl',pos=(590,540))
    d.edge('sshow','t1','[click 1 DV/PT]','rt',[(950,550),(950,110),(1180,110)],(1010,98))
    d.edge('t1','t2','[dịch vụ]','bl',[(1180,320),(990,320),(990,392.5)],(1008,310))
    d.edge('t1','t3','[phụ tùng]','br',[(1180,320),(1368,320),(1368,392.5)],(1230,310))
    d.edge('t2','tshow','[lấy xong dữ liệu]','bt',[(1087.5,480),(1180,480)],(1000,465))
    d.edge('t3','tshow','[lấy xong dữ liệu]','bt',[(1272.5,480),(1180,480)],(1205,465))
    d.edge('tshow','s2','[click quay lại]','bt',[(1180,730),(630,730),(630,300),(480,300)],(1010,720))
    d.edge('sshow','end','[kết thúc tra cứu]','bl',[(785,665),(320,665),(320,811)],(365,655))
    d.edge('s3','q1','','bl',[(480,730),(335,730),(335,810)],call=True)
    d.edge('s3','q2','','bb',[(480,710),(935,710),(935,875),(785,875)],call=True)
    d.edge('t2','q3','','ll',[(965,392.5),(965,810)],call=True)
    d.edge('t3','q4','','rr',[(1400,392.5),(1400,810)],call=True)
    d.note(40,935,['KH: khách hàng; DV: dịch vụ; PT: phụ tùng; GD: giao diện.',
                   'Quay lại: giữ điều kiện và truy vấn lại. Lỗi HTTP: xem bảng đối chiếu.'])
    d.save()

def purchase():
    """Report activity: seven analysis views, main import flow and add branches.
    Technical details and recovery exceptions are documented separately.
    """
    d=Diagram('purchase-activity',3500,3590,'Biểu đồ thiết kế hoạt động modul nhân viên kho nhập phụ tùng')
    for id,title in [('home','warehouseHome.jsp'),('supplier','selectSupplier.jsp'),('newSupplier','newSupplier.jsp'),('draft','purchaseInvoice.jsp'),('newPart','newSparePart.jsp'),('confirmed','confirmedInvoice.jsp'),('print','printInvoice.jsp')]:
        d.group(id,0,0,100,100,'Xử lí tại '+title)
    actions={
      'start':('Bắt đầu','start'),'end':('Kết thúc','end'),
      'h1':('Nhận thông tin NV','action'),'hshow':('Hiển thị GD','display'),
      's1':('Nhận thông tin NV','action'),'s2':('Nhận tên NCC','action'),'s3':('Lấy thông tin NCC','action'),'sshow':('Hiển thị GD','display'),
      'n1':('Nhận thông tin NV','action'),'nshow':('Hiển thị GD','display'),'n2':('Nhận thông tin NCC','action'),'n3':('Lưu NCC','action'),
      'd1':('Nhận thông tin NV','action'),'d2':('Nhận thông tin NCC','action'),'d3':('Lấy thông tin\nNCC, NV','action'),
      'd4':('Nhận tên PT','action'),'d5':('Lấy thông tin PT','action'),'dshow':('Hiển thị GD','display'),
      'd6':('Lấy chi tiết PT','action'),'d7':('Thêm PT vào HĐ','action'),'d8':('Nhận lượng, giá','action'),
      'd10':('Nhận thông tin HĐ','action'),'d9':('Tính tổng tiền','action'),'d11':('Lưu HĐ','action'),'cancel':('Hủy HĐ dự kiến','action'),
      'p1':('Nhận thông tin NV','action'),'p2':('Lấy thông tin nhóm PT','action'),'pshow':('Hiển thị GD','display'),'p3':('Nhận thông tin PT','action'),'p4':('Lưu PT','action'),
      'c1':('Nhận thông tin HĐ','action'),'c2':('Lấy thông tin HĐ','action'),'cshow':('Hiển thị GD','display'),
      'c3':('Nhận số tiền đã trả','action'),'c4':('Lưu thanh toán','action'),'c5':('Thông báo thành công','action'),
      'i1':('Nhận thông tin HĐ','action'),'i2':('Lấy thông tin HĐ','action'),'ishow':('Hiển thị GD','display'),'i3':('In HĐ','action')}
    for id,(label,kind) in actions.items():d.node(id,0,0,200,60,label,kind=kind)
    calls={
      'qs':('SupplierDAO4','findSuppliersByName()'),'qn':('SupplierDAO4','addSupplier()'),
      'qinfo':('SupplierDAO4','getSupplierDetails()'),'qstaff':('WarehouseStaffDAO4','getWarehouseStaffDetails()'),
      'qpart':('SparePartDAO4','findSparePartsByName()'),'qget':('SparePartDAO4','getSparePartDetails()'),
      'qsave':('PurchaseInvoiceDAO4','savePurchaseInvoice()'),'qgroup':('SparePartCategoryDAO4','getActiveCategories()'),
      'qpnew':('SparePartDAO4','addSparePart()'),'qinvoice':('PurchaseInvoiceDAO4','getPurchaseInvoiceDetails()'),
      'qpayment':('PurchaseInvoiceDAO4','recordPayment()')}
    for id,(owner,method) in calls.items():d.node(id,0,0,300,80,owner+':',method,kind='callout')
    links=[
      ('start','h1',''),('h1','hshow',''),('hshow','s1','[click nhập PT]'),
      ('s1','sshow','[chưa tìm]'),('sshow','s2','[click tìm]'),('s2','s3',''),('s3','sshow','[lấy xong dữ liệu]'),
      ('sshow','n1','[click thêm mới]'),('n1','nshow',''),('nshow','n2','[click lưu]'),('n2','n3',''),('n3','s3','[lưu xong]'),
      ('sshow','d1','[click 1 NCC]'),('d1','d2',''),('d2','d3',''),('d3','dshow','[lấy xong dữ liệu]'),
      ('dshow','d4','[click tìm]'),('d4','d5',''),('d5','dshow','[lấy xong dữ liệu]'),
      ('dshow','d6','[click 1 PT]'),('d6','d7',''),('d7','dshow','[thêm xong]'),
      ('dshow','d8','[nhập lượng, giá]'),('d8','dshow','[nhập xong]'),
      ('dshow','d10','[click xác nhận]'),('d10','d9','[hợp lệ]'),('d9','d11',''),
      ('d10','dshow','[chưa hợp lệ]'),('d11','c1','[lưu xong]'),
      ('dshow','p1','[click thêm PT]'),('p1','p2',''),('p2','pshow','[lấy xong dữ liệu]'),
      ('pshow','p3','[click lưu]'),('p3','p4','[hợp lệ]'),('p4','d4','[lưu xong]'),
      ('dshow','cancel','[click hủy; đồng ý]'),('cancel','end',''),
      ('c1','c2',''),('c2','cshow','[lấy xong dữ liệu]'),('cshow','i1','[click in HĐ]'),
      ('i1','i2',''),('i2','ishow','[lấy xong dữ liệu]'),('ishow','i3','[click in]'),('i3','c1','[in xong]'),
      ('cshow','c3','[click đã thanh toán]'),('c3','c4',''),('c4','c1','[ghi nhận xong]'),
      ('cshow','c5','[đã thanh toán]'),('c5','end','')]
    for a,b,label in links:d.edge(a,b,label)
    for a,b in [('s3','qs'),('n3','qn'),('d3','qinfo'),('d3','qstaff'),('d5','qpart'),('d6','qget'),('d11','qsave'),('p2','qgroup'),('p4','qpnew'),('c2','qinvoice'),('i2','qinvoice'),('c4','qpayment')]:d.edge(a,b,call=True)
    d.save()

if __name__=='__main__':
    catalog();purchase()
    print('Generated catalog-activity and purchase-activity: SVG, drawio, JSON')
