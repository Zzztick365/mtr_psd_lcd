package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd13BE;
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

public class MyPSDTopLcd13 extends MyPSDTop implements IStandaloneTopModule {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_GLASS_LCD13.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd13BE(blockPos, blockState);
	}

	@Override
	@Nonnull
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		return IBlock.checkHoldingBrush(world, player, () -> {
			final BlockPSDTop.EnumPersistent current = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
			final BlockPSDTop.EnumPersistent next = current == BlockPSDTop.EnumPersistent.ROUTE ? BlockPSDTop.EnumPersistent.NONE : BlockPSDTop.EnumPersistent.ROUTE;
			world.setBlockState(pos, state.with(new Property<>(BlockPSDTop.PERSISTENT.data), next));
			final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
			propagatePersistent(world, pos, facing.rotateYClockwise(), next);
			propagatePersistent(world, pos, facing.rotateYCounterclockwise(), next);
		});
	}

	private void propagatePersistent(World world, BlockPos pos, Direction dir, BlockPSDTop.EnumPersistent value) {
		for (int i = 1; ; i++) {
			final BlockPos nextPos = pos.offset(dir, i);
			final BlockState neighbor = world.getBlockState(nextPos);
			if (!(neighbor.getBlock().data instanceof MyPSDTopLcd13)) {
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
	}
}
