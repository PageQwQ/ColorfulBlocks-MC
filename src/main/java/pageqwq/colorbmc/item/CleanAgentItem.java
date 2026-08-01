package pageqwq.colorbmc.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;

public class CleanAgentItem extends Item {
    private static final float FADE_RATE = 0.25f;

    public CleanAgentItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            int currentColor = rgbBlockEntity.getColor();
            if (currentColor == -1) return InteractionResult.SUCCESS;

            int r = (currentColor >> 16) & 0xFF;
            int g = (currentColor >> 8) & 0xFF;
            int b = currentColor & 0xFF;

            int newR = Math.min(255, r + Math.round((255 - r) * FADE_RATE));
            int newG = Math.min(255, g + Math.round((255 - g) * FADE_RATE));
            int newB = Math.min(255, b + Math.round((255 - b) * FADE_RATE));

            int newColor = (newR << 16) | (newG << 8) | newB;
            rgbBlockEntity.setColor(newColor);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL_IMMEDIATE);

            if (!level.isClientSide()) {
                Player player = context.getPlayer();
                if (player != null && !player.isCreative()) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }

            level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.PLAYERS, 1.0f, 1.0f);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}