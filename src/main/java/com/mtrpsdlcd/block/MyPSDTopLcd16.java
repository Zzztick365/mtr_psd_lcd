package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd16BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.mapper.BlockEntityExtension;

import javax.annotation.Nonnull;

public class MyPSDTopLcd16 extends MyPSDTopLcd9 {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_TOP_LCD16.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd16BE(blockPos, blockState);
	}
}
