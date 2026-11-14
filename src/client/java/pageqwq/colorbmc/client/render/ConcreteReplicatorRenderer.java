package pageqwq.colorbmc.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.block.ConcreteReplicatorBlock;
import pageqwq.colorbmc.block.entity.ConcreteReplicatorBlockEntity;

/** Renders the replicator's projected ghost blocks from the real concrete model. */
public class ConcreteReplicatorRenderer implements BlockEntityRenderer<ConcreteReplicatorBlockEntity> {
    private static final int SIZE = 16;
    private static final float ALPHA = 0.45F;
    private static final int LIGHT = 0xF000F0;

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

    private static TextureAtlasSprite sprite() {
        if (cachedSprite == null) {
            cachedSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "block/concrete"));
        }
        return cachedSprite;
    }

    @Override
    public void render(ConcreteReplicatorBlockEntity entity, float partialTick, PoseStack poses, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        BlockState state = entity.getBlockState();
        if (!state.getValue(ConcreteReplicatorBlock.ACTIVE)) {
            return;
        }
        TextureAtlasSprite sprite = sprite();
        if (sprite == null) {
            return;
        }
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));

        poses.pushPose();
        poses.translate(0.5F, 0.5F, 0.5F);
        poses.mulPose(Axis.YP.rotationDegrees(-state.getValue(ConcreteReplicatorBlock.FACING).toYRot()));
        poses.translate(-0.5F, -0.5F, -0.5F);
        PoseStack.Pose pose = poses.last();

        final float z0 = 1.02F;
        final float u0 = sprite.getU0();
        final float u1 = sprite.getU1();
        final float v0 = sprite.getV0();
        final float v1 = sprite.getV1();

        Direction forward = state.getValue(ConcreteReplicatorBlock.FACING);
        Direction right = forward.getCounterClockWise();
        BlockPos origin = entity.getBlockPos();
        Level level = entity.getLevel();

        for (int py = 0; py < SIZE; py++) {
            for (int px = 0; px < SIZE; px++) {
                int color = PATTERN[py * SIZE + px];
                if ((color >>> 24) == 0) {
                    continue;
                }
                BlockPos cell = origin.offset(forward.getStepX(), 0, forward.getStepZ())
                    .relative(right, px).above(SIZE - 1 - py);
                cube(buffer, pose, px, py, z0, u0, u1, v0, v1, color, level, cell, forward, right);
            }
        }

        poses.popPose();
    }

    private static boolean solid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && (PATTERN[y * SIZE + x] >>> 24) != 0;
    }

    private static boolean worldSolid(Level level, BlockPos pos) {
        if (level == null) {
            return false;
        }
        BlockState blockState = level.getBlockState(pos);
        return blockState.isCollisionShapeFullBlock(level, pos);
    }

    private static void cube(VertexConsumer buffer, PoseStack.Pose pose, int px, int py, float z0,
                             float u0, float u1, float v0, float v1, int color,
                             Level level, BlockPos cell, Direction forward, Direction right) {
        float x0 = px;
        float x1 = px + 1.0F;
        float y0 = SIZE - 1 - py;
        float y1 = SIZE - py;
        float z1 = z0 + 1.0F;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (((color >>> 24) & 0xFF) * ALPHA);

        if (!worldSolid(level, cell.relative(forward))) {
            face(buffer, pose, x0, y1, z1, x1, y1, z1, x1, y0, z1, x0, y0, z1, u0, v0, u1, v1, r, g, b, a, 0.85F, 0, 0, 1);
        }
        if (!worldSolid(level, cell.relative(forward.getOpposite()))) {
            face(buffer, pose, x1, y1, z0, x0, y1, z0, x0, y0, z0, x1, y0, z0, u0, v0, u1, v1, r, g, b, a, 0.85F, 0, 0, -1);
        }
        if (!solid(px + 1, py) && !worldSolid(level, cell.relative(right))) {
            face(buffer, pose, x1, y1, z0, x1, y1, z1, x1, y0, z1, x1, y0, z0, u0, v0, u1, v1, r, g, b, a, 0.70F, 1, 0, 0);
        }
        if (!solid(px - 1, py) && !worldSolid(level, cell.relative(right.getOpposite()))) {
            face(buffer, pose, x0, y1, z1, x0, y1, z0, x0, y0, z0, x0, y0, z1, u0, v0, u1, v1, r, g, b, a, 0.70F, -1, 0, 0);
        }
        if (!solid(px, py - 1) && !worldSolid(level, cell.above())) {
            face(buffer, pose, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, u0, v0, u1, v1, r, g, b, a, 1.00F, 0, 1, 0);
        }
        if (!solid(px, py + 1) && !worldSolid(level, cell.below())) {
            face(buffer, pose, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, u0, v0, u1, v1, r, g, b, a, 0.55F, 0, -1, 0);
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
