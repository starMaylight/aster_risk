"""
Aster Risk - Tool and Weapon Texture Generator
Minecraft-style 16x16 textures for tools, weapons, wands, and special items.

Run from project root:
  python scripts/generate_tools.py
"""

from PIL import Image
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT_DIR = ROOT / "src" / "main" / "resources" / "assets" / "aster_risk" / "textures" / "item"
OUT_DIR.mkdir(parents=True, exist_ok=True)


def make_image():
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def render_template(template, palette_map):
    """Render an ascii template using a char->color map."""
    img = make_image()
    px = img.load()
    for y, row in enumerate(template):
        for x, ch in enumerate(row):
            if x >= 16 or y >= 16:
                continue
            color = palette_map.get(ch)
            if color is not None:
                px[x, y] = color
    return img


# =============================================================================
# Material palettes for tool heads
# H=highlight, B=base, S=shadow, D=dark outline
# =============================================================================
HEADS = {
    "silver": (
        (235, 240, 250, 255),
        (190, 200, 215, 255),
        (140, 150, 170, 255),
        (60, 70, 90, 255),
    ),
    "moonstone": (
        (235, 245, 255, 255),
        (170, 200, 240, 255),
        (105, 135, 195, 255),
        (40, 60, 110, 255),
    ),
    "lunar": (
        (215, 225, 250, 255),
        (155, 175, 220, 255),
        (105, 125, 175, 255),
        (40, 55, 100, 255),
    ),
    "meteorite": (
        (160, 140, 120, 255),
        (115, 95, 80, 255),
        (75, 60, 50, 255),
        (35, 25, 20, 255),
    ),
    "void": (
        (140, 100, 175, 255),
        (90, 60, 130, 255),
        (55, 30, 85, 255),
        (15, 5, 30, 255),
    ),
    "stellar": (
        (255, 240, 165, 255),
        (235, 200, 90, 255),
        (180, 145, 50, 255),
        (95, 65, 25, 255),
    ),
    "eclipse": (
        (180, 90, 185, 255),
        (110, 40, 130, 255),
        (60, 15, 75, 255),
        (15, 5, 25, 255),
    ),
    "stardust": (
        (255, 255, 215, 255),
        (250, 225, 110, 255),
        (200, 165, 55, 255),
        (110, 75, 20, 255),
    ),
    "prismatic": (
        (255, 255, 255, 255),
        (220, 200, 240, 255),
        (140, 110, 180, 255),
        (50, 30, 80, 255),
    ),
    "shadow": (
        (90, 85, 110, 255),
        (55, 50, 75, 255),
        (30, 25, 45, 255),
        (10, 5, 20, 255),
    ),
}

# Wood/handle palette - standard stick brown
HANDLE = {
    "h": (140, 100, 60, 255),    # handle base
    "i": (165, 125, 80, 255),    # handle highlight
    "j": (95, 65, 35, 255),      # handle shadow
    "k": (50, 30, 15, 255),      # handle outline
}

# Special handle colors for magical items
HANDLE_GOLD = {
    "h": (215, 175, 75, 255),
    "i": (250, 220, 130, 255),
    "j": (160, 120, 40, 255),
    "k": (80, 55, 15, 255),
}

HANDLE_DARK = {
    "h": (60, 50, 75, 255),
    "i": (85, 75, 105, 255),
    "j": (35, 25, 45, 255),
    "k": (15, 10, 20, 255),
}


def palette_map(head, handle=HANDLE):
    """Build a char->color map combining head + handle palette."""
    H, B, S, D = head
    return {
        "H": H, "B": B, "S": S, "D": D,
        **handle,
    }


# =============================================================================
# Tool templates
# =============================================================================

# Standard sword - 16px diagonal
SWORD = [
    "..............kk",
    ".............kBk",
    "............kBHk",
    "...........kBHSk",
    "..........kBHSDk",
    ".........kBHSDk.",
    "........kBHSDk..",
    ".......kBBSDk...",
    "......kBBSDk....",
    ".....kBBSDk.....",
    "....kjBSDk......",
    "...kkjSDk.......",
    "..khjkjkk.......",
    ".khijkk.........",
    "khijkk..........",
    "kkkk............",
]

