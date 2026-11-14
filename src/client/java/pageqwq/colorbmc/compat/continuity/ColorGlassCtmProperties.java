package pageqwq.colorbmc.compat.continuity;

import me.pepperbell.continuity.client.properties.OrientedConnectingCtmProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.block.RGBGlassBlock;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;

import java.util.Properties;

/** CTM properties for colored glass; connects only blocks with the same RGB value. */
public class ColorGlassCtmProperties extends OrientedConnectingCtmProperties {
    public ColorGlassCtmProperties(Properties properties, ResourceLocation resourceId, PackResources pack,
                                   int packPriority, ResourceManager resourceManager, String method) {
        super(properties, resourceId, pack, packPriority, resourceManager, method);
    }

    @Override
    public void init() {
        super.init();
        this.connectionPredicate = ColorGlassCtmProperties::shouldConnect;
    }

    private static boolean shouldConnect(BlockAndTintGetter level, BlockState appearanceState, BlockState state,
                                         BlockPos pos, BlockState otherAppearanceState, BlockState otherState,
                                         BlockPos otherPos, Direction face, TextureAtlasSprite sprite) {
        if (!(state.getBlock() instanceof RGBGlassBlock) || !(otherState.getBlock() instanceof RGBGlassBlock)) {
            return false;
        }
        int color = colorAt(level, pos);
        return color != -1 && color == colorAt(level, otherPos);
    }

    private static int colorAt(BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof RGBBlockEntity blockEntity) {
            return blockEntity.getColor() & 0xFFFFFF;
        }
        return -1;
    }
}
