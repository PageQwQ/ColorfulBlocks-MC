package platinpython.rgbblocks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import platinpython.rgbblocks.client.ClientPlayHelperImpl;
import platinpython.rgbblocks.client.colorhandlers.PaintBucketItemColor;
import platinpython.rgbblocks.client.colorhandlers.RGBBlockColor;
import platinpython.rgbblocks.client.colorhandlers.RGBBlockItemColor;
import platinpython.rgbblocks.client.renderer.entity.RGBFallingBlockRenderer;
import platinpython.rgbblocks.util.ClientProxy;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.EntityRegistry;
import platinpython.rgbblocks.util.registries.ItemRegistry;

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

        EntityRendererRegistry.register(EntityRegistry.RGB_FALLING_BLOCK, RGBFallingBlockRenderer::new);
    }
}