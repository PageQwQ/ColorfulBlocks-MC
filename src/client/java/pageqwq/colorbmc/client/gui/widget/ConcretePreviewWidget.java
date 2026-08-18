package pageqwq.colorbmc.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import pageqwq.colorbmc.util.registries.BlockRegistry;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class ConcretePreviewWidget extends AbstractWidget {
    private static final int ITEM_SIZE = 16;
    private int color;
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (this.stack == null) {
            this.stack = new ItemStack(BlockRegistry.RGB_CONCRETE);
        }
        this.stack.set(DataComponentRegistry.COLOR, this.color);
        this.stack.set(DataComponents.DYED_COLOR, new DyedItemColor(this.color));
        float scale = (float) this.width / ITEM_SIZE;
        graphics.pose().pushMatrix();
        graphics.pose().translate(this.getX(), this.getY());
        graphics.pose().scale(scale, scale);
        graphics.item(this.stack, 0, 0);
        graphics.pose().popMatrix();
    }
}
