package platinpython.rgbblocks.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import platinpython.rgbblocks.util.Color;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

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
                    stack.set(DataComponentRegistry.COLOR, tag.getInt("color"));
                    tag.remove("color");
                }
            }));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        Color color = new Color(stack.getOrDefault(DataComponentRegistry.COLOR, -1));
        tooltip.add(Component.literal("#" + Integer.toHexString(color.getRGB()).substring(2)));
    }
}
