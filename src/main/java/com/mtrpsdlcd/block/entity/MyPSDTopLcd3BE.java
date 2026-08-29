package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public class MyPSDTopLcd3BE extends MyPSDTopBE {
	public MyPSDTopLcd3BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD3.get(), pos, state);
	}

	public MyPSDTopLcd3BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
