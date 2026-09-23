package com.mtrpsdlcd.item;

import com.mtrpsdlcd.block.PSDCustomText;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.ItemExtension;

import javax.annotation.Nonnull;

public class MyPSDTool extends ItemExtension {
	public MyPSDTool(ItemSettings itemSettings) {
		super(itemSettings);
	}

	@Override
	@Nonnull
	public ActionResult useOnBlock2(ItemUsageContext context) {
		final World world = context.getWorld();
		final BlockPos topPos = PSDCustomText.resolveTopPos(world, context.getBlockPos());
		if (topPos == null) {
			return ActionResult.PASS;
		}
		if (world.isClient()) {

			com.mtrpsdlcd.client.CustomTextScreenOpener.open(topPos, PSDCustomText.read(world, topPos), PSDCustomText.readImage(world, topPos));
		}
		return ActionResult.SUCCESS;
	}
}
