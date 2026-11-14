package pageqwq.colorbmc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pageqwq.colorbmc.block.RGBBlock;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;
import pageqwq.colorbmc.util.MapColorResolver;

/** Makes maps use the color stored in an {@link RGBBlockEntity}. */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateMapColorMixin {
    @Shadow
    public abstract Block getBlock();

    // getMapColor is baked into the BlockState, so consult the block entity here.
    @Inject(method = "getMapColor", at = @At("HEAD"), cancellable = true, require = 1)
    private void colorblockmc$useBlockEntityColor(BlockGetter level, BlockPos pos,
                                                  CallbackInfoReturnable<MapColor> cir) {
        if (level == null || pos == null || !(this.getBlock() instanceof RGBBlock)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof RGBBlockEntity blockEntity)) {
            return;
        }
        int color = blockEntity.getColor();
        if (color == -1) {
            // Unpainted blocks keep the vanilla white-concrete map color.
            return;
        }
        cir.setReturnValue(MapColorResolver.forColor(color));
    }
}
