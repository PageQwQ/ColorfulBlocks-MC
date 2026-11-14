package pageqwq.colorbmc.machine.workshop;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import pageqwq.colorbmc.RGBBlocks;
import pageqwq.colorbmc.registry.BlockEntityRegistry;
import pageqwq.colorbmc.registry.BlockRegistry;
import pageqwq.colorbmc.machine.workshop.ConcreteWorkshopBlock.Stage;

/** Concrete workshop: batch production, 2 sand per block into the right-side container. */
public class ConcreteWorkshopBlockEntity extends BlockEntity {
    /** Sand capacity. */
    public static final int SAND_CAPACITY = 2048;
    private static final int SAND_PER_CONCRETE = 2;
    private static final int CONCRETE_PER_MINUTE = 1024;
    private static final int TICKS_PER_MINUTE = 1200;

    private int sand;
    private int produced;
    private double budget;

    public ConcreteWorkshopBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.WORKSHOP, pos, state);
    }

    public int getSand() {
        return this.sand;
    }

    public int getRoom() {
        return SAND_CAPACITY - this.sand;
    }

    public int addSand(int amount) {
        int moved = Math.min(amount, getRoom());
        if (moved > 0) {
            this.sand += moved;
            setChanged();
        }
        return moved;
    }

    private UUID shutdownPlayer;
    private long shutdownDeadline;

    /** Sneak-use twice to shut down; the first call returns false and records the player. */
    public boolean confirmShutdown(Player player, long now) {
        if (this.shutdownPlayer != null && this.shutdownPlayer.equals(player.getUUID()) && now <= this.shutdownDeadline) {
            this.shutdownPlayer = null;
            return true;
        }
        this.shutdownPlayer = player.getUUID();
        this.shutdownDeadline = now + 60L;
        return false;
    }

    /** Starts a new batch. */
    public void startBatch() {
        this.produced = 0;
        this.budget = 0.0D;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("sand", this.sand);
        output.putInt("produced", this.produced);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.sand = input.getIntOr("sand", 0);
        this.produced = input.getIntOr("produced", 0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ConcreteWorkshopBlockEntity entity) {
        Stage stage = state.getValue(ConcreteWorkshopBlock.STAGE);
        if (stage == Stage.OFF) {
            return;
        }

        // Pull sand from the container above.
        if (entity.sand < SAND_PER_CONCRETE) {
            int pulled = pullSand(level, pos, SAND_PER_CONCRETE - entity.sand);
            if (pulled > 0) {
                entity.sand += pulled;
                entity.setChanged();
            }
        }

        // No sand: finish the batch if anything was produced, otherwise wait.
        if (entity.sand < SAND_PER_CONCRETE) {
            if (entity.produced > 0) {
                complete(level, pos, state, entity);
            } else {
                setStage(level, pos, state, Stage.WAITING);
            }
            return;
        }

        // Container on the right.
        Direction outDir = state.getValue(ConcreteWorkshopBlock.FACING).getCounterClockWise();
        Container out = HopperBlockEntity.getContainerAt(level, pos.relative(outDir));
        if (out == null) {
            setStage(level, pos, state, Stage.ARROW);
            return;
        }

        ItemStack product = new ItemStack(BlockRegistry.RGB_CONCRETE);
        // Check room first: if full, stay in FULL and do not accumulate budget.
        if (!canInsert(out, product)) {
            if (state.getValue(ConcreteWorkshopBlock.STAGE) != Stage.FULL) {
                setStage(level, pos, state, Stage.FULL);
                notifyNearby(level, pos, "full");
            }
            return;
        }

        // Rate limit: 1024 blocks per minute.
        entity.budget += (double) CONCRETE_PER_MINUTE / TICKS_PER_MINUTE;
        if (entity.budget < 1.0D) {
            setStage(level, pos, state, Stage.WORKING);
            return;
        }
        entity.budget -= 1.0D;

        insert(out, product);
        entity.sand -= SAND_PER_CONCRETE;
        entity.produced++;
        entity.setChanged();
        setStage(level, pos, state, Stage.WORKING);
    }

    private static int pullSand(Level level, BlockPos pos, int wanted) {
        Container above = HopperBlockEntity.getContainerAt(level, pos.above());
        if (above == null) {
            return 0;
        }
        int taken = 0;
        for (int i = 0; i < above.getContainerSize() && taken < wanted; i++) {
            ItemStack slot = above.getItem(i);
            if (slot.isEmpty() || !slot.is(Items.SAND)) {
                continue;
            }
            int move = Math.min(slot.getCount(), wanted - taken);
            slot.shrink(move);
            taken += move;
            if (slot.isEmpty()) {
                above.setItem(i, ItemStack.EMPTY);
            }
            above.setChanged();
        }
        return taken;
    }

    private static boolean canInsert(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                return true;
            }
            if (ItemStack.isSameItemSameComponents(slot, stack)
                && slot.getCount() + stack.getCount() <= slot.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private static boolean insert(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                container.setItem(i, stack.copy());
                container.setChanged();
                return true;
            }
            if (ItemStack.isSameItemSameComponents(slot, stack)
                && slot.getCount() + stack.getCount() <= slot.getMaxStackSize()) {
                slot.grow(stack.getCount());
                container.setChanged();
                return true;
            }
        }
        return false;
    }

    private static void complete(Level level, BlockPos pos, BlockState state, ConcreteWorkshopBlockEntity entity) {
        entity.produced = 0;
        entity.budget = 0.0D;
        entity.sand = 0;
        entity.setChanged();
        level.setBlock(pos, state.setValue(ConcreteWorkshopBlock.STAGE, Stage.OFF), 3);
        notifyNearby(level, pos, "done");
    }

    /** Sends an action-bar hint to players within 32 blocks. */
    public static void notifyNearby(Level level, BlockPos pos, String key) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Component message = Component.translatable("message." + RGBBlocks.MOD_ID + ".concrete_workshop." + key);
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 32.0D * 32.0D) {
                player.sendSystemMessage(message, true);
            }
        }
    }

    private static void setStage(Level level, BlockPos pos, BlockState state, Stage stage) {
        if (state.getValue(ConcreteWorkshopBlock.STAGE) != stage) {
            level.setBlock(pos, state.setValue(ConcreteWorkshopBlock.STAGE, stage), 3);
        }
    }
}
