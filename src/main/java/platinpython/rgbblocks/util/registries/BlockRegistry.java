package platinpython.rgbblocks.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.block.RGBBlock;
import platinpython.rgbblocks.block.RGBConcretePowderBlock;
import platinpython.rgbblocks.block.RGBSlabBlock;
import platinpython.rgbblocks.block.RGBStairsBlock;
import platinpython.rgbblocks.item.RGBBlockItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    public static final List<Block> ALL_BLOCKS = new ArrayList<>();

    public static final Block RGB_CONCRETE = register(
        "concrete", new RGBBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE))
    );
    public static final Block RGB_CONCRETE_SLAB = register(
        "concrete_slab", new RGBSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE))
    );
    public static final Block RGB_CONCRETE_STAIRS = register(
        "concrete_stairs", new RGBStairsBlock(RGB_CONCRETE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE))
    );
    public static final Block RGB_CONCRETE_POWDER = register(
        "concrete_powder", new RGBConcretePowderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE_POWDER))
    );

    private static Block register(String name, Block block) {
        Block registered = Registry.register(
            BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            block
        );
        ALL_BLOCKS.add(registered);
        ItemRegistry.registerBlockItem(name, registered);
        return registered;
    }

    public static void register() {}
}
