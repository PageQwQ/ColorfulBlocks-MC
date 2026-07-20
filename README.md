<center><img src="src/main/resources/logo.png" alt="logo" style="zoom:25%;" /></center>

# ColorfulBlocks

**ColorfulBlocks** is a Minecraft mod that lets you color blocks in **16,777,216 (24-bit RGB) colors** using a Paint Bucket tool. Right-click any vanilla concrete block to turn it into a fully customizable colored block, and use the color picker to create any shade you want.

The idea for this mod comes from [PlatinPython's RGBBlocks](https://github.com/PlatinPython/RGBBlocks). This version is built for the **Fabric Loader**.

## Features

- **Full RGB Color**: Choose from over 16 million colors using the color picker GUI (RGB sliders, HSB sliders, or hex input).
- **Paint Bucket**: Right-click on concrete to paint it. Shift + right-click on a painted block to copy its color. Shift + right-click in air to open the color picker.
- **Color Retention**: Color data is preserved when mined.

## Items & Blocks

<center><img src="DescriptionImages/colorblockmc__paint_bucket.png" alt="logo" style="zoom:50%;" /></center>

<center><b>Paint Bucket</b></center>

<img src="DescriptionImages/craft.png" alt="logo" style="zoom:50%;" />

The bucket with 500 durability. Open color picker, paint concrete (right-click), or copy color (Shift + right-click block).

> [!note]
>
> The first time you right-click on the concrete, it won't apply color directly; you can apply color by right-clicking again.

<center><img src="DescriptionImages/colorblockmc__concrete.png" alt="logo" style="zoom:50%;" /></center>

<center><b>Stained Concrete</b></center>

Concrete block that displays any RGB color. Drops with color data intact.

This block is created by converting vanilla concrete using the Paint Bucket.

<center><img src="DescriptionImages/colorblockmc__glowing_concrete.png" alt="logo" style="zoom:50%;" /></center>

<center><b>Glowing Stained Concrete</b></center>

<img src="DescriptionImages/craft2.png" alt="logo" style="zoom:50%;" />

As the name suggests, it's a glowing stained concrete.

This is a variant of common stained concrete. It can help you build glowing things.

*I suggest you enable Screenspace Colored Blocklight in your shaderpack. This can achieve a better visual effect.*

The following is the visual effect of the block at night.

<img src="DescriptionImages/example.png" alt="logo" style="zoom:75%;" />

## ColorPicker GUI

Paint Bucket has a GUI.

When you press Shift + Right Click and aim at something other than Stained Concrete,you will see it:

<img src="DescriptionImages/GUI.png" alt="logo" style="zoom:50%;" />

The color picker supports three input modes:

- **RGB Mode** — Red, Green, Blue sliders (0–255 each)

- **HSB Mode** — Hue (0–360°), Saturation & Brightness (0–100%)

- **Hex Input** — Direct hex color code (e.g. #FF6B35)

Toggle between RGB and HSB mode using the switch button.

## Paint Bucket Controls

| Action | Result |
|---|---|
| **Right-click** vanilla concrete | Converts it to Stained Concrete in the bucket's current color. |
| **Right-click** Stained Concrete | Recolors the block. |
| **Shift + Right-click** Stained Concrete | Copies the block's color to the bucket. |
| **Shift + Right-click** something other than Stained Concrete | Opens the color picker GUI. |
| **Right-click** with empty bucket (in dispenser) | Paints the block in front. |

## Additional support

When you install this mod along with [ModernUI](https://modrinth.com/mod/modern-ui), the tooltip box will match the color of the stained concrete or bucket.

<img src="DescriptionImages/additional.png" alt="logo" style="zoom:75%;" />

## Requirements

| Dependency | Version |
|---|---|
| [Fabric Loader](https://fabricmc.net/) | `>=0.16.0` |
| [Fabric API](https://modrinth.com/mod/fabric-api) | `*` |
| Minecraft | `1.21.1` |
| Java | `>=21` |

## Installation

1. Install **Fabric Loader** (follow instructions on [fabricmc.net](https://fabricmc.net/)).
2. Download **Fabric API** and place it in your `mods/` folder.
3. Download the latest **ColorfulBlocks** jar from the [Releases](https://github.com/PageQwQ/ColorfulBlocks-MC/releases) page and put it in your `mods/` folder.
4. Launch the game.

## Building from Source

```sh
git clone https://github.com/PageQwQ/ColorfulBlocks-MC.git
cd ColorfulBlocks-MC
./gradlew build
```

The built jar will be in `build/libs/`.

