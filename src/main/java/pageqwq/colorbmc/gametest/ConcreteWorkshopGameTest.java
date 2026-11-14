package pageqwq.colorbmc.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pageqwq.colorbmc.block.ConcreteWorkshopBlock;
import pageqwq.colorbmc.block.ConcreteWorkshopBlock.Stage;
import pageqwq.colorbmc.block.entity.ConcreteWorkshopBlockEntity;
import pageqwq.colorbmc.registry.BlockRegistry;

/** Verifies the workshop output side with real blocks, sand, a container and ticks. */
public class ConcreteWorkshopGameTest {
    private static final int SAND = 32;

    private static BlockPos place(GameTestHelper helper, BlockPos rel) {
        BlockPos pos = helper.absolutePos(rel);
        BlockState state = BlockRegistry.CONCRETE_WORKSHOP.defaultBlockState()
            .setValue(ConcreteWorkshopBlock.FACING, Direction.SOUTH)
            .setValue(ConcreteWorkshopBlock.STAGE, Stage.OFF);
        helper.getLevel().setBlock(pos, state, 3);
        return pos;
    }

    private static ConcreteWorkshopBlockEntity entity(GameTestHelper helper, BlockPos pos) {
        BlockEntity be = helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(be instanceof ConcreteWorkshopBlockEntity, "workshop block entity missing");
        return (ConcreteWorkshopBlockEntity) be;
    }

    private static void powerOn(GameTestHelper helper, BlockPos pos) {
        BlockState state = helper.getLevel().getBlockState(pos);
        helper.getLevel().setBlock(pos, state.setValue(ConcreteWorkshopBlock.STAGE, Stage.WAITING), 3);
    }

    private static void tick(GameTestHelper helper, BlockPos pos, int times) {
        for (int i = 0; i < times; i++) {
            BlockState state = helper.getLevel().getBlockState(pos);
            BlockEntity be = helper.getLevel().getBlockEntity(pos);
            if (!(be instanceof ConcreteWorkshopBlockEntity workshop)) {
                return;
            }
            ConcreteWorkshopBlockEntity.serverTick(helper.getLevel(), pos, state, workshop);
        }
    }

    private static int countConcrete(Container container) {
        int total = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(BlockRegistry.RGB_CONCRETE.asItem())) {
                total += stack.getCount();
            }
        }
        return total;
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void producesOneConcretePerTwoSandAndShutsDown(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        BlockPos pos = place(helper, rel);
        BlockPos chestPos = helper.absolutePos(rel.east());
        helper.getLevel().setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        ConcreteWorkshopBlockEntity workshop = entity(helper, pos);
        workshop.addSand(SAND);
        powerOn(helper, pos);
        tick(helper, pos, 200);

        Container chest = (Container) helper.getLevel().getBlockEntity(chestPos);
        int produced = countConcrete(chest);
        helper.assertTrue(produced == SAND / 2, SAND + " sand should yield " + (SAND / 2) + " concrete, got " + produced);

        Stage stage = helper.getLevel().getBlockState(pos).getValue(ConcreteWorkshopBlock.STAGE);
        helper.assertTrue(stage == Stage.OFF, "sand exhausted with output > 0 should power off, stage=" + stage);
        helper.assertTrue(workshop.getSand() == 0, "sand buffer should be cleared on completion, got " + workshop.getSand());
        helper.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void pausesWhenOutputContainerIsFull(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        BlockPos pos = place(helper, rel);
        BlockPos chestPos = helper.absolutePos(rel.east());
        helper.getLevel().setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        Container chest = (Container) helper.getLevel().getBlockEntity(chestPos);
        for (int i = 0; i < chest.getContainerSize(); i++) {
            chest.setItem(i, new ItemStack(BlockRegistry.RGB_CONCRETE, 64));
        }

        ConcreteWorkshopBlockEntity workshop = entity(helper, pos);
        workshop.addSand(SAND);
        powerOn(helper, pos);
        tick(helper, pos, 100);

        Stage stage = helper.getLevel().getBlockState(pos).getValue(ConcreteWorkshopBlock.STAGE);
        helper.assertTrue(stage == Stage.FULL, "full container should hold the FULL stage, stage=" + stage);
        helper.assertTrue(workshop.getSand() == SAND, "no sand should be consumed while the output is full, got " + workshop.getSand());
        helper.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void asksForContainerWhenMissing(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        BlockPos pos = place(helper, rel);

        ConcreteWorkshopBlockEntity workshop = entity(helper, pos);
        workshop.addSand(SAND);
        powerOn(helper, pos);
        tick(helper, pos, 5);

        Stage stage = helper.getLevel().getBlockState(pos).getValue(ConcreteWorkshopBlock.STAGE);
        helper.assertTrue(stage == Stage.ARROW, "missing output container should show the ARROW stage, stage=" + stage);
        helper.assertTrue(workshop.getSand() == SAND, "no sand should be consumed without a container, got " + workshop.getSand());
        helper.succeed();
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void waitsWhenNoSandAvailable(GameTestHelper helper) {
        BlockPos rel = new BlockPos(1, 2, 1);
        BlockPos pos = place(helper, rel);
        BlockPos chestPos = helper.absolutePos(rel.east());
        helper.getLevel().setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        powerOn(helper, pos);
        tick(helper, pos, 20);

        Stage stage = helper.getLevel().getBlockState(pos).getValue(ConcreteWorkshopBlock.STAGE);
        helper.assertTrue(stage == Stage.WAITING, "no sand and no output should stay WAITING, stage=" + stage);
        Container chest = (Container) helper.getLevel().getBlockEntity(chestPos);
        helper.assertTrue(countConcrete(chest) == 0, "nothing should be produced without sand");
        helper.succeed();
    }
}
