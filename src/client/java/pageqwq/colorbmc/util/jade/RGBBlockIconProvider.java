package pageqwq.colorbmc.util.jade;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jspecify.annotations.Nullable;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

public class RGBBlockIconProvider implements IBlockComponentProvider {
    public static final RGBBlockIconProvider INSTANCE = new RGBBlockIconProvider();

    @Override
    public Identifier getUid() {
        return ColorBlockJadePlugin.UID_ICON;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    }

    @Override
    public @Nullable Element getIcon(BlockAccessor accessor, IPluginConfig config, @Nullable Element currentIcon) {
        int color = ColorBlockJadePlugin.getColor(accessor);
        if (color == -1) {
            return null;
        }
        ItemStack stack = new ItemStack(accessor.getBlock());
        stack.set(DataComponentRegistry.COLOR, color);
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        return JadeUI.item(stack);
    }
}
