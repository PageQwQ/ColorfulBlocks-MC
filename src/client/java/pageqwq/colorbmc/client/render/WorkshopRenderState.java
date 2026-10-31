package pageqwq.colorbmc.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import pageqwq.colorbmc.block.ConcreteWorkshopBlock.Stage;

/** 混凝土车间渲染用的每帧快照。 */
public class WorkshopRenderState extends BlockEntityRenderState {
    public Stage stage = Stage.OFF;
    public Direction facing = Direction.SOUTH;
    public long gameTime;
}
