"""Explicit activity placement and obstacle-aware orthogonal connector routing.
Only presentation is changed: node IDs/labels and edge topology are preserved.
"""
from collections import Counter,defaultdict
import heapq,math

STEP=10

def arrange(d):
    if d.stem=='catalog-activity':
        groups={'home':(50,160,280,300),'search':(460,160,640,580),'detail':(1270,160,580,580)}
        positions={
            'start':(180,90,20,20),'h1':(80,240,220,60),'hshow':(110,360,160,60),
            's1':(500,240,220,60),'s2':(500,400,220,60),'s3':(500,580,220,60),'sshow':(880,580,170,60),
            't1':(1310,240,500,60),'t2':(1310,400,220,60),'t3':(1590,400,220,60),'tshow':(1470,580,180,60),
            'q1':(460,880,300,80),'q2':(820,880,300,80),'q3':(1250,880,260,80),'q4':(1570,880,290,80),
            'end':(180,900,20,20)}
        d.w,d.h=1940,1120
        d.notes=[(50,1040,['KH: khách hàng; DV: dịch vụ; PT: phụ tùng; GD: giao diện.', 'Quay lại giữ điều kiện và truy vấn lại. Lỗi HTTP: xem bảng đối chiếu.'])]
    else:
        groups={'home':(60,160,490,340),'supplier':(760,160,680,550),'newSupplier':(1690,160,680,550),
                'draft':(760,1020,1460,1170),'newPart':(2680,1020,650,650),
                'confirmed':(760,2520,1160,710),'print':(2440,2520,760,640)}
        positions={
            'start':(180,90,20,20),'h1':(80,240,230,60),'hshow':(350,370,160,60),
            
            's1':(800,240,230,60),'s2':(800,410,230,60),'s3':(800,600,230,60),'sshow':(1220,600,170,60),
            'n1':(1730,240,240,60),'nshow':(2140,240,170,60),'n2':(1730,570,240,60),'n3':(2140,570,170,60),
            'qs':(760,800,340,80),'qn':(2080,800,340,80),
            'd1':(800,1100,220,60),'d2':(800,1250,220,60),'d3':(800,1430,220,70),
            'd4':(1120,1100,240,60),'d5':(1520,1270,220,60),
            'dshow':(1390,1480,200,70),'d6':(1900,1430,230,60),'d7':(1900,1650,230,60),
            'd8':(1260,1760,220,60),'d9':(800,1910,220,60),'d10':(800,1760,220,60),'d11':(800,2050,220,60),
            'cancel':(1900,2090,220,60),
            'p1':(2720,1100,230,60),'p2':(2720,1280,230,60),'pshow':(3080,1280,180,60),
            'p3':(2720,1520,230,60),'p4':(3080,1520,180,60),
            'qinfo':(280,1370,340,80),'qstaff':(280,1510,340,80),'qpart':(1400,860,340,80),
            'qget':(2270,1430,340,80),'qsave':(800,2320,340,80),'qgroup':(2680,1820,340,80),'qpnew':(3060,1820,340,80),
            'c1':(800,2660,230,60),'c2':(1560,2660,230,60),'cshow':(1560,2800,210,60),
            'c3':(1540,3070,250,60),'c4':(1160,3070,230,60),'c5':(800,3070,260,60),
            'i1':(2480,2660,240,60),'i2':(2480,2800,240,60),'ishow':(2940,2800,180,60),'i3':(2940,3020,180,60),
            'qinvoice':(2010,2600,340,80),'qpayment':(1110,3370,340,80),'end':(540,3420,20,20)}
        d.w,d.h=3500,3590
        d.notes=[(60,3540,['NV: nhân viên kho; NCC: nhà cung cấp; PT: phụ tùng; HĐ: hóa đơn; GD: giao diện.', 'Lỗi HTTP và chi tiết nháp: xem bảng đối chiếu.'])]
    for g in d.groups:g.update(zip(('x','y','w','h'),groups[g['id']]))
    if d.stem=='purchase-activity':
        d.notes=[(60,3540,['NV: nhân viên kho; NCC: nhà cung cấp; PT: phụ tùng; HĐ: hóa đơn; GD: giao diện.', 'Phạm vi hình: luồng nhập theo đề 26, hai nhánh thêm mới và hủy HĐ dự kiến. Ngoại lệ khác: xem thuyết minh.'])]
    assert set(positions)==set(d.nodes)
    for key,p in positions.items():d.nodes[key].update(zip(('x','y','w','h'),p))
    route(d)

