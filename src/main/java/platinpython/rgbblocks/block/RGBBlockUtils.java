package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.BlockEntityRegistry;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

public final class RGBBlockUtils {
    public static BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.RGB.create(pos, state);
    }

    public static ItemStack getCloneItemStack(BlockState state, LevelReader level, BlockPos pos) {
        ItemStack stack = new ItemStack(state.getBlock().asItem());
        if (level.getBlockEntity(pos) instanceof RGBBlockEntity blockEntity) {
            stack.set(DataComponentRegistry.COLOR, blockEntity.getColor());
        }
        return stack;
    }

    public static MapColor getMapColor(BlockGetter level, BlockPos pos, MapColor defaultColor) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            return rgbBlockEntity.getMapColor();
        } else {
            return defaultColor;
        }
    }

    public static float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            return new Color(rgbBlockEntity.getColor()).getRGBColorComponents();
        } else {
            return null;
        }
    }
}
