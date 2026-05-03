"""
Aster Risk - Texture Generator
Minecraft-style 16x16 pixel art textures for armor and material items.

Run from project root:
  python scripts/generate_textures.py
"""

from PIL import Image
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT_DIR = ROOT / "src" / "main" / "resources" / "assets" / "aster_risk" / "textures" / "item"
OUT_DIR.mkdir(parents=True, exist_ok=True)


def make_image():
    """Create a transparent 16x16 image."""
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def put(img, pixels, color):
    """Set multiple pixels to a color. pixels is a list of (x, y) tuples."""
    px = img.load()
    for x, y in pixels:
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = color


def fill_rect(img, x1, y1, x2, y2, color):
    """Fill a rectangle region (inclusive) with a color."""
    px = img.load()
    for y in range(y1, y2 + 1):
        for x in range(x1, x2 + 1):
            if 0 <= x < 16 and 0 <= y < 16:
                px[x, y] = color


# =============================================================================
# Color Palettes  - (highlight, base, shadow, dark_outline)
# =============================================================================
PALETTES = {
    "silver": (
        (235, 240, 250, 255),  # highlight - bright silver
        (190, 200, 215, 255),  # base - silver
        (140, 150, 170, 255),  # shadow - dark silver
        (60, 70, 90, 255),     # outline - very dark silver
    ),
    "lunar": (
        (215, 225, 250, 255),  # highlight - moonlit blue-white
        (155, 175, 220, 255),  # base - lunar blue
        (105, 125, 175, 255),  # shadow
        (40, 55, 100, 255),    # outline
    ),
    "meteorite": (
        (130, 110, 95, 255),   # highlight - warm tan
        (95, 75, 65, 255),     # base - meteorite brown
        (60, 45, 40, 255),     # shadow
        (30, 20, 15, 255),     # outline
    ),
    "void": (
        (110, 80, 145, 255),   # highlight - violet
        (75, 50, 110, 255),    # base - dark violet
        (45, 25, 75, 255),     # shadow - deep purple
        (15, 5, 30, 255),      # outline - near black
    ),
    "stellar": (
        (255, 240, 165, 255),  # highlight - star gold
        (235, 200, 90, 255),   # base - bright gold
        (180, 145, 50, 255),   # shadow - amber
        (95, 65, 25, 255),     # outline - dark gold
    ),
    "eclipse": (
        (160, 80, 165, 255),   # highlight - eclipse magenta
        (95, 35, 110, 255),    # base - dark eclipse
        (50, 15, 65, 255),     # shadow
        (15, 5, 25, 255),      # outline - nearly black
    ),
}


# =============================================================================
# Armor templates
# Each template is a 16x16 grid where each cell is one of:
#   '.' transparent
#   'D' dark outline
#   'S' shadow
#   'B' base
#   'H' highlight
# =============================================================================

HELMET_TEMPLATE = [
    "................",
    "................",
    "................",
    "...DDDDDDDDDD...",
    "..DSBBBBBBBBSD..",
    "..DBHBBBBBBBBD..",
    "..DBBBBBBBBBBD..",
    "..DBDDDDDDDDBD..",
    "..DBDSSSSSSDBD..",
    "..DBDBBBBBBDBD..",
    "..DBDDDDDDDDBD..",
    "..DBSBBBBBBSBD..",
    "..DDSSSSSSSSDD..",
    "...DDDDDDDDDD...",
    "................",
    "................",
]

CHESTPLATE_TEMPLATE = [
    "................",
    "................",
    "..DDD......DDD..",
    "..DBSDDDDDDSBD..",
    "..DBSBBBBBBSBD..",
    ".DSBHBBSSBBHBSD.",
    ".DBBBBBSSBBBBBD.",
    ".DBBBBBBBBBBBBD.",
    ".DBSBBBBBBBBSBD.",
    ".DBSBBBSSBBBSBD.",
    ".DBSBBBSSBBBSBD.",
    ".DBSBBBSSBBBSBD.",
    ".DBSSSSDDSSSSBD.",
    ".DDDDDDDDDDDDDD.",
    "................",
    "................",
]

