package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDDoorLcd4BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.mapper.BlockEntityExtension;

import javax.annotation.Nonnull;

public class MyPSDDoorLcd4 extends MyPSDDoor {
	public MyPSDDoorLcd4() {
		super(0);
	}

	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_DOOR_LCD4.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDDoorLcd4BE(blockPos, blockState);
	}
}
