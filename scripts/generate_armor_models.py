"""
Aster Risk - Armor Model Texture Generator

Uses the vanilla iron armor (64x32) as a base template and recolors it
for each material set using palette remapping. This produces proper
armor model textures that wrap onto the player model.

Run from project root:
  python scripts/generate_armor_models.py
"""

from PIL import Image
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT_DIR = ROOT / "src" / "main" / "resources" / "assets" / "aster_risk" / "textures" / "models" / "armor"
OUT_DIR.mkdir(parents=True, exist_ok=True)

# Iron armor template paths (vanilla bedrock samples)
BEDROCK = Path("C:/Users/yanas/MCreatorWorkspace/bedrock-samples-v1.26.30.25-preview-full/resource_pack/textures/models/armor")
IRON_1 = BEDROCK / "iron_1.png"
IRON_2 = BEDROCK / "iron_2.png"


# =============================================================================
# Color palettes - 4 brightness levels each
# Format: (highlight, base, shadow, dark)
# =============================================================================
PALETTES = {
    "silver": (
        (235, 240, 250),
        (190, 200, 215),
        (140, 150, 170),
        (60, 70, 90),
    ),
    "lunar": (
        (215, 225, 250),
        (155, 175, 220),
        (105, 125, 175),
        (40, 55, 100),
    ),
    "meteorite": (
        (130, 110, 95),
        (95, 75, 65),
        (60, 45, 40),
        (30, 20, 15),
    ),
    "void": (
        (110, 80, 145),
        (75, 50, 110),
        (45, 25, 75),
        (15, 5, 30),
    ),
    "stellar": (
        (255, 240, 165),
        (235, 200, 90),
        (180, 145, 50),
        (95, 65, 25),
    ),
    "stellar_endgame": (
        (255, 255, 230),
        (255, 230, 130),
        (210, 175, 70),
        (120, 85, 30),
    ),
    "eclipse": (
        (160, 80, 165),
        (95, 35, 110),
        (50, 15, 65),
        (15, 5, 25),
    ),
}


def remap_iron_to_palette(iron_img: Image.Image, palette: tuple) -> Image.Image:
    """
    Remap the iron armor's grayscale colors to the target palette by
    bucketing on luminance.

    The iron armor uses a small palette of grays. We classify each pixel
    by brightness and map it to the corresponding palette tier.
    """
    H, B, S, D = palette
    src = iron_img.convert("RGBA")
    out = Image.new("RGBA", src.size, (0, 0, 0, 0))

    src_px = src.load()
    out_px = out.load()
    w, h = src.size

    for y in range(h):
        for x in range(w):
            r, g, b, a = src_px[x, y]
            if a == 0:
                continue  # transparent stays transparent

            # Compute luminance
            lum = (r * 0.299 + g * 0.587 + b * 0.114)

            # Map to palette tier - thresholds tuned for vanilla iron armor
            if lum >= 200:
                color = H
            elif lum >= 160:
                color = B
            elif lum >= 110:
                color = S
            else:
                color = D

            out_px[x, y] = (color[0], color[1], color[2], a)

    return out


def generate_armor_models():
    if not IRON_1.exists() or not IRON_2.exists():
        print(f"ERROR: Iron armor templates not found at:\n  {IRON_1}\n  {IRON_2}")
        return

    iron_1 = Image.open(IRON_1)
    iron_2 = Image.open(IRON_2)

    for name, palette in PALETTES.items():
        layer1 = remap_iron_to_palette(iron_1, palette)
        layer2 = remap_iron_to_palette(iron_2, palette)
        layer1.save(OUT_DIR / f"{name}_layer_1.png")
        layer2.save(OUT_DIR / f"{name}_layer_2.png")
        print(f"  -> {name}_layer_1.png + {name}_layer_2.png")


if __name__ == "__main__":
    print("Generating armor model textures from vanilla iron template...")
    generate_armor_models()
    print(f"\nDone. Models written to {OUT_DIR}")
