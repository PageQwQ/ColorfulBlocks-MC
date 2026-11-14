# 包结构与重构映射

本文件是包名/目录调整的唯一依据。两个版本分支都必须按同一张表迁移。未提交的 WIP 不迁移：先提交，再搬目录。

## 目标结构

`src/main/java/pageqwq/colorbmc/`

```
RGBBlocks.java              入口：只做注册、网络、命令的装配
color/                      颜色模型与数据，不含 GUI
  Color.java                纯 Java 颜色换算
  ColoredItems.java         颜色组件写入的唯一入口（仅 26.1.2）
  MapColorResolver.java     原版地图颜色解析
rgb/                        RGB 方块家族（混凝土 / 发光 / 玻璃 + BlockEntity + BlockItem）
paint/                      油漆桶完整功能（物品、投掷、发射器、配方、网络包、客户端桥）
machine/workshop/           混凝土工作台：Block + BlockEntity
machine/replicator/         混凝土复制器：Block + BlockEntity（WIP）
advancement/                进度触发：CriterionRegistry + ColorActionTrigger
command/                    命令
network/                    只放通用传输（NetworkHandler）
registry/                   Minecraft 内容注册
compat/jade/                Jade 适配
mixin/                      Mixin（包名保持不变）
gametest/                   GameTest
```

`src/client/java/pageqwq/colorbmc/`

```
RGBBlocksClient.java        客户端入口
color/                      颜色名、方块/物品着色 provider
paint/                      取色界面、ScreenUtils、ClientPlayHelperImpl
paint/widget/               取色控件（滑块、色块、hex 输入等）
machine/workshop/           Renderer + RenderState
machine/replicator/         Renderer + RenderState（WIP）
compat/continuity/          Continuity 适配（仅 1.21.1）
mixin/                      Mixin（包名保持不变）
dev/                        仅开发环境代码（AutoTest）
```

## 五条规则

1. **同一层只用一种分类轴。** 顶层要么全是功能（`rgb` / `paint` / `machine`），要么全是类型（`block` / `item` / `entity`），不要混。
2. **删除测试。** 删掉 `compat/continuity/` 或 `machine/replicator/` 时，编译器只应报同一个目录里的错。删一个功能要改五个地方，说明聚合没做到。
3. **第三方模组适配只允许出现在 `compat/<modid>/`。** 不再区分 integration / compat；Jade 和 Continuity 都是第三方模组。
4. **mixin 类不换包。** 这样 `colorblockmc.mixins.json` 不需要改；如果要改，必须同时改配置里的 `package` / 类名。
5. **未提交的 WIP 不迁移。** 先让功能落在 commit 里，再改目录；已经提交的 WIP 代码可以搬，但只搬位置、不改逻辑。

## WIP 说明

复制器、玻璃 CTM 这两个功能仍在开发，但相关 Java 类已经随第 4 步迁到目标包：

- 复制器：`machine/replicator/`、client `machine/replicator/`
- 玻璃：`rgb/RGBGlassBlock.java`
- Continuity 适配仍留在原地（26.1.2 的 client mixin / 1.21.1 的 `client/compat/continuity/`），因为它不是在搬包范围内

迁移只改包名和 import，没有改任何逻辑。**后续开发直接在新路径上改**；再新增 WIP 类时，先提交再决定放哪个功能包，不要先搬未提交的文件。

## 路径映射表

| 旧路径 | 新路径 | 状态 |
|---|---|---|
| `util/registries/*.java`（其余 7 个） | `registry/*.java` | 已完成 |
| `util/registries/CriterionRegistry.java` | `advancement/CriterionRegistry.java` | 已完成 |
| `util/registries/ColorActionTrigger.java` | `advancement/ColorActionTrigger.java` | 已完成 |
| `util/Color.java` | `color/Color.java` | 已完成 |
| `util/ColoredItems.java` | `color/ColoredItems.java` | 已完成（仅 26.1.2） |
| `util/MapColorResolver.java` | `color/MapColorResolver.java` | 已完成 |
| `util/network/NetworkHandler.java` | `network/NetworkHandler.java` | 已完成 |
| `util/network/packets/PaintBucketSyncPayload.java` | `paint/PaintBucketSyncPayload.java` | 已完成 |
| `util/ClientProxy.java`、`util/ClientPlayHelper.java` | `paint/` | 已完成 |
| `item/PaintBucketItem.java`、`item/CleanAgentItem.java` | `paint/` | 已完成 |
| `entity/ThrownPaintBucket.java`、`entity/PaintBucketThrowLogic.java` | `paint/` | 已完成 |
| `dispenser/DispensePaintBucketBehaviour.java` | `paint/` | 已完成 |
| `item/crafting/*.java` | `paint/crafting/` | 已完成 |
| `block/RGBBlock.java`、`RGBGlowingBlock.java`、`RGBGlassBlock.java` | `rgb/` | 已完成 |
| `block/entity/RGBBlockEntity.java` | `rgb/` | 已完成 |
| `item/RGBBlockItem.java` | `rgb/` | 已完成 |
| `block/ConcreteWorkshopBlock.java`、`block/entity/ConcreteWorkshopBlockEntity.java` | `machine/workshop/` | 已完成 |
| `block/ConcreteReplicatorBlock.java`、`block/entity/ConcreteReplicatorBlockEntity.java` | `machine/replicator/` | 已完成（WIP 类，仅移动） |
| `util/jade/*.java` | `compat/jade/*.java` | 已完成（26.1.2 在 client 源集，1.21.1 在 main 源集） |
| `client/colorhandlers/*.java` | `client/color/` | 已完成 |
| `client/util/ColorNames.java` | `client/color/ColorNames.java` | 已完成 |
| `client/gui/screen/ColorSelectScreen.java`、`client/gui/widget/*`、`client/gui/ScreenUtils.java` | `client/paint/`、`client/paint/widget/` | 已完成 |
| `client/ClientPlayHelperImpl.java` | `client/paint/ClientPlayHelperImpl.java` | 已完成 |
| `client/render/ConcreteWorkshop*、WorkshopRenderState` | `client/machine/workshop/` | 已完成 |
| `client/render/ConcreteReplicator*、ReplicatorRenderState` | `client/machine/replicator/` | 已完成（WIP 类，仅移动） |
| `client/autotest/AutoTest.java` | `client/dev/AutoTest.java` | 已完成 |

移动 `util/jade` 时必须同步改 `fabric.mod.json` 的 `jade` entrypoint。
移动 `gametest` 包时必须同步改 `fabric-gametest` entrypoint。

## 两分支差异（迁移时按分支落地）

| 内容 | 26.1.2（main） | 1.21.1 |
|---|---|---|
| Jade 源码集 | client | main |
| `ColoredItems` | 有 | 无 |
| `RGBBlockItemColor` / `PaintBucketItemColor` | 无（模型 dye tint） | 有 |
| `WorkshopRenderState` / `ReplicatorRenderState` | 有 | 无 |
| Continuity | client mixin | client `compat/continuity` |

## 验收

每步之后：

```bash
./gradlew compileJava compileClientJava
```

全部搬完后：

```bash
printf 'eula=true\n' > run/eula.txt
./gradlew build runGameTest
```
