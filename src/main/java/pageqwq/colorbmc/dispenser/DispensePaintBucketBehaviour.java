package pageqwq.colorbmc.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;
import pageqwq.colorbmc.item.PaintBucketItem;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class DispensePaintBucketBehaviour extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        if (!(stack.getItem() instanceof PaintBucketItem)) {
            return super.execute(source, stack);
        }

        Level level = source.level();
        BlockPos frontPos = source.pos().relative(source.state().getValue(net.minecraft.world.level.block.DispenserBlock.FACING));
        BlockEntity blockEntity = level.getBlockEntity(frontPos);

        if (blockEntity instanceof RGBBlockEntity rgbBlockEntity) {
            int color = stack.getOrDefault(DataComponentRegistry.COLOR, -1);
            rgbBlockEntity.setColor(color);
            level.sendBlockUpdated(frontPos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);

            if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
                stack.shrink(1);
                return new ItemStack(net.minecraft.world.item.Items.BUCKET);
            } else {
                stack.setDamageValue(stack.getDamageValue() + 1);
                return stack;
            }
        }

        return super.execute(source, stack);
    }
}
