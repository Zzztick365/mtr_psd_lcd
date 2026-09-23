package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public class MyPSDTopLcd10BE extends MyPSDTopBE {
	public MyPSDTopLcd10BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD10.get(), pos, state);
	}

	public MyPSDTopLcd10BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