# Pickaxe
PICKAXE = [
    ".kkkk......kkkk.",
    "kBHHBkkkkkkBHHBk",
    "kBHHHHBBBBHHHHBk",
    "kBHHHHHHHHHHHHBk",
    ".kBHHSSSSSSHHBk.",
    "..kBSSjjjjSSBk..",
    "..kkkjkkkkjkkk..",
    ".....khijk......",
    "....khjijk......",
    "....kjijk.......",
    "...khjijk.......",
    "...kjijk........",
    "..khjijk........",
    "..kjijk.........",
    ".khjik..........",
    ".kkkk...........",
]

# Axe - blade on right
AXE = [
    "kkkk............",
    "kBHkk...........",
    "kBHHkkk.........",
    "kBHHHBkk........",
    "kBHHHHHkk.......",
    "kBSHHHHHkk......",
    ".kBSSSHHHkkk....",
    "..kkjjSSSHBkk...",
    "....kkkkjjBHk...",
    "......khijkk....",
    ".....khjik......",
    ".....kjik.......",
    "....khjik.......",
    "....kjik........",
    "...khjik........",
    "...kkkk.........",
]

# Shovel
SHOVEL = [
    ".......kkkk.....",
    "......kBHHBk....",
    "......kBHSBk....",
    "......kBSSBk....",
    "......kBSSBk....",
    "......kBSSBk....",
    "......kkSSkk....",
    ".......kkkk.....",
    ".......khik.....",
    "......khijk.....",
    "......kjijk.....",
    ".....khjik......",
    ".....kjijk......",
    "....khjik.......",
    "....kjik........",
    "...kkkk.........",
]

# Hoe
HOE = [
    "kkkkkkkk........",
    "kBHHHHHBk.......",
    "kBHSSSSBkk......",
    "kBHSSSSSBk......",
    ".kBSSSSSBk......",
    "..kkkSjjkk......",
    "....kkjkk.......",
    ".....khik.......",
    "....khijk.......",
    "....kjijk.......",
    "...khjik........",
    "...kjijk........",
    "..khjik.........",
    "..kjik..........",
    ".khjik..........",
    ".kkkk...........",
]

# Greatsword - wider, longer blade
GREATSWORD = [
    ".............kkk",
    "............kBHk",
    "...........kBHHk",
    "..........kBHHSk",
    ".........kBHHSDk",
    "........kBHHSDk.",
    ".......kBHHSDk..",
    "......kBHHSDk...",
    ".....kBHHSDk....",
    "....kBHHSDk.....",
    "...kBHHSDk......",
    "..kBHSDkk.......",
    ".kkkkkkk........",
    "..khijk.........",
    "..khjk..........",
    "..kkk...........",
]

# Dagger - short blade
DAGGER = [
    "...........kk...",
    "..........kBk...",
    ".........kBHk...",
    "........kBHSk...",
    ".......kBHSDk...",
    "......kBHSDk....",
    ".....kBHSDk.....",
    "....kBHSDk......",
    "...kBHSDk.......",
    "..kBSSDk........",
    ".kkkjkk.........",
    "..khijk.........",
    "..kjik..........",
    "..khik..........",
    "..kjk...........",
    "..kk............",
]

# Hammer - blocky head
HAMMER = [
    "...kkkkkk.......",
    "..kBHHHHBk......",
    ".kBHHHHHHBk.....",
    ".kBHHSSHHBk.....",
    ".kBHSSSSHBk.....",
    ".kBHSSSSHBk.....",
    ".kBHHSSHHBk.....",
    "..kkBHHBkk......",
    "....khijk.......",
    "....kjijk.......",
    "....khjik.......",
    "....kjijk.......",
    "...khjik........",
    "...kjik.........",
    "..khjik.........",
    "..kkkk..........",
]

