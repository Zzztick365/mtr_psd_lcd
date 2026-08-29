package com.mtrpsdlcd.block;

import com.mtrpsdlcd.registry.Items;
import org.mtr.mapping.holder.Item;
import org.mtr.mod.block.BlockPSDGlass;

import javax.annotation.Nonnull;

public class MyPSDGlass extends BlockPSDGlass {
	private final int style;

	public MyPSDGlass(int style) {
		super(style);
		this.style = style;
	}

	@Override
	@Nonnull
	public Item asItem2() {
		return style == 0 ? Items.PSD_GLASS.get() : Items.PSD_GLASS_2.get();
	}
}
