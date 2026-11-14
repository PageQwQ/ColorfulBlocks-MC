package pageqwq.colorbmc.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.paint.CleanAgentItem;
import pageqwq.colorbmc.paint.PaintBucketItem;
import pageqwq.colorbmc.rgb.RGBBlockItem;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    public static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static final Item PAINT_BUCKET = register(
        "paint_bucket", new PaintBucketItem(new Item.Properties().durability(500)
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket")))
            .component(DataComponentRegistry.COLOR, -1)
            .component(DataComponentRegistry.RGB_SELECTED, false))
    );

    public static final Item CLEAN_AGENT = register(
        "clean_agent", new CleanAgentItem(new Item.Properties().durability(120)
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "clean_agent"))))
    );

    public static final Item PAINT_BUCKET_THROW = Registry.register(
        BuiltInRegistries.ITEM,
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket_throw"),
        new Item(new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "paint_bucket_throw")))
            .component(DataComponentRegistry.COLOR, 0x4466FF))
    );

    static void registerBlockItem(String name, Block block) {
        Item item = Registry.register(
            BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            new RGBBlockItem(block, new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, name)))
                .component(DataComponentRegistry.COLOR, -1))
        );
        ALL_ITEMS.add(item);
    }

    static void registerBlockItemPlain(String name, Block block) {
        Item item = Registry.register(
            BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            new net.minecraft.world.item.BlockItem(block, new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, name))))
        );
        ALL_ITEMS.add(item);
    }

    private static Item register(String name, Item item) {
        Item registered = Registry.register(
            BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, name),
            item
        );
        ALL_ITEMS.add(registered);
        return registered;
    }

    public static void register() {}
}
