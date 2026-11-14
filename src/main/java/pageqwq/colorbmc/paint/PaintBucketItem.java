package pageqwq.colorbmc.paint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import pageqwq.colorbmc.rgb.RGBBlockEntity;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.DataComponentRegistry;

import java.util.List;
import java.util.Set;

public class PaintBucketItem extends Item {
    private static final int THROW_USE_DURATION = 72000;
    private static final int THROW_CHARGE_TICKS = 20;
    private static final int MIN_THROW_TICKS = 5;
    private static final float MIN_THROW_VELOCITY = 0.7F;
    private static final float MAX_THROW_VELOCITY = 1.6F;

    public static final Set<Block> VANILLA_CONCRETE = Set.of(
        Blocks.WHITE_CONCRETE, Blocks.ORANGE_CONCRETE, Blocks.MAGENTA_CONCRETE,
        Blocks.LIGHT_BLUE_CONCRETE, Blocks.YELLOW_CONCRETE, Blocks.LIME_CONCRETE,
        Blocks.PINK_CONCRETE, Blocks.GRAY_CONCRETE, Blocks.LIGHT_GRAY_CONCRETE,
        Blocks.CYAN_CONCRETE, Blocks.PURPLE_CONCRETE, Blocks.BLUE_CONCRETE,
        Blocks.BROWN_CONCRETE, Blocks.GREEN_CONCRETE, Blocks.RED_CONCRETE,
        Blocks.BLACK_CONCRETE
    );

    public PaintBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        int colorValue = stack.getOrDefault(DataComponentRegistry.COLOR, -1);
        String hex = String.format("#%06X", colorValue & 0xFFFFFF);
        Component hexComponent;
        if (colorValue == -1) {
            hexComponent = Component.literal(hex);
        } else {
            hexComponent = Component.literal(hex).withStyle(style -> style.withColor(colorValue & 0xFFFFFF));
        }
        tooltip.add(hexComponent);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (playerIn.isShiftKeyDown()) {
                if (level.isClientSide() && ClientProxy.getClientPlayHelper() != null) {
                    ClientProxy.getClientPlayHelper().openColorSelectScreen(
                        playerIn,
                        stack.getOrDefault(DataComponentRegistry.COLOR, -1),
                        stack.getOrDefault(DataComponentRegistry.RGB_SELECTED, false)
                    );
                    return InteractionResultHolder.success(stack);
                }
                return InteractionResultHolder.consume(stack);
            }
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return THROW_USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (level.isClientSide() || !(entity instanceof Player player)) return;

        int heldTicks = THROW_USE_DURATION - timeLeft;
        if (heldTicks < MIN_THROW_TICKS) return;

        float charge = Math.min(1.0F, (float) heldTicks / THROW_CHARGE_TICKS);
        float velocity = MIN_THROW_VELOCITY + (MAX_THROW_VELOCITY - MIN_THROW_VELOCITY) * charge;
        float radiusCharge = Math.min(1.0F, (float) heldTicks / pageqwq.colorbmc.paint.PaintBucketThrowLogic.MAX_CHARGE_TICKS);
        float radius = pageqwq.colorbmc.paint.PaintBucketThrowLogic.radiusForCharge(radiusCharge);

        ThrownPaintBucket bucket = new ThrownPaintBucket(level, player, stack);
        bucket.setRadius(radius);
        double pitch = Math.toRadians(player.getXRot());
        double yaw = Math.toRadians(player.getYRot());
        double dx = -Math.sin(yaw) * Math.cos(pitch);
        double dy = -Math.sin(pitch);
        double dz = Math.cos(yaw) * Math.cos(pitch);
        bucket.shoot(dx, dy, dz, velocity, 1.0F);

        ((ServerLevel) level).addFreshEntity(bucket);
        playThrowEffects((ServerLevel) level, player, stack);
        player.getCooldowns().addCooldown(this, 10);
        stack.shrink(1);
    }

    public static void playThrowEffects(ServerLevel level, Player player, ItemStack stack) {
        int color = stack.getOrDefault(DataComponentRegistry.COLOR, -1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 1.0F);
        level.sendParticles(makeDust(color, 0.8F), player.getX(), player.getEyeY(), player.getZ(), 8, 0.3, 0.2, 0.3, 0.1);
    }

    public static DustParticleOptions makeDust(int color, float scale) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        return new DustParticleOptions(new Vector3f(r, g, b), scale);
    }

    public static void playImpactEffects(ServerLevel level, double x, double y, double z, int color) {
        level.playSound(null, x, y, z, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.2F);
        level.sendParticles(makeDust(color, 1.0F), x, y, z, 40, 3.0, 3.0, 3.0, 0.6);
    }

    public static boolean applyColorToBlock(Level level, BlockPos pos, int color) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            if (rgbBlockEntity.getColor() == color) return false;
            rgbBlockEntity.setColor(color);
            rgbBlockEntity.resetOriginalColor();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL_IMMEDIATE);
            return true;
        } else if (VANILLA_CONCRETE.contains(block)) {
            level.setBlock(pos, BlockRegistry.RGB_CONCRETE.defaultBlockState(),
                Block.UPDATE_NEIGHBORS | Block.UPDATE_INVISIBLE | Block.UPDATE_IMMEDIATE);
            if (level.getBlockEntity(pos) instanceof RGBBlockEntity newEntity) {
                newEntity.setColor(color);
            }
            level.sendBlockUpdated(pos, state, level.getBlockState(pos), Block.UPDATE_ALL_IMMEDIATE);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        // 1) Already an RGB block → change color or copy color
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            if (player != null && player.isShiftKeyDown()) {
                stack.set(DataComponentRegistry.COLOR, rgbBlockEntity.getColor());
            } else {
                int color = stack.getOrDefault(DataComponentRegistry.COLOR, -1);
                if (player != null && !player.isCreative() && color != rgbBlockEntity.getColor()) {
                    if (stack.getDamageValue() == stack.getMaxDamage() - 1) {
                        player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                    } else {
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    }
                }
                applyColorToBlock(level, pos, color);
                if (!level.isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    pageqwq.colorbmc.registry.CriterionRegistry.COLOR_ACTION.trigger(serverPlayer,
                        pageqwq.colorbmc.registry.ColorActionTrigger.TYPE_DYE, color & 0xFFFFFF, color & 0xFFFFFF, 1, false, false);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // 2) Vanilla concrete → replace with RGB concrete
        if (VANILLA_CONCRETE.contains(block)) {
            int color = stack.getOrDefault(DataComponentRegistry.COLOR, -1);

            if (!level.isClientSide() && player != null && !player.isCreative()) {
                if (stack.getDamageValue() == stack.getMaxDamage() - 1) {
                    player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                } else {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }

            applyColorToBlock(level, pos, color);
            if (!level.isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                pageqwq.colorbmc.registry.CriterionRegistry.COLOR_ACTION.trigger(serverPlayer,
                    pageqwq.colorbmc.registry.ColorActionTrigger.TYPE_DYE, color & 0xFFFFFF, color & 0xFFFFFF, 1, false, false);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}