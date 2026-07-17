package platinpython.rgbblocks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.util.registries.BlockEntityRegistry;

public class RGBConcretePowderBlock extends ConcretePowderBlock implements EntityBlock {
    public RGBConcretePowderBlock(Properties properties) {
        super(platinpython.rgbblocks.util.registries.BlockRegistry.RGB_CONCRETE, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.RGB.create(pos, state);
    }
}
