package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public class MyPSDTopLcd12BE extends MyPSDTopBE {
	public MyPSDTopLcd12BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD12.get(), pos, state);
	}

	public MyPSDTopLcd12BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
