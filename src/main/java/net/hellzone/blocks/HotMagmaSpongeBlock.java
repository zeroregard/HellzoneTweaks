// Based on https://github.com/UltrusBot/ExtraSponges/blob/master/src/main/java/io/github/ultrusbot/extrasponges/block/HotLavaSpongeBlock.java

package net.hellzone.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

public class HotMagmaSpongeBlock extends Block {
    private final BlockState originalBlock;
    public static final IntProperty LAVA_ABSORBED = IntProperty.of("lava_absorbed", 0, 64);

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(LAVA_ABSORBED);
    }

    public HotMagmaSpongeBlock(Settings settings, BlockState originalBlock) {
        super(settings);
        this.originalBlock = originalBlock;
        setDefaultState(getStateManager().getDefaultState().with(LAVA_ABSORBED, 1));
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        var lavaAbsorbedValue = stack.getOrCreateNbt().getInt("lava_absorbed");
        world.setBlockState(pos, state.with(LAVA_ABSORBED, lavaAbsorbedValue));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        boolean isTouchingWater = false;
        for (BlockPos blockPos : BlockPos.iterateOutwards(pos, 1, 1, 1)) {
            if (world.getBlockState(blockPos).getFluidState().isIn(FluidTags.WATER)) {
                isTouchingWater = true;
            }
        }
        if (isTouchingWater) {
            convertToColdMagmaSponge(state, world, pos);
        }
    }

    private void convertToColdMagmaSponge(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, originalBlock, 3);
        world.syncWorldEvent(2009, pos, 0);
        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F,
                (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
    }

    public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
        List<ItemStack> stacks = super.getDroppedStacks(state, builder);
        for (ItemStack stack : stacks) {
            stack.getOrCreateNbt().putInt("lava_absorbed", state.get(LAVA_ABSORBED).intValue());
        }
        return stacks;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        var stack = player.getMainHandStack();
        if (stack.getItem() == Items.BUCKET) {
            // offer lava bucket, remove a bucket from that stack
            player.getInventory().offerOrDrop(new ItemStack(Items.LAVA_BUCKET, 1));
            stack.setCount(stack.getCount() - 1);

            // update lava count and possibly go back to the cold sponge
            var lavaAbsorbedValue = state.get(LAVA_ABSORBED).intValue() - 1;
            if (lavaAbsorbedValue < 0) { // crash prevention
                lavaAbsorbedValue = 0;
            }
            world.setBlockState(pos, state.with(LAVA_ABSORBED, lavaAbsorbedValue));
            if (lavaAbsorbedValue == 0) {
                convertToColdMagmaSponge(state, world, pos);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction direction = Direction.random(random);
        if (direction != Direction.UP) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!state.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                double d = pos.getX();
                double e = pos.getY();
                double f = pos.getZ();
                if (direction == Direction.DOWN) {
                    e -= 0.05D;
                    d += random.nextDouble();
                    f += random.nextDouble();
                } else {
                    e += random.nextDouble() * 0.8D;
                    if (direction.getAxis() == Direction.Axis.X) {
                        f += random.nextDouble();
                        if (direction == Direction.EAST) {
                            ++d;
                        } else {
                            d += 0.05D;
                        }
                    } else {
                        d += random.nextDouble();
                        if (direction == Direction.SOUTH) {
                            ++f;
                        } else {
                            f += 0.05D;
                        }
                    }
                }

                world.addParticle(ParticleTypes.DRIPPING_LAVA, d, e, f, 0.0D, 0.0D, 0.0D);
            }
        }
    }

}