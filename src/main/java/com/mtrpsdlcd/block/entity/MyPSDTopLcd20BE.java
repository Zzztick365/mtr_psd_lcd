package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public class MyPSDTopLcd20BE extends MyPSDTopLcd14BE {

	public MyPSDTopLcd20BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD20.get(), pos, state);
	}

	public MyPSDTopLcd20BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
