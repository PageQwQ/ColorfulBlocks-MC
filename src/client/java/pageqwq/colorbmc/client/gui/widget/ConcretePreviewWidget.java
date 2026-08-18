package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import pageqwq.colorbmc.util.registries.BlockRegistry;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class ConcretePreviewWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 16;
    private final ItemStack stack;

    public ConcretePreviewWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.stack = new ItemStack(BlockRegistry.RGB_CONCRETE);
    }

    public void setColor(int color) {
        this.stack.set(DataComponentRegistry.COLOR, color);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
        float scale = (float) this.width / ITEM_SIZE;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.renderItem(this.stack, 0, 0);
        guiGraphics.pose().popPose();
    }
}