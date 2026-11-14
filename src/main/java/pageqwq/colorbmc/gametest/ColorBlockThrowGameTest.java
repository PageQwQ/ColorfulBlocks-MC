package pageqwq.colorbmc.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import pageqwq.colorbmc.rgb.RGBBlockEntity;
import pageqwq.colorbmc.command.GiveColorCommand;
import pageqwq.colorbmc.paint.PaintBucketThrowLogic;
import pageqwq.colorbmc.paint.PaintBucketItem;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.DataComponentRegistry;
import pageqwq.colorbmc.registry.ItemRegistry;

public class ColorBlockThrowGameTest {
    private static final int COLOR = 0x3366CC;

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void radiusScalesWithCharge(GameTestHelper helper) {
        helper.assertTrue(PaintBucketThrowLogic.radiusForCharge(0.0F) == PaintBucketThrowLogic.MIN_RADIUS, "min charge -> min radius");
        helper.assertTrue(PaintBucketThrowLogic.radiusForCharge(1.0F) == PaintBucketThrowLogic.MAX_RADIUS, "full charge -> max radius 13");
        float mid = PaintBucketThrowLogic.radiusForCharge(0.5F);
        helper.assertTrue(mid > PaintBucketThrowLogic.MIN_RADIUS && mid < PaintBucketThrowLogic.MAX_RADIUS, "mid charge -> mid radius");
        helper.assertTrue(PaintBucketThrowLogic.radiusForCharge(2.0F) == PaintBucketThrowLogic.MAX_RADIUS, "charge above 10s clamps to max");
        helper.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
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
                BlockPos p = center.offset(dx, 0, dz);
                Block b = helper.getLevel().getBlockState(p).getBlock();
                helper.assertTrue(b == BlockRegistry.RGB_CONCRETE, "concrete at " + dx + "," + dz + " not converted: " + b);
                BlockEntity be = helper.getLevel().getBlockEntity(p);
                helper.assertTrue(be instanceof RGBBlockEntity && (((RGBBlockEntity) be).getColor() & 0xFFFFFF) == COLOR,
                    "concrete at " + dx + "," + dz + " color not applied: " + (be instanceof RGBBlockEntity ? (((RGBBlockEntity) be).getColor() & 0xFFFFFF) : "no-be"));
            }
        }
        helper.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void useOnColorsConcrete(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(3, 3, 3));
        helper.getLevel().setBlock(pos, Blocks.BLUE_CONCRETE.defaultBlockState(), 3);

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemStack bucket = new ItemStack(ItemRegistry.PAINT_BUCKET);
        bucket.set(DataComponentRegistry.COLOR, COLOR);
        player.setItemInHand(InteractionHand.MAIN_HAND, bucket);

        BlockHitResult hit = new BlockHitResult(pos.getCenter(), Direction.UP, pos, false);
        UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);
        ItemRegistry.PAINT_BUCKET.useOn(ctx);

        helper.assertTrue(helper.getLevel().getBlockState(pos).getBlock() == BlockRegistry.RGB_CONCRETE,
            "useOn did not convert concrete: " + helper.getLevel().getBlockState(pos).getBlock());
        BlockEntity be = helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(be instanceof RGBBlockEntity && (((RGBBlockEntity) be).getColor() & 0xFFFFFF) == COLOR,
            "useOn color wrong: " + (be instanceof RGBBlockEntity ? (((RGBBlockEntity) be).getColor() & 0xFFFFFF) : "no-be"));
        helper.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
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
     * The item form of a colored block must carry BOTH components, and both must be opaque ARGB.
     *
     * <p>Ours ({@code colorblockmc:color}) is what the mod reads back. Vanilla's
     * {@code minecraft:dyed_color} is what the item model's {@code minecraft:dye} tint actually samples,
     * so a stack with only the first one renders untinted — which is what a mined drop and a {@code /givec}
     * stack used to be. The opaque-alpha part matters because {@code PaintBucketItem.useOn} and the
     * durability-aware recipe compare the raw component against the block entity's color.
     */
    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void itemFormCarriesColorAndTintComponents(GameTestHelper helper) {
        int opaque = COLOR | 0xFF000000;

        // A drop's components come from BlockEntity.collectComponents(), which is what the loot table's
        // copy_components function reads.
        BlockPos pos = helper.absolutePos(new BlockPos(3, 3, 3));
        helper.getLevel().setBlock(pos, Blocks.YELLOW_CONCRETE.defaultBlockState(), 3);
        PaintBucketItem.applyColorToBlock(helper.getLevel(), pos, opaque);

        BlockEntity painted = helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(painted instanceof RGBBlockEntity, "expected an RGB block entity after painting");
        DataComponentMap dropped = ((RGBBlockEntity) painted).collectComponents();

        Integer droppedColor = dropped.get(DataComponentRegistry.COLOR);
        helper.assertTrue(droppedColor != null && droppedColor == opaque,
            "drop should carry the opaque color, got " + droppedColor);
        DyedItemColor droppedTint = dropped.get(DataComponents.DYED_COLOR);
        helper.assertTrue(droppedTint != null && droppedTint.rgb() == opaque,
            "drop should carry minecraft:dyed_color or it renders untinted, got " + droppedTint);

        // /givec builds its stacks through applyColor and must agree with the drop.
        ItemStack given = new ItemStack(BlockRegistry.RGB_CONCRETE.asItem(), 1);
        GiveColorCommand.applyColor(given, opaque);
        Integer givenColor = given.get(DataComponentRegistry.COLOR);
        helper.assertTrue(givenColor != null && givenColor == opaque,
            "/givec stack should carry the opaque color, got " + givenColor);
        DyedItemColor givenTint = given.get(DataComponents.DYED_COLOR);
        helper.assertTrue(givenTint != null && givenTint.rgb() == opaque,
            "/givec stack should carry minecraft:dyed_color, got " + givenTint);

        // And the command's parser must produce that opaque value from user input.
        int parsed = parseOrFail(helper, "#3366CC");
        helper.assertTrue(parsed == opaque, "/givec #3366CC should parse to opaque ARGB, got " + Integer.toHexString(parsed));

        helper.succeed();
    }

    private static int parseOrFail(GameTestHelper helper, String hex) {
        try {
            return GiveColorCommand.parseHex(hex);
        } catch (Exception e) {
            throw new AssertionError("parseHex(" + hex + ") threw " + e, e);
        }
    }
}
