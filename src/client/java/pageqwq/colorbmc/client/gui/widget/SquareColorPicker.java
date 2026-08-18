package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import pageqwq.colorbmc.client.gui.ScreenUtils;
import pageqwq.colorbmc.util.Color;

public class SquareColorPicker extends AbstractWidget {
    public interface Listener {
        void onChange(float saturation, float brightness);
    }

    private float hue;
    private float saturation = 100.0F;
    private float brightness = 100.0F;
    private final Listener listener;

    public SquareColorPicker(int x, int y, int width, int height, Listener listener) {
        super(x, y, width, height, Component.empty());
        this.listener = listener;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void setValues(float saturation, float brightness) {
        this.saturation = saturation;
        this.brightness = brightness;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isMouseOver(mouseX, mouseY)) {
            updateFromMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            updateFromMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }

    private void updateFromMouse(double mouseX, double mouseY) {
        float s = (float) Mth.clamp((mouseX - this.getX()) / this.width, 0.0, 1.0) * 100.0F;
        float b = (1.0F - (float) Mth.clamp((mouseY - this.getY()) / this.height, 0.0, 1.0)) * 100.0F;
        this.saturation = s;
        this.brightness = b;
        this.listener.onChange(s, b);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        int x = this.getX();
        int y = this.getY();
        int w = this.width;
        int h = this.height;
        int hueColor = Color.HSBtoRGB(this.hue / 360.0F, 1.0F, 1.0F);
        ScreenUtils.fillGradient(guiGraphics, x, y, x + w, y + h, 0xFFFFFFFF, hueColor);
        guiGraphics.fillGradient(x, y, x + w, y + h, 0x00000000, 0xFF000000);

        int dotX = x + Math.round(this.saturation / 100.0F * (w - 1));
        int dotY = y + Math.round((1.0F - this.brightness / 100.0F) * (h - 1));
        guiGraphics.fill(dotX - 3, dotY - 3, dotX + 4, dotY + 4, 0xFF000000);
        guiGraphics.fill(dotX - 2, dotY - 2, dotX + 3, dotY + 3, 0xFFFFFFFF);
    }
}