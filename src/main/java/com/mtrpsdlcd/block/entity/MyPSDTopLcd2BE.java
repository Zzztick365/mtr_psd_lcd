package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public class MyPSDTopLcd2BE extends MyPSDTopBE {
	public MyPSDTopLcd2BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD2.get(), pos, state);
	}

	public MyPSDTopLcd2BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
