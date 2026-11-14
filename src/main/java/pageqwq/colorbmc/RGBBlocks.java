package pageqwq.colorbmc;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.DispenserBlock;
import pageqwq.colorbmc.command.GiveColorCommand;
import pageqwq.colorbmc.paint.DispensePaintBucketBehaviour;
import pageqwq.colorbmc.registry.BlockEntityRegistry;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.CreativeTabRegistry;
import pageqwq.colorbmc.advancement.CriterionRegistry;
import pageqwq.colorbmc.registry.DataComponentRegistry;
import pageqwq.colorbmc.registry.EntityRegistry;
import pageqwq.colorbmc.registry.ItemRegistry;
import pageqwq.colorbmc.registry.RecipeSerializerRegistry;
import pageqwq.colorbmc.network.NetworkHandler;
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
        EntityRegistry.register();
        CriterionRegistry.register();
        RecipeSerializerRegistry.register();
        CreativeTabRegistry.register();

        NetworkHandler.register();
        GiveColorCommand.register();

        DispenserBlock.registerBehavior(ItemRegistry.PAINT_BUCKET, new DispensePaintBucketBehaviour());
    }
}
