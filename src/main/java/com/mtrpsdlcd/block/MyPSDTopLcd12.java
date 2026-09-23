package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd12BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
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

public class MyPSDTopLcd12 extends MyPSDTop implements IStandaloneTopModule {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_TOP_LCD12.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd12BE(blockPos, blockState);
	}

	@Override
	@Nonnull
	public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

		return IBlock.getVoxelShapeByDirection(0.0, 0.0, 0.0, 16.0, 16.0, 6.0, IBlock.getStatePropertySafe(state, BlockPSDTop.FACING));
	}

	@Override
	@Nonnull
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		return IBlock.checkHoldingItem(world, player, item -> {
			if (item.data instanceof org.mtr.mod.item.ItemBrush) {
				final BlockPSDTop.EnumPersistent current = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
				final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
				if (current != BlockPSDTop.EnumPersistent.ROUTE) {

					world.setBlockState(pos, state.with(new Property<>(BlockPSDTop.PERSISTENT.data), BlockPSDTop.EnumPersistent.ROUTE));
					propagatePersistent(world, pos, facing.rotateYClockwise(), BlockPSDTop.EnumPersistent.ROUTE);
					propagatePersistent(world, pos, facing.rotateYCounterclockwise(), BlockPSDTop.EnumPersistent.ROUTE);
				} else {

					final int arrow = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
					final int nextArrow = arrow == 2 ? 0 : 2;
					world.setBlockState(pos, state.with(new Property<>(BlockPSDTop.ARROW_DIRECTION.data), nextArrow));
					propagateArrow(world, pos, facing.rotateYClockwise(), nextArrow);
					propagateArrow(world, pos, facing.rotateYCounterclockwise(), nextArrow);
				}
			}
		}, null, org.mtr.mod.Items.BRUSH.get(), org.mtr.mapping.holder.Items.getShearsMapped());
	}

	private void propagatePersistent(World world, BlockPos pos, Direction dir, BlockPSDTop.EnumPersistent value) {
		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(dir, i);
			final BlockState neighbor = world.getBlockState(nextPos);
			final Block neighborBlock = neighbor.getBlock();
			if (!(neighborBlock.data instanceof MyPSDTopLcd12)) {
				break;
			}
			world.setBlockState(nextPos, neighbor.with(new Property<>(BlockPSDTop.PERSISTENT.data), value));
		}
	}

	private void propagateArrow(World world, BlockPos pos, Direction dir, int value) {
		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(dir, i);
			final BlockState neighbor = world.getBlockState(nextPos);
			if (!(neighbor.getBlock().data instanceof MyPSDTopLcd12)) {
				break;
			}
			world.setBlockState(nextPos, neighbor.with(new Property<>(BlockPSDTop.ARROW_DIRECTION.data), value));
		}
	}
}
