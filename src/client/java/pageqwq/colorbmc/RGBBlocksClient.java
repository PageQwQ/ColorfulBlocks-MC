package pageqwq.colorbmc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import pageqwq.colorbmc.client.ClientPlayHelperImpl;
import pageqwq.colorbmc.client.colorhandlers.PaintBucketItemColor;
import pageqwq.colorbmc.client.colorhandlers.RGBBlockColor;
import pageqwq.colorbmc.client.colorhandlers.RGBBlockItemColor;
import pageqwq.colorbmc.util.ClientProxy;
import pageqwq.colorbmc.util.registries.BlockRegistry;
import pageqwq.colorbmc.util.registries.ItemRegistry;

public class RGBBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientProxy.clientPlayHelper = new ClientPlayHelperImpl();

        ColorProviderRegistry.BLOCK.register(
            new RGBBlockColor(),
            BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0])
        );
        ColorProviderRegistry.ITEM.register(
            new RGBBlockItemColor(),
            BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0])
        );
        ColorProviderRegistry.ITEM.register(new PaintBucketItemColor(), ItemRegistry.PAINT_BUCKET);
    }
}
