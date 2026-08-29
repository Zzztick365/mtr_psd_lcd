package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDDoorBE;
import com.mtrpsdlcd.registry.BlockEntities;
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
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.BlockPSDDoor;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;

public class MyPSDDoor extends BlockPSDDoor {
	private final int style;

	public MyPSDDoor(int style) {
		super(style);
		this.style = style;
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDDoorBE(style == 0 ? BlockEntities.PSD_DOOR.get() : BlockEntities.PSD_DOOR_2.get(), blockPos, blockState);
	}

	@Override
	@Nonnull
	public Item asItem2() {
		return style == 0 ? Items.PSD_DOOR.get() : Items.PSD_DOOR_2.get();
	}

	@Override
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		return IBlock.checkHoldingBrush(world, player, () -> {
			final boolean unlocked = IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.UNLOCKED);
			for (int y = -1; y <= 1; ++y) {
				final BlockState scanState = world.getBlockState(pos.up(y));
				if (!state.isOf(scanState.getBlock())) {
					continue;
				}
				lockDoor(world, pos.up(y), scanState, !unlocked);
			}
			player.sendMessage((unlocked ? org.mtr.mod.generated.lang.TranslationProvider.GUI_MTR_PSD_APG_DOOR_LOCKED : org.mtr.mod.generated.lang.TranslationProvider.GUI_MTR_PSD_APG_DOOR_UNLOCKED).getText(new Object[0]), true);
		});
	}

	private void lockDoor(World world, BlockPos pos, BlockState state, boolean unlocked) {
		BlockState toggled;
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.FACING);
		final BlockPos leftPos = pos.offset(facing.rotateYCounterclockwise());
		final BlockPos rightPos = pos.offset(facing.rotateYClockwise());
		final BlockState leftState = world.getBlockState(leftPos);
		final BlockState rightState = world.getBlockState(rightPos);
		if (leftState.isOf(state.getBlock())) {
			toggled = leftState.with(new Property<>(BlockPSDAPGDoorBase.UNLOCKED.data), unlocked);
			world.setBlockState(leftPos, toggled);
		}
		if (rightState.isOf(state.getBlock())) {
			toggled = rightState.with(new Property<>(BlockPSDAPGDoorBase.UNLOCKED.data), unlocked);
			world.setBlockState(rightPos, toggled);
		}
		world.setBlockState(pos, state.with(new Property<>(BlockPSDAPGDoorBase.UNLOCKED.data), unlocked));
	}
}
