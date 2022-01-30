// Copied from https://github.com/UltrusBot/ExtraSponges/blob/master/src/main/java/io/github/ultrusbot/extrasponges/block/LavaSpongeBlock.java

package net.hellzone.helpers;

import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Queue;
public class LavaHelper {

    public static boolean isFullLava(BlockState blockState) {
        var fluidState = blockState.getFluidState();
        return fluidState.isIn(FluidTags.LAVA) && blockState.getBlock() instanceof FluidDrainable;
    }
    
    /**
     * Absorbs lava in {@code world} at {@code pos} within a {@code range}, otherwise only stopping if the {@code maxAbsorbAmount} has been reached
     * @return Count of (full) lava blocks absorbed
     */
    public static int absorbLava(World world, BlockPos pos, int range, int maxAbsorbAmount) {
        Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Pair<BlockPos, Integer>(pos, 0));
        int i = 0;
        int fullReplacedLava = 0;

        while (!queue.isEmpty()) {
            Pair<BlockPos, Integer> pair = queue.poll();
            BlockPos blockPos = pair.getLeft();
            int j = pair.getRight();
            Direction[] var8 = Direction.values();
            int var9 = var8.length;

            for (int var10 = 0; var10 < var9; ++var10) {
                Direction direction = var8[var10];
                BlockPos blockPos2 = blockPos.offset(direction);
                BlockState blockState = world.getBlockState(blockPos2);
                FluidState fluidState = world.getFluidState(blockPos2);
                if (fluidState.isIn(FluidTags.LAVA)) {
                    if (blockState.getBlock() instanceof FluidDrainable && !((FluidDrainable) blockState.getBlock())
                            .tryDrainFluid(world, blockPos2, blockState).isEmpty()) {
                        ++i;
                        fullReplacedLava++;
                        if (j < range) {
                            queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
                        }
                    } else if (blockState.getBlock() instanceof FluidBlock) {
                        world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
                        ++i;
                        if (j < range) {
                            queue.add(new Pair<BlockPos, Integer>(blockPos2, j + 1));
                        }
                    }
                }
            }

            if (i > maxAbsorbAmount) {
                break;
            }
        }
        return fullReplacedLava;
    }
}
