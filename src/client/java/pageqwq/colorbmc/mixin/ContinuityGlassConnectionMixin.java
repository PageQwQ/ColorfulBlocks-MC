package pageqwq.colorbmc.mixin;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pageqwq.colorbmc.block.RGBGlassBlock;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;

/** Makes Continuity's block connection test compare colors for our glass. */
@Pseudo
@Mixin(targets = "me.pepperbell.continuity.client.properties.BasicConnectingCtmProperties$ConnectionType$1", remap = false)
public abstract class ContinuityGlassConnectionMixin {
    @Inject(method = "shouldConnect", at = @At("HEAD"), cancellable = true, require = 0)
    private void colorblockmc$sameColorOnly(BlockAndTintGetter level, BlockPos pos, BlockState appearanceState,
                                            BlockState state, BlockPos otherPos, BlockState otherAppearanceState,
                                            BlockState otherState, Direction face, TextureAtlasSprite quadSprite,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (!(state.getBlock() instanceof RGBGlassBlock) || !(otherState.getBlock() instanceof RGBGlassBlock)) {
            return;
        }
        int color = colorAt(level, pos);
        cir.setReturnValue(color != -1 && color == colorAt(level, otherPos));
    }

    private static int colorAt(BlockAndTintGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof RGBBlockEntity blockEntity) {
            return blockEntity.getColor() & 0xFFFFFF;
        }
        return -1;
    }
}
