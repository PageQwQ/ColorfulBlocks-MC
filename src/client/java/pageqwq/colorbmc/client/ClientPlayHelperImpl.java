package pageqwq.colorbmc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import pageqwq.colorbmc.client.gui.screen.ColorSelectScreen;
import pageqwq.colorbmc.util.ClientPlayHelper;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class ClientPlayHelperImpl implements ClientPlayHelper {
    @Override
    public void openColorSelectScreen(Player player, int color, boolean isRGBSelected) {
        Minecraft.getInstance().setScreen(new ColorSelectScreen(color, isRGBSelected));
    }
}
