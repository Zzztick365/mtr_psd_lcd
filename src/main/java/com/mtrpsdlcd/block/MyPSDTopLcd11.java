package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd11BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.mapper.BlockEntityExtension;

import javax.annotation.Nonnull;

public class MyPSDTopLcd11 extends MyPSDTopLcd7 implements IStandaloneTopModule {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_TOP_LCD11.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd11BE(blockPos, blockState);
	}
}
