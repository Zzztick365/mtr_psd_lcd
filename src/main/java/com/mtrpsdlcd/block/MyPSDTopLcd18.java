package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd18BE;
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
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;

public class MyPSDTopLcd18 extends MyPSDTopLcd14 {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_TOP_LCD18.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd18BE(blockPos, blockState);
	}

	@Override
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak2(world, pos, state, player);
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
		recomputeSideAfterBreak(world, pos, facing);
	}

	@Override
	@Nonnull
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		return IBlock.checkHoldingBrush(world, player, () -> {
			final org.mtr.mapping.holder.BlockEntity be = world.getBlockEntity(pos);
			if (be != null && be.data instanceof MyPSDTopLcd18BE) {
				final MyPSDTopLcd18BE lcd18be = (MyPSDTopLcd18BE) be.data;
				final BlockPSDTop.EnumPersistent current = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
				final int newIndex;
				if (current == BlockPSDTop.EnumPersistent.NONE) {
					world.setBlockState(pos, state.with(new Property<>(BlockPSDTop.PERSISTENT.data), BlockPSDTop.EnumPersistent.ROUTE));
					newIndex = 0;
				} else {

					newIndex = lcd18be.getRouteIndex();
					final boolean flipped = !PSDCustomText.readDirectionFlip(world, pos);
					PSDCustomText.applyDirectionFlip(world, pos, flipped);
					if (com.mtrpsdlcd.Constants.DEBUG_LOG) org.mtr.mod.Init.LOGGER.info("[LCD18] onUse2 翻转显示方向 pos={} → flip={}", pos, flipped);
				}
				lcd18be.setRouteIndex(newIndex);
				final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
				propagateRouteIndex(world, pos, facing.rotateYClockwise(), newIndex);
				propagateRouteIndex(world, pos, facing.rotateYCounterclockwise(), newIndex);
				propagatePersistent(world, pos, facing.rotateYClockwise(), BlockPSDTop.EnumPersistent.ROUTE);
				propagatePersistent(world, pos, facing.rotateYCounterclockwise(), BlockPSDTop.EnumPersistent.ROUTE);
			}
		});
	}

	private void propagateRouteIndex(World world, BlockPos pos, Direction dir, int index) {
		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(dir, i);
			final BlockState neighbor = world.getBlockState(nextPos);
			if (!(neighbor.getBlock().data instanceof MyPSDTopLcd14)) {
				break;
			}
			final org.mtr.mapping.holder.BlockEntity be = world.getBlockEntity(nextPos);
			if (be != null && be.data instanceof com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE) {
				((com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE) be.data).setRouteIndex(index);
			}
		}
	}

	private void propagatePersistent(World world, BlockPos pos, Direction dir, BlockPSDTop.EnumPersistent value) {
		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(dir, i);
			final BlockState neighbor = world.getBlockState(nextPos);
			if (!(neighbor.getBlock().data instanceof MyPSDTopLcd14)) {
				break;
			}
			world.setBlockState(nextPos, neighbor.with(new Property<>(BlockPSDTop.PERSISTENT.data), value));
		}
	}

	public static void recomputeSide(World world, BlockPos pos, Direction facing) {
		recomputeSideRun(world, pos, facing, null);
	}

	public static void recomputeSideAfterBreak(World world, BlockPos brokenPos, Direction facing) {
		recomputeSideRun(world, brokenPos, facing, brokenPos);
	}

	private static void recomputeSideRun(World world, BlockPos anchor, Direction facing, BlockPos excluded) {

		for (BlockPos p = anchor.offset(facing.rotateYCounterclockwise()); ; p = p.offset(facing.rotateYCounterclockwise())) {
			if (!(world.getBlockState(p).getBlock().data instanceof MyPSDTopLcd14)) {
				break;
			}
			recalcGlassSide(world, p, facing, excluded);
		}

		for (BlockPos p = anchor.offset(facing.rotateYClockwise()); ; p = p.offset(facing.rotateYClockwise())) {
			if (!(world.getBlockState(p).getBlock().data instanceof MyPSDTopLcd14)) {
				break;
			}
			recalcGlassSide(world, p, facing, excluded);
		}

		if (!anchor.equals(excluded)) {
			recalcGlassSide(world, anchor, facing, excluded);
		}
	}

	private static void recalcGlassSide(World world, BlockPos pos, Direction facing, BlockPos excluded) {
		final BlockState state = world.getBlockState(pos);
		if (!(state.getBlock().data instanceof MyPSDTopLcd14)) {
			return;
		}

		final BlockState leftState = world.getBlockState(pos.offset(facing.rotateYCounterclockwise()));
		final boolean hasLeft = !pos.offset(facing.rotateYCounterclockwise()).equals(excluded) && leftState.getBlock().data instanceof MyPSDTopLcd14;

		final BlockState rightState = world.getBlockState(pos.offset(facing.rotateYClockwise()));
		final boolean hasRight = !pos.offset(facing.rotateYClockwise()).equals(excluded) && rightState.getBlock().data instanceof MyPSDTopLcd14;
		final IBlock.EnumSide ownSide = hasLeft && hasRight ? IBlock.EnumSide.MIDDLE : (hasLeft ? IBlock.EnumSide.RIGHT : (hasRight ? IBlock.EnumSide.LEFT : IBlock.EnumSide.SINGLE));
		world.setBlockState(pos, state.with(new Property<>(IBlock.SIDE_EXTENDED.data), ownSide));
	}
}
