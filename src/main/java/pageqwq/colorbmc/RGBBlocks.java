package pageqwq.colorbmc;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.DispenserBlock;
import pageqwq.colorbmc.dispenser.DispensePaintBucketBehaviour;
import pageqwq.colorbmc.util.registries.BlockEntityRegistry;
import pageqwq.colorbmc.util.registries.BlockRegistry;
import pageqwq.colorbmc.util.registries.CreativeTabRegistry;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;
import pageqwq.colorbmc.util.registries.ItemRegistry;
import pageqwq.colorbmc.util.registries.RecipeSerializerRegistry;
import pageqwq.colorbmc.util.network.NetworkHandler;
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
