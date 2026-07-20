package pageqwq.colorbmc.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Block;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.item.PaintBucketItem;
import pageqwq.colorbmc.item.RGBBlockItem;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    public static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static final Item PAINT_BUCKET = register(
        "paint_bucket", new PaintBucketItem(new Item.Properties().durability(500)
            .component(DataComponentRegistry.COLOR, -1)
            .component(DataComponentRegistry.RGB_SELECTED, true)
            .component(DataComponents.DYED_COLOR, new DyedItemColor(-1, false)))
    );

    static void registerBlockItem(String name, Block block) {
        Item item = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            new RGBBlockItem(block, new Item.Properties()
                .component(DataComponentRegistry.COLOR, -1)
                .component(DataComponents.DYED_COLOR, new DyedItemColor(-1, false)))
        );
        ALL_ITEMS.add(item);
    }

    private static Item register(String name, Item item) {
        Item registered = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            item
        );
        ALL_ITEMS.add(registered);
        return registered;
    }

    public static void register() {}
}
