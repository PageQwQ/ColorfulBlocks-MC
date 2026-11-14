package pageqwq.colorbmc.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import pageqwq.colorbmc.util.registries.BlockRegistry;

/** Verifies that mod concrete is exposed through the c:concretes convention tags. */
public class ConcreteTagGameTest {
    @GameTest(structure = "fabric-gametest-api-v1:empty")
    public void concreteIsInConventionTags(GameTestHelper helper) {
        TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "concretes"));
        TagKey<Item> itemTag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "concretes"));

        helper.assertTrue(BlockRegistry.RGB_CONCRETE.defaultBlockState().is(blockTag),
            "colorblockmc:concrete missing from c:concretes block tag");
        helper.assertTrue(BlockRegistry.RGB_GLOWING_CONCRETE.defaultBlockState().is(blockTag),
            "colorblockmc:glowing_concrete missing from c:concretes block tag");
        helper.assertTrue(new ItemStack(BlockRegistry.RGB_CONCRETE).is(itemTag),
            "colorblockmc:concrete missing from c:concretes item tag");
        helper.assertTrue(new ItemStack(BlockRegistry.RGB_GLOWING_CONCRETE).is(itemTag),
            "colorblockmc:glowing_concrete missing from c:concretes item tag");

        helper.succeed();
    }
}
