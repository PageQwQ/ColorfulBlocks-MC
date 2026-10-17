package pageqwq.colorbmc.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import pageqwq.colorbmc.RGBBlocks;

public class CriterionRegistry {
    public static final ColorActionTrigger COLOR_ACTION = Registry.register(
        BuiltInRegistries.TRIGGER_TYPES,
        ResourceLocation.fromNamespaceAndPath(RGBBlocks.MOD_ID, "color_action"),
        new ColorActionTrigger()
    );

    public static void register() {}
}
