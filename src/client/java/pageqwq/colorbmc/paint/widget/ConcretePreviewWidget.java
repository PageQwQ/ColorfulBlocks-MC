package pageqwq.colorbmc.paint.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.registry.DataComponentRegistry;

public class ConcretePreviewWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 16;
    /** "No color picked" — keeps the preview untinted before the screen feeds it a real color. */
    private static final int NO_COLOR = -1;
    private int color = NO_COLOR;
    private ItemStack stack;

    public ConcretePreviewWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (this.stack == null) {
            this.stack = new ItemStack(BlockRegistry.RGB_CONCRETE);
        }
        this.stack.set(DataComponentRegistry.COLOR, this.color);
        float scale = (float) this.width / ITEM_SIZE;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.renderItem(this.stack, 0, 0);
        guiGraphics.pose().popPose();
    }
}
