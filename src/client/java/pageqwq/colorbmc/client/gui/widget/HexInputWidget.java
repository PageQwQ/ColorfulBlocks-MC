package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class HexInputWidget extends AbstractWidget {
    public interface Listener {
        void onChange(String text);
    }

    private final Listener listener;
    private String text = "";
    private int cursorPos;

    public HexInputWidget(int x, int y, int width, int height, Listener listener) {
        super(x, y, width, height, Component.empty());
        this.listener = listener;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        this.cursorPos = text.length();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isMouseOver(mouseX, mouseY)) {
            this.setFocused(true);
            int rel = Math.max(0, (int) Math.round(mouseX - this.getX()) - 2);
            int width = Minecraft.getInstance().font.width(this.text);
            int pos = Math.round(rel / (float) Math.max(width, 1) * this.text.length());
            this.cursorPos = Math.max(0, Math.min(this.text.length(), pos));
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.isFocused()) {
            return false;
        }
        if (codePoint == '#') {
            if (!this.text.contains("#") && this.text.length() < 7) {
                this.insert("#");
                return true;
            }
            return false;
        }
        if (isHexChar(codePoint) && this.text.length() < 7) {
            this.insert(String.valueOf(Character.toUpperCase(codePoint)));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isFocused()) {
            return false;
        }
        if (keyCode == 259) {
            if (this.cursorPos > 0) {
                this.text = this.text.substring(0, this.cursorPos - 1) + this.text.substring(this.cursorPos);
                this.cursorPos--;
                this.notifyChange();
            }
            return true;
        }
        if (keyCode == 261) {
            if (this.cursorPos < this.text.length()) {
                this.text = this.text.substring(0, this.cursorPos) + this.text.substring(this.cursorPos + 1);
                this.notifyChange();
            }
            return true;
        }
        if (keyCode == 263) {
            this.cursorPos = Math.max(0, this.cursorPos - 1);
            return true;
        }
        if (keyCode == 262) {
            this.cursorPos = Math.min(this.text.length(), this.cursorPos + 1);
            return true;
        }
        if (keyCode == 268) {
            this.cursorPos = 0;
            return true;
        }
        if (keyCode == 269) {
            this.cursorPos = this.text.length();
            return true;
        }
        return false;
    }

    private void insert(String s) {
        this.text = this.text.substring(0, this.cursorPos) + s + this.text.substring(this.cursorPos);
        this.cursorPos += s.length();
        this.notifyChange();
    }

    private void notifyChange() {
        if (this.listener != null) {
            this.listener.onChange(this.text);
        }
    }

    private static boolean isHexChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
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
        guiGraphics.fill(x, y, x + w, y + h, this.isFocused() ? 0x80333333 : 0x66333333);
        int textX = x + 2;
        int textY = y + (h - 9) / 2;
        guiGraphics.drawString(Minecraft.getInstance().font, this.text, textX, textY, 0xFFFFFFFF, false);
        if (this.isFocused()) {
            String before = this.text.substring(0, this.cursorPos);
            int cursorX = textX + Minecraft.getInstance().font.width(before);
            guiGraphics.fill(cursorX, y + 2, cursorX + 1, y + h - 2, 0xFFFFFFFF);
        }
    }
}
