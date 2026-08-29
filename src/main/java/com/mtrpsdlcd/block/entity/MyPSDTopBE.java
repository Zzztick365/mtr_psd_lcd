package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mod.block.BlockPSDTop;

public class MyPSDTopBE extends BlockPSDTop.BlockEntityBase {
	public MyPSDTopBE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP.get(), pos, state);
	}

	protected MyPSDTopBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
