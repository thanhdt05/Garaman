import json
from pathlib import Path

def metrics(d):
 segments=[]
 for i,e in enumerate(d['edges']):
  for a,b in zip(e['points'],e['points'][1:]):segments.append((i,a,b))
 crosses=set();overlaps=0;hits=[]
 for k,(i,a,b) in enumerate(segments):
  for j,c,f in segments[k+1:]:
   if i==j:continue
   if a[1]==b[1] and c[0]==f[0] and min(a[0],b[0])<c[0]<max(a[0],b[0]) and min(c[1],f[1])<a[1]<max(c[1],f[1]):crosses.add((min(i,j),max(i,j),c[0],a[1]))
   elif a[0]==b[0] and c[1]==f[1] and min(a[1],b[1])<c[1]<max(a[1],b[1]) and min(c[0],f[0])<a[0]<max(c[0],f[0]):crosses.add((min(i,j),max(i,j),a[0],c[1]))
   if a[1]==b[1]==c[1]==f[1]:overlaps+=max(0,min(max(a[0],b[0]),max(c[0],f[0]))-max(min(a[0],b[0]),min(c[0],f[0])))
   elif a[0]==b[0]==c[0]==f[0]:overlaps+=max(0,min(max(a[1],b[1]),max(c[1],f[1]))-max(min(a[1],b[1]),min(c[1],f[1])))
  for n in d['nodes']:
   e=d['edges'][i]
   if n['id'] in (e['a'],e['b']):continue
   x,y,w,h=(n[t] for t in ('x','y','w','h'))
   if (a[1]==b[1] and y<a[1]<y+h and min(max(a[0],b[0]),x+w)>max(min(a[0],b[0]),x)) or (a[0]==b[0] and x<a[0]<x+w and min(max(a[1],b[1]),y+h)>max(min(a[1],b[1]),y)):hits.append((e['a'],e['b'],n['id']))
 return {'proper_crossings':len(crosses),'overlap_length':overlaps,'node_intersections':hits}
def separation(d):
 from collections import Counter
 pins=Counter(tuple(p) for e in d['edges'] for p in (e['points'][0],e['points'][-1]))
 segments=[(i,a,b) for i,e in enumerate(d['edges']) for a,b in zip(e['points'],e['points'][1:])]
 bad=set()
 for k,(i,a,b) in enumerate(segments):
  for j,c,f in segments[k+1:]:
   if i==j:continue
   if a[1]==b[1] and c[0]==f[0]:point=(c[0],a[1])
   elif a[0]==b[0] and c[1]==f[1]:point=(a[0],c[1])
   else:
    for p in (a,b):
     if p==c or p==f:bad.add((min(i,j),max(i,j),tuple(p)))
    continue
   x,y=point
   if all(min(p[t],q[t])<=point[t]<=max(p[t],q[t]) for p,q in [(a,b),(c,f)] for t in (0,1)):
    if point in (tuple(a),tuple(b),tuple(c),tuple(f)):bad.add((min(i,j),max(i,j),point))
 return {'duplicate_endpoints':[list(p) for p,n in pins.items() if n>1], 'junction_touches':len(bad)}

if __name__=='__main__':
 here=Path(__file__).resolve().parent
 for stem in ['catalog','purchase']:
  current=json.loads((here/f'{stem}-activity.json').read_text())
  result={**metrics(current),**separation(current)}
  assert not result['node_intersections'],result
  assert result['overlap_length']==0,result
  assert not result['duplicate_endpoints'],result
  assert not result['junction_touches'],result
  print(stem,json.dumps(result))
