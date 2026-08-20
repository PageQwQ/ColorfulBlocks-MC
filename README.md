<div align="center"><center>

<img alt="Icon" width=200 src="DescriptionImages/logo.png">

## Colorful Blocks

[![Release](https://img.shields.io/github/v/release/PageQwQ/ColorfulBlocks-MC?style=flat)](https://github.com/PageQwQ/ColorfulBlocks-MC)
[![Available for Fabric](https://img.shields.io/badge/Available%20for-Fabric-dbd0b4?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAcBAMAAACNPbLgAAABhGlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9TpX5UHMwgIpihOtlFRRxLFYtgobQVWnUwufQLmjQkKS6OgmvBwY/FqoOLs64OroIg+AHi6uKk6CIl/i8ptIj14Lgf7+497t4BQr3MNKsrAmi6bSZjUSmTXZUCr+iHiADG0Cszy4inFtPoOL7u4ePrXZhndT735xhQcxYDfBJxhBmmTbxBPLtpG5z3iUVWlFXic+JJky5I/Mh1xeM3zgWXBZ4pmunkPLFILBXaWGljVjQ14hnikKrplC9kPFY5b3HWylXWvCd/YTCnr6S4TnMUMSwhjgQkKKiihDJshGnVSbGQpP1oB/+I60+QSyFXCYwcC6hAg+z6wf/gd7dWfnrKSwpGge4Xx/kYBwK7QKPmON/HjtM4AfzPwJXe8lfqwNwn6bWWFjoCBreBi+uWpuwBlzvA8JMhm7Ir+WkK+TzwfkbflAWGboG+Na+35j5OH4A0dbV8AxwcAhMFyl7v8O6e9t7+PdPs7wd+dXKrd9SjeQAAAAlwSFlzAAAuIwAALiMBeKU/dgAAAAd0SU1FB+cLFAcgIbOcUjoAAAAbUExURQAAAB0tQTg0KoB6bZqSfq6mlLyynMa8pdvQtJRJT6UAAAABdFJOUwBA5thmAAAAAWJLR0QB/wIt3gAAAF5JREFUGNN10FENwCAMhOFqOQuzMAtYOAtYqGw6mkEvhL59yR9Ca5YDqyOC465eKYqQm6LoCkVwnwQOBYKdeA5l51zhFtrsnPmg6m3Z2akk15dFH1lWFQVxlUFv+2sAJlA9O7NwQRQAAAAASUVORK5CYII=)](https://fabricmc.net)

[![get on modrinth](https://img.shields.io/badge/Get%20on-Modrinth-02AF5C?logo=modrinth)](https://modrinth.com/project/VuBMNaL4)

**ColorfulBlocks** is a Minecraft mod that lets you color blocks in **16,777,216 (24-bit RGB) colors** using a Paint Bucket tool. Right-click any vanilla concrete block to turn it into a fully customizable colored block, and use the color picker to create any shade you want.

The idea for this mod comes from [PlatinPython's RGBBlocks](https://github.com/PlatinPython/RGBBlocks). This version is built for the **Fabric Loader**.

</center></div>

## Features

- **Full RGB Color**: Choose from over 16 million colors using the color picker GUI (RGB sliders, HSB sliders, or hex input).
- **Paint Bucket**: Right-click on concrete to paint it. Shift + right-click on a painted block to copy its color. Shift + right-click in air to open the color picker.
- **Color Retention**: Color data is preserved when mined.
- **`/givec` Command**: Give yourself a stained concrete block of any hex color directly (e.g. `/givec #FF6B35`). Requires cheats/operator permission.

## Items & Blocks

<div align="center"><center>

<img alt="paint_bucket" width=64 src="DescriptionImages/colorblockmc__paint_bucket.png">   
<img alt="craft" height=64 src="DescriptionImages/craft.png">

### Paint Bucket

</center></div>

The bucket with 500 durability. Open color picker, paint concrete (right-click), or copy color (Shift + right-click block).

> [!note]
>
> The first time you right-click on the concrete, it won't apply color directly; you can apply color by right-clicking again.

<div align="center"><center>

<img alt="clean_agent" width=64 src="DescriptionImages/clean_agent.png">   
<img alt="craft" height=64 src="DescriptionImages/craft3.png">

### Clean Agent

</center></div>

Right-click on the Stained concrete to use. Shift the color 25% closer to white each time. *The darker the color, the more clicks are required.*

It has 120 Durability; consumes 1 point per use. No further consumption occurs after turning completely white. 

<div align="center"><center>

<img alt="stained_concrete" width=64 src="DescriptionImages/colorblockmc__concrete.png">   

### Stained Concrete

</center></div>

Concrete block that displays any RGB color. Drops with color data intact.

This block is created by converting vanilla concrete using the Paint Bucket.

<div align="center"><center>

<img alt="glowing_stained_concrete" width=64 src="DescriptionImages/colorblockmc__glowing_concrete.png">   
<img alt="craft" height=64 src="DescriptionImages/craft2.png">

### Glowing Stained Concrete

</center></div>

As the name suggests, it's a glowing stained concrete.

This is a variant of common stained concrete. It can help you build glowing things.

> [!tip]
>
> you can enable Screenspace Colored Blocklight in your shaderpack. This can achieve a better visual effect.

<img alt="example" width=500 src="DescriptionImages/example.png">

## GUI

Paint Bucket has a GUI.

When you press Shift + Right Click and aim at something other than Stained Concrete,you will see it:

<img alt="GUI" width=500 src="DescriptionImages/GUI.png">

This interface displays a color alongside its appearance on dyed concrete; it shows the color name based on the entered HEX value and allows for color adjustment using HSB sliders and a square color picker.

## Paint Bucket Controls

| Action | Result |
|---|---|
| **Right-click** vanilla concrete | Converts it to Stained Concrete in the bucket's current color. |
| **Right-click** Stained Concrete | Recolors the block. |
| **Shift + Right-click** Stained Concrete | Copies the block's color to the bucket. |
| **Shift + Right-click** something other than Stained Concrete | Opens the color picker GUI. |
| **Right-click** with empty bucket (in dispenser) | Paints the block in front. |

## `/givec` Command

Give yourself a stained concrete block of any color directly, no Paint Bucket needed. Requires **cheats / operator permission (level 2)**.

| Command | Result |
|---|---|
| `/givec <color>` | Gives 1 Stained Concrete of that color. |
| `/givec <color> <count>` | Gives the specified amount. |
| `/givec <color> <count> <glowing>` | Set `<glowing>` to `true` for Glowing Stained Concrete. |

`<color>` is a hex color code, with or without the leading `#` (e.g. `#FF6B35` or `FF6B35`).

Example: `/givec #FF6B35 16 true` gives you 16 Glowing Stained Concrete blocks colored `#FF6B35`.

## Additional support

When you install this mod along with [ModernUI](https://modrinth.com/mod/modern-ui), the tooltip box will match the color of the stained concrete or bucket.

<img src="DescriptionImages/additional.png" />