# Scythe - curved blade with long handle
SCYTHE = [
    "..kkkkkk........",
    ".kBHHHHHBk......",
    "kBHHSDDDDk......",
    "kBHSDk..........",
    "kBSDk...........",
    "kkDk............",
    "..kk............",
    "...kkk..........",
    "....khijk.......",
    "....kjijk.......",
    "....khjik.......",
    "....kjijk.......",
    "...khjik........",
    "...kjijk........",
    "..khjik.........",
    "..kkkk..........",
]

# Wand - thin handle with a small head
WAND = [
    "...........kk...",
    "..........kBHk..",
    ".........kBHSk..",
    "........kBHSk...",
    ".......kBHSk....",
    "......kkSDk.....",
    ".....khijkk.....",
    "....kjijk.......",
    "...khjik........",
    "...kjijk........",
    "..khjik.........",
    "..kjijk.........",
    ".khjik..........",
    ".kjik...........",
    "khjik...........",
    "kkkk............",
]

# Staff - long handle with orb on top
STAFF = [
    "...kkk..........",
    "..kBHBk.........",
    "..kBSBk.........",
    "..kkBkk.........",
    "...kHk..........",
    "..khSjk.........",
    "..khijk.........",
    "...kijk.........",
    "..khjik.........",
    "..kjijk.........",
    ".khjik..........",
    ".kjijk..........",
    "khjik...........",
    "kjijk...........",
    "khjk............",
    "kkk.............",
]

# Scepter - ornate handle with crystal
SCEPTER = [
    "............kk..",
    "...........kHk..",
    "..........kHBk..",
    "..........kBHk..",
    ".........kBHk...",
    ".......kkBHkk...",
    "......kBkSkBk...",
    ".....kBkkjkkk...",
    "....khijkkk.....",
    "...khjijk.......",
    "...kjijk........",
    "..khjik.........",
    "..kjijk.........",
    ".khjik..........",
    ".kjik...........",
    "kkkk............",
]

# Compass - circular face
COMPASS = [
    "................",
    "................",
    "....kkkkkkkk....",
    "...kBHHHHHHBk...",
    "..kBHHSSSSHHBk..",
    ".kBHSDDDDDDSHBk.",
    ".kBHSDHkBkDSHBk.",
    ".kBHSDkBkkDSHBk.",
    ".kBHSDkkBkDSHBk.",
    ".kBHSDkBkkDSHBk.",
    ".kBHSDDDDDDSHBk.",
    "..kBHSSSSSHHBk..",
    "...kBHHHHHHBk...",
    "....kkkkkkkk....",
    "................",
    "................",
]

# Book / Guide
GUIDE = [
    "................",
    "kkkkkkkkkkkkkk..",
    "kBHBHBHBHBHBHk..",
    "kBHHHHHHHHHHBk..",
    "kBHkkkkkkkkHBk..",
    "kBHkBBBBBBkHBk..",
    "kBHkBSSSSBkHBk..",
    "kBHkBSSSSBkHBk..",
    "kBHkBSSSSBkHBk..",
    "kBHkBBBBBBkHBk..",
    "kBHkkkkkkkkHBk..",
    "kBHHHHHHHHHHBk..",
    "kBSSSSSSSSSSBk..",
    "kkkkkkkkkkkkkk..",
    "................",
    "................",
]

# Charm - pendant on chain
CHARM = [
    "................",
    "..kk.kk.kk.kk...",
    "...k...k...k....",
    "....k.k.k.k.....",
    ".....kkkkk......",
    ".....kBHBk......",
    "....kBHHHBk.....",
    "...kBHHHHHBk....",
    "...kBHSSSHBk....",
    "...kBHSSSHBk....",
    "...kBHSSSHBk....",
    "....kBSSSBk.....",
    ".....kBSBk......",
    "......kkk.......",
    "................",
    "................",
]


# =============================================================================
# Generators
# =============================================================================

def generate_basic_tools():
    """Sword, pickaxe, axe, shovel, hoe for silver and moonstone."""
    materials = {"silver": HEADS["silver"], "moonstone": HEADS["moonstone"]}
    tools = [
        ("sword", SWORD),
        ("pickaxe", PICKAXE),
        ("axe", AXE),
        ("shovel", SHOVEL),
        ("hoe", HOE),
    ]
    for mat_name, head in materials.items():
        for tool_name, template in tools:
            img = render_template(template, palette_map(head))
            img.save(OUT_DIR / f"{mat_name}_{tool_name}.png")
            print(f"  -> {mat_name}_{tool_name}.png")