def route(d):
    nx,ny=d.w//STEP,d.h//STEP
    blocked=set();rects=[]
    def obstacle(x,y,w,h,pad=10):
        rects.append((x-pad,y-pad,x+w+pad,y+h+pad))
        for ix in range(max(1,int((x-pad)//STEP)),min(nx,int((x+w+pad)//STEP)+1)):
            for iy in range(max(6,int((y-pad)//STEP)),min(ny-5,int((y+h+pad)//STEP)+1)):blocked.add((ix,iy))
    centers=lambda n:(n['x']+n['w']/2,n['y']+n['h']/2)
    def facing(a,b):
        x,y=centers(d.nodes[a]);xx,yy=centers(d.nodes[b]);dx,dy=xx-x,yy-y
        return ('r' if dx>=0 else 'l') if abs(dx)>=abs(dy) else ('b' if dy>=0 else 't')
    opp={'r':'l','l':'r','b':'t','t':'b'}
    overrides={
       ('dshow','d4'):'tl',('dshow','dkeep'):'tr',('dkeep','d5'):'bt',('d5','dshow'):'bl',
       ('dshow','d6'):'rt',('d6','dshow'):'bl',('d7','dshow'):'lt',('d9','dshow'):'rt',
       ('dshow','d8'):'bt',('dshow','d10'):'ll',('d10','dshow'):'rt',('d11','dshow'):'rt',
       ('dshow','dremove'):'rb',('dremove','dshow'):'lt',('dshow','cancel'):'bb',
       ('d3','qinfo'):'lt',('d3','qstaff'):'lb',('d5','qpart'):'tl',
       ('cancel','end'):'bt',('cshow','end'):'bl',('c3','c2'):'rt',('c4','c2'):'tt',('c5','c1'):'ll',('cshow','c3'):'bt',
       ('cshow','i1'):'rt',('ishow','c1'):'tt',('i3','c1'):'bl',('hshow','c1'):'ll',
       ('tshow','s2'):'tt',('sshow','t1'):'rt',('sshow','s2'):'tt',('s1','sshow'):'rt',
       ('s2','sshow'):'rl',('s3','sshow'):'rl',('nshow','s1'):'tt',('n3','s3'):'bl',
       ('dkeep','p1'):'rt',('pshow','dshow'):'tt',('p4','d4'):'bl',
       ('hu2','hshow'):'rr',('d6','qget'):'rr',('s3','qs'):'bt',('n3','qn'):'bt',
       ('d11','qsave'):'bt',('p2','qgroup'):'lb',('p4','qpnew'):'bt',('c2','qinvoice'):'rl',('i2','qinvoice'):'lt'}
    sides=[]
    for e in d.edges:
        side=overrides.get((e['a'],e['b']))
        if side is None:
            a=facing(e['a'],e['b']);side=a+opp[a]
        sides.append(side)
    totals=Counter((key,side) for e,ss in zip(d.edges,sides) for key,side in zip((e['a'],e['b']),ss));used=Counter()
    for key,n in d.nodes.items():
        if n['kind'] not in ('start','end'):
            n['h']=max(n['h'],20*(max(totals[key,'l'],totals[key,'r'])+1))
            n['w']=max(n['w'],20*(max(totals[key,'t'],totals[key,'b'])+1))
    for n in d.nodes.values():obstacle(n['x'],n['y'],n['w'],n['h'])
    for g in d.groups:obstacle(g['x']+10,g['y']+5,min(g['w']-20,len(g['title'])*10),32,5)
    for ix in range(nx+1):
        for iy in list(range(0,6))+list(range(ny-5,ny+1)):blocked.add((ix,iy))
    ports=[]
    endpoint_coordinates=set()
    for e,ss in zip(d.edges,sides):
        pair=[]
        for key,side in zip((e['a'],e['b']),ss):
            n=d.nodes[key];total=totals[key,side];i=used[key,side];used[key,side]+=1
            length=n['w'] if side in 'tb' else n['h']
            offset=round(length*(i+1)/(total+1)/STEP)*STEP
            x=n['x']+(offset if side in 'tb' else (n['w'] if side=='r' else 0))
            y=n['y']+(offset if side in 'lr' else (n['h'] if side=='b' else 0))
            port=(x//STEP,y//STEP)
            assert port not in endpoint_coordinates,('Duplicate connector endpoint',key,side,port)
            endpoint_coordinates.add(port)
            dx,dy={'r':(1,0),'l':(-1,0),'t':(0,-1),'b':(0,1)}[side]
            lead=(port[0]+dx*3,port[1]+dy*3)
            pair.append((port,lead))
        ports.append(pair)

    # Hard constraints: a unit segment belongs to exactly one connector.
    # At existing straight runs only perpendicular, straight-through crossings
    # are allowed. Bends and endpoints cannot be used as junctions.
    segments={};vertices=defaultdict(lambda:defaultdict(set));reserved={}
    def key(a,b):return tuple(sorted((a,b)))
    def unit_steps(path):
        for a,b in zip(path,path[1:]):
            dx=(b[0]>a[0])-(b[0]<a[0]);dy=(b[1]>a[1])-(b[1]<a[1]);x,y=a
            while (x,y)!=b:
                q=(x+dx,y+dy);yield (x,y),q;x,y=q
    def register(path,owner):
        for a,b in unit_steps(path):
            k=key(a,b)
            assert k not in segments or segments[k]==owner,('Overlapping track',owner,segments.get(k),k)
            segments[k]=owner
            dx,dy=b[0]-a[0],b[1]-a[1]
            vertices[a][owner].add((dx,dy));vertices[b][owner].add((-dx,-dy))
    for owner,pair in enumerate(ports):
        for port,lead in pair:
            register([port,lead],owner)
            for a,b in unit_steps([port,lead]):
                for cell in (a,b):
                    assert cell not in reserved or reserved[cell]==owner,('Clashing port stems',owner,reserved.get(cell),cell)
                    reserved[cell]=owner
    def other_axis(cell,owner):
        other=[directions for who,directions in vertices.get(cell,{}).items() if who!=owner]
        if not other:return None
        if len(other)!=1:return -1
        directions=other[0]
        if directions=={(1,0),(-1,0)}:return 0
        if directions=={(0,1),(0,-1)}:return 1
        return -1
    crossing_cost=45
    def astar(start,end,owner):
        queue=[(0,0,start[0],start[1],4)];dist={(start[0],start[1],4):0};prev={};goal=None
        while queue:
            _,cost,x,y,direction=heapq.heappop(queue);state=x,y,direction
            if cost!=dist.get(state):continue
            if (x,y)==end:goal=state;break
            here=(x,y);existing=other_axis(here,owner)
            for nd,(dx,dy) in enumerate(((1,0),(0,1),(-1,0),(0,-1))):
                if direction!=4 and nd==(direction+2)%4:continue
                xx,yy=x+dx,y+dy;cell=xx,yy;axis=nd%2
                if not(2<=xx<nx-2 and 7<=yy<ny-6):continue
                if cell in blocked and cell not in (start,end):continue
                if cell in reserved and reserved[cell]!=owner:continue
                if key(here,cell) in segments and segments[key(here,cell)]!=owner:continue
                if existing is not None and (existing<0 or existing==axis or (direction!=4 and direction!=nd)):continue
                there=other_axis(cell,owner)
                if there is not None and (there<0 or there==axis):continue
                penalty=crossing_cost if there is not None else 0
                nc=cost+1+(12 if direction!=4 and direction!=nd else 0)+penalty
                ns=xx,yy,nd
                if nc<dist.get(ns,math.inf):
                    dist[ns]=nc;prev[ns]=state;h=abs(xx-end[0])+abs(yy-end[1]);heapq.heappush(queue,(nc+h,nc,xx,yy,nd))
        if goal is None:raise RuntimeError(('No separate connector track',owner,start,end))
        result=[];current=goal
        while current in prev:result.append(current[:2]);current=prev[current]
        result.append(start);return result[::-1]
    order=sorted(range(len(d.edges)),key=lambda i:abs(ports[i][0][1][0]-ports[i][1][1][0])+abs(ports[i][0][1][1]-ports[i][1][1][1]))
    if d.stem=='purchase-activity':
        priority=[i for i,e in enumerate(d.edges) if (e['a'],e['b'])==('d11','c1')]
        order=priority+[i for i in order if i not in priority]
    routes={}
    for i in order:
        p,q=ports[i];path=[p[0],*astar(p[1],q[1],i),q[0]];register(path,i);routes[i]=path
    # Revisit routes with a stronger crossing penalty, keeping separation hard.
    crossing_cost=180
    for i in reversed(order):
        for a,b in unit_steps(routes[i]):
            k=key(a,b)
            if segments.get(k)==i:del segments[k]
            for cell in (a,b):vertices[cell].pop(i,None)
        for port,lead in ports[i]:register([port,lead],i)
        p,q=ports[i];path=[p[0],*astar(p[1],q[1],i),q[0]];register(path,i);routes[i]=path
    for i,path in routes.items():
        clean=[]
        for point in path:
            if clean and clean[-1]==point:continue
            if len(clean)>=2 and ((clean[-2][0]==clean[-1][0]==point[0]) or (clean[-2][1]==clean[-1][1]==point[1])):clean.pop()
            clean.append(point)
        d.edges[i]['points']=[(x*STEP,y*STEP) for x,y in clean]
    place_labels(d)

def place_labels(d):
    occupied=[(n['x']-5,n['y']-5,n['x']+n['w']+5,n['y']+n['h']+5) for n in d.nodes.values()]
    occupied += [(g['x']+5,g['y']+3,g['x']+min(g['w']-10,len(g['title'])*10),g['y']+40) for g in d.groups]
    def overlap(a,b):return max(0,min(a[2],b[2])-max(a[0],b[0]))*max(0,min(a[3],b[3])-max(a[1],b[1]))
    segments=[(a,b) for e in d.edges for a,b in zip(e['points'],e['points'][1:])]
    for e in sorted(d.edges,key=lambda e:-len(e['label'])):
        if not e['label']:e['pos']=None;continue
        width=len(e['label'])*7.2+8;height=20;candidates=[]
        for a,b in zip(e['points'],e['points'][1:]):
            length=abs(a[0]-b[0])+abs(a[1]-b[1])
            for fraction in (.5,.25,.75):
                cx=a[0]+(b[0]-a[0])*fraction;cy=a[1]+(b[1]-a[1])*fraction
                for sign in (-1,1):
                    if a[1]==b[1]:x,y=cx-width/2,cy+(-10 if sign<0 else 24)
                    else:x,y=cx+(12 if sign>0 else -width-12),cy+5
                    box=x-3,y-17,x+width,y+4
                    if box[0]<5 or box[2]>d.w-5 or box[1]<55 or box[3]>d.h-60:continue
                    score=sum(overlap(box,o) for o in occupied)*100
                    for p,q in segments:
                        if p[1]==q[1] and box[1]<p[1]<box[3]:score+=max(0,min(box[2],max(p[0],q[0]))-max(box[0],min(p[0],q[0])))*2
                        elif p[0]==q[0] and box[0]<p[0]<box[2]:score+=max(0,min(box[3],max(p[1],q[1]))-max(box[1],min(p[1],q[1])))*2
                    score+=max(0,width-length)*.5+abs(fraction-.5)*10
                    candidates.append((score,x,y,box))
        assert candidates,e
        _,x,y,box=min(candidates);e['pos']=(round(x,1),round(y,1));occupied.append(box)
