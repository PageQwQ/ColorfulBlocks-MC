package pageqwq.colorbmc.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import pageqwq.colorbmc.command.GiveColorCommand;
import pageqwq.colorbmc.entity.PaintBucketThrowLogic;
import pageqwq.colorbmc.entity.ThrownPaintBucket;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.DataComponentRegistry;
import pageqwq.colorbmc.registry.EntityRegistry;
import pageqwq.colorbmc.registry.ItemRegistry;

public class ColorBlockThrowGameTest implements FabricGameTest {
    private static final int COLOR = 0x3366CC;

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void radiusScalesWithCharge(GameTestHelper helper) {
        helper.assertTrue(PaintBucketThrowLogic.radiusForCharge(0.0F) == PaintBucketThrowLogic.MIN_RADIUS, "min charge -> min radius");
        helper.assertTrue(PaintBucketThrowLogic.radiusForCharge(1.0F) == PaintBucketThrowLogic.MAX_RADIUS, "full charge -> max radius 13");
        float mid = PaintBucketThrowLogic.radiusForCharge(0.5F);
        helper.assertTrue(mid > PaintBucketThrowLogic.MIN_RADIUS && mid < PaintBucketThrowLogic.MAX_RADIUS, "mid charge -> mid radius");
        helper.assertTrue(PaintBucketThrowLogic.radiusForCharge(2.0F) == PaintBucketThrowLogic.MAX_RADIUS, "charge above 10s clamps to max");
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void paintsConcreteInRadiusAndCounts(GameTestHelper helper) {
        BlockPos center = helper.absolutePos(new BlockPos(3, 3, 3));
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                helper.getLevel().setBlock(center.offset(dx, 0, dz), Blocks.YELLOW_CONCRETE.defaultBlockState(), 3);
            }
        }
        int count = PaintBucketThrowLogic.applyRadiusPaint(helper.getLevel(), center, 2.0F, COLOR);
        helper.assertTrue(count == 9, "expected 9 concrete colored, got " + count);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block b = helper.getLevel().getBlockState(center.offset(dx, 0, dz)).getBlock();
                helper.assertTrue(b == BlockRegistry.RGB_CONCRETE, "concrete at " + dx + "," + dz + " not converted: " + b);
            }
        }
        helper.succeed();
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void durabilityCostAndBucketReturn(GameTestHelper helper) {
        ItemStack bucket = new ItemStack(ItemRegistry.PAINT_BUCKET);
        int maxDamage = bucket.getMaxDamage();

        ItemStack smallDrop = PaintBucketThrowLogic.resolveDrop(bucket, PaintBucketThrowLogic.computeCost(1, bucket));
        helper.assertTrue(smallDrop.getItem() == ItemRegistry.PAINT_BUCKET, "small paint should keep the paint bucket");
        helper.assertTrue(smallDrop.getDamageValue() == 1, "small paint should cost 1 durability, got " + smallDrop.getDamageValue());

        ItemStack bigDrop = PaintBucketThrowLogic.resolveDrop(bucket, PaintBucketThrowLogic.computeCost(334, bucket));
        helper.assertTrue(bigDrop.getItem() == Items.BUCKET, "exhausting the bucket should return an empty bucket, got " + bigDrop.getItem());

        int capped = PaintBucketThrowLogic.computeCost(500, bucket);
        helper.assertTrue(capped == 500, "cost with 500 colors should cap at 500, got " + capped);
        helper.assertTrue(maxDamage == 500, "paint bucket max durability should be 500, got " + maxDamage);

        ItemStack halfDrop = PaintBucketThrowLogic.resolveDrop(bucket, PaintBucketThrowLogic.computeCost(10, bucket));
        helper.assertTrue(halfDrop.getItem() == ItemRegistry.PAINT_BUCKET && halfDrop.getDamageValue() == 15,
            "10 colors should cost 15 durability, got " + halfDrop.getDamageValue());
        helper.succeed();
    }

    /**
     * `/givec` must hand out opaque ARGB.
     *
     * <p>A bare 24-bit value compares unequal to the {@code 0xFFRRGGBB} a block entity stores, and that
     * comparison is what {@code PaintBucketItem.useOn} and the durability-aware recipe use to decide "this
     * block is already that color, so no durability is spent". Item tinting is unaffected either way on
     * this branch, because the Java {@code ItemColor} providers OR {@code 0xFF} in themselves — which is
     * precisely why the mismatch could go unnoticed.
     */
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void givecProducesOpaqueArgb(GameTestHelper helper) {
        int opaque = COLOR | 0xFF000000;

        int parsed = parseOrFail("#3366CC");
        helper.assertTrue(parsed == opaque,
            "/givec #3366CC should parse to opaque ARGB, got " + Integer.toHexString(parsed));

        ItemStack stack = new ItemStack(BlockRegistry.RGB_CONCRETE.asItem(), 1);
        GiveColorCommand.applyColor(stack, opaque);
        Integer stored = stack.get(DataComponentRegistry.COLOR);
        helper.assertTrue(stored != null && stored == opaque,
            "/givec stack should carry the opaque color, got " + stored);

        helper.succeed();
    }

    /**
     * Pins the shape of the thrown bucket's entity type.
     *
     * <p>`EntityRegistry.thrownBucketType()` builds the `EntityType` by hand instead of going through
     * `EntityType.Builder.build(String)`, because on this Minecraft version that method's only remaining
     * job is to log "No data fixer registered for &lt;id&gt;" for a saveable modded entity type. A
     * mistyped argument in a 12-parameter constructor would not fail anywhere else — it would just make
     * the bucket stop being saveable, or silently change its hitbox — so assert the things the builder
     * call used to guarantee.
     */
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void thrownBucketEntityTypeKeepsItsShape(GameTestHelper helper) {
        EntityType<ThrownPaintBucket> type = EntityRegistry.THROWN_PAINT_BUCKET;

        helper.assertTrue(type.canSerialize(), "an in-flight bucket must survive a save (serialize)");
        helper.assertTrue(type.canSummon(), "summon");
        helper.assertTrue(type.getWidth() == 0.25F && type.getHeight() == 0.25F,
            "expected a 0.25 x 0.25 hitbox, got " + type.getWidth() + " x " + type.getHeight());
        helper.assertTrue(type.clientTrackingRange() == 64,
            "expected clientTrackingRange 64, got " + type.clientTrackingRange());
        helper.assertTrue(type.updateInterval() == 10,
            "expected updateInterval 10, got " + type.updateInterval());

        ThrownPaintBucket created = type.create(helper.getLevel());
        helper.assertTrue(created != null && created.getType() == type,
            "type.create(level) should produce a ThrownPaintBucket with this type");

        helper.succeed();
    }

    private static int parseOrFail(String hex) {
        try {
            return GiveColorCommand.parseHex(hex);
        } catch (Exception e) {
            throw new AssertionError("parseHex(" + hex + ") threw " + e, e);
        }
    }
}
