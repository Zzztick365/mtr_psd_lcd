package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public class MyPSDTopLcd11BE extends MyPSDTopBE {
	public MyPSDTopLcd11BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD11.get(), pos, state);
	}

	public MyPSDTopLcd11BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
