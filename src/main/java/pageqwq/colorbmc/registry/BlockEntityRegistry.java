package pageqwq.colorbmc.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.machine.replicator.ConcreteReplicatorBlockEntity;
import pageqwq.colorbmc.machine.workshop.ConcreteWorkshopBlockEntity;
import pageqwq.colorbmc.rgb.RGBBlockEntity;

public class BlockEntityRegistry {
    public static final BlockEntityType<RGBBlockEntity> RGB = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "rgb"),
        FabricBlockEntityTypeBuilder.create(
            RGBBlockEntity::new,
            BlockRegistry.ALL_BLOCKS.toArray(new Block[0])
        ).build()
    );

    public static final BlockEntityType<ConcreteWorkshopBlockEntity> WORKSHOP = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "concrete_workshop"),
        FabricBlockEntityTypeBuilder.create(ConcreteWorkshopBlockEntity::new, BlockRegistry.CONCRETE_WORKSHOP).build()
    );

    public static final BlockEntityType<ConcreteReplicatorBlockEntity> REPLICATOR = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "concrete_replicator"),
        FabricBlockEntityTypeBuilder.create(ConcreteReplicatorBlockEntity::new, BlockRegistry.CONCRETE_REPLICATOR).build()
    );

    public static void register() {}
}
