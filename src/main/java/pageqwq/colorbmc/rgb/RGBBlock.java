package pageqwq.colorbmc.rgb;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.registry.BlockEntityRegistry;

public class RGBBlock extends Block implements EntityBlock {
    public RGBBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.RGB.create(pos, state);
    }
}
