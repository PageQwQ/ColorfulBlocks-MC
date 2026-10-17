package pageqwq.colorbmc.util.registries;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.entity.ThrownPaintBucket;

public class EntityRegistry {
    public static final EntityType<ThrownPaintBucket> THROWN_PAINT_BUCKET = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket_projectile"),
        thrownBucketType()
    );

    /**
     * Exactly what {@code EntityType.Builder.of(ThrownPaintBucket::new, MobCategory.MISC)
     * .sized(0.25F, 0.25F).clientTrackingRange(64).updateInterval(10)
     * .build("colorblockmc:paint_bucket_projectile")} produced — minus one call.
     *
     * <h2>Why not just use the builder</h2>
     * On this Minecraft version {@code EntityType.Builder.build(String)} starts with
     * <pre>
     *   if (this.serialize) { Util.fetchChoiceType(References.ENTITY_TREE, id); }   // result POPPED
     * </pre>
     * The return value is discarded: that call exists only so vanilla can log
     * {@code "No data fixer registered for <id>"} for a saveable modded entity type. There is no API here
     * to attach a data fixer ({@code EntityType}'s constructor takes none, and the builder exposes no
     * hook), so going through the builder means a red ERROR line on every world load — and the way to
     * silence it through the builder would be {@code noSave()}, which would make an in-flight bucket
     * vanish when its chunk unloads (the entity carries the player's item).
     *
     * <p>Constructing the type directly reproduces that method's body byte for byte, minus the logging
     * call. The literals below are the builder's own defaults (checked against its bytecode) with the
     * four values this entity actually sets: size 0.25×0.25, tracking range 64, update interval 10,
     * category MISC. <b>{@code serialize} deliberately stays {@code true}</b> — keep it that way, or
     * thrown buckets stop surviving a save.
     */
    private static EntityType<ThrownPaintBucket> thrownBucketType() {
        return new EntityType<>(
            ThrownPaintBucket::new,
            MobCategory.MISC,
            true,                        // serialize — an in-flight bucket must survive a save
            true,                        // summon
            false,                       // fireImmune
            false,                       // canSpawnFarFromPlayer
            ImmutableSet.of(),           // immuneTo
            EntityDimensions.scalable(0.25F, 0.25F).withAttachments(EntityAttachments.builder()),
            1.0F,                        // spawnDimensionsScale
            64,                          // clientTrackingRange
            10,                          // updateInterval
            FeatureFlags.VANILLA_SET);   // requiredFeatures
    }

    public static void register() {}
}
