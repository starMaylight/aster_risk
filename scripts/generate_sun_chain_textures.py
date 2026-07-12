"""
Aster Risk - Sun Summoning Chain Item Textures
16x16 textures for unstable_core, stable_full_moon_core, meteor_core_sun.

Run from project root:
  python scripts/generate_sun_chain_textures.py
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


# 不安定な核 - 金と紫のエネルギーがせめぎ合う歪んだ球体
UNSTABLE_CORE = [
    "................",
    "......W..k......",
    "....kDGGPDk.....",
    "...kGGWGPPDk....",
    "..kDGWWGGPPDk...",
    "..kGGWGGGPPPk...",
    ".kDGGGGGPPVPDk..",
    ".kGWGGGPPVPPPk..",
    ".kGGGGPPVPPVPk..",
    ".kDGGPPVPPPPDk..",
    "..kGPPPVPPVPk...",
    "..kDPPPPPVPDk...",
    "...kDPVPPPDk....",
    "....kDPPDDk.....",
    "......k..W......",
    "................",
]
UNSTABLE_PALETTE = {
    "k": (60, 25, 15, 255),      # 外殻
    "D": (140, 70, 20, 255),     # 暗部
    "G": (250, 200, 70, 255),    # 金エネルギー
    "W": (255, 255, 210, 255),   # 白熱
    "P": (150, 60, 170, 255),    # 紫エネルギー
    "V": (90, 30, 120, 255),     # 暗紫
}

# 安定した満月の核 - 静謐な白銀の球体に満月の紋
STABLE_FULL_MOON_CORE = [
    "................",
    "................",
    ".....kkkkkk.....",
    "....kSBBBBSk....",
    "...kSBHHHHBSk...",
    "..kSBHWWWWHBSk..",
    "..kBHWWWWWWHBk..",
    "..kBHWWWWWWHBk..",
    "..kBHWWWWWWHBk..",
    "..kBHWWWWWWHBk..",
    "..kSBHWWWWHBSk..",
    "...kSBHHHHBSk...",
    "....kSBBBBSk....",
    ".....kkkkkk.....",
    "................",
    "................",
]
STABLE_PALETTE = {
    "k": (40, 55, 100, 255),     # 外殻（藍）
    "S": (105, 130, 180, 255),   # 影
    "B": (170, 195, 235, 255),   # 銀青
    "H": (215, 230, 250, 255),   # 明部
    "W": (250, 252, 255, 255),   # 満月の白
}

# 太陽の流星核 - 燃え盛る太陽の球体
METEOR_CORE_SUN = [
    "................",
    "..R....W....R...",
    "....kkkkkk......",
    "...kOGGGGOk.....",
    "..kOGYYYYGOk....",
    ".WkGYWWWWYGkW...",
    "..kGYWWWWYGk....",
    "..kGYWWWWYGk....",
    "..kGYWWWWYGk....",
    "..kGYWWWWYGk....",
    ".WkGYWWWWYGkW...",
    "..kOGYYYYGOk....",
    "...kOGGGGOk.....",
    "....kkkkkk......",
    "..R....W....R...",
    "................",
]
SUN_PALETTE = {
    "k": (150, 45, 12, 255),     # 外殻（焦げ赤）
    "O": (232, 118, 28, 255),    # 橙
    "G": (250, 180, 50, 255),    # 濃金
    "Y": (255, 220, 80, 255),    # 金
    "W": (255, 250, 200, 255),   # 白熱
    "R": (255, 140, 40, 200),    # 外周の炎の粒
}


if __name__ == "__main__":
    render(UNSTABLE_CORE, UNSTABLE_PALETTE).save(OUT_DIR / "unstable_core.png")
    print("  -> unstable_core.png")
    render(STABLE_FULL_MOON_CORE, STABLE_PALETTE).save(OUT_DIR / "stable_full_moon_core.png")
    print("  -> stable_full_moon_core.png")
    render(METEOR_CORE_SUN, SUN_PALETTE).save(OUT_DIR / "meteor_core_sun.png")
    print("  -> meteor_core_sun.png")
    print("Done.")