def generate_weapons():
    """Specialty weapons: greatswords, daggers, hammer, scythe, blades."""
    # Greatswords
    img = render_template(GREATSWORD, palette_map(HEADS["meteorite"]))
    img.save(OUT_DIR / "meteorite_greatsword.png")
    print("  -> meteorite_greatsword.png")

    img = render_template(GREATSWORD, palette_map(HEADS["prismatic"]))
    img.save(OUT_DIR / "prismatic_greatsword.png")
    print("  -> prismatic_greatsword.png")

    # Daggers
    img = render_template(DAGGER, palette_map(HEADS["stardust"]))
    img.save(OUT_DIR / "stardust_dagger.png")
    print("  -> stardust_dagger.png")

    img = render_template(DAGGER, palette_map(HEADS["void"], HANDLE_DARK))
    img.save(OUT_DIR / "void_dagger.png")
    print("  -> void_dagger.png")

    # Hammer
    img = render_template(HAMMER, palette_map(HEADS["meteorite"]))
    img.save(OUT_DIR / "meteor_hammer.png")
    print("  -> meteor_hammer.png")

    # Scythe
    img = render_template(SCYTHE, palette_map(HEADS["shadow"], HANDLE_DARK))
    img.save(OUT_DIR / "shadow_scythe.png")
    print("  -> shadow_scythe.png")

    # Long blades (sword-style for lunar/eclipse)
    img = render_template(SWORD, palette_map(HEADS["lunar"]))
    img.save(OUT_DIR / "lunar_blade.png")
    print("  -> lunar_blade.png")

    img = render_template(SWORD, palette_map(HEADS["eclipse"], HANDLE_DARK))
    img.save(OUT_DIR / "eclipse_blade.png")
    print("  -> eclipse_blade.png")


def generate_magical_items():
    """Wands, staves, scepters, charms, guides."""
    # Wands
    img = render_template(WAND, palette_map(HEADS["stellar"], HANDLE_GOLD))
    img.save(OUT_DIR / "stargazer_wand.png")
    print("  -> stargazer_wand.png")

    img = render_template(WAND, palette_map(HEADS["meteorite"]))
    img.save(OUT_DIR / "meteor_wand.png")
    print("  -> meteor_wand.png")

    img = render_template(WAND, palette_map(HEADS["lunar"]))
    img.save(OUT_DIR / "moonlight_wand.png")
    print("  -> moonlight_wand.png")

    img = render_template(WAND, palette_map(HEADS["silver"]))
    img.save(OUT_DIR / "linking_wand.png")
    print("  -> linking_wand.png")

    # Staves
    img = render_template(STAFF, palette_map(HEADS["lunar"]))
    img.save(OUT_DIR / "lunar_healing_staff.png")
    print("  -> lunar_healing_staff.png")

    # Scepter
    img = render_template(SCEPTER, palette_map(HEADS["stellar"], HANDLE_GOLD))
    img.save(OUT_DIR / "stellar_scepter.png")
    print("  -> stellar_scepter.png")

    # Compass
    img = render_template(COMPASS, palette_map(HEADS["lunar"]))
    img.save(OUT_DIR / "lunar_compass.png")
    print("  -> lunar_compass.png")

    # Guide book
    img = render_template(GUIDE, palette_map(HEADS["void"], HANDLE_DARK))
    img.save(OUT_DIR / "aster_guide.png")
    print("  -> aster_guide.png")

    # Charm
    img = render_template(CHARM, palette_map(HEADS["stellar"], HANDLE_GOLD))
    img.save(OUT_DIR / "celestial_charm.png")
    print("  -> celestial_charm.png")


if __name__ == "__main__":
    print("Generating basic tools...")
    generate_basic_tools()
    print("\nGenerating weapons...")
    generate_weapons()
    print("\nGenerating magical items...")
    generate_magical_items()
    print(f"\nDone. Tools written to {OUT_DIR}")
