package pageqwq.colorbmc.util.jade;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public class RGBBlockIconProvider implements IBlockComponentProvider {
    public static final RGBBlockIconProvider INSTANCE = new RGBBlockIconProvider();

    @Override
    public ResourceLocation getUid() {
        return ColorBlockJadePlugin.UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    }

    @Override
    public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
        int color = ColorBlockJadePlugin.getColor(accessor);
        if (color == -1) {
            return null;
        }
        ItemStack stack = new ItemStack(accessor.getBlock());
        stack.set(DataComponentRegistry.COLOR, color);
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
        return IElementHelper.get().item(stack);
    }
}
