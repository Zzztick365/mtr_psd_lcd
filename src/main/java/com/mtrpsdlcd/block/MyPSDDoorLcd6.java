package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDDoorLcd6BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.mapper.BlockEntityExtension;

import javax.annotation.Nonnull;

public class MyPSDDoorLcd6 extends MyPSDDoor {
	public MyPSDDoorLcd6() {
		super(0);
	}

	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_DOOR_LCD6.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDDoorLcd6BE(blockPos, blockState);
	}
}
