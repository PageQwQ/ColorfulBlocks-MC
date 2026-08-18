package pageqwq.colorbmc.client.gui.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pageqwq.colorbmc.client.gui.widget.ColorSlider;
import pageqwq.colorbmc.client.gui.widget.ConcretePreviewWidget;
import pageqwq.colorbmc.client.gui.widget.HueStripWidget;
import pageqwq.colorbmc.client.gui.widget.SliderType;
import pageqwq.colorbmc.client.gui.widget.SquareColorPicker;
import pageqwq.colorbmc.client.util.ColorNames;
import pageqwq.colorbmc.util.Color;
import pageqwq.colorbmc.util.network.packets.PaintBucketSyncPayload;

import java.util.Locale;

public class ColorSelectScreen extends Screen {
    private static final int PANEL_WIDTH = 316;
    private static final int PANEL_HEIGHT = 192;
    private static final int BAR_HEIGHT = 34;
    private static final int PREVIEW_SIZE = 46;
    private static final int COLUMN_W = 150;
    private static final int SQUARE_SIZE = 112;
    private static final int STRIP_W = 14;
    private static final int WIDGET_HEIGHT = 18;
    private static final int SPACING = 22;

    public static final double MIN_VALUE = 0.0D;
    public static final double MAX_VALUE_HUE = 360.0D;
    public static final double MAX_VALUE_SB = 100.0D;

    public ColorSlider hueSlider, saturationSlider, brightnessSlider;
    public SquareColorPicker squarePicker;
    public HueStripWidget hueStrip;
    public EditBox hexBox;
    public ConcretePreviewWidget concretePreview;

    private double hue, saturation, brightness;
    private String colorName = "";
    private final boolean chineseNames;
    private boolean valuesInitialized = false;

    private int barX, barY, sliderX, sliderY, sliderW, squareX, squareY, stripX, stripY;
    private int hexX, hexY, nameX, nameY;

    public ColorSelectScreen(int colorIn, boolean isRGBSelected) {
        super(Component.empty());
        Color color = new Color(colorIn);
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
        this.hue = hsb[0] * MAX_VALUE_HUE;
        this.saturation = hsb[1] * MAX_VALUE_SB;
        this.brightness = hsb[2] * MAX_VALUE_SB;

        this.chineseNames = isChineseLanguage();
    }

    private static boolean isChineseLanguage() {
        try {
            String lang = Minecraft.getInstance().getLanguageManager().getSelected();
            return lang != null && lang.toLowerCase(Locale.ENGLISH).startsWith("zh");
        } catch (Exception ignored) {
            return false;
        }
    }

