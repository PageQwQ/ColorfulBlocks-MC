package pageqwq.colorbmc.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import pageqwq.colorbmc.RGBBlocks;

public class CreativeTabRegistry {
    public static final CreativeModeTab TAB = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "tab"),
        CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("item_group." + RGBBlocks.MOD_ID + ".tab"))
            .icon(() -> new ItemStack(ItemRegistry.PAINT_BUCKET))
            .displayItems((parameters, output) -> {
                for (net.minecraft.world.item.Item item : ItemRegistry.ALL_ITEMS) {
                    output.accept(item);
                }
            })
            .build()
    );

    public static void register() {}
}
