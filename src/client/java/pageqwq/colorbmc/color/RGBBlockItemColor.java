package pageqwq.colorbmc.color;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import pageqwq.colorbmc.rgb.RGBBlockItem;
import pageqwq.colorbmc.registry.DataComponentRegistry;

public class RGBBlockItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (stack.getItem() instanceof RGBBlockItem) {
            return stack.getOrDefault(DataComponentRegistry.COLOR, -1) | 0xFF000000;
        }
        return -1;
    }
}