package pageqwq.colorbmc.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.machine.replicator.ConcreteReplicatorBlock;
import pageqwq.colorbmc.machine.workshop.ConcreteWorkshopBlock;
import pageqwq.colorbmc.rgb.RGBBlock;
import pageqwq.colorbmc.rgb.RGBGlassBlock;
import pageqwq.colorbmc.rgb.RGBGlowingBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    public static final List<Block> ALL_BLOCKS = new ArrayList<>();
    /** Machine blocks: no RGB component, so keep them out of the RGB block entity type. */
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

    public static final Block RGB_GLASS = register(
        "stained_glass", new RGBGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "stained_glass"))))
    );

    /** Concrete workshop: plain block, tracked separately from the RGB blocks. */
    public static final Block CONCRETE_WORKSHOP = registerPlain(
        "concrete_workshop", new ConcreteWorkshopBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "concrete_workshop"))))
    );

    /** Item replicator (prototype). */
    public static final Block CONCRETE_REPLICATOR = registerPlain(
        "concrete_replicator", new ConcreteReplicatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "concrete_replicator"))))
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
