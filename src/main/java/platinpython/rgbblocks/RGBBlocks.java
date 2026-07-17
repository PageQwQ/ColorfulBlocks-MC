package platinpython.rgbblocks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.DispenserBlock;
import platinpython.rgbblocks.dispenser.DispensePaintBucketBehaviour;
import platinpython.rgbblocks.util.registries.BlockEntityRegistry;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.CreativeTabRegistry;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;
import platinpython.rgbblocks.util.registries.RecipeSerializerRegistry;
import platinpython.rgbblocks.util.network.NetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RGBBlocks implements ModInitializer {
    public static final String MOD_ID = "colorblockmc";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        DataComponentRegistry.register();
        BlockRegistry.register();
        ItemRegistry.register();
        BlockEntityRegistry.register();
        RecipeSerializerRegistry.register();
        CreativeTabRegistry.register();

        NetworkHandler.register();

        DispenserBlock.registerBehavior(ItemRegistry.PAINT_BUCKET, new DispensePaintBucketBehaviour());
    }
}
