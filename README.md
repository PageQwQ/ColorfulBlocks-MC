# ColorfulBlocks

![logo](src/main/resources/logo.png)

**ColorfulBlocks** is a Minecraft mod that lets you color blocks in **16,777,216 (24-bit RGB) colors** using a Paint Bucket tool. Right-click any vanilla concrete block to turn it into a fully customizable colored block, and use the color picker to create any shade you want.

The idea for this mod comes from [PlatinPython's RGBBlocks](https://github.com/PlatinPython/RGBBlocks). This version is built for the **Fabric Loader**.

---

## Features

- **🎨 Full RGB Color**: Choose from over 16 million colors using the color picker GUI (RGB sliders, HSB sliders, or hex input).
- **🪣 Paint Bucket**: Right-click on concrete to paint it. Shift + right-click on a painted block to copy its color. Shift + right-click in air to open the color picker.
- **🔨 Tool Support**: Only pickaxes can mine these blocks. Tool tier and Efficiency enchantment are respected.
- **🎭 Color Retention**: Color data is preserved when mined and when placed.
- **✨ ModernUI Integration**: When ModernUI is loaded, tooltip borders match the item's color automatically.
- **🧩 Lightweight**: Only adds one block — **Stained Concrete** — and one item — **Paint Bucket**.

---

## Items & Blocks

| Item / Block | Description |
|---|---|
| **Paint Bucket** | Dye bucket with 500 durability. Open color picker (Shift + right-click air), paint concrete (right-click), or copy color (Shift + right-click block). |
| **Stained Concrete** | Concrete block that displays any RGB color. Drops with color data intact. |

---

## Recipe

### Paint Bucket

| Ingredients | Recipe |
|---|---|
| Water Bucket + Red Dye + Green Dye + Blue Dye | ![Recipe](https://via.placeholder.com/16/FF0000/000000?text=+) Red + ![Recipe](https://via.placeholder.com/16/00FF00/000000?text=+) Green + ![Recipe](https://via.placeholder.com/16/0000FF/000000?text=+) Blue Dyes |

- Shapeless crafting.

---

## Usage

### Paint Bucket Controls

| Action | Result |
|---|---|
| **Right-click** vanilla concrete | Converts it to Stained Concrete in the bucket's current color. |
| **Right-click** Stained Concrete | Recolors the block. |
| **Shift + Right-click** Stained Concrete | Copies the block's color to the bucket. |
| **Shift + Right-click** air | Opens the color picker GUI. |
| **Right-click** with empty bucket (in dispenser) | Paints the block in front. |

### Color Picker

The color picker supports three input modes:

- **RGB Mode** — Red, Green, Blue sliders (0–255 each)
- **HSB Mode** — Hue (0–360°), Saturation & Brightness (0–100%)
- **Hex Input** — Direct hex color code (e.g. `#FF6B35`)

Toggle between RGB and HSB mode using the switch button.

---

## Requirements

| Dependency | Version |
|---|---|
| [Fabric Loader](https://fabricmc.net/) | `>=0.16.0` |
| [Fabric API](https://modrinth.com/mod/fabric-api) | `*` |
| Minecraft | `1.21.1` |
| Java | `>=21` |

### Optional

| Mod | Purpose |
|---|---|
| [ModernUI](https://github.com/BloCamLimb/ModernUI) | Enables color-matched tooltip borders. |

---

## Installation

1. Install **Fabric Loader** (follow instructions on [fabricmc.net](https://fabricmc.net/)).
2. Download **Fabric API** and place it in your `mods/` folder.
3. Download the latest **ColorfulBlocks** jar from the [Releases](https://github.com/PageQwQ/ColorfulBlocks-MC/releases) page and put it in your `mods/` folder.
4. Launch the game.

---

## Building from Source

```sh
git clone https://github.com/PageQwQ/ColorfulBlocks-MC.git
cd ColorfulBlocks-MC
./gradlew build
```

The built jar will be in `build/libs/`.

---

## License

This project is licensed under the **MIT License**. See [LICENSE.md](LICENSE.md) for details.

## Credits

- **PlatinPython** — Original RGBBlocks mod for NeoForge.
- **BloCamLimb** — ModernUI framework.
- **PageQwQ** — Fabric port and ongoing development.
