package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd15BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.mapper.BlockEntityExtension;

import javax.annotation.Nonnull;

public class MyPSDTopLcd15 extends MyPSDTopLcd2 {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_DOOR_LCD15.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd15BE(blockPos, blockState);
	}
}
