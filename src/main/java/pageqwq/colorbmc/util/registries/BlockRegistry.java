package pageqwq.colorbmc.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.block.RGBBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockRegistry {
    public static final List<Block> ALL_BLOCKS = new ArrayList<>();

    public static final Block RGB_CONCRETE = register(
        "concrete", new RGBBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CONCRETE).requiresCorrectToolForDrops())
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
