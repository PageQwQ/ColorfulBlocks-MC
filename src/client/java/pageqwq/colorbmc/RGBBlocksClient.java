package pageqwq.colorbmc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import pageqwq.colorbmc.client.ClientPlayHelperImpl;
import pageqwq.colorbmc.client.colorhandlers.RGBBlockColor;
import pageqwq.colorbmc.util.ClientProxy;
import pageqwq.colorbmc.util.registries.BlockRegistry;
import pageqwq.colorbmc.util.registries.EntityRegistry;

import java.util.List;

public class RGBBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientProxy.clientPlayHelper = new ClientPlayHelperImpl();

        BlockColorRegistry.register(
            List.of(new RGBBlockColor()),
            BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0])
        );

        EntityRendererRegistry.register(
            EntityRegistry.THROWN_PAINT_BUCKET,
            context -> new ThrownItemRenderer<>(context)
        );
    }
}