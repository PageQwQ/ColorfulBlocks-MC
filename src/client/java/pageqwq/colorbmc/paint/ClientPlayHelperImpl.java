package pageqwq.colorbmc.paint;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import pageqwq.colorbmc.paint.ColorSelectScreen;

public class ClientPlayHelperImpl implements ClientPlayHelper {
    @Override
    public void openColorSelectScreen(Player player, int color, boolean isRGBSelected) {
        Minecraft.getInstance().setScreen(new ColorSelectScreen(color, isRGBSelected));
    }
}
