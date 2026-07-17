package platinpython.rgbblocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import platinpython.rgbblocks.util.registries.BlockRegistry;
import platinpython.rgbblocks.util.registries.EntityRegistry;

public class RGBFallingBlockEntity extends FallingBlockEntity {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(
        RGBFallingBlockEntity.class, EntityDataSerializers.INT
    );

    public RGBFallingBlockEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("color", getColor());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("color")) {
            setColor(tag.getInt("color"));
        }
    }

    @Override
    public BlockState getBlockState() {
        BlockState state = super.getBlockState();
        if (state == null || state.isAir()) {
            return BlockRegistry.RGB_CONCRETE_POWDER.defaultBlockState();
        }
        return state;
    }

    public static RGBFallingBlockEntity create(Level level, BlockPos pos, BlockState state, int color) {
        RGBFallingBlockEntity entity = new RGBFallingBlockEntity(EntityRegistry.RGB_FALLING_BLOCK, level);
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        entity.setDeltaMovement(0, 0, 0);
        entity.blockState = state;
        entity.setColor(color);
        entity.setStartPos(pos);
        return entity;
    }
}
