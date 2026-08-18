package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
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
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        updateFromMouse(event.x(), event.y());
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dx, double dy) {
        updateFromMouse(event.x(), event.y());
        super.onDrag(event, dx, dy);
    }

    private void updateFromMouse(double mouseX, double mouseY) {
        float s = (float) Mth.clamp((mouseX - this.getX()) / this.width, 0.0, 1.0) * 100.0F;
        float b = (1.0F - (float) Mth.clamp((mouseY - this.getY()) / this.height, 0.0, 1.0)) * 100.0F;
        this.saturation = s;
        this.brightness = b;
        this.listener.onChange(s, b);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = this.getX();
        int y = this.getY();
        int w = this.width;
        int h = this.height;
        int hueColor = Color.HSBtoRGB(this.hue / 360.0F, 1.0F, 1.0F);
        ScreenUtils.fillGradient(graphics, x, y, x + w, y + h, 0xFFFFFFFF, hueColor);
        graphics.fillGradient(x, y, x + w, y + h, 0x00000000, 0xFF000000);

        int dotX = x + Math.round(this.saturation / 100.0F * (w - 1));
        int dotY = y + Math.round((1.0F - this.brightness / 100.0F) * (h - 1));
        graphics.fill(dotX - 3, dotY - 3, dotX + 4, dotY + 4, 0xFF000000);
        graphics.fill(dotX - 2, dotY - 2, dotX + 3, dotY + 3, 0xFFFFFFFF);
    }
}