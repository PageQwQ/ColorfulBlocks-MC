package pageqwq.colorbmc.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.util.registries.BlockEntityRegistry;

/** Item replicator prototype: render-only block entity. */
public class ConcreteReplicatorBlockEntity extends BlockEntity {
    public ConcreteReplicatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.REPLICATOR, pos, state);
    }
}
