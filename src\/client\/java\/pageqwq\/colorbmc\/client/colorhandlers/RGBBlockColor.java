package pageqwq.colorbmc.client.colorhandlers;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;

public class RGBBlockColor implements BlockColor {
    public int getColor(
        BlockState blockState,
        BlockAndTintGetter blockDisplayReader,
        BlockPos blockPos,
        int tintIndex
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
