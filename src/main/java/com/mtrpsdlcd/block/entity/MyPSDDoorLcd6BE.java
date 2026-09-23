package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mod.block.BlockPSDAPGDoorBase;

public class MyPSDDoorLcd6BE extends BlockPSDAPGDoorBase.BlockEntityBase {
	public MyPSDDoorLcd6BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_DOOR_LCD6.get(), pos, state);
	}

	public MyPSDDoorLcd6BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
