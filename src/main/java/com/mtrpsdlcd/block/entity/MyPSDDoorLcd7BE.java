package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mod.block.BlockPSDAPGDoorBase;

public class MyPSDDoorLcd7BE extends BlockPSDAPGDoorBase.BlockEntityBase {
	public MyPSDDoorLcd7BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_DOOR_LCD7.get(), pos, state);
	}

	public MyPSDDoorLcd7BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
