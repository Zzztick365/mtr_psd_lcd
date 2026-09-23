package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;

public class MyPSDTopLcd14 extends MyPSDTop implements IStandaloneTopModule {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_GLASS_LCD14.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd14BE(blockPos, blockState);
	}

	@Override
	@Nonnull
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		return IBlock.checkHoldingBrush(world, player, () -> {
			final org.mtr.mapping.holder.BlockEntity be = world.getBlockEntity(pos);
			if (be != null && be.data instanceof MyPSDTopLcd14BE) {
				final MyPSDTopLcd14BE lcd14be = (MyPSDTopLcd14BE) be.data;
				final BlockPSDTop.EnumPersistent current = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
				final int newIndex;
				if (current == BlockPSDTop.EnumPersistent.NONE) {

					world.setBlockState(pos, state.with(new Property<>(BlockPSDTop.PERSISTENT.data), BlockPSDTop.EnumPersistent.ROUTE));
					newIndex = 0;
				} else {

					newIndex = lcd14be.getRouteIndex();
					final boolean flipped = !PSDCustomText.readDirectionFlip(world, pos);
					PSDCustomText.applyDirectionFlip(world, pos, flipped);
					if (com.mtrpsdlcd.Constants.DEBUG_LOG) org.mtr.mod.Init.LOGGER.info("[LCD14] onUse2 翻转显示方向 pos={} → flip={}", pos, flipped);
				}
				if (com.mtrpsdlcd.Constants.DEBUG_LOG) org.mtr.mod.Init.LOGGER.info("[LCD14] onUse2 刷子 pos={} oldRouteIndex={} newRouteIndex={} persistent={}", pos, lcd14be.getRouteIndex(), newIndex, current);

				lcd14be.setRouteIndex(newIndex);
				final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
				propagateRouteIndex(world, pos, facing.rotateYClockwise(), newIndex);
				propagateRouteIndex(world, pos, facing.rotateYCounterclockwise(), newIndex);

				propagatePersistent(world, pos, facing.rotateYClockwise(), BlockPSDTop.EnumPersistent.ROUTE);
				propagatePersistent(world, pos, facing.rotateYCounterclockwise(), BlockPSDTop.EnumPersistent.ROUTE);

				MyPSDTopLcd18.recomputeSide(world, pos, facing);

				mergeGlassRun(world, pos, facing);
			}
		});
	}

	private void mergeGlassRun(World world, BlockPos anchor, Direction facing) {
		recalcGlassColumn(world, anchor, facing);
		for (int dir = 0; dir < 2; dir++) {
			final Direction d = dir == 0 ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
			for (BlockPos p = anchor.offset(d); ; p = p.offset(d)) {
				if (!(world.getBlockState(p).getBlock().data instanceof MyPSDTopLcd14)) {
					break;
				}
				recalcGlassColumn(world, p, facing);
			}
		}
	}

	private void recalcGlassColumn(World world, BlockPos topPos, Direction facing) {
		for (int dy = 1; dy <= 2; dy++) {
			final BlockPos gPos = topPos.down(dy);
			final BlockState gState = world.getBlockState(gPos);
			final org.mtr.mapping.holder.Block gBlock = gState.getBlock();
			if (!isOurPsdGlass(gBlock)) {
				continue;
			}
			final boolean hasLeft = world.getBlockState(gPos.offset(facing.rotateYCounterclockwise())).getBlock().data.getClass() == gBlock.data.getClass();
			final boolean hasRight = world.getBlockState(gPos.offset(facing.rotateYClockwise())).getBlock().data.getClass() == gBlock.data.getClass();
			final IBlock.EnumSide ownSide = hasLeft && hasRight ? IBlock.EnumSide.MIDDLE : (hasLeft ? IBlock.EnumSide.RIGHT : (hasRight ? IBlock.EnumSide.LEFT : IBlock.EnumSide.SINGLE));
			world.setBlockState(gPos, gState.with(new Property<>(IBlock.SIDE_EXTENDED.data), ownSide));
		}
	}

	private static boolean isOurPsdGlass(org.mtr.mapping.holder.Block block) {
		return block.data instanceof MyPSDGlassLcd14 || block.data instanceof MyPSDGlassLcd17 || block.data instanceof MyPSDGlassLcd22;
	}

	private void propagateRouteIndex(World world, BlockPos pos, Direction dir, int index) {
		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(dir, i);
			final BlockState neighbor = world.getBlockState(nextPos);
			if (!(neighbor.getBlock().data instanceof MyPSDTopLcd14)) {
				break;
			}
			final org.mtr.mapping.holder.BlockEntity be = world.getBlockEntity(nextPos);
			if (be != null && be.data instanceof MyPSDTopLcd14BE) {
				((MyPSDTopLcd14BE) be.data).setRouteIndex(index);
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

	@Override
	@Nonnull
	public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return IBlock.getVoxelShapeByDirection(0.0, 0.0, 0.0, 16.0, 16.0, 6.0, IBlock.getStatePropertySafe(state, BlockPSDTop.FACING));
	}

	@Override
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {

		super.onBreak2(world, pos, state, player);
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
		refreshTopSide(world, pos, facing);
	}

	private void refreshTopSide(World world, BlockPos pos, Direction facing) {

		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(facing.rotateYClockwise(), i);
			if (!(world.getBlockState(nextPos).getBlock().data instanceof MyPSDTopLcd14)) {
				break;
			}
			recalcTopSide(world, nextPos, facing);
		}

		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(facing.rotateYCounterclockwise(), i);
			if (!(world.getBlockState(nextPos).getBlock().data instanceof MyPSDTopLcd14)) {
				break;
			}
			recalcTopSide(world, nextPos, facing);
		}
	}

	private void recalcTopSide(World world, BlockPos pos, Direction facing) {
		final BlockState state = world.getBlockState(pos);
		final boolean hasLeft = world.getBlockState(pos.offset(facing.rotateYCounterclockwise())).getBlock().data instanceof MyPSDTopLcd14;
		final boolean hasRight = world.getBlockState(pos.offset(facing.rotateYClockwise())).getBlock().data instanceof MyPSDTopLcd14;
		final IBlock.EnumSide ownSide = hasLeft && hasRight ? IBlock.EnumSide.MIDDLE : (hasLeft ? IBlock.EnumSide.RIGHT : (hasRight ? IBlock.EnumSide.LEFT : IBlock.EnumSide.SINGLE));
		world.setBlockState(pos, state.with(new Property<>(IBlock.SIDE_EXTENDED.data), ownSide));
	}
}
