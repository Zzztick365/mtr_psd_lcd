package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Constants;
import org.mtr.mapping.holder.ItemConvertible;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.registry.CreativeModeTabHolder;

public class ItemGroups {
	public static final CreativeModeTabHolder MAIN = ModRegistry.REGISTRY.createCreativeModeTabHolder(Constants.id("main"), () -> new ItemStack(new ItemConvertible(Blocks.PSD_LCD.get().data.asItem())));
}
