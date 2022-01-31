// Based on https://github.com/UltrusBot/ExtraSponges/blob/master/src/main/java/io/github/ultrusbot/extrasponges/block/LavaSpongeBlock.java

package net.hellzone.blocks;

import net.hellzone.helpers.LavaHelper;
import net.minecraft.block.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class MagmaSpongeBlock extends Block {

    private final int range;
    private final int absorbAmount;
    private Block hotSponge;
    private int lavaAbsorbedCount = 0;
    public static final IntProperty LAVA_ABSORBED = IntProperty.of("lava_absorbed", 0, 64);

    public MagmaSpongeBlock(Settings settings, int range, int absorbAmount) {
        super(settings);
        this.range = range;
        this.absorbAmount = absorbAmount;
    }

    public void setHotSponge(Block hotSponge) {
        this.hotSponge = hotSponge;
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            var replacedLava = LavaHelper.isFullLava(oldState);
            this.update(state, world, pos, replacedLava);
        }
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!player.isCreative()) {
            ItemStack itemStack = new ItemStack(this);
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }

        super.onBreak(world, pos, state, player);
    }

    protected void update(BlockState state, World world, BlockPos pos, boolean replacedLava) {
        lavaAbsorbedCount = LavaHelper.absorbLava(world, pos, range, absorbAmount) + (replacedLava ? 1 : 0);
        System.out.println(lavaAbsorbedCount);
        if (lavaAbsorbedCount > 0) {
            world.setBlockState(pos, hotSponge.getDefaultState().with(LAVA_ABSORBED, lavaAbsorbedCount), 2);
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(Blocks.LAVA.getDefaultState()));
        }
    }

}
