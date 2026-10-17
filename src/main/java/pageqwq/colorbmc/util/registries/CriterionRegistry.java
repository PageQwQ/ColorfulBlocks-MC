package pageqwq.colorbmc.util.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import pageqwq.colorbmc.RGBBlocks;

public class CriterionRegistry {
    public static final ColorActionTrigger COLOR_ACTION = Registry.register(
        BuiltInRegistries.TRIGGER_TYPES,
        Identifier.fromNamespaceAndPath(RGBBlocks.MOD_ID, "color_action"),
        new ColorActionTrigger()
    );

    public static void register() {}
}
