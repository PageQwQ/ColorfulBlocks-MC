package pageqwq.colorbmc.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import pageqwq.colorbmc.util.Color;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

import java.util.List;

public class RGBBlockItem extends BlockItem {
    public RGBBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
                if (tag.contains("color")) {
                    int color = tag.getInt("color");
                    stack.set(DataComponentRegistry.COLOR, color);
                    stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
                    tag.remove("color");
                }
            }));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        int colorValue = stack.getOrDefault(DataComponentRegistry.COLOR, -1);
        Color color = new Color(colorValue);
        String hex = "#" + Integer.toHexString(color.getRGB()).substring(2);
        Component hexComponent;
        if (colorValue == -1) {
            hexComponent = Component.literal(hex);
        } else {
            hexComponent = Component.literal(hex).withStyle(style -> style.withColor(colorValue & 0xFFFFFF));
        }
        tooltip.add(hexComponent);
    }
}
