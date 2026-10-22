"""Optional PNG and PDF export on Linux using installed librsvg and libcairo.
Run render_activity.py first. No network or third-party Python package required.
"""
import ctypes as C
from pathlib import Path
import xml.etree.ElementTree as E
HERE=Path(__file__).resolve().parent
r=C.CDLL('librsvg-2.so.2');c=C.CDLL('libcairo.so.2')
def bind(lib,name,args,result=None):
 f=getattr(lib,name);f.argtypes=args;f.restype=result;return f
handle=bind(r,'rsvg_handle_new_from_file',[C.c_char_p,C.c_void_p],C.c_void_p)
render=bind(r,'rsvg_handle_render_cairo',[C.c_void_p,C.c_void_p],C.c_int)
image=bind(c,'cairo_image_surface_create',[C.c_int,C.c_int,C.c_int],C.c_void_p)
pdf=bind(c,'cairo_pdf_surface_create',[C.c_char_p,C.c_double,C.c_double],C.c_void_p)
ctx=bind(c,'cairo_create',[C.c_void_p],C.c_void_p)
png=bind(c,'cairo_surface_write_to_png',[C.c_void_p,C.c_char_p],C.c_int)
scale=bind(c,'cairo_scale',[C.c_void_p,C.c_double,C.c_double])
destroy=bind(c,'cairo_destroy',[C.c_void_p]);finish=bind(c,'cairo_surface_finish',[C.c_void_p]);free=bind(c,'cairo_surface_destroy',[C.c_void_p])
for stem in ['catalog','purchase']:
 p=HERE/f'{stem}-activity.svg';root=E.parse(p).getroot();w,h=int(root.get('width')),int(root.get('height'));doc=handle(str(p).encode(),None)
 surface=image(0,w,h);cr=ctx(surface);assert render(doc,cr);assert png(surface,str(p.with_suffix('.png')).encode())==0;destroy(cr);free(surface)
 # Vector PDF, A3 landscape for catalog, A1 portrait for the larger purchase flow.
 pw,ph=(1190.55,841.89) if stem=='catalog' else (1683.78,2383.94)
 surface=pdf(str(p.with_suffix('.pdf')).encode(),pw,ph);cr=ctx(surface);factor=min(pw/w,ph/h);scale(cr,factor,factor);assert render(doc,cr);destroy(cr);finish(surface);free(surface)
 print(p.stem, f'{w} x {h} PNG; vector PDF exported')
