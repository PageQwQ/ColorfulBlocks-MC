package pageqwq.colorbmc.client.colorhandlers;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import pageqwq.colorbmc.item.PaintBucketItem;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class PaintBucketItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1 && stack.getItem() instanceof PaintBucketItem) {
            return stack.getOrDefault(DataComponentRegistry.COLOR, -1);
        }
        return -1;
    }
}