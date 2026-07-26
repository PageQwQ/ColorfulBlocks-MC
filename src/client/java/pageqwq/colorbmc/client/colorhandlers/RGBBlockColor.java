package pageqwq.colorbmc.client.colorhandlers;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;

public class RGBBlockColor implements BlockTintSource {
    @Override
    public int color(BlockState blockState) {
        return -1;
    }

    @Override
    public int colorInWorld(
        BlockState blockState,
        BlockAndTintGetter blockDisplayReader,
        BlockPos blockPos
    ) {
        if (blockDisplayReader == null || blockPos == null) {
            return -1;
        }
        BlockEntity blockEntity = blockDisplayReader.getBlockEntity(blockPos);
        if (blockEntity == null) {
            blockEntity = blockDisplayReader.getBlockEntity(blockPos.below());
            if (blockEntity == null) {
                return -1;
            }
        }
        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            return rgbBlockEntity.getColor();
        } else {
            return -1;
        }
    }
}