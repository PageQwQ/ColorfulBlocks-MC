package pageqwq.colorbmc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import pageqwq.colorbmc.client.gui.screen.ColorSelectScreen;

public class ClientUtils {
    public static void openColorSelectScreen(int color, boolean isRGBSelected) {
        Minecraft.getInstance().setScreen(new ColorSelectScreen(color, isRGBSelected));
    }
}
