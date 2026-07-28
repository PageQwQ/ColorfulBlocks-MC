package pageqwq.colorbmc.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import pageqwq.colorbmc.util.Color;
import pageqwq.colorbmc.util.registries.BlockEntityRegistry;
import pageqwq.colorbmc.util.registries.DataComponentRegistry;

public class RGBBlockEntity extends BlockEntity {
    private int color = -1;
    private MapColor mapColor = MapColor.NONE;

    public RGBBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.RGB, pos, state);
    }

    public void setColor(int color) {
        this.color = new Color(color).getRGB();
        this.mapColor = Color.getNearestMapColor(this.color);
        setChanged();
    }

    public int getColor() {
        return color;
    }

    public MapColor getMapColor() {
        return mapColor;
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.color = components.getOrDefault(DataComponentRegistry.COLOR, -1);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentRegistry.COLOR, this.color);
        builder.set(DataComponents.DYED_COLOR, new DyedItemColor(this.color));
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