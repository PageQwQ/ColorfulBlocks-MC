<div align="center"><center>

<img alt="Icon" width=200 src="DescriptionImages/logo.png">

## Colorful Blocks

[![Release](https://img.shields.io/github/v/release/PageQwQ/ColorfulBlocks-MC?style=flat)](https://github.com/PageQwQ/ColorfulBlocks-MC)
[![Available for Fabric](https://img.shields.io/badge/Available%20for-Fabric-dbd0b4?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAcBAMAAACNPbLgAAABhGlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9TpX5UHMwgIpihOtlFRRxLFYtgobQVWnUwufQLmjQkKS6OgmvBwY/FqoOLs64OroIg+AHi6uKk6CIl/i8ptIj14Lgf7+497t4BQr3MNKsrAmi6bSZjUSmTXZUCr+iHiADG0Cszy4inFtPoOL7u4ePrXZhndT735xhQcxYDfBJxhBmmTbxBPLtpG5z3iUVWlFXic+JJky5I/Mh1xeM3zgWXBZ4pmunkPLFILBXaWGljVjQ14hnikKrplC9kPFY5b3HWylXWvCd/YTCnr6S4TnMUMSwhjgQkKKiihDJshGnVSbGQpP1oB/+I60+QSyFXCYwcC6hAg+z6wf/gd7dWfnrKSwpGge4Xx/kYBwK7QKPmON/HjtM4AfzPwJXe8lfqwNwn6bWWFjoCBreBi+uWpuwBlzvA8JMhm7Ir+WkK+TzwfkbflAWGboG+Na+35j5OH4A0dbV8AxwcAhMFyl7v8O6e9t7+PdPs7wd+dXKrd9SjeQAAAAlwSFlzAAAuIwAALiMBeKU/dgAAAAd0SU1FB+cLFAcgIbOcUjoAAAAbUExURQAAAB0tQTg0KoB6bZqSfq6mlLyynMa8pdvQtJRJT6UAAAABdFJOUwBA5thmAAAAAWJLR0QB/wIt3gAAAF5JREFUGNN10FENwCAMhOFqOQuzMAtYOAtYqGw6mkEvhL59yR9Ca5YDqyOC465eKYqQm6LoCkVwnwQOBYKdeA5l51zhFtrsnPmg6m3Z2akk15dFH1lWFQVxlUFv+2sAJlA9O7NwQRQAAAAASUVORK5CYII=)](https://fabricmc.net)

[![get on modrinth](https://img.shields.io/badge/Get%20on-Modrinth-02AF5C?logo=modrinth)](https://modrinth.com/project/VuBMNaL4)

</center></div>

## Differences from the `main` (26.1.2) branch

The two branches are separate projects; a change to shared behavior usually has to be made twice. What
actually differs:

| | `main` (26.1.2) | this branch (1.21.1) |
|---|---|---|
| **Item tinting** | declarative: `assets/colorblockmc/items/*.json` declare a `minecraft:dye` tint, which reads the vanilla `minecraft:dyed_color` component | Java: `ColorProviderRegistry.ITEM` with `RGBBlockItemColor` + `PaintBucketItemColor`, both reading `colorblockmc:color` and OR-ing in `0xFF000000` |
| **`minecraft:dyed_color`** | written by `PaintBucketSyncPayload` to feed the item-model tint | not used anywhere |
| **Block tint API** | `BlockTintSource` (`colorInWorld` / `color`) registered through `BlockColorRegistry` | `BlockColor` (`getColor(state, getter, pos, tintIndex)`) registered through `ColorProviderRegistry.BLOCK` |
| **Identifiers** | `Identifier` | `ResourceLocation` |
| **Block entity persistence** | `ValueOutput` / `ValueInput` | `CompoundTag` + `HolderLookup.Provider`, `BlockEntity.DataComponentInput` |
| **`Item.use`** | `InteractionResult` | `InteractionResultHolder<ItemStack>` |
| **Use animation** | `ItemUseAnimation.NONE` | `UseAnim.NONE` |
| **`Item.releaseUsing`** | returns `boolean` | returns `void` |
| **`appendHoverText`** | `Consumer<Component>` | `List<Component>` |
| **Dust particles** | `DustParticleOptions(int rgb, scale)` | `DustParticleOptions(Vector3f rgb, scale)` |
| **Jade sources** | `src/client/.../util/jade/` | `src/main/.../util/jade/` |
| **GameTests** | 4 (includes `useOnColorsConcrete`) | 3 |
| **Java / Loom / loader** | Java 25, Loom `1.17.21`, loader `>=0.19.3` | Java 21, Loom `1.7.4`, loader `>=0.16.9` |

This version predates the item-model-definition format, so `assets/colorblockmc/items/*.json` is never
read here — item tinting is done entirely by the Java `ItemColor` providers listed above, and the folder
has been removed from this branch rather than left as dead weight. The practical upside of the Java route
is that those providers read `colorblockmc:color` directly, so an item keeps its color however it was
obtained: a mined drop, `/givec` and creative all work without a second component.

## The design in one idea

Vanilla concrete has 16 colors because each color is a separate block. This mod has **one** block and
stores the color as **data**:

- `RGBBlockEntity` holds a single `int color`.
- A data component `colorblockmc:color` carries that value on the block *item* and on the block entity's
  implicit components, so color survives mining, dropping, and `copy_components` in the loot table.

Everything else — the paint bucket, the thrown bucket, the clean agent, the color picker, the Jade
overlay, the tooltip tint — is a consumer or producer of that one integer. Adding a color is never a
registration; it is a value.

**Colors are opaque ARGB** (`0xFFRRGGBB`) throughout, matching `util/Color` (`new Color(rgb)` ORs in
`0xFF`) and `Color.HSBtoRGB`. `-1` means "no color picked yet" and is the default on the item component.
See *Conventions and gotchas* before touching any code that compares or stores a color.

## Bootstrap

`RGBBlocks.onInitialize()` runs the registries in dependency order and then wires the two callbacks that
are not registry-driven:

| Order | Call | Why here |
|---|---|---|
| 1 | `DataComponentRegistry.register()` | components are referenced by every item/block built after this |
| 2 | `BlockRegistry.register()` | registering a block also registers its `RGBBlockItem` |
| 3 | `ItemRegistry.register()` | requires the block instances for `RGBBlockItem` |
| 4 | `BlockEntityRegistry.register()` | block entities reference the block set |
| 5 | `EntityRegistry.register()` | the thrown-bucket entity type |
| 6 | `CriterionRegistry.register()` | the `color_action` advancement trigger |
| 7 | `RecipeSerializerRegistry.register()` | both custom serializers |
| 8 | `CreativeTabRegistry.register()` | reads the accumulated `ALL_BLOCKS` / `ALL_ITEMS` lists |
| 9 | `NetworkHandler.register()` | payload type + serverbound receiver |
| 10 | `GiveColorCommand.register()` | `CommandRegistrationCallback` |
| 11 | `DispenserBlock.registerBehavior(...)` | dispenser behavior for the paint bucket |

## Registration style

There is **no `DeferredRegister`** and no registry events. Each `util/registries/*Registry` class holds
`public static final` fields initialized by a plain

```java
Registry.register(BuiltInRegistries.ITEM,
    ResourceLocation.fromNamespaceAndPath(MOD_ID, name), instance)
```

and exposes a no-op `register()` method whose only purpose is to force class initialization — the static
initializers do the work. Two consequences worth knowing:

- Registration happens when the class is first touched, so **the order in the table above is load-bearing**.
- `BlockRegistry.register()` calls `ItemRegistry.registerBlockItem()` itself; you never register a block
  item by hand, and every RGB block automatically gets an `RGBBlockItem`.

## Block color

`RGBBlockEntity` (`color`, `originalColor`) is the single source of truth for a placed block.

| Concern | Mechanism |
|---|---|
| Persistence | `saveAdditional(CompoundTag, HolderLookup.Provider)` / `loadAdditional(...)` → NBT key `color` |
| Client sync | `getUpdateTag` writes `color`, `getUpdatePacket` returns `ClientboundBlockEntityDataPacket.create(this)` |
| Re-render on arrival | `applyImplicitComponents` and `loadAdditional` call `level.sendBlockUpdated(...)` when `isClientSide()` |
| Implicit component | `collectImplicitComponents` exposes `colorblockmc:color`; `applyImplicitComponents(BlockEntity.DataComponentInput)` reads it back |
| Survives mining | `data/colorblockmc/loot_table/blocks/*.json` uses `minecraft:copy_components` with `source: block_entity` and `include: ["colorblockmc:color"]` |
| Map color | `mixin/BlockStateMapColorMixin` intercepts `BlockStateBase.getMapColor(level, pos)`; `util/MapColorResolver` picks the closest vanilla palette entry from the block entity color |

Maps cannot store arbitrary RGB. A map pixel is one byte: a 6-bit vanilla `MapColor` id plus 2 bits of
brightness, and the palette is fixed at 64 entries. A painted block therefore appears as the closest
palette color, not an exact match, and land pixels are further darkened by `Brightness.LOWEST`. Blocks
that were never painted keep the map color copied from white concrete. The mixin is needed because the
state's map color is baked into the `BlockState` at construction, so `getMapColor(level, pos)` otherwise
ignores its arguments.

`originalColor` is scratch state for the clean agent: it remembers the color a block had before washing
started, so the advancement can report `was_color`.

## Item color and tinting

Two components, both registered with `persistent(...)` + `networkSynchronized(...)`:

- `colorblockmc:color` (int) — the color itself, default `-1`.
- `colorblockmc:rgb_selected` (bool) — whether the player explicitly picked a color.

Tinting is done in Java, in `RGBBlocksClient.onInitializeClient()`:

```java
ColorProviderRegistry.BLOCK.register(new RGBBlockColor(), ALL_BLOCKS.toArray(new Block[0]));
ColorProviderRegistry.ITEM.register(new RGBBlockItemColor(), ALL_BLOCKS.toArray(new Block[0]));
ColorProviderRegistry.ITEM.register(new PaintBucketItemColor(), ItemRegistry.PAINT_BUCKET);
```

`RGBBlockItemColor` returns `stack.getOrDefault(COLOR, -1) | 0xFF000000` for any `RGBBlockItem`, so a block
keeps its color as an item however it was obtained — a mined drop, `/givec`, or creative.
`PaintBucketItemColor` answers only `tintIndex == 1`, which is the overlay layer of
`models/item/paint_bucket.json` (layer 0 is the untinted base).

## Rendering

| Where | Path |
|---|---|
| Block in world | `blockstates/*.json` → `models/block/concrete.json` (parent `models/block/block.json`, all faces `"tintindex": 0`) → tint resolved by `RGBBlockColor` |
| Block tint source | `RGBBlockColor implements BlockColor`; it looks up the block entity at the position (falling back to the block **below**) and returns its color, `-1` when there is none |
| Item tint | the Java `ItemColor` providers above |
| Texture | one static texture, `textures/block/concrete.png`, tinted per block |
| Thrown bucket entity renderer | `EntityRendererRegistry.register(EntityRegistry.THROWN_PAINT_BUCKET, ThrownItemRenderer::new)` |
## Interaction paths

### Paint bucket — `PaintBucketItem.useOn`

| Target | Result |
|---|---|
| Stained concrete, shift held | copies the block's color onto the bucket, no durability cost |
| Stained concrete | `applyColorToBlock` recolors; costs 1 durability **only if the color actually changes** |
| Vanilla concrete (the 16 vanilla variants, listed in `VANILLA_CONCRETE`) | replaced with `RGB_CONCRETE`, then the new block entity is colored; costs 1 durability |

At 1 durability remaining the bucket is replaced with an empty `Items.BUCKET`.

`applyColorToBlock` is the shared entry point and returns **false when nothing changed** — that boolean is
what the throw path counts.

### Thrown paint bucket — hold right-click to charge

`use()` starts the use animation, `releaseUsing` resolves it. All numbers live in
`entity/PaintBucketThrowLogic`:

| Constant | Value | Meaning |
|---|---|---|
| `MIN_THROW_TICKS` | 5 | shorter presses do nothing |
| `THROW_CHARGE_TICKS` | 20 | velocity is at full after 1 s |
| `MAX_CHARGE_TICKS` | 200 | radius is at maximum after 10 s |
| `MIN_RADIUS` / `MAX_RADIUS` | 2 → 13 | `radiusForCharge` lerps between them |
| `MIN_THROW_VELOCITY` / `MAX_THROW_VELOCITY` | 0.7 → 1.6 | |
| `MAX_COLORED_BLOCKS` | 500 | painting stops after this many blocks |
| `MAX_DURABILITY_COST` | 500 | |

`applyRadiusPaint` walks a cube of side `2·ceil(radius)+1`, skips positions outside world bounds / not
loaded / beyond `radiusSqr`, and counts the blocks `applyColorToBlock` actually changed. Cost is
`min(count × 1.5, 500, remaining durability)`; at zero remaining the drop becomes an empty bucket. The
thrown entity is `ThrownPaintBucket extends ThrowableItemProjectile`, which trails dust particles on the
client and resolves paint + drop + effects on impact. Throwing consumes the stack (`stack.shrink(1)`);
the drop is created at the impact point.

### Clean agent — `CleanAgentItem.useOn`

Moves each channel 25 % of the way toward 255 (`r + round((255 - r) * 0.25)`), snapping to pure white once
all three channels reach ≥ 250, and stores the pre-wash color in `originalColor` on the first use.
Durability 120, one point per use, no cost once already white.

### Dispenser — `DispensePaintBucketBehaviour`

Paints the block entity directly in front of the dispenser and consumes one durability, returning an empty
bucket when exhausted. It only handles `RGBBlockEntity` in front; anything else falls through to the
default behavior.

### `/givec` — `GiveColorCommand`

`/givec <hex> [count] [glowing]`, gamemaster permission. Accepts 6 hex digits with or without `#`, and
hands out `RGB_CONCRETE` or `RGB_GLOWING_CONCRETE` through `GiveColorCommand.applyColor`, which stores the
color as **opaque ARGB** in `colorblockmc:color`. If the inventory is full, the stack is dropped at the
player.

## Networking

One C2S payload, `paint_bucket_sync` (`PaintBucketSyncPayload`), sent when the color picker closes:

- Registered in `NetworkHandler` via `PayloadTypeRegistry.serverboundPlay()` plus
  `ServerPlayNetworking.registerGlobalReceiver(...)`.
- The handler runs on the server thread and **normalizes the incoming color to opaque ARGB**
  (`0xFF000000 | (c & 0xFFFFFF)`) before writing it into the item. The client chooses a color; it does not
  get to write an arbitrary integer. Legitimate senders only ever produce `0xFFRRGGBB` or `-1`, so this is
  a no-op for them.
- Unlike `main`, nothing here has to keep a `minecraft:dyed_color` component in step, because item tinting
  reads `colorblockmc:color` directly.

## Client-only GUI

The main source set never touches client classes. `RGBBlocks` calls through a tiny indirection instead:

- `util/ClientPlayHelper` — interface with `openColorSelectScreen(...)`.
- `util/ClientProxy` — static field holding the implementation.
- `client/ClientPlayHelperImpl` — assigns itself in `RGBBlocksClient.onInitializeClient()` and calls
  `Minecraft.getInstance().setScreen(new ColorSelectScreen(...))`.

`ColorSelectScreen` is the picker: `ColorSlider` (RGB), `HueStripWidget` + `SquareColorPicker` +
`HexInputWidget` (HSB/hex), `ConcretePreviewWidget` (live preview), `client/util/ColorNames` (localised
color name, read from `assets/colorblockmc/colornames/{en,zh}.json`), and `ScreenUtils` for gradient
fills. Closing it sends the sync payload.

## Advancements

`ColorActionTrigger` is a `SimpleCriterionTrigger` registered as `colorblockmc:color_action`. Its
`TriggerInstance` codec takes optional fields `player`, `type` (`dye` / `clean`), `color`, `was_color`,
`min_count`, `thrown` and `changed`, so the ten JSON advancements in `data/colorblockmc/advancement/` are
pure data — no Java per advancement. `colorMatches` additionally accepts the three primary colors as
*dominant-channel* matches, which is how "dye something green" is expressed.

## Optional integrations

Both are opt-in and fail safe.

- **Jade** — `util/jade/` (in the **main** source set on this branch) provides the plugin and the info/icon
  providers. It is registered under Jade's own `jade` entrypoint namespace, so nothing runs when Jade is
  absent, and it uses Jade's public API.
- **ModernUI** — `mixin/TooltipRendererMixin` (`@Pseudo`, `remap = false`) tints the tooltip stroke and
  fill to match the item. It reaches ModernUI's `mWorkStrokeColor` / `mUseSpectrum` / `sFillColor` fields
  and `Color.HSVToColor` reflectively, so it is version-bound by nature: the config sets
  `"defaultRequire": 0` and the injection carries `require = 0`, which makes a missed injection mean "no
  tint" rather than an exception thrown into ModernUI's render loop. The reflected members are resolved
  once and latched on failure (one log line per session, not per frame), and the **static** `sFillColor`
  array is snapshotted and restored so its colors cannot leak into later tooltips.

## Conventions and gotchas

Things that are easy to get wrong, collected in one place:

1. **Never strip the alpha byte from a color.** Compare and store `0xFFRRGGBB`. `PaintBucketItem.useOn`
   and `ShapelessDurabilityAwarePaintBucketRecipe` compare the raw component against the block entity's
   color, so a `0x00RRGGBB` value compares unequal to the `0xFFRRGGBB` it was painted from and a
   repainted block silently starts draining durability. `PaintBucketSyncPayload.handle` therefore
   *normalizes to* `0xFF000000 | (c & 0xFFFFFF)` rather than masking the alpha off, and the Java
   `ItemColor` providers OR the same bit in before returning.
2. **`/givec` must hand out opaque ARGB.** It previously stored a bare 24-bit value
   (`Integer.parseInt(hex, 16)` with no alpha). Item tinting was unaffected — the color provider ORs
   `0xFF` in — but the raw component compared unequal to the `0xFFRRGGBB` a block entity stores, so a
   stack from the command was not recognized as "already that color" by `useOn`. `parseHex` now ORs
   `0xFF000000`, and `givecProducesOpaqueArgb` pins it.
3. **Item tint on this branch is code, not data.** Adding a tinted item means registering an `ItemColor`
   with `ColorProviderRegistry.ITEM`; there is no `assets/colorblockmc/items/` folder on this branch and
   adding one would do nothing (see *Differences* above).
4. **The mixin is deliberately soft.** `colorblockmc.mixins.json` sets `"defaultRequire": 0` and the
   injection carries `require = 0`. Raising either turns "ModernUI renamed a method" into a crash thrown
   from ModernUI's render loop.
5. **There is no access widener**, on purpose. `fabric.mod.json` no longer declares `accessWidener` and
   nothing uses `MapColor.MATERIAL_COLORS`; `validateAccessWidener NO-SOURCE` in the build log is expected.
6. **Nothing writes `gradle.properties`.** `mod_version` is bumped by hand, **in both branches at once**,
   in the release commit — one GitHub release carries both jars. (It used to auto-increment on every build,
   at configuration time, so failed and `--dry-run` builds consumed version numbers too.)
7. **The GameTest classes ship in the jar.** `fabric.mod.json` declares a `fabric-gametest` entrypoint
   pointing at them, so excluding them from `jar` would ship metadata naming a class that is not there.
8. **`build/libs` accumulates jars.** Always `./gradlew clean build` before a release: the documented
   release command uploads by glob, and two main jars matching `-fabric-*.jar` is a silent way to publish
   the wrong build.

## Tests

`gametest/ColorBlockThrowGameTest` is registered through the `fabric-gametest` entrypoint and runs on a real
dedicated server with a real level — it asserts on actual block states, block entities and item damage:

```
./gradlew runGameTest        # writes build/gametest-results.xml
```

Four tests cover charge→radius, radius painting (9 blocks converted and each one's color checked),
durability cost/drop resolution, and that `/givec` hands out opaque ARGB (parsing and the stack it
produces). `RGBMapColorGameTest` additionally checks the map hook directly, then builds a 16×16 painted
platform, runs the real `MapItem.update` against a fresh `MapItemSavedData`, and asserts the stored pixel
uses the resolved palette id. CI (`.github/workflows/build.yml`) runs `build` and `runGameTest` on every
push and fails if the report is missing or contains a `<failure>`.

Not covered: the ModernUI mixin, the GUI, client↔server sync, the dispenser, both recipes, `/givec`, Jade
and the advancement tree.

## Building

```sh
./gradlew build          # jar + sources jar → build/libs/
./gradlew runClient      # dev client
./gradlew runServer      # dev server
./gradlew runGameTest    # see Tests
```

Requirements: **JDK 21** and Fabric Loom `1.7.4` (pinned in `build.gradle`).

`mod_version` in `gradle.properties` is the single source of the version and **nothing writes it back** —
bump it by hand in the commit that prepares a release, **in both branches at once**, because one GitHub
release carries both jars. The jar name is
`Colorful Blocks-<mc>-<version>-fabric-<epoch seconds>.jar`.

## Repository layout

```
src/main/java/pageqwq/colorbmc/
  RGBBlocks.java              mod initializer
  block/                      RGBBlock, RGBGlowingBlock
  block/entity/               RGBBlockEntity — the color
  item/                       PaintBucketItem, RGBBlockItem, CleanAgentItem, crafting/
  entity/                     ThrownPaintBucket, PaintBucketThrowLogic
  dispenser/                  DispensePaintBucketBehaviour
  command/                    GiveColorCommand
  gametest/                   the GameTest suite
  mixin/                      ModernUI tooltip + BlockState map-color mixins
  util/                       Color, MapColorResolver, ClientProxy, ClientPlayHelper, network/, registries/, jade/
src/client/java/pageqwq/colorbmc/
  RGBBlocksClient.java        block/item color providers + entity renderer
  client/colorhandlers/       RGBBlockColor, RGBBlockItemColor, PaintBucketItemColor
  client/gui/                 ColorSelectScreen and its widgets
src/main/resources/
  assets/colorblockmc/        block models, textures, lang, colornames
  data/colorblockmc/          recipes, loot tables, advancements
```

## Credits

The idea comes from [PlatinPython's RGBBlocks](https://github.com/PlatinPython/RGBBlocks). This is an
independent Fabric implementation. Declared MIT in `fabric.mod.json`.
