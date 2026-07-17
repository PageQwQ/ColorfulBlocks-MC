package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.util.registries.BlockEntityRegistry;

public class RGBStairsBlock extends StairBlock implements EntityBlock {
    public RGBStairsBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.RGB.create(pos, state);
    }
}
