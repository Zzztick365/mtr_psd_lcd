package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd5BE;
import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.mapper.BlockEntityExtension;

import javax.annotation.Nonnull;

public class MyPSDTopLcd5 extends MyPSDTop {
	@Override
	@Nonnull
	public Item asItem2() {
		return Items.PSD_DOOR_LCD5.get();
	}

	@Override
	@Nonnull
	public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MyPSDTopLcd5BE(blockPos, blockState);
	}
}
