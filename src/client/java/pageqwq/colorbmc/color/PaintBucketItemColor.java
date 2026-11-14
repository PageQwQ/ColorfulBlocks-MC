package pageqwq.colorbmc.color;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import pageqwq.colorbmc.paint.PaintBucketItem;
import pageqwq.colorbmc.registry.DataComponentRegistry;

public class PaintBucketItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1 && stack.getItem() instanceof PaintBucketItem) {
            return stack.getOrDefault(DataComponentRegistry.COLOR, -1) | 0xFF000000;
        }
        return -1;
    }
}