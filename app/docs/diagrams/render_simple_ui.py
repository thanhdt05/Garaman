"""Draw report wireframes following lecture Fig. 4.3; run with py -3.

SVG is the editable vector deliverable. PNG is only a convenient preview.
"""
from pathlib import Path
from html import escape
import os
os.environ.setdefault('QT_QPA_PLATFORM', 'offscreen')
from PySide6.QtGui import QGuiApplication, QImage, QPainter, QFont, QFontMetricsF, QFontDatabase, QPainterPath, QPainterPathStroker, QPolygonF
from PySide6.QtSvg import QSvgRenderer
from PySide6.QtCore import QByteArray, Qt, QPointF

OUT = Path(__file__).resolve().parent
app = QGuiApplication.instance() or QGuiApplication([])
for fontfile in ('arial.ttf', 'arialbd.ttf'):
    QFontDatabase.addApplicationFont(str(Path('C:/Windows/Fonts') / fontfile))

class Drawing:
    def __init__(self, w, h, title):
        self.w, self.h = w, h
        self.parts = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}" role="img" aria-labelledby="title"><title id="title">{escape(title)}</title>',
            '<defs><marker id="arrow" viewBox="0 0 24 30" refX="23" refY="15" markerWidth="24" markerHeight="30" markerUnits="userSpaceOnUse" orient="auto"><path d="M1 1 L23 15 L1 29 Z" fill="#ccc" stroke="#333"/></marker></defs>',
            '<style>text{font-family:Arial,sans-serif;fill:#111}rect,path,line{vector-effect:non-scaling-stroke}</style>',
            f'<rect width="{w}" height="{h}" fill="white"/>']

    def text(self, x, y, s, size=18, bold=False, anchor='start', limit=None):
        f = QFont('Arial'); f.setPixelSize(size); f.setBold(bold)
        if limit is not None:
            assert QFontMetricsF(f).horizontalAdvance(s) <= limit, (s, limit)
        self.parts.append(f'<text x="{x}" y="{y}" xml:space="preserve" font-size="{size}" font-weight="{700 if bold else 400}" text-anchor="{anchor}">{escape(s)}</text>')

    def rect(self, x, y, w, h, fill='white'):
        self.parts.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" fill="{fill}" stroke="#333"/>')

    def screen(self, x, y, w, h, title):
        self.rect(x,y,w,h)
        self.text(x+w/2,y+29,title,20,True,'middle',w-24)
        self.parts.append(f'<line x1="{x}" y1="{y+43}" x2="{x+w}" y2="{y+43}" stroke="#333"/>')

    def button(self,x,y,w,label):
        submit = label in ('Tìm kiếm', 'Lưu', 'Xác nhận', 'Đã thanh toán', 'In')
        self.rect(x,y,w,32,'#e5e5e5' if submit else 'white')
        self.text(x+w/2,y+22,label,17,False,'middle',w-12)

    def field(self,x,y,w,label,value='',labelw=135):
        self.text(x,y+22,label,17,limit=labelw-6)
        self.rect(x+labelw,y,w-labelw,30)
        if '▾' in value:
            self.text(x+labelw+7,y+21,value.replace('▾','').rstrip(),16,limit=w-labelw-34)
            self.parts.append(f'<path d="M{x+w-21},{y+12} l10,0 l-5,7 Z" fill="#333"/>')
        else:
            self.text(x+labelw+7,y+21,value,16,limit=w-labelw-14)

    def table(self,x,y,widths,headers,rows,size=16,rowh=38,editable=()):
        for r, row in enumerate([headers]+rows):
            xx=x
            for c,(w,s) in enumerate(zip(widths,row)):
                self.rect(xx,y+r*rowh,w,rowh)
                if r>0 and c in editable:
                    self.rect(xx+5,y+r*rowh+5,w-10,rowh-10)
                self.text(xx+7,y+r*rowh+rowh/2+6,s,size,r==0,limit=w-14)
                xx+=w

    def arrow(self,points,label='',lx=0,ly=0,dashed=False):
        x,y=points[-1];px,py=points[-2]
        length=((x-px)**2+(y-py)**2)**.5
        ux,uy=(x-px)/length,(y-py)/length
        shaft=QPainterPath(QPointF(*points[0]))
        for a,b in points[1:-1]:shaft.lineTo(a,b)
        shaft.lineTo(x-ux*22,y-uy*22)
        stroker=QPainterPathStroker();stroker.setWidth(14)
        stroker.setJoinStyle(Qt.PenJoinStyle.MiterJoin)
        head=QPainterPath()
        head.addPolygon(QPolygonF([QPointF(x,y),QPointF(x-ux*26-uy*15,y-uy*26+ux*15),QPointF(x-ux*26+uy*15,y-uy*26-ux*15)]))
        head.closeSubpath()
        shape=stroker.createStroke(shaft).united(head).simplified()
        for poly in shape.toFillPolygons():
            coords=' '.join(f'{p.x():.2f},{p.y():.2f}' for p in poly)
            self.parts.append(f'<polygon points="{coords}" fill="#ccc" stroke="#333"/>')
        if label:self.text(lx,ly,label,16)

    def save(self,name):
        raw=('\n'.join(self.parts)+'</svg>').encode('utf-8')
        (OUT/f'{name}.svg').write_bytes(raw)
        renderer=QSvgRenderer(QByteArray(raw)); assert renderer.isValid()
        img=QImage(self.w,self.h,QImage.Format.Format_ARGB32); img.fill(Qt.GlobalColor.white)
        p=QPainter(img); renderer.render(p); p.end()
        assert img.save(str(OUT/f'{name}.png'))
        print(f'{name}: valid SVG, {self.w} x {self.h}')


