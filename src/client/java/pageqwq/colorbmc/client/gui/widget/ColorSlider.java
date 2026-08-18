package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import pageqwq.colorbmc.client.gui.ScreenUtils;
import pageqwq.colorbmc.client.gui.screen.ColorSelectScreen;
import pageqwq.colorbmc.util.Color;

import java.util.function.Function;

public class ColorSlider extends AbstractSliderButton {
    private final SliderType type;
    private final double minValue;
    private final double maxValue;
    private Runnable onChange = () -> {};

    private static final int HANDLE_WIDTH = 8;

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public ColorSlider(
        int x, int y, int width, int height, Component label,
        double minValue, double maxValue, double currentValue, SliderType type
    ) {
        super(x, y, width, height, label, (currentValue - minValue) / (maxValue - minValue));
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = (currentValue - this.minValue) / (this.maxValue - this.minValue);
    }

    @Override
    protected void updateMessage() {}

    @Override
    protected void applyValue() {
        this.onChange.run();
    }

    public int getValueInt() {
        return (int) Math.round(this.value * (maxValue - minValue) + minValue);
    }

    public double getValue() {
        return this.value * (maxValue - minValue) + minValue;
    }

    public void setValueInt(int value) {
        this.value = (value - this.minValue) / (this.maxValue - this.minValue);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        if (!this.visible) return;

        Minecraft minecraft = Minecraft.getInstance();
        int x = this.getX(), y = this.getY(), w = this.width, h = this.height;

        // Draw gradient background
        if (minecraft.screen instanceof ColorSelectScreen screen) {
            switch (type) {
                case HUE: renderHueBackground(guiGraphics, screen); break;
                case SATURATION: renderSaturationBackground(guiGraphics, screen); break;
                case BRIGHTNESS: renderBrightnessBackground(guiGraphics, screen); break;
            }
        }

        // Draw handle (transparent fill, white border)
        int handleX = x + (int)(this.value * (w - HANDLE_WIDTH));
        guiGraphics.fill(handleX, y, handleX + HANDLE_WIDTH, y + 1, 0xFFFFFFFF);
        guiGraphics.fill(handleX, y + h - 1, handleX + HANDLE_WIDTH, y + h, 0xFFFFFFFF);
        guiGraphics.fill(handleX, y, handleX + 1, y + h, 0xFFFFFFFF);
        guiGraphics.fill(handleX + HANDLE_WIDTH - 1, y, handleX + HANDLE_WIDTH, y + h, 0xFFFFFFFF);

        // Draw value text on right
        String valueStr = Integer.toString(getValueInt());
        int valueWidth = minecraft.font.width(valueStr);
        guiGraphics.drawString(minecraft.font, valueStr, x + w - valueWidth - 4, y + (h - 9) / 2, 0xFFFFFFFF, true);
    }

    private void renderHueBackground(GuiGraphics guiGraphics, ColorSelectScreen screen) {
        if (screen.saturationSlider == null || screen.brightnessSlider == null) return;
        Function<Integer, Integer> lerp = (pct) -> (int) Math.floor(net.minecraft.util.Mth.lerp(pct / 100f, this.getX() + 1, this.getX() + this.width - 1));
        Function<Integer, Integer> color = (pct) -> Color.HSBtoRGB(
            (pct / 100f), (float) (screen.saturationSlider.getValueInt() / ColorSelectScreen.MAX_VALUE_SB),
            (float) (screen.brightnessSlider.getValueInt() / ColorSelectScreen.MAX_VALUE_SB));
        int[] pts = {0, 17, 34, 50, 66, 82, 100};
        for (int i = 0; i < pts.length - 1; i++) {
            ScreenUtils.fillGradient(guiGraphics, lerp.apply(pts[i]), this.getY() + 1, lerp.apply(pts[i + 1]), this.getY() + this.height - 1, color.apply(pts[i]), color.apply(pts[i + 1]));
        }
    }

    private void renderSaturationBackground(GuiGraphics guiGraphics, ColorSelectScreen screen) {
        if (screen.hueSlider == null || screen.brightnessSlider == null) return;
        int leftColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE), 0.0f,
            (float) (screen.brightnessSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB));
        int rightColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE), 1.0f,
            (float) (screen.brightnessSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB));
        ScreenUtils.fillGradient(guiGraphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, leftColor, rightColor);
    }

    private void renderBrightnessBackground(GuiGraphics guiGraphics, ColorSelectScreen screen) {
        if (screen.hueSlider == null || screen.saturationSlider == null) return;
        int leftColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE),
            (float) (screen.saturationSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB), 0.0f);
        int rightColor = Color.HSBtoRGB(
            (float) (screen.hueSlider.getValue() / ColorSelectScreen.MAX_VALUE_HUE),
            (float) (screen.saturationSlider.getValue() / ColorSelectScreen.MAX_VALUE_SB), 1.0f);
        ScreenUtils.fillGradient(guiGraphics, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, leftColor, rightColor);
    }
}
