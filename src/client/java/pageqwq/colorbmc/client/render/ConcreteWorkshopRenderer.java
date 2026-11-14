package pageqwq.colorbmc.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.block.ConcreteWorkshopBlock;
import pageqwq.colorbmc.block.ConcreteWorkshopBlock.Stage;
import pageqwq.colorbmc.block.entity.ConcreteWorkshopBlockEntity;

import java.util.Map;

/** Draws the screen icon with the emissive layer so it stays lit in the dark. */
public class ConcreteWorkshopRenderer implements BlockEntityRenderer<ConcreteWorkshopBlockEntity, WorkshopRenderState> {
    private record Icon(Identifier texture, int frames, int ticksPerFrame) {
    }

    private static final Map<Stage, Icon> ICONS = Map.of(
        Stage.WAITING, icon("concrete_workshop_screen_waiting", 2, 12),
        Stage.WORKING, icon("concrete_workshop_screen_working", 3, 6),
        Stage.ARROW, icon("concrete_workshop_screen_arrow", 2, 12),
        Stage.FULL, icon("concrete_workshop_screen_full", 1, 1)
    );

    private static Icon icon(String name, int frames, int ticksPerFrame) {
        return new Icon(Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "textures/block/" + name + ".png"), frames, ticksPerFrame);
    }

    @Override
    public WorkshopRenderState createRenderState() {
        return new WorkshopRenderState();
    }

    @Override
    public void extractRenderState(ConcreteWorkshopBlockEntity entity, WorkshopRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        BlockState blockState = entity.getBlockState();
        state.stage = blockState.getValue(ConcreteWorkshopBlock.STAGE);
        state.facing = blockState.getValue(ConcreteWorkshopBlock.FACING);
        state.gameTime = entity.getLevel() == null ? 0L : entity.getLevel().getGameTime();
    }

    @Override
    public void submit(WorkshopRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        Icon icon = ICONS.get(state.stage);
        if (icon == null) {
            return;
        }
        int frame = (int) ((state.gameTime / icon.ticksPerFrame()) % icon.frames());
        float v0 = (float) frame / icon.frames();
        float v1 = (float) (frame + 1) / icon.frames();

        poses.pushPose();
        // Match the blockstate y rotation: the model faces south, facing sets the direction.
        poses.translate(0.5F, 0.5F, 0.5F);
        poses.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poses.translate(-0.5F, -0.5F, -0.5F);

        RenderType renderType = RenderTypes.entityTranslucentEmissive(icon.texture());
        float x0 = 3.0F / 16.0F;
        float x1 = 13.0F / 16.0F;
        float y0 = 4.0F / 16.0F;
        float y1 = 14.0F / 16.0F;
        float z = 15.03F / 16.0F;

        collector.submitCustomGeometry(poses, renderType, (pose, buffer) -> {
            vertex(buffer, pose, x0, y0, z, 0.0F, v1);
            vertex(buffer, pose, x1, y0, z, 1.0F, v1);
            vertex(buffer, pose, x1, y1, z, 1.0F, v0);
            vertex(buffer, pose, x0, y1, z, 0.0F, v0);
        });
        poses.popPose();
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        buffer.addVertex(pose, x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(0xF000F0)
            .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}