    public int getColor() {
        return Color.HSBtoRGB(
            (float) (hue / MAX_VALUE_HUE),
            (float) (saturation / MAX_VALUE_SB),
            (float) (brightness / MAX_VALUE_SB)
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;
        this.barX = cx - PANEL_WIDTH / 2;
        this.barY = cy - PANEL_HEIGHT / 2;
        this.sliderX = this.barX;
        this.sliderW = COLUMN_W;
        this.sliderY = this.barY + BAR_HEIGHT + 34;
        this.stripX = this.barX + PANEL_WIDTH - STRIP_W;
        this.stripY = this.sliderY;
        this.squareX = this.stripX - 8 - SQUARE_SIZE;
        this.squareY = this.sliderY;
        this.hexY = this.barY + BAR_HEIGHT + 10;
        this.hexX = this.barX + 58;
        this.nameX = this.barX + 144;
        this.nameY = this.hexY + 1;

        int hexBoxW = 76;

        if (hueSlider == null) {
            hueSlider = new ColorSlider(sliderX, sliderY, sliderW, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_HUE, hue, SliderType.HUE);
            hueSlider.setOnChange(() -> { hue = hueSlider.getValueInt(); refresh(); });
        } else { hueSlider.setX(sliderX); hueSlider.setY(sliderY); }

        if (saturationSlider == null) {
            saturationSlider = new ColorSlider(sliderX, sliderY + SPACING, sliderW, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_SB, saturation, SliderType.SATURATION);
            saturationSlider.setOnChange(() -> { saturation = saturationSlider.getValueInt(); refresh(); });
        } else { saturationSlider.setX(sliderX); saturationSlider.setY(sliderY + SPACING); }

        if (brightnessSlider == null) {
            brightnessSlider = new ColorSlider(sliderX, sliderY + 2 * SPACING, sliderW, WIDGET_HEIGHT,
                Component.empty(), MIN_VALUE, MAX_VALUE_SB, brightness, SliderType.BRIGHTNESS);
            brightnessSlider.setOnChange(() -> { brightness = brightnessSlider.getValueInt(); refresh(); });
        } else { brightnessSlider.setX(sliderX); brightnessSlider.setY(sliderY + 2 * SPACING); }

        if (hueStrip == null) {
            hueStrip = new HueStripWidget(stripX, stripY, STRIP_W, SQUARE_SIZE, h -> {
                hue = h;
                refresh();
            });
        } else { hueStrip.setX(stripX); hueStrip.setY(stripY); }

        if (squarePicker == null) {
            squarePicker = new SquareColorPicker(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE, (s, b) -> {
                saturation = s;
                brightness = b;
                refresh();
            });
        } else { squarePicker.setX(squareX); squarePicker.setY(squareY); }

        if (concretePreview == null) {
            concretePreview = new ConcretePreviewWidget(barX + 4, barY + 4, PREVIEW_SIZE, PREVIEW_SIZE);
        } else { concretePreview.setX(barX + 4); concretePreview.setY(barY + 4); }

        if (hexBox == null) {
            hexBox = new EditBox(this.font, hexX, hexY, hexBoxW, WIDGET_HEIGHT, Component.literal("Hex")) {
                @Override
                public void insertText(String textToWrite) {
                    textToWrite = textToWrite.contains("#") ? textToWrite.substring(1) : textToWrite;
                    textToWrite = textToWrite.toUpperCase(Locale.ENGLISH);
                    super.insertText(textToWrite);
                    if (valuesInitialized) updateFromHex();
                }

                @Override
                public void deleteChars(int pNum) {
                    super.deleteChars(pNum);
                    if (valuesInitialized) updateFromHex();
                }
            };
            hexBox.setMaxLength(7);
        } else { hexBox.setX(hexX); hexBox.setY(hexY); }

        addRenderableWidget(hueSlider);
        addRenderableWidget(saturationSlider);
        addRenderableWidget(brightnessSlider);
        addRenderableWidget(hueStrip);
        addRenderableWidget(squarePicker);
        addRenderableWidget(concretePreview);
        addRenderableWidget(hexBox);

        valuesInitialized = true;
        refresh();
    }

    private void updateFromHex() {
        try {
            String raw = hexBox.getValue();
            if (raw.contains("#")) raw = raw.substring(1);
            if (raw.length() < 6) return;
            Color color = new Color(Integer.parseInt(raw, 16));
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue());
            this.hue = hsb[0] * MAX_VALUE_HUE;
            this.saturation = hsb[1] * MAX_VALUE_SB;
            this.brightness = hsb[2] * MAX_VALUE_SB;
            refresh();
        } catch (NumberFormatException ignored) {
        }
    }

    private void refresh() {
        int rgb = getColor();
        Color c = new Color(rgb);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        if (hexBox != null && !hexBox.isFocused()) {
            hexBox.setValue("#" + Integer.toHexString(rgb).substring(2).toUpperCase(Locale.ENGLISH));
        }
        this.colorName = chineseNames ? ColorNames.nearestChinese(r, g, b) : ColorNames.nearestEnglish(r, g, b);

        if (concretePreview != null) concretePreview.setColor(rgb);
        if (squarePicker != null) {
            squarePicker.setHue((float) hue);
            squarePicker.setValues((float) saturation, (float) brightness);
        }
        if (hueStrip != null) hueStrip.setHue((float) hue);
        if (hueSlider != null) hueSlider.setValueInt((int) hue);
        if (saturationSlider != null) saturationSlider.setValueInt((int) saturation);
        if (brightnessSlider != null) brightnessSlider.setValueInt((int) brightness);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        renderHeader(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, a);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // transparent, matching the 26.1.2 build
    }

    private void renderHeader(GuiGraphics guiGraphics) {
        guiGraphics.fill(barX, barY, barX + PANEL_WIDTH, barY + BAR_HEIGHT, getColor());
        guiGraphics.drawString(font, colorName, nameX, nameY, 0xFFFFFFFF, true);
    }

    @Override
    public void onClose() {
        ClientPlayNetworking.send(new PaintBucketSyncPayload(getColor(), false));
        super.onClose();
    }
}
