package pageqwq.colorbmc.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.entity.ThrownPaintBucket;

public class EntityRegistry {
    public static final EntityType<ThrownPaintBucket> THROWN_PAINT_BUCKET = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket_projectile"),
        EntityType.Builder.<ThrownPaintBucket>of(ThrownPaintBucket::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(64)
            .updateInterval(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket_projectile")))
    );

    public static void register() {}
}
