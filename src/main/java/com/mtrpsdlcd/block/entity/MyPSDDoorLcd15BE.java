package com.mtrpsdlcd.block.entity;

import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mod.block.BlockPSDAPGDoorBase;

public class MyPSDDoorLcd15BE extends BlockPSDAPGDoorBase.BlockEntityBase {
	public MyPSDDoorLcd15BE(BlockPos pos, BlockState state) {
		this(com.mtrpsdlcd.registry.BlockEntities.PSD_DOOR_LCD15.get(), pos, state);
	}

	public MyPSDDoorLcd15BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
