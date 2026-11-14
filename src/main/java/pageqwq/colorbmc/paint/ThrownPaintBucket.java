package pageqwq.colorbmc.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import pageqwq.colorbmc.registry.DataComponentRegistry;
import pageqwq.colorbmc.registry.EntityRegistry;
import pageqwq.colorbmc.registry.ItemRegistry;

public class ThrownPaintBucket extends ThrowableItemProjectile {
    private static final int DROP_DELAY = 10;
    private float radius = PaintBucketThrowLogic.MIN_RADIUS;

    public ThrownPaintBucket(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public ThrownPaintBucket(Level level, LivingEntity shooter, ItemStack stack) {
        super(EntityRegistry.THROWN_PAINT_BUCKET, shooter, level, stack);
    }

    public void setRadius(float radius) {
        this.radius = Math.max(0.0F, radius);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            int color = this.getItem().getOrDefault(DataComponentRegistry.COLOR, -1);
            this.level().addParticle(PaintBucketItem.makeDust(color, 0.5F),
                this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.PAINT_BUCKET;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            int color = this.getItem().getOrDefault(DataComponentRegistry.COLOR, -1);
            int count = PaintBucketThrowLogic.applyRadiusPaint(serverLevel, BlockPos.containing(result.getLocation()), this.radius, color);
            int cost = PaintBucketThrowLogic.computeCost(count, this.getItem());
            ItemStack drop = PaintBucketThrowLogic.resolveDrop(this.getItem(), cost);
            this.dropBucket(serverLevel, result.getLocation().x, result.getLocation().y, result.getLocation().z, drop);
            PaintBucketItem.playImpactEffects(serverLevel, result.getLocation().x, result.getLocation().y, result.getLocation().z, color);
            if (this.getOwner() instanceof net.minecraft.server.level.ServerPlayer owner) {
                pageqwq.colorbmc.registry.CriterionRegistry.COLOR_ACTION.trigger(owner,
                    pageqwq.colorbmc.registry.ColorActionTrigger.TYPE_DYE, color & 0xFFFFFF, color & 0xFFFFFF, count, true, false);
            }
        }
        this.discard();
    }

    private void dropBucket(ServerLevel level, double x, double y, double z, ItemStack stack) {
        ItemEntity item = new ItemEntity(level, x, y, z, stack);
        item.setPickUpDelay(DROP_DELAY);
        level.addFreshEntity(item);
    }
}
