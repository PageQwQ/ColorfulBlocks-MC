package pageqwq.colorbmc.util.registries;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import pageqwq.colorbmc.RGBBlocks;

public class CreativeTabRegistry {
    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(
        BuiltInRegistries.CREATIVE_MODE_TAB.key(),
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "tab")
    );
    public static final CreativeModeTab TAB = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        TAB_KEY,
        FabricCreativeModeTab.builder()
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