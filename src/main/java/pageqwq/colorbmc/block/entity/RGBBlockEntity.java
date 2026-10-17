package pageqwq.colorbmc.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.component.DyedItemColor;
import pageqwq.colorbmc.util.Color;
import pageqwq.colorbmc.util.registries.BlockEntityRegistry;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class RGBBlockEntity extends BlockEntity {
    private int color = -1;
    private int originalColor = -1;

    public RGBBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.RGB, pos, state);
    }

    public void setColor(int color) {
        this.color = new Color(color).getRGB();
        setChanged();
    }

    public int getOriginalColor() {
        return originalColor;
    }

    public void setOriginalColor(int originalColor) {
        this.originalColor = originalColor;
    }

    public void resetOriginalColor() {
        this.originalColor = -1;
    }

    public int getColor() {
        return color;
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.color = components.getOrDefault(DataComponentRegistry.COLOR, -1);
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.COLOR, this.color);
        // The item form needs BOTH components on this branch. Ours (`colorblockmc:color`) is the value the
        // mod reads; vanilla's `minecraft:dyed_color` is what the item model's `minecraft:dye` tint
        // actually samples (see assets/colorblockmc/items/*.json). Without the second one a mined drop or
        // a /givec stack has no tint at all and renders white while the placed block is colored.
        // Skipped while the color is unset (-1): tinting with white is indistinguishable from no tint,
        // and this would otherwise put a meaningless component on every plain block's drop.
        if (this.color != -1) {
            builder.set(DataComponents.DYED_COLOR, new DyedItemColor(this.color));
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("color", color);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.color = input.getIntOr("color", -1);
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putInt("color", color);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}