def catalog():
    d=Drawing(1100,1340,'Thiết kế giao diện — Tra cứu dịch vụ / phụ tùng')
    d.screen(360,65,380,150,'Trang chính khách hàng')
    d.text(380,140,'Khách hàng A')
    d.button(380,165,340,'Tìm dịch vụ / phụ tùng')
    d.arrow([(550,235),(550,285)])
    d.screen(30,310,1040,300,'Tìm dịch vụ / phụ tùng')
    d.field(50,370,480,'Tên cần tìm','lọc',120)
    d.button(550,370,125,'Tìm kiếm')
    d.field(50,415,625,'Loại thông tin','Dịch vụ & phụ tùng ▾',145)
    d.table(50,470,[65,345,120,220,250],['Mã','Tên','Loại','Đơn giá / Giá bán','Thao tác'],[
        ['DV1','Thay lọc dầu động cơ','Dịch vụ','120.000','Xem chi tiết'],
        ['PT1','Lọc dầu động cơ AF-01','Phụ tùng','150.000','Xem chi tiết']],17)
    d.arrow([(285,630),(285,680)])
    d.arrow([(815,630),(815,680)])
    d.screen(30,705,510,595,'Chi tiết dịch vụ')
    rows=['Mã: DV1','Tên: Thay lọc dầu động cơ','Nhóm: [Nhóm dịch vụ]',
        'Mô tả: [Mô tả dịch vụ]','Nội dung: [Nội dung thực hiện]',
        'Tiền công: 120.000 đồng','Thời gian dự kiến: [Thời gian]',
        'Bảo hành: [Thông tin nếu có]']
    for i,t in enumerate(rows):d.text(50,780+i*38,t,18,limit=470)
    d.rect(50,1100,180,80);d.text(140,1145,'Hình ảnh',18,anchor='middle')
    d.button(50,1240,130,'Quay lại')
    d.screen(560,705,510,595,'Chi tiết phụ tùng')
    rows=['Mã: PT1 — Lọc dầu động cơ AF-01','Nhóm: Phụ tùng động cơ',
        'Đơn vị: Cái — Hãng: AutoFilter','Giá bán: 150.000 đồng',
        'Mô tả: Lọc cặn bẩn trong dầu động cơ','Thông số: Dạng vặn; 76 mm × 85 mm',
        'Dòng xe: Chưa cập nhật','Bảo hành: 6 tháng, với lỗi sản xuất.',
        'Không áp dụng khi lắp / sử dụng sai.', 'Trạng thái: Đang kinh doanh']
    for i,t in enumerate(rows):d.text(580,780+i*32,t,18,limit=470)
    d.rect(580,1110,180,80);d.text(670,1155,'Hình ảnh',18,anchor='middle')
    d.button(580,1240,130,'Quay lại')
    d.save('catalog-ui-simple')


