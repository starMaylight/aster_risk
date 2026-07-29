"""
Aster Risk - Sun Fragment / Sun Sword textures
16x16 textures matching the existing solar palette.

Run from project root:
  python scripts/generate_sun_items.py
"""

from PIL import Image
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT_DIR = ROOT / "src" / "main" / "resources" / "assets" / "aster_risk" / "textures" / "item"


def render(template, palette):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(template):
        for x, ch in enumerate(row):
            color = palette.get(ch)
            if color is not None:
                px[x, y] = color
    return img


# 太陽の欠片 - 割れた太陽の破片（鋭い多角形）
SUN_FRAGMENT = [
    "................",
    ".......kk.......",
    "......kWYk......",
    ".....kWWYGk.....",
    "....kWWYYGOk....",
    "...kWYYYGGOk....",
    "...kYYYGGOOk....",
    "..kYYGGGOOOk....",
    "..kYGGGOOODk....",
    "..kGGGOOODDk....",
    "...kGOOODDk.....",
    "...kOOODDk......",
    "....kODDk.......",
    ".....kDk........",
    "......k.........",
    "................",
]
FRAGMENT_PALETTE = {
    "k": (120, 40, 10, 255),
    "D": (170, 70, 20, 255),
    "O": (232, 118, 28, 255),
    "G": (250, 180, 50, 255),
    "Y": (255, 220, 80, 255),
    "W": (255, 252, 210, 255),
}

# 太陽の剣 - 燃え盛る長剣
SUN_SWORD = [
    "..............kk",
    ".............kWk",
    "............kWYk",
    "...........kWYGk",
    "..........kWYGOk",
    ".........kWYGOk.",
    "........kWYGOk..",
    ".......kWYGOk...",
    "......kWYGOk....",
    ".....kWYGOk.....",
    "....kYYGOk......",
    "...kGkGOkk......",
    "..kGYkkOGk......",
    ".khbkkkkGk......",
    "khbbk...kk......",
    "kbbk............",
]
SWORD_PALETTE = {
    "k": (110, 35, 8, 255),      # 輪郭
    "O": (200, 90, 20, 255),     # 刃の影
    "G": (250, 180, 50, 255),    # 刃の中間
    "Y": (255, 225, 95, 255),    # 刃の明部
    "W": (255, 253, 225, 255),   # 刃先の白熱
    "h": (150, 100, 40, 255),    # 柄
    "b": (215, 175, 75, 255),    # 柄の金装飾
}


if __name__ == "__main__":
    render(SUN_FRAGMENT, FRAGMENT_PALETTE).save(OUT_DIR / "sun_fragment.png")
    print("  -> sun_fragment.png")
    render(SUN_SWORD, SWORD_PALETTE).save(OUT_DIR / "sun_sword.png")
    print("  -> sun_sword.png")
    print("Done.")
