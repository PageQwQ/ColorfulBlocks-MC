package pageqwq.colorbmc.machine.replicator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import pageqwq.colorbmc.RGBBlocks;

/** Renders the replicator's projected ghost blocks from the real concrete model. */
public class ConcreteReplicatorRenderer implements BlockEntityRenderer<ConcreteReplicatorBlockEntity, ReplicatorRenderState> {
    private static final int SIZE = 16;
    private static final float ALPHA = 0.45F;
    private static final int LIGHT = 0xF000F0;

    /** Test image: white border, color gradient, transparent center. */
    private static final int[] PATTERN = buildPattern();

    private static TextureAtlasSprite cachedSprite;

    private static int[] buildPattern() {
        int[] p = new int[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (x >= 6 && x <= 9 && y >= 6 && y <= 9) {
                    p[y * SIZE + x] = 0;
                    continue;
                }
                int r = 40 + x * 13;
                int g = 40 + y * 13;
                int b = 200 - (x + y) * 5;
                if (x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1) {
                    r = g = b = 240;
                }
                p[y * SIZE + x] = 0xFF000000 | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
            }
        }
        return p;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** Concrete texture from the block atlas. */
    private static TextureAtlasSprite sprite() {
        if (cachedSprite == null) {
            cachedSprite = Minecraft.getInstance().getAtlasManager().get(new SpriteId(
                TextureAtlas.LOCATION_BLOCKS,
                Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "block/concrete")
            ));
        }
        return cachedSprite;
    }

    @Override
    public ReplicatorRenderState createRenderState() {
        return new ReplicatorRenderState();
    }

    @Override
    public void extractRenderState(ConcreteReplicatorBlockEntity entity, ReplicatorRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(entity, state, crumblingOverlay);
        BlockState blockState = entity.getBlockState();
        state.active = blockState.getValue(ConcreteReplicatorBlock.ACTIVE);
        state.facing = blockState.getValue(ConcreteReplicatorBlock.FACING);
        state.origin = entity.getBlockPos();
        state.level = entity.getLevel();
    }

    @Override
    public void submit(ReplicatorRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.active) {
            return;
        }
        TextureAtlasSprite sprite = sprite();
        if (sprite == null) {
            return;
        }
        poses.pushPose();
        poses.translate(0.5F, 0.5F, 0.5F);
        poses.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poses.translate(-0.5F, -0.5F, -0.5F);

        final float z0 = 1.02F;
        final float u0 = sprite.getU0();
        final float u1 = sprite.getU1();
        final float v0 = sprite.getV0();
        final float v1 = sprite.getV1();

        collector.submitCustomGeometry(poses, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, buffer) -> {
            for (int py = 0; py < SIZE; py++) {
                for (int px = 0; px < SIZE; px++) {
                    int color = PATTERN[py * SIZE + px];
                    if ((color >>> 24) == 0) {
                        continue;
                    }
                    cube(buffer, pose, px, py, z0, u0, u1, v0, v1, color, state);
                }
            }
        });

        poses.popPose();
    }

    /** Whether the projected cell is opaque. */
    private static boolean solid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && (PATTERN[y * SIZE + x] >>> 24) != 0;
    }

    /** Whether the world block here is solid, for face culling. */
    private static boolean worldSolid(ReplicatorRenderState state, BlockPos pos) {
        if (state.level == null) {
            return false;
        }
        BlockState blockState = state.level.getBlockState(pos);
        return blockState.isCollisionShapeFullBlock(state.level, pos);
    }

    private static void cube(VertexConsumer buffer, PoseStack.Pose pose, int px, int py, float z0,
                             float u0, float u1, float v0, float v1, int color, ReplicatorRenderState state) {
        float x0 = px;
        float x1 = px + 1.0F;
        float y0 = SIZE - 1 - py;
        float y1 = SIZE - py;
        float z1 = z0 + 1.0F;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (((color >>> 24) & 0xFF) * ALPHA);

        // World block behind the projected wall, one block in front of the machine.
        Direction forward = state.facing;
        Direction right = forward.getCounterClockWise();
        BlockPos cell = state.origin.offset(forward.getStepX(), 0, forward.getStepZ())
            .relative(right, px).above(SIZE - 1 - py);

        // Skip faces blocked by a projected neighbor or a real block to avoid z-fighting.
        if (!solid(px, py - 1) && !worldSolid(state, cell.above())) {
            face(buffer, pose, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, u0, v0, u1, v1, r, g, b, a, 1.00F, 0, 1, 0);
        }
        if (!solid(px, py + 1) && !worldSolid(state, cell.below())) {
            face(buffer, pose, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, u0, v0, u1, v1, r, g, b, a, 0.55F, 0, -1, 0);
        }
        if (!worldSolid(state, cell.relative(forward))) {
            face(buffer, pose, x0, y1, z1, x1, y1, z1, x1, y0, z1, x0, y0, z1, u0, v0, u1, v1, r, g, b, a, 0.85F, 0, 0, 1);
        }
        if (!worldSolid(state, cell.relative(forward.getOpposite()))) {
            face(buffer, pose, x1, y1, z0, x0, y1, z0, x0, y0, z0, x1, y0, z0, u0, v0, u1, v1, r, g, b, a, 0.85F, 0, 0, -1);
        }
        if (!solid(px + 1, py) && !worldSolid(state, cell.relative(right))) {
            face(buffer, pose, x1, y1, z0, x1, y1, z1, x1, y0, z1, x1, y0, z0, u0, v0, u1, v1, r, g, b, a, 0.70F, 1, 0, 0);
        }
        if (!solid(px - 1, py) && !worldSolid(state, cell.relative(right.getOpposite()))) {
            face(buffer, pose, x0, y1, z1, x0, y1, z0, x0, y0, z0, x0, y0, z1, u0, v0, u1, v1, r, g, b, a, 0.70F, -1, 0, 0);
        }
    }

    private static void face(VertexConsumer buffer, PoseStack.Pose pose,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz,
                             float u0, float v0, float u1, float v1,
                             int r, int g, int b, int a, float shade, float nx, float ny, float nz) {
        int rr = Math.min(255, (int) (r * shade));
        int gg = Math.min(255, (int) (g * shade));
        int bb = Math.min(255, (int) (b * shade));
        vertex(buffer, pose, ax, ay, az, u0, v0, rr, gg, bb, a, nx, ny, nz);
        vertex(buffer, pose, bx, by, bz, u1, v0, rr, gg, bb, a, nx, ny, nz);
        vertex(buffer, pose, cx, cy, cz, u1, v1, rr, gg, bb, a, nx, ny, nz);
        vertex(buffer, pose, dx, dy, dz, u0, v1, rr, gg, bb, a, nx, ny, nz);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z,
                               float u, float v, int r, int g, int b, int a, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
            .setColor(r, g, b, a)
            .setUv(u, v)
            .setLight(LIGHT)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setNormal(nx, ny, nz);
    }
}
