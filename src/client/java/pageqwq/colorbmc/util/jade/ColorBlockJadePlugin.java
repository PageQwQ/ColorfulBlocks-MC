package pageqwq.colorbmc.util.jade;

import net.minecraft.resources.Identifier;
import pageqwq.colorbmc.block.RGBBlock;
import pageqwq.colorbmc.block.entity.RGBBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ColorBlockJadePlugin implements IWailaPlugin {
    public static final Identifier UID_INFO = Identifier.fromNamespaceAndPath("colorblockmc", "rgb_block_info");
    public static final Identifier UID_ICON = Identifier.fromNamespaceAndPath("colorblockmc", "rgb_block_icon");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockIcon(RGBBlockIconProvider.INSTANCE, RGBBlock.class);
        registration.registerBlockComponent(RGBBlockInfoProvider.INSTANCE, RGBBlock.class);
    }

    static int getColor(BlockAccessor accessor) {
        return accessor.getBlockEntity() instanceof RGBBlockEntity rgb ? rgb.getColor() : -1;
    }
}
