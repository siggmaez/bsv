"""Deterministic sizing/optimization of original BSV graphics. Requires Pillow."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageOps
ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'assets/graphics'
UI = ROOT / 'core-ui/src/main/res/drawable-nodpi'
APP = ROOT / 'app/src/main/res'
UI.mkdir(parents=True, exist_ok=True)
logo_source = Image.open(SOURCE / 'bsv-logo-source.png').convert('RGBA')
bounds = logo_source.getchannel('A').point(lambda value: 255 if value > 1 else 0).getbbox()
assert bounds is not None
logo = logo_source.crop((max(0,bounds[0]-12),max(0,bounds[1]-12),min(logo_source.width,bounds[2]+12),min(logo_source.height,bounds[3]+12)))
logo.save(SOURCE / 'bsv-logo.png', optimize=True)
mobile_logo = ImageOps.contain(logo, (640, 320), Image.Resampling.LANCZOS)
mobile_logo.save(UI / 'bsv_logo.webp', quality=94, method=6)
banner_source = Image.open(SOURCE / 'bsv-banner-source.png').convert('RGB')
# Contain preserves geometry, including source rounding of the intended 16:9 ratio.
banner = Image.new('RGB', (2560,1440), '#101014')
fit = ImageOps.contain(banner_source,(2560,1440),Image.Resampling.LANCZOS)
banner.paste(fit,((2560-fit.width)//2,(1440-fit.height)//2))
banner.save(SOURCE / 'bsv-banner-2560x1440.png', optimize=True)
banner.resize((1280,720),Image.Resampling.LANCZOS).save(UI / 'bsv_banner.webp',quality=88,method=6)

def background(size):
    im=Image.new('RGBA',(size,size))
    px=im.load()
    for y in range(size):
        for x in range(size):
            glow=max(0,1-((x/size-.5)**2+(y/size-.5)**2)**.5*1.5)
            px[x,y]=(int(16+15*glow),int(12+5*glow),int(24+34*glow),255)
    d=ImageDraw.Draw(im)
    d.polygon([(0,size*.26),(size*.26,0),(size*.45,0),(0,size*.45)],fill='#37136D')
    d.polygon([(size*.6,size),(size,size*.6),(size,size*.75),(size*.75,size)],fill='#6623C4')
    return im

def mark(canvas,width):
    fitted=ImageOps.contain(logo,(width,width),Image.Resampling.LANCZOS)
    canvas.alpha_composite(fitted,((canvas.width-fitted.width)//2,(canvas.height-fitted.height)//2))
    return canvas
for density, scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
    directory=APP/f'mipmap-{density}'
    directory.mkdir(parents=True,exist_ok=True)
    size=round(108*scale)
    foreground=mark(Image.new('RGBA',(size,size)),round(60*scale))
    # Keep visible artwork inside Android's central 66dp safe circle.
    alpha = foreground.getchannel('A')
    center = size / 2
    radius = size * 33 / 108
    assert all(alpha.getpixel((x,y)) <= 16 or (x-center)**2 + (y-center)**2 <= radius**2
               for y in range(size) for x in range(size)), 'Adaptive icon exceeds safe zone'
    foreground.save(directory/'ic_launcher_foreground.png',optimize=True)
    icon=mark(background(round(48*scale)),round(39*scale))
    icon.save(directory/'ic_launcher.png',optimize=True)
    mask=Image.new('L',icon.size)
    ImageDraw.Draw(mask).ellipse((0,0,icon.width-1,icon.height-1),fill=255)
    icon.putalpha(mask)
    icon.save(directory/'ic_launcher_round.png',optimize=True)
mark(background(512),416).save(SOURCE/'bsv-icon-512.png',optimize=True)
mark(Image.new('RGBA',(432,432)),240).save(SOURCE/'bsv-icon-foreground.png',optimize=True)
print('Prepared exact 2560x1440 master, WebP mobile assets and 5 mipmap densities.')
print('Logo aspect ratio:',logo.width,logo.height)
for f in UI.iterdir(): print(f.name,f.stat().st_size,'bytes')
