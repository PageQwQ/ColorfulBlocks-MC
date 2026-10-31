package pageqwq.colorbmc.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.block.ConcreteWorkshopBlock;
import pageqwq.colorbmc.block.RGBBlock;
import pageqwq.colorbmc.block.RGBGlowingBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    public static final List<Block> ALL_BLOCKS = new ArrayList<>();
    /** 机器类方块：没有 RGB 染色组件，不能混进 ALL_BLOCKS（那会被算进 RGB 的 BlockEntityType）。 */
    public static final List<Block> ALL_MACHINES = new ArrayList<>();

    public static final Block RGB_CONCRETE = register(
        "concrete", new RGBBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "concrete"))))
    );

    public static final Block RGB_GLOWING_CONCRETE = register(
        "glowing_concrete", new RGBGlowingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)
            .lightLevel($ -> 15)
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "glowing_concrete"))))
    );

    /** 混凝土车间：不带 RGB 染色的普通方块，单独维护列表，不进入 RGB 的 BlockEntityType。 */
    public static final Block CONCRETE_WORKSHOP = registerPlain(
        "concrete_workshop", new ConcreteWorkshopBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "concrete_workshop"))))
    );

    private static Block registerPlain(String name, Block block) {
        Block registered = Registry.register(
            BuiltInRegistries.BLOCK,
            Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            block
        );
        ALL_MACHINES.add(registered);
        ItemRegistry.registerBlockItemPlain(name, registered);
        return registered;
    }

    private static Block register(String name, Block block) {
        Block registered = Registry.register(
            BuiltInRegistries.BLOCK,
            Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            block
        );
        ALL_BLOCKS.add(registered);
        ItemRegistry.registerBlockItem(name, registered);
        return registered;
    }

    public static void register() {}
}
