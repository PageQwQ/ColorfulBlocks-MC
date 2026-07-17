package platinpython.rgbblocks.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.ClientProxy;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

import java.util.List;

public class PaintBucketItem extends Item {
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
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                context.getItemInHand().set(DataComponentRegistry.COLOR, rgbBlockEntity.getColor());
            } else {
                int color = context.getItemInHand().getOrDefault(DataComponentRegistry.COLOR, -1);
                if (context.getPlayer() != null && !context.getPlayer().isCreative() && color != rgbBlockEntity.getColor()) {
                    if (context.getItemInHand().getDamageValue() == context.getItemInHand().getMaxDamage() - 1) {
                        context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
                    } else {
                        context.getItemInHand()
                            .hurtAndBreak(1, context.getPlayer(), LivingEntity.getSlotForHand(context.getHand()));
                    }
                }
                rgbBlockEntity.setColor(color);
                context.getLevel()
                    .sendBlockUpdated(
                        context.getClickedPos(), blockEntity.getBlockState(), blockEntity.getBlockState(),
                        Block.UPDATE_ALL_IMMEDIATE
                    );
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
