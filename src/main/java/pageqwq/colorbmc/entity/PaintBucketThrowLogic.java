package pageqwq.colorbmc.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import pageqwq.colorbmc.item.PaintBucketItem;

public final class PaintBucketThrowLogic {
    public static final float MIN_RADIUS = 2.0F;
    public static final float MAX_RADIUS = 13.0F;
    public static final int MAX_COLORED_BLOCKS = 500;
    public static final int MAX_DURABILITY_COST = 500;
    public static final int MAX_CHARGE_TICKS = 200;

    private PaintBucketThrowLogic() {
    }

    public static float radiusForCharge(float chargeFraction) {
        float fraction = Math.min(1.0F, Math.max(0.0F, chargeFraction));
        return MIN_RADIUS + (MAX_RADIUS - MIN_RADIUS) * fraction;
    }

    public static int applyRadiusPaint(ServerLevel level, BlockPos center, float radius, int color) {
        int count = 0;
        int range = (int) Math.ceil(radius);
        float radiusSqr = radius * radius;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        outer:
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    mutable.set(center).move(dx, dy, dz);
                    if (!level.isInWorldBounds(mutable) || !level.isLoaded(mutable)) continue;
                    if (mutable.distSqr(center) > radiusSqr) continue;
                    if (!PaintBucketItem.applyColorToBlock(level, mutable, color)) continue;
                    count++;
                    if (count >= MAX_COLORED_BLOCKS) break outer;
                }
            }
        }
        return count;
    }

    public static int computeCost(int coloredCount, ItemStack stack) {
        int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
        int rawCost = (int) (coloredCount * 1.5F);
        return Math.min(Math.min(rawCost, MAX_DURABILITY_COST), remaining);
    }

    public static ItemStack resolveDrop(ItemStack stack, int cost) {
        int newDamage = stack.getDamageValue() + cost;
        if (newDamage >= stack.getMaxDamage()) {
            return new ItemStack(Items.BUCKET);
        }
        ItemStack result = stack.copy();
        result.setDamageValue(newDamage);
        return result;
    }
}
