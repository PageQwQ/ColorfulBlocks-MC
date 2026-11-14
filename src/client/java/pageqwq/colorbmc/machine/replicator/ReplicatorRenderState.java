package pageqwq.colorbmc.machine.replicator;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/** Per-frame snapshot for the replicator projection. */
public class ReplicatorRenderState extends BlockEntityRenderState {
    public boolean active;
    public Direction facing = Direction.SOUTH;
    public BlockPos origin = BlockPos.ZERO;
    public Level level;
}
