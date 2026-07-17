package platinpython.rgbblocks.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.ClientProxy;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.util.List;
import java.util.Set;

public class PaintBucketItem extends Item {
    private static final Set<Block> VANILLA_CONCRETE = Set.of(
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
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
                if (tag.contains("color")) {
                    stack.set(DataComponentRegistry.COLOR, tag.getInt("color"));
                    tag.remove("color");
                }
                if (tag.contains("isRGBSelected")) {
                    stack.set(DataComponentRegistry.RGB_SELECTED, tag.getBoolean("isRGBSelected"));
                    tag.remove("isRGBSelected");
                }
            }));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        platinpython.rgbblocks.util.Color color = new platinpython.rgbblocks.util.Color(stack.getOrDefault(DataComponentRegistry.COLOR, -1));
        tooltip.add(Component.literal("#" + Integer.toHexString(color.getRGB()).substring(2)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        if (handIn == InteractionHand.MAIN_HAND && playerIn.isShiftKeyDown()) {
            if (level.isClientSide && ClientProxy.getClientPlayHelper() != null) {
                ClientProxy.getClientPlayHelper().openColorSelectScreen(
                    playerIn,
                    playerIn.getMainHandItem().getOrDefault(DataComponentRegistry.COLOR, -1),
                    playerIn.getMainHandItem().getOrDefault(DataComponentRegistry.RGB_SELECTED, true)
                );
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getMainHandItem());
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
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
                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                    }
                }
                rgbBlockEntity.setColor(color);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL_IMMEDIATE);
            }
            return InteractionResult.SUCCESS;
        }

        // 2) Vanilla concrete → replace with RGB concrete
        if (VANILLA_CONCRETE.contains(block)) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            int color = stack.getOrDefault(DataComponentRegistry.COLOR, -1);

            if (player != null && !player.isCreative()) {
                if (stack.getDamageValue() == stack.getMaxDamage() - 1) {
                    player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                } else {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                }
            }

            level.setBlock(pos, BlockRegistry.RGB_CONCRETE.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            if (level.getBlockEntity(pos) instanceof RGBBlockEntity newEntity) {
                newEntity.setColor(color);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
