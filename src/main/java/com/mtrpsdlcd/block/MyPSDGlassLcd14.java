package com.mtrpsdlcd.block;

import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.BlockPSDGlass;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;

public class MyPSDGlassLcd14 extends BlockPSDGlass {
	public MyPSDGlassLcd14() {
		super(0);
	}

	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_GLASS_LCD14.get();
	}

	@Override
	@Nonnull
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		return IBlock.checkHoldingBrush(world, player, () -> {

			for (int dy = -1; dy <= 1; dy++) {
				final BlockPos upPos = pos.up(dy);
				final BlockState upState = world.getBlockState(upPos);
				if (upState.getBlock().data instanceof MyPSDGlassLcd14) {
					connectGlass(world, upPos, upState);
				}
			}

			final IBlock.DoubleBlockHalf half = IBlock.getStatePropertySafe(state, IBlock.HALF);
			final BlockPos topPos = half == IBlock.DoubleBlockHalf.LOWER ? pos.up(2) : pos.up(1);
			final BlockState topState = world.getBlockState(topPos);
			if (topState.getBlock().data instanceof MyPSDTopLcd14) {
				final BlockPSDTop.EnumPersistent current = IBlock.getStatePropertySafe(topState, BlockPSDTop.PERSISTENT);
				final BlockPSDTop.EnumPersistent next = current == BlockPSDTop.EnumPersistent.ROUTE ? BlockPSDTop.EnumPersistent.NONE : BlockPSDTop.EnumPersistent.ROUTE;
				final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
				toggleTopPersistent(world, topPos, next);
				propagateGlassChain(world, pos, facing.rotateYClockwise(), next);
				propagateGlassChain(world, pos, facing.rotateYCounterclockwise(), next);
			}
		});
	}

	private void toggleTopPersistent(World world, BlockPos topPos, BlockPSDTop.EnumPersistent value) {
		final BlockState top = world.getBlockState(topPos);
		if (top.getBlock().data instanceof MyPSDTopLcd14) {
			world.setBlockState(topPos, top.with(new Property<>(BlockPSDTop.PERSISTENT.data), value));
		}
	}

	private void propagateGlassChain(World world, BlockPos glassPos, Direction dir, BlockPSDTop.EnumPersistent value) {
		for (int i = 1; ; i++) {
			final BlockPos nextGlass = glassPos.offset(dir, i);
			final BlockState nextState = world.getBlockState(nextGlass);
			if (!(nextState.getBlock().data instanceof MyPSDGlassLcd14)) {
				break;
			}
			final IBlock.DoubleBlockHalf half = IBlock.getStatePropertySafe(nextState, IBlock.HALF);
			toggleTopPersistent(world, half == IBlock.DoubleBlockHalf.LOWER ? nextGlass.up(2) : nextGlass.up(1), value);
		}
	}

	private void connectGlass(World world, BlockPos pos, BlockState state) {
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);

		final BlockPos leftPos = pos.offset(facing.rotateYCounterclockwise());
		final BlockState leftState = world.getBlockState(leftPos);
		final boolean hasLeft = leftState.getBlock().data instanceof MyPSDGlassLcd14;
		if (hasLeft) {
			final IBlock.EnumSide leftSide = IBlock.getStatePropertySafe(leftState, IBlock.SIDE_EXTENDED);
			final IBlock.EnumSide newLeft = leftSide == IBlock.EnumSide.RIGHT ? IBlock.EnumSide.MIDDLE : (leftSide == IBlock.EnumSide.SINGLE ? IBlock.EnumSide.LEFT : leftSide);
			world.setBlockState(leftPos, leftState.with(new Property<>(IBlock.SIDE_EXTENDED.data), newLeft));
		}

		final BlockPos rightPos = pos.offset(facing.rotateYClockwise());
		final BlockState rightState = world.getBlockState(rightPos);
		final boolean hasRight = rightState.getBlock().data instanceof MyPSDGlassLcd14;
		if (hasRight) {
			final IBlock.EnumSide rightSide = IBlock.getStatePropertySafe(rightState, IBlock.SIDE_EXTENDED);
			final IBlock.EnumSide newRight = rightSide == IBlock.EnumSide.LEFT ? IBlock.EnumSide.MIDDLE : (rightSide == IBlock.EnumSide.SINGLE ? IBlock.EnumSide.RIGHT : rightSide);
			world.setBlockState(rightPos, rightState.with(new Property<>(IBlock.SIDE_EXTENDED.data), newRight));
		}

		final IBlock.EnumSide ownSide = hasLeft && hasRight ? IBlock.EnumSide.MIDDLE : (hasLeft ? IBlock.EnumSide.RIGHT : (hasRight ? IBlock.EnumSide.LEFT : IBlock.EnumSide.SINGLE));
		world.setBlockState(pos, state.with(new Property<>(IBlock.SIDE_EXTENDED.data), ownSide));
	}

	@Override
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		final IBlock.DoubleBlockHalf half = IBlock.getStatePropertySafe(state, IBlock.HALF);
		final BlockPos topPos = half == IBlock.DoubleBlockHalf.LOWER ? pos.up(2) : pos.up(1);
		final BlockState topState = world.getBlockState(topPos);
		if (topState.getBlock().data instanceof MyPSDTopLcd14) {
			world.setBlockState(topPos, org.mtr.mapping.holder.Blocks.getAirMapped().getDefaultState(), 34);
		}
		super.onBreak2(world, pos, state, player);

		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
		recalcGlassSide(world, pos, facing, facing.rotateYClockwise());
		recalcGlassSide(world, pos, facing, facing.rotateYCounterclockwise());
	}

	private void recalcGlassSide(World world, BlockPos pos, Direction facing, Direction dir) {
		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(dir, i);
			final BlockState neighbor = world.getBlockState(nextPos);
			final IBlock.DoubleBlockHalf half = IBlock.getStatePropertySafe(neighbor, IBlock.HALF);

			final boolean isCol = neighbor.getBlock().data instanceof MyPSDGlassLcd14;
			if (!isCol) {
				break;
			}

			final BlockPos other = half == IBlock.DoubleBlockHalf.LOWER ? nextPos.up(1) : nextPos.down(1);
			recalcOneGlassTop(world, nextPos, facing);
			if (world.getBlockState(other).getBlock().data instanceof MyPSDGlassLcd14) {
				recalcOneGlassTop(world, other, facing);
			}
		}
	}

	private void recalcOneGlassTop(World world, BlockPos pos, Direction facing) {
		final BlockState state = world.getBlockState(pos);
		final boolean hasLeft = world.getBlockState(pos.offset(facing.rotateYCounterclockwise())).getBlock().data instanceof MyPSDGlassLcd14;
		final boolean hasRight = world.getBlockState(pos.offset(facing.rotateYClockwise())).getBlock().data instanceof MyPSDGlassLcd14;
		final IBlock.EnumSide ownSide = hasLeft && hasRight ? IBlock.EnumSide.MIDDLE : (hasLeft ? IBlock.EnumSide.RIGHT : (hasRight ? IBlock.EnumSide.LEFT : IBlock.EnumSide.SINGLE));
		world.setBlockState(pos, state.with(new Property<>(IBlock.SIDE_EXTENDED.data), ownSide));
	}
}
