package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDDoorLcd2BE;
import com.mtrpsdlcd.registry.BlockEntities;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.mapper.BlockEntityExtension;

import javax.annotation.Nonnull;

public class MyPSDDoorLcd2 extends MyPSDDoor {
	public MyPSDDoorLcd2() {
		super(1);
	}

	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_DOOR_LCD2.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDDoorLcd2BE(blockPos, blockState);
	}
}
