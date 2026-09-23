package com.mtrpsdlcd.item;

import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Block;
import com.mtrpsdlcd.block.MyPSDTopLcd14;
import com.mtrpsdlcd.block.MyPSDTopLcd18;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.holder.WorldAccess;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;

public class ItemPSDTopModule extends ItemExtension implements IBlock {

	private final BlockRegistryObject moduleBlock;
	private final boolean doubleWidth;

	public ItemPSDTopModule(BlockRegistryObject moduleBlock, boolean doubleWidth, ItemSettings itemSettings) {
		super(itemSettings);
		this.moduleBlock = moduleBlock;
		this.doubleWidth = doubleWidth;
	}

	@Override
	@Nonnull
	public ActionResult useOnBlock2(ItemUsageContext context) {
		final int horizontal = doubleWidth ? 2 : 1;
		final World world = context.getWorld();
		final Direction facing = context.getPlayerFacing();
		final BlockPos basePos = context.getBlockPos().offset(context.getSide());

		for (int i = 0; i < horizontal; ++i) {
			final BlockPos checkPos = basePos.offset(facing.rotateYClockwise(), i);
			if (!world.getBlockState(checkPos).getBlock().equals(org.mtr.mapping.holder.Blocks.getAirMapped())) {
				return ActionResult.FAIL;
			}
		}

		for (int i = 0; i < horizontal; ++i) {
			final BlockPos newPos = basePos.offset(facing.rotateYClockwise(), i);
			final IBlock.EnumSide side = doubleWidth ? (i == 0 ? IBlock.EnumSide.LEFT : IBlock.EnumSide.RIGHT) : IBlock.EnumSide.SINGLE;
			BlockState state = moduleBlock.get().getDefaultState()
					.with(new Property<>(DirectionHelper.FACING.data), facing.data)
					.with(new Property<>(IBlock.SIDE_EXTENDED.data), side);
			world.setBlockState(newPos, state);

			world.setBlockState(newPos, BlockPSDTop.getActualState(WorldAccess.cast(world), newPos));
		}

		final Block placedBlock = world.getBlockState(basePos).getBlock();
		if (placedBlock.data instanceof MyPSDTopLcd14) {
			MyPSDTopLcd18.recomputeSide(world, basePos, facing);
		}

		context.getStack().decrement(1);
		return ActionResult.SUCCESS;
	}
}