def purchase():
    d=Drawing(1100,2270,'Thiết kế giao diện — Nhân viên kho nhập phụ tùng')
    d.screen(30,65,320,170,'Trang chính kho')
    d.text(50,140,'Nhân viên kho A')
    d.button(50,175,280,'Nhập phụ tùng')
    d.arrow([(375,155),(435,155)])
    d.screen(460,65,610,235,'Tìm / chọn nhà cung cấp')
    d.field(480,125,370,'Tên NCC','Hòa Phát',95)
    d.button(870,125,175,'Tìm kiếm')
    d.table(480,175,[65,135,115,150,105],['Mã','Tên','Địa chỉ','Điện thoại','Chọn'],[
        ['NCC1','Hòa Phát','Hà Nội','0900000001','Chọn']],15,37)
    d.button(480,255,140,'Thêm mới');d.button(640,255,130,'Quay lại')
    # Add forms occupy a separate row; broad arrows follow the lecture example.
    d.arrow([(550,320),(550,350),(200,350),(200,385)])
    d.screen(30,410,340,300,'Thêm nhà cung cấp')
    for y,label in [(470,'Tên'),(513,'Địa chỉ'),(556,'Điện thoại'),(599,'Email')]:
        d.field(50,y,300,label,labelw=105)
    d.button(50,654,110,'Lưu');d.button(180,654,165,'Quay lại')
    d.screen(460,410,610,365,'Thêm phụ tùng')
    labels=['Tên','Nhóm','Đơn vị tính','Hãng sản xuất','Mô tả','Thông số',
        'Dòng xe','Hình ảnh','Giá bán','Thời hạn BH','Điều kiện BH','Trạng thái']
    for i,label in enumerate(labels):
        col=i//6;row=i%6
        d.field(475+col*295,470+row*40,280,label,'▾' if label in ('Nhóm','Trạng thái') else '',120)
    d.button(475,724,110,'Lưu');d.button(610,724,165,'Quay lại')
    # Selecting a supplier leads straight to the invoice; no extra review screen.
    d.arrow([(1050,320),(1087,320),(1087,820),(1000,820),(1000,850)])
    d.screen(30,875,1040,450,'Lập hóa đơn nhập')
    d.text(50,950,'Nhà cung cấp: Hòa Phát     Nhân viên kho: A     Ngày nhập: [Ngày / giờ]',18)
    d.field(50,975,440,'Tên phụ tùng','lọc',125)
    d.button(510,975,125,'Tìm kiếm');d.button(790,975,255,'Thêm phụ tùng mới')
    d.table(50,1025,[80,345,95,250,230],['Mã','Tên phụ tùng','ĐVT','Hãng sản xuất','Chọn'],[
        ['PT1','Lọc dầu động cơ AF-01','Cái','AutoFilter','Chọn']],17)
    d.text(50,1130,'Các dòng nhập',18,True)
    d.table(50,1150,[50,280,70,150,75,145,150,80],
        ['STT','Tên phụ tùng','ĐVT','Hãng','SL','Giá nhập','Thành tiền','Xóa'],[
        ['1','Lọc dầu động cơ AF-01','Cái','AutoFilter','10','80.000','800.000','Xóa']],16,40,(4,5))
    d.text(50,1265,'Tổng tiền: 800.000 đồng',20,True)
    d.button(660,1250,160,'Xác nhận');d.button(850,1250,110,'Hủy')
    d.arrow([(800,850),(800,800)])
    d.arrow([(550,1345),(550,1395)])
    d.screen(30,1420,1040,350,'Hóa đơn nhập đã xác nhận')
    d.text(50,1490,'Mã: HN1     NCC: Hòa Phát     Nhân viên kho: A     Ngày nhập: [Ngày / giờ]',18)
    headers=['STT','Tên phụ tùng','ĐVT','Số lượng','Đơn giá nhập','Thành tiền']
    row=['1','Lọc dầu động cơ AF-01','Cái','10','80.000','800.000']
    widths=[55,345,90,130,190,190]
    d.table(50,1515,widths,headers,[row],17,40)
    d.text(50,1640,'Tổng tiền: 800.000 đồng',20,True)
    d.text(50,1675,'Trạng thái: Chưa thanh toán',18)
    d.button(820,1645,220,'In hóa đơn')
    d.field(50,1710,440,'Số tiền đã trả','800.000',160)
    d.button(520,1710,195,'Đã thanh toán')
    d.arrow([(550,1790),(550,1840)])
    d.screen(30,1865,1040,365,'Bản in hóa đơn nhập')
    d.text(50,1940,'Mã: HN1     Nhà cung cấp: Hòa Phát',18)
    d.text(50,1975,'Nhân viên kho: A     Ngày nhập: [Ngày / giờ]',18)
    d.table(50,2005,widths,headers,[row],17,40)
    d.text(50,2130,'Tổng tiền: 800.000 đồng',20,True)
    d.text(50,2170,'Trạng thái: Chưa thanh toán',18)
    d.button(660,2175,130,'In');d.button(820,2175,170,'Quay lại')
    d.save('purchase-ui-simple')

if __name__ == '__main__':
    catalog()
    purchase()
