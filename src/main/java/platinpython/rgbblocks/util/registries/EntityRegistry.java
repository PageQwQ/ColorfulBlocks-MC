package platinpython.rgbblocks.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.entity.RGBFallingBlockEntity;

public class EntityRegistry {
    public static final EntityType<RGBFallingBlockEntity> RGB_FALLING_BLOCK = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "rgb_falling_block"),
        EntityType.Builder.<RGBFallingBlockEntity>of(RGBFallingBlockEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.98F)
            .clientTrackingRange(10)
            .updateInterval(20)
            .build(ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "rgb_falling_block").toString())
    );

    public static void register() {}
}
