package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public class MyPSDTopLcd22BE extends MyPSDTopLcd14BE {
	public MyPSDTopLcd22BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD22.get(), pos, state);
	}

	public MyPSDTopLcd22BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
