# 包结构与重构映射

本文件是包名/目录调整的唯一依据。两个版本分支都必须按同一张表迁移；表中标 `冻结` 的条目要等对应功能开发完成后再动。

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
paint/                      取色界面与控件、ClientPlayHelperImpl
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
5. **WIP 冻结区最后迁移。** 见下节。

## WIP 冻结区（本轮重构不碰）

以下文件对应的功能仍在开发、接口可能变，**不参与前几轮搬迁**，等功能稳定后单独提交：

- 26.1.2 分支
  - `block/ConcreteReplicatorBlock.java`
  - `block/entity/ConcreteReplicatorBlockEntity.java`
  - `client/render/ConcreteReplicatorRenderer.java`
  - `client/render/ReplicatorRenderState.java`
  - `block/RGBGlassBlock.java`、`client/mixin/ContinuityGlassConnectionMixin.java`、`resourcepacks/ctm/**`
- 1.21.1 分支
  - `block/ConcreteReplicatorBlock.java`
  - `block/entity/ConcreteReplicatorBlockEntity.java`
  - `client/render/ConcreteReplicatorRenderer.java`
  - `block/RGBGlassBlock.java`、`client/compat/continuity/**`、`resourcepacks/ctm/**`

移动这些文件时，同时检查 `colorblockmc.mixins.json` 与 `fabric.mod.json`。

## 路径映射表

| 旧路径 | 新路径 | 状态 |
|---|---|---|
| `util/registries/*.java` | `registry/*.java` | 已完成 |
| `util/Color.java` | `color/Color.java` | 已完成 |
| `util/ColoredItems.java` | `color/ColoredItems.java` | 已完成（仅 26.1.2） |
| `util/MapColorResolver.java` | `color/MapColorResolver.java` | 已完成 |
| `util/network/NetworkHandler.java` | `network/NetworkHandler.java` | 待办 |
| `util/network/packets/PaintBucketSyncPayload.java` | `paint/PaintBucketSyncPayload.java` | 待办 |
| `util/ClientProxy.java`、`util/ClientPlayHelper.java` | `paint/` | 待办 |
| `item/PaintBucketItem.java`、`item/CleanAgentItem.java` | `paint/` | 待办 |
| `entity/ThrownPaintBucket.java`、`entity/PaintBucketThrowLogic.java` | `paint/` | 待办 |
| `dispenser/DispensePaintBucketBehaviour.java` | `paint/` | 待办 |
| `item/crafting/*.java` | `paint/crafting/` | 待办 |
| `block/RGBBlock.java`、`RGBGlowingBlock.java`、`RGBGlassBlock.java` | `rgb/` | 待办（玻璃冻结） |
| `block/entity/RGBBlockEntity.java` | `rgb/` | 待办 |
| `item/RGBBlockItem.java` | `rgb/` | 待办 |
| `block/ConcreteWorkshopBlock.java`、`block/entity/ConcreteWorkshopBlockEntity.java` | `machine/workshop/` | 待办 |
| `block/ConcreteReplicatorBlock.java`、`block/entity/ConcreteReplicatorBlockEntity.java` | `machine/replicator/` | 冻结 |
| `util/jade/*.java` | `compat/jade/*.java` | 待办（26.1.2 在 client 源集，1.21.1 在 main 源集） |
| `client/colorhandlers/*.java` | `client/color/` | 待办 |
| `client/util/ColorNames.java` | `client/color/ColorNames.java` | 待办 |
| `client/gui/screen/ColorSelectScreen.java`、`client/gui/widget/*`、`client/gui/ScreenUtils.java` | `client/paint/` | 待办 |
| `client/ClientPlayHelperImpl.java` | `client/paint/ClientPlayHelperImpl.java` | 待办 |
| `client/render/ConcreteWorkshop*、WorkshopRenderState` | `client/machine/workshop/` | 待办 |
| `client/render/ConcreteReplicator*、ReplicatorRenderState` | `client/machine/replicator/` | 冻结 |
| `client/autotest/AutoTest.java` | `client/dev/AutoTest.java` | 待办 |

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
