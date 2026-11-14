package pageqwq.colorbmc.machine.workshop;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import pageqwq.colorbmc.machine.workshop.ConcreteWorkshopBlock.Stage;

/** Per-frame snapshot for the workshop renderer. */
public class WorkshopRenderState extends BlockEntityRenderState {
    public Stage stage = Stage.OFF;
    public Direction facing = Direction.SOUTH;
    public long gameTime;
}
