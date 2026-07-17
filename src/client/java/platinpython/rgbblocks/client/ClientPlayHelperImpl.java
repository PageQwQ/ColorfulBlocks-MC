package platinpython.rgbblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import platinpython.rgbblocks.client.gui.screen.ColorSelectScreen;
import platinpython.rgbblocks.util.ClientPlayHelper;
import platinpython.rgbblocks.util.registries.DataComponentRegistry;

public class ClientPlayHelperImpl implements ClientPlayHelper {
    @Override
    public void openColorSelectScreen(Player player, int color, boolean isRGBSelected) {
        Minecraft.getInstance().setScreen(new ColorSelectScreen(color, isRGBSelected));
    }
}
