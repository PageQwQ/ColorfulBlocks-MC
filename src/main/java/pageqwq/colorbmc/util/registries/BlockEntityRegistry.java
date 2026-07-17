package pageqwq.colorbmc.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;

public class BlockEntityRegistry {
    public static final BlockEntityType<RGBBlockEntity> RGB = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "rgb"),
        BlockEntityType.Builder.of(RGBBlockEntity::new, BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0]))
            .build(null)
    );

    public static void register() {}
}
