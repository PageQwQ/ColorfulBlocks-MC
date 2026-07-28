package pageqwq.colorbmc.util.registries;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;

public class BlockEntityRegistry {
    public static final BlockEntityType<RGBBlockEntity> RGB = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "rgb"),
        FabricBlockEntityTypeBuilder.create(
            RGBBlockEntity::new,
            BlockRegistry.ALL_BLOCKS.toArray(new Block[0])
        ).build()
    );

    public static void register() {}
}
