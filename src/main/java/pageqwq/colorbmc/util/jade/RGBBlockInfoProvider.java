package pageqwq.colorbmc.util.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public class RGBBlockInfoProvider implements IBlockComponentProvider {
    public static final RGBBlockInfoProvider INSTANCE = new RGBBlockInfoProvider();

    @Override
    public ResourceLocation getUid() {
        return ColorBlockJadePlugin.UID;
    }

    @Override
    public int getDefaultPriority() {
        return TooltipPosition.HEAD;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        int color = ColorBlockJadePlugin.getColor(accessor);
        if (color == -1) {
            return;
        }
        tooltip.remove(JadeIds.CORE_OBJECT_NAME);
        tooltip.add(0, IThemeHelper.get().title(Component.translatable(accessor.getBlock().getDescriptionId())));
        String hex = String.format("#%06x", color & 0xFFFFFF);
        tooltip.add(Component.literal("█").withStyle(style -> style.withColor(color & 0xFFFFFF))
                .append(" ")
                .append(hex));
    }
}
