"""
Aster Risk - Sun Incarnate Entity Texture Generator
128x128 blazing sun texture for the SunIncarnateModel UV layout.

Run from project root:
  python scripts/generate_sun_incarnate_texture.py
"""

import random
from PIL import Image
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "src" / "main" / "resources" / "assets" / "aster_risk" / "textures" / "entity" / "sun_incarnate.png"

random.seed(20260712)

SIZE = 128
img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
px = img.load()

# --- 溶岩風の暖色ノイズで全面をベース塗り ---
# モデルのUVはボックス展開なので、全面を統一質感で塗れば各面が破綻なく拾える
BASE = (232, 118, 28)       # 燃える橙
BRIGHT = (255, 214, 64)     # 太陽の金
HOT = (255, 244, 180)       # 白熱
DARK = (150, 45, 12)        # 焦げた赤

for y in range(SIZE):
    for x in range(SIZE):
        r = random.random()
        if r < 0.06:
            c = HOT
        elif r < 0.28:
            c = BRIGHT
        elif r < 0.88:
            c = BASE
        else:
            c = DARK
        # わずかな明度ゆらぎ
        jitter = random.randint(-12, 12)
        px[x, y] = (
            max(0, min(255, c[0] + jitter)),
            max(0, min(255, c[1] + jitter)),
            max(0, min(255, c[2] + max(-8, min(8, jitter)))),
            255,
        )

# --- 溶岩の亀裂（暗い筋）を流す ---
for _ in range(40):
    cx = random.randint(0, SIZE - 1)
    cy = random.randint(0, SIZE - 1)
    length = random.randint(6, 18)
    dx = random.choice((-1, 0, 1))
    dy = random.choice((-1, 1))
    for i in range(length):
        nx = cx + dx * i + random.randint(-1, 1)
        ny = cy + dy * i
        if 0 <= nx < SIZE and 0 <= ny < SIZE:
            px[nx, ny] = (110, 30, 8, 255)

# --- 白熱スポット（マグマの吹き出し） ---
for _ in range(60):
    cx = random.randint(1, SIZE - 2)
    cy = random.randint(1, SIZE - 2)
    px[cx, cy] = (255, 250, 210, 255)
    for ox, oy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
        if random.random() < 0.5:
            px[cx + ox, cy + oy] = (255, 228, 120, 255)

# --- 頭部（太陽核）UV領域 (0,30)-(48,54) を一段明るく ---
for y in range(30, 54):
    for x in range(0, 48):
        r, g, b, a = px[x, y]
        px[x, y] = (min(255, r + 30), min(255, g + 40), min(255, b + 20), 255)

# --- コロナ (48,30)-(84,48) と光輪 (28,54)-(72,76) は炎の透過グラデーション ---
def flame_plane(x0, y0, w, h):
    cx = x0 + w / 2
    cy = y0 + h / 2
    max_dist = (w + h) / 4
    for y in range(y0, y0 + h):
        for x in range(x0, x0 + w):
            dist = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            ratio = dist / max_dist
            if ratio > 1.0:
                px[x, y] = (0, 0, 0, 0)  # 外周は透明
            elif ratio > 0.75:
                if random.random() < (ratio - 0.75) * 4:
                    px[x, y] = (0, 0, 0, 0)
                else:
                    px[x, y] = (255, 150, 40, 200)
            elif ratio > 0.45:
                px[x, y] = (255, 200, 70, 235)
            else:
                px[x, y] = (255, 246, 190, 255)

flame_plane(48, 30, 36, 36)   # corona 18x18 面 (両面分UV 36幅)
flame_plane(28, 54, 44, 44)   # halo 22x22 面

OUT.parent.mkdir(parents=True, exist_ok=True)
img.save(OUT)
print(f"-> {OUT}")
