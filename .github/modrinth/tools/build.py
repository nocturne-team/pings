"""Builds the page headlines into images/. Run from anywhere: python3 tools/build.py"""
from pathlib import Path
from PIL import Image
from headline import headline

HERE = Path(__file__).resolve().parent
PAGE = HERE.parent
RAW = PAGE / 'raw'
OUT = PAGE / 'images'
FONT = PAGE / 'fonts' / 'Jersey10-Regular.ttf'
ICON = PAGE.parent.parent / 'src' / 'main' / 'resources' / 'assets' / 'callouts' / 'icon.png'

PINK = (241, 53, 128)
BLUE = (77, 142, 240)
GOLD = (232, 184, 60)
GREEN = (108, 192, 74)
SHADOW = (0, 0, 0, 120)


def glyph(height):
	"""The icon's pink pixels on a transparent background, cropped and scaled to height."""
	im = Image.open(ICON).convert('RGBA')
	px = [(r, g, b, 255) if r > 150 and g < 120 else (0, 0, 0, 0) for r, g, b, a in im.get_flattened_data()]
	im.putdata(px)
	im = im.crop(im.getbbox())
	return im.resize((round(im.width * height / im.height), height), Image.NEAREST)


def headlines():
	for name, lines, color in [
		('h-quick', ["ONE KEY.", "ONE PING."], PINK),
		('h-wheel', ["HOLD FOR", "THE WHEEL."], GOLD),
		('h-walls', ["SEEN THROUGH", "WALLS."], BLUE),
		('h-sound', ["YOUR SOUND,", "YOUR CALL."], GREEN),
	]:
		headline(lines, color, FONT, 112, OUT / f'{name}.png', shadow=SHADOW)

	icon = glyph(200)
	top = headline(["PING IT."], PINK, FONT, 120, shadow=SHADOW)
	bottom = headline(["YOUR TEAM SEES IT."], BLUE, FONT, 120, shadow=SHADOW)
	gap = 40
	w = icon.width + gap + max(top.width, bottom.width)
	h = max(icon.height, top.height + bottom.height)
	hero = Image.new('RGBA', (w, h), (0, 0, 0, 0))
	hero.alpha_composite(icon, (0, (h - icon.height) // 2))
	ty = (h - top.height - bottom.height) // 2
	hero.alpha_composite(top, (icon.width + gap, ty))
	hero.alpha_composite(bottom, (icon.width + gap, ty + top.height))
	hero.save(OUT / 'hero.png')


def shot(src, box, out, width=1200):
	im = Image.open(RAW / src).convert('RGB')
	if box:
		im = im.crop(box)
	im = im.resize((width, round(im.height * width / im.width)), Image.LANCZOS)
	im.save(OUT / out, quality=90, optimize=True, progressive=True)


def screenshots():
	# Crops assume 1920x1080 screenshots from tools/DemoShots.java.
	shot('raw-walls.png', (0, 200, 1920, 1010), 's-walls.jpg')
	shot('raw-field.png', (0, 180, 1920, 1000), 's-field.jpg')
	shot('raw-wheel.png', (240, 160, 1680, 970), 's-wheel.jpg')


if __name__ == '__main__':
	OUT.mkdir(exist_ok=True)
	headlines()
	if RAW.exists():
		screenshots()
	else:
		print(f'{RAW} not found, only rebuilt the headlines')
