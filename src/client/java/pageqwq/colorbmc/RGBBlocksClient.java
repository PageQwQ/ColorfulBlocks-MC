package pageqwq.colorbmc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import pageqwq.colorbmc.client.ClientPlayHelperImpl;
import pageqwq.colorbmc.client.colorhandlers.RGBBlockColor;
import pageqwq.colorbmc.util.ClientProxy;
import pageqwq.colorbmc.util.registries.BlockRegistry;

import java.util.List;

public class RGBBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientProxy.clientPlayHelper = new ClientPlayHelperImpl();

        BlockColorRegistry.register(
            List.of(new RGBBlockColor()),
            BlockRegistry.ALL_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0])
        );
    }
}