LEGGINGS_TEMPLATE = [
    "................",
    "................",
    "................",
    "..DDDDDDDDDDDD..",
    "..DBBBBBBBBBBD..",
    "..DBSBBBBBBSBD..",
    "..DBSBBDDBBSBD..",
    "..DBSBBDDBBSBD..",
    "..DBBBDDDDBBBD..",
    "..DDDD....DDDD..",
    "..DBBD....DBBD..",
    "..DBSD....DSBD..",
    "..DBSD....DSBD..",
    "..DDDD....DDDD..",
    "................",
    "................",
]

BOOTS_TEMPLATE = [
    "................",
    "................",
    "................",
    "................",
    "................",
    "................",
    "..DDDD....DDDD..",
    "..DBBD....DBBD..",
    "..DBSD....DSBD..",
    "..DBSD....DSBD..",
    "..DBBDDDDDDBBD..",
    "..DBSBBBBBBSBD..",
    "..DBSBBBBBBSBD..",
    "..DBBBBBBBBBBD..",
    "..DDDDDDDDDDDD..",
    "................",
]

CHAR_MAP = {".": None, "D": 3, "S": 2, "B": 1, "H": 0}


def render_template(template, palette):
    """Render an ascii template with the given palette."""
    img = make_image()
    px = img.load()
    for y, row in enumerate(template):
        for x, ch in enumerate(row):
            idx = CHAR_MAP.get(ch)
            if idx is not None:
                px[x, y] = palette[idx]
    return img


# =============================================================================
# Generate armor sets
# =============================================================================

def generate_armor():
    pieces = [
        ("helmet", HELMET_TEMPLATE),
        ("chestplate", CHESTPLATE_TEMPLATE),
        ("leggings", LEGGINGS_TEMPLATE),
        ("boots", BOOTS_TEMPLATE),
    ]
    sets = ["silver", "lunar", "meteorite", "void", "stellar", "eclipse"]
    for s in sets:
        for piece_name, template in pieces:
            img = render_template(template, PALETTES[s])
            img.save(OUT_DIR / f"{s}_{piece_name}.png")
            print(f"  -> {s}_{piece_name}.png")


# =============================================================================
# Material item generators
# =============================================================================

def gen_ingot(name, palette):
    """Bar-shaped ingot, similar to vanilla iron/gold ingots."""
    img = make_image()
    H, B, S, D = palette
    # Outline
    fill_rect(img, 4, 6, 13, 11, D)
    # Base fill
    fill_rect(img, 5, 7, 12, 10, B)
    # Top highlight
    fill_rect(img, 5, 7, 12, 7, H)
    # Bottom shadow
    fill_rect(img, 5, 10, 12, 10, S)
    # Side bevel
    img.load()[5, 8] = H
    img.load()[12, 9] = S
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_raw_chunk(name, palette):
    """Irregular raw ore chunk."""
    img = make_image()
    H, B, S, D = palette
    chunk = [
        "................",
        "................",
        "....DDDD........",
        "...DBBBBD.......",
        "..DBBHBBBD......",
        ".DBBHHBBBBD.....",
        ".DBHHBBBSBD.....",
        "DBBBBBBBSSBD....",
        "DBHBBBBSSSBD....",
        "DBBBBSSSSBBDD...",
        ".DBBBBSSSBBBD...",
        ".DBBBBSSBBBBD...",
        "..DDBSSBBBBBD...",
        "....DDBBBBBDD...",
        ".....DDDDDDD....",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(chunk):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_shard(name, palette):
    """Crystal shard, vertical pointed shape."""
    img = make_image()
    H, B, S, D = palette
    shard = [
        "................",
        ".......D........",
        "......DBD.......",
        "......DBD.......",
        "......DHBD......",
        ".....DBHBD......",
        ".....DBHBSD.....",
        ".....DBHBSD.....",
        "....DBBHBSSD....",
        "....DBBHBSSD....",
        "....DBBHBBSD....",
        ".....DBHBSD.....",
        ".....DBHBSD.....",
        "......DBSD......",
        "......DBSD......",
        ".......DD.......",
    ]
    px = img.load()
    for y, row in enumerate(shard):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_dust(name, palette):
    """Powdery dust pile."""
    img = make_image()
    H, B, S, D = palette
    dust = [
        "................",
        "................",
        ".....H..H.......",
        "...H...........H",
        "....HH..H.......",
        ".......H...H....",
        "..H....BB.......",
        ".....BBBBBB.....",
        "....BBBHBBBB....",
        "...BBHBHBBSBB...",
        "..BBBBBBSSBSBB..",
        "..BSBSSSBSSSSB..",
        "..DSDDDDDDDDDD..",
        "................",
        "................",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(dust):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_round_gem(name, palette, glow=False):
    """Spherical gem like the diamond/emerald style. If glow, add bright sparkles."""
    img = make_image()
    H, B, S, D = palette
    gem = [
        "................",
        "................",
        "....DDDDDD......",
        "...DSBBBBSD.....",
        "..DSBHHBBBSD....",
        "..DBHHHBBBBD....",
        ".DBHHBBBBBSSD...",
        ".DBHBBBBBBSSD...",
        ".DBBBBBBBBSSD...",
        ".DBBBBBBBBSSD...",
        "..DBBBBBBBSD....",
        "..DBBBBBBSSD....",
        "...DSBBBSSD.....",
        "....DDDDDD......",
        "................",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(gem):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    if glow:
        glow_color = (255, 255, 255, 255)
        px[8, 4] = glow_color
        px[5, 6] = glow_color
        px[10, 8] = glow_color
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_heart(name, palette):
    """Glowing pulsing heart - stellar_heart."""
    img = make_image()
    H, B, S, D = palette
    heart = [
        "................",
        "................",
        "..DDD....DDD....",
        ".DSBBD..DBBSD...",
        "DSBHBBDDBBHBSD..",
        "DBHHBBBBBBHHBD..",
        "DBHHBBBBBBHHBD..",
        "DBBHBBBBBBHBBD..",
        ".DBBHBBBBHBBSD..",
        "..DBBHBBHBBSD...",
        "...DBBHHBBSD....",
        "....DBBHBSD.....",
        ".....DBHBD......",
        "......DBSD......",
        "......DSDD......",
        ".......DD.......",
    ]
    px = img.load()
    for y, row in enumerate(heart):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    # glow accents
    px[5, 5] = (255, 255, 220, 255)
    px[10, 5] = (255, 255, 220, 255)
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_core(name, palette, sparkle_color=(255, 230, 180, 255)):
    """Small dense core orb - eclipse_core / celestial_nucleus."""
    img = make_image()
    H, B, S, D = palette
    core = [
        "................",
        "................",
        "................",
        "......DDDD......",
        ".....DSBBSD.....",
        "....DSBHHBSD....",
        "....DBHHHHBSD...",
        "....DBHHHHBSD...",
        "....DBBHHBBBD...",
        "....DBBBBBBSD...",
        ".....DBBBBSD....",
        "......DDDDD.....",
        "................",
        "................",
        "................",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(core):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    # sparkles
    px[3, 4] = sparkle_color
    px[12, 8] = sparkle_color
    px[6, 11] = sparkle_color
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_fragment(name, palette):
    """Irregular fragment / shard - meteorite_fragment, star_fragment, prismatic_meteorite."""
    img = make_image()
    H, B, S, D = palette
    frag = [
        "................",
        "................",
        ".....DDD........",
        "....DBBSD.......",
        "...DBHBSSD......",
        "..DBHHBSSSD.....",
        "..DBHBBSSSD.....",
        ".DBHHBBBSSSDD...",
        ".DBHBBBSSSSBD...",
        "..DBBBSSSSBSD...",
        "...DBBSSBBBSD...",
        "....DBSBBBSDD...",
        "....DDSDDDDD....",
        ".....DDDD.......",
        "................",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(frag):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_star_fragment(name, palette):
    """Star-shaped fragment."""
    img = make_image()
    H, B, S, D = palette
    star = [
        "................",
        "................",
        ".......DD.......",
        ".......DBD......",
        "......DBHBD.....",
        "DDDDDDDBHBDDDDDD",
        "DBBBBBBBHBBBBBBD",
        "DSBBBHHHHHBBBBSD",
        "DSSBBBHHHBBBBSSD",
        ".DSSBBBBBBBBBSD.",
        "..DDSSBBBBSSDD..",
        "....DSSBBSSD....",
        "....DBSSSSBD....",
        "...DBSDDDDSBD...",
        "..DDD......DDD..",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(star):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_essence(name, palette):
    """Liquid essence in a vial-like shape."""
    img = make_image()
    H, B, S, D = palette
    ess = [
        "................",
        "................",
        ".....DDDDDD.....",
        ".....D....D.....",
        "......D..D......",
        "......DBBD......",
        ".....DBHBBD.....",
        "....DBHHBBBD....",
        "....DBHHHBBD....",
        "....DBHBBBBD....",
        "....DBBBBBSD....",
        "....DBBBBSSD....",
        "....DSBBSSDD....",
        "....DDSSDDD.....",
        ".....DDDDD......",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(ess):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_corrupted_core(name):
    """Corrupted core - dark with red veins."""
    img = make_image()
    pal_dark = (15, 5, 15, 255)
    pal_base = (60, 25, 50, 255)
    pal_red = (160, 30, 50, 255)
    pal_glow = (255, 80, 80, 255)
    core = [
        "................",
        "................",
        "................",
        "......DDDD......",
        ".....DBBBBD.....",
        "....DBRBBRBD....",
        "....DBBRRBBD....",
        "....DRRBHRRBD...",
        "....DBRBBRRBD...",
        "....DBBRBRBBD...",
        ".....DBBBBBD....",
        "......DDDDD.....",
        "................",
        "................",
        "................",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(core):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = pal_dark
            elif ch == "B":
                px[x, y] = pal_base
            elif ch == "R":
                px[x, y] = pal_red
            elif ch == "H":
                px[x, y] = pal_glow
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_radiant_diamond(name):
    """Radiant diamond - extra bright."""
    pal = (
        (255, 255, 255, 255),
        (180, 240, 255, 255),
        (110, 180, 220, 255),
        (40, 80, 130, 255),
    )
    gen_round_gem(name, pal, glow=True)


def gen_enhanced_gold(name):
    """Enhanced gold - bright golden."""
    pal = (
        (255, 245, 180, 255),
        (245, 200, 70, 255),
        (190, 140, 30, 255),
        (90, 60, 15, 255),
    )
    gen_ingot(name, pal)


def gen_prismatic_meteorite(name):
    """Prismatic meteorite - rainbow effect."""
    img = make_image()
    px = img.load()
    # Multi-color core with rainbow gradient
    colors = [
        (255, 100, 100, 255),  # red
        (255, 200, 80, 255),   # orange
        (255, 255, 100, 255),  # yellow
        (100, 255, 100, 255),  # green
        (100, 200, 255, 255),  # cyan
        (180, 100, 255, 255),  # purple
    ]
    dark = (30, 20, 40, 255)
    light = (255, 240, 255, 255)
    frag = [
        "................",
        "................",
        ".....DDD........",
        "....D012D.......",
        "...D01HBSD......",
        "..D012HBSSD.....",
        "..D012BBSSD.....",
        ".D0123BBSSSDD...",
        ".D012BBBSSSBD...",
        "..D012BSSSSBD...",
        "...D34BSSBBSD...",
        "....D45BBBSDD...",
        "....DDSDDDDD....",
        ".....DDDD.......",
        "................",
        "................",
    ]
    for y, row in enumerate(frag):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = dark
            elif ch == "H":
                px[x, y] = light
            elif ch == "B":
                px[x, y] = (200, 200, 230, 255)
            elif ch == "S":
                px[x, y] = (120, 120, 160, 255)
            elif ch.isdigit():
                px[x, y] = colors[int(ch)]
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


def gen_stardust_catalyst(name):
    """Stardust Catalyst - stardust contained in a vial / orb."""
    img = make_image()
    H = (255, 255, 200, 255)
    B = (240, 215, 110, 255)
    S = (180, 145, 50, 255)
    D = (90, 60, 20, 255)
    glass = (180, 220, 255, 200)
    cat = [
        "................",
        "................",
        "......DDDD......",
        ".....DggggD.....",
        "....DgHHHHgD....",
        "....DgHBBBgD....",
        "....DgHBBBgD....",
        "....DgHBBBgD....",
        "....DgBBBSgD....",
        "....DgBSSSgD....",
        "....DgSSSSgD....",
        "....DggSSggD....",
        ".....DggggD.....",
        "......DDDD......",
        "................",
        "................",
    ]
    px = img.load()
    for y, row in enumerate(cat):
        for x, ch in enumerate(row):
            if ch == "D":
                px[x, y] = D
            elif ch == "S":
                px[x, y] = S
            elif ch == "B":
                px[x, y] = B
            elif ch == "H":
                px[x, y] = H
            elif ch == "g":
                px[x, y] = glass
    img.save(OUT_DIR / f"{name}.png")
    print(f"  -> {name}.png")


# =============================================================================
# Generate material items
# =============================================================================

def generate_materials():
    # Ingots / metallic items
    gen_ingot("silver_ingot", PALETTES["silver"])
    gen_raw_chunk("raw_silver", (
        (170, 175, 195, 255),
        (115, 120, 145, 255),
        (75, 80, 105, 255),
        (35, 40, 60, 255),
    ))
    gen_ingot("enhanced_gold", PALETTES["stellar"])

    # Gems / shards
    gen_round_gem("moonstone", (
        (235, 245, 255, 255),
        (170, 195, 235, 255),
        (105, 130, 180, 255),
        (45, 60, 100, 255),
    ), glow=True)
    gen_shard("void_shard", PALETTES["void"])
    gen_radiant_diamond("radiant_diamond")

    # Dusts / powders
    gen_dust("stardust", (
        (255, 255, 220, 255),
        (250, 230, 130, 255),
        (200, 165, 60, 255),
        (110, 80, 20, 255),
    ))
    gen_dust("lunar_dust", (
        (235, 245, 255, 255),
        (175, 200, 245, 255),
        (115, 145, 195, 255),
        (50, 75, 130, 255),
    ))

    # Fragments
    gen_fragment("meteorite_fragment", PALETTES["meteorite"])
    gen_star_fragment("star_fragment", (
        (255, 255, 220, 255),
        (250, 220, 90, 255),
        (200, 160, 40, 255),
        (105, 75, 20, 255),
    ))
    gen_prismatic_meteorite("prismatic_meteorite")

    # Cores / orbs
    gen_core("eclipse_core", PALETTES["eclipse"], sparkle_color=(220, 130, 240, 255))
    gen_core("celestial_nucleus", (
        (255, 255, 255, 255),
        (220, 230, 255, 255),
        (140, 165, 230, 255),
        (50, 70, 140, 255),
    ), sparkle_color=(255, 255, 255, 255))
    gen_corrupted_core("corrupted_core")

    # Hearts / essences
    gen_heart("stellar_heart", (
        (255, 255, 200, 255),
        (255, 215, 95, 255),
        (200, 145, 35, 255),
        (110, 70, 15, 255),
    ))
    gen_essence("shadow_essence", (
        (110, 80, 145, 255),
        (60, 35, 90, 255),
        (35, 15, 55, 255),
        (10, 5, 20, 255),
    ))

    # Stardust Catalyst (special)
    gen_stardust_catalyst("stardust_catalyst")


# =============================================================================
# Main
# =============================================================================

if __name__ == "__main__":
    print("Generating armor textures...")
    generate_armor()
    print("\nGenerating material item textures...")
    generate_materials()
    print(f"\nDone. Textures written to {OUT_DIR}")
