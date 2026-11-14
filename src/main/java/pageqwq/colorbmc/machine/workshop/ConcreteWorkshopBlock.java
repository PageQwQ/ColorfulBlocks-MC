package pageqwq.colorbmc.machine.workshop;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

/** Concrete workshop: right-click to start or inspect; output is on the screen's right side. */
public class ConcreteWorkshopBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Stage> STAGE = EnumProperty.create("stage", Stage.class);

    public enum Stage implements StringRepresentable {
        OFF("off"),
        WAITING("waiting"),
        WORKING("working"),
        ARROW("arrow"),
        FULL("full");

        private final String name;

        Stage(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public ConcreteWorkshopBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.SOUTH)
            .setValue(STAGE, Stage.OFF));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STAGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(STAGE, Stage.OFF);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        Stage stage = state.getValue(STAGE);
        boolean hasEntity = level.getBlockEntity(pos) instanceof ConcreteWorkshopBlockEntity;

        // Sneak-use twice to shut down.
        if (player.isShiftKeyDown()) {
            if (stage == Stage.OFF) {
                showHint(player, Stage.OFF);
                return InteractionResult.SUCCESS;
            }
            if (hasEntity
                && ((ConcreteWorkshopBlockEntity) level.getBlockEntity(pos)).confirmShutdown(player, level.getGameTime())) {
                shutdown(level, pos, state);
                showHint(player, Stage.OFF);
            } else {
                showMessage(player, "confirm_off");
            }
            return InteractionResult.SUCCESS;
        }

        if (stage == Stage.OFF) {
            if (hasEntity) {
                ((ConcreteWorkshopBlockEntity) level.getBlockEntity(pos)).startBatch();
            }
            level.setBlock(pos, state.setValue(STAGE, Stage.WAITING), Block.UPDATE_ALL);
            showHint(player, Stage.WAITING);
            return InteractionResult.SUCCESS;
        }

        // Right-click while full shuts down immediately.
        if (stage == Stage.FULL) {
            shutdown(level, pos, state);
            showHint(player, Stage.OFF);
            return InteractionResult.SUCCESS;
        }

        showHint(player, stage);
        return InteractionResult.SUCCESS;
    }

    /** Shuts down but keeps the sand buffer for the next start. */
    private static void shutdown(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(STAGE, Stage.OFF), Block.UPDATE_ALL);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide() || !stack.is(net.minecraft.world.item.Items.SAND)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (level.getBlockEntity(pos) instanceof ConcreteWorkshopBlockEntity workshop) {
            int moved = workshop.addSand(stack.getCount());
            if (moved > 0) {
                stack.shrink(moved);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConcreteWorkshopBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof ConcreteWorkshopBlockEntity workshop) {
                ConcreteWorkshopBlockEntity.serverTick(lvl, pos, st, workshop);
            }
        };
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide() && placer instanceof ServerPlayer serverPlayer) {
            showHint(serverPlayer, state.getValue(STAGE));
        }
    }

    /** Sends an action-bar hint instead of chat. */
    public static void showHint(Player player, Stage stage) {
        showMessage(player, stage.getSerializedName());
    }

    public static void showMessage(Player player, String key) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(
                Component.translatable("message." + pageqwq.colorbmc.RGBBlocks.MOD_ID + ".concrete_workshop." + key),
                true
            );
        }
    }
}
