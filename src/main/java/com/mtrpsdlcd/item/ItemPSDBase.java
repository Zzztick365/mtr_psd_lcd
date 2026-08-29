package com.mtrpsdlcd.item;

import com.mtrpsdlcd.registry.Blocks;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.StringIdentifiable;
import org.mtr.mapping.holder.TextFormatting;
import org.mtr.mapping.holder.TooltipContext;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.holder.WorldAccess;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.block.BlockPSDAPGBase;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemPSDBase extends ItemExtension implements IBlock {
	private final EnumPSDAPGItem item;
	private final EnumPSDAPGType type;

	public ItemPSDBase(EnumPSDAPGItem item, EnumPSDAPGType type, ItemSettings itemSettings) {
		super(itemSettings);
		this.item = item;
		this.type = type;
	}

	@Override
	@Nonnull
	public ActionResult useOnBlock2(ItemUsageContext context) {
		final int horizontalBlocks = this.item.isDoor ? 2 : 1;
		if (ItemPSDBase.blocksNotReplaceable(context, horizontalBlocks, 3, this.getBlockStateFromItem().getBlock())) {
			return ActionResult.FAIL;
		}
		final World world = context.getWorld();
		final Direction playerFacing = context.getPlayerFacing();
		final BlockPos pos = context.getBlockPos().offset(context.getSide());
		for (int x = 0; x < horizontalBlocks; ++x) {
			final BlockPos newPos = pos.offset(playerFacing.rotateYClockwise(), x);
			for (int y = 0; y < 2; ++y) {
				BlockState state = this.getBlockStateFromItem().with(new Property<>(BlockPSDAPGBase.FACING.data), playerFacing.data).with(new Property<>(IBlock.HALF.data), y == 1 ? IBlock.DoubleBlockHalf.UPPER : IBlock.DoubleBlockHalf.LOWER);
				if (this.item.isDoor) {
					final BlockState neighborState = state.with(new Property<>(IBlock.SIDE.data), x == 0 ? IBlock.EnumSide.LEFT : IBlock.EnumSide.RIGHT);
					world.setBlockState(newPos.up(y), neighborState);
				} else {
					world.setBlockState(newPos.up(y), state.with(new Property<>(IBlock.SIDE_EXTENDED.data), IBlock.EnumSide.SINGLE));
				}
			}
				if (this.type.isPSD) {

					final BlockPos topPos = newPos.up(2);
					if (this.item.isDoor) {
						final BlockRegistryObject topBlock;
						switch (this.type) {
							case PSD_2_LCD2:
								topBlock = Blocks.PSD_TOP_LCD2;
								break;
							case PSD_2_LCD3:
								topBlock = Blocks.PSD_TOP_LCD3;
								break;
							case PSD_2_LCD4:
								topBlock = Blocks.PSD_TOP_LCD4;
								break;
							case PSD_2_LCD5:
								topBlock = Blocks.PSD_TOP_LCD5;
								break;
							case PSD_2_LCD6:
								topBlock = Blocks.PSD_TOP_LCD6;
								break;
							case PSD_2_LCD7:
								topBlock = Blocks.PSD_TOP_LCD7;
								break;
							default:
								topBlock = Blocks.PSD_TOP;
								break;
						}
						world.setBlockState(topPos, topBlock.get().getDefaultState());
					} else {

						world.setBlockState(topPos, org.mtr.mod.Blocks.PSD_TOP.get().getDefaultState());
					}
					world.setBlockState(topPos, BlockPSDTop.getActualState(WorldAccess.cast(world), topPos));
				}
		}
		context.getStack().decrement(1);
		return ActionResult.SUCCESS;
	}

	@Override
	public void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
		tooltip.add(TextHelper.translatable("tooltip.mtr_psd_lcd." + this.item.name, new Object[0]).formatted(TextFormatting.GRAY));
	}

	private BlockState getBlockStateFromItem() {
		switch (this.type) {
			case PSD_1:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS.get().getDefaultState();
				}
				break;
			case PSD_2:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR_2.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS_2.get().getDefaultState();
				}
				break;
			case PSD_2_LCD2:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR_LCD2.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS_2.get().getDefaultState();
				}
				break;
			case PSD_2_LCD3:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR_LCD3.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS_2.get().getDefaultState();
				}
				break;
			case PSD_2_LCD4:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR_LCD4.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS_2.get().getDefaultState();
				}
				break;
			case PSD_2_LCD5:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR_LCD5.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS_2.get().getDefaultState();
				}
				break;
			case PSD_2_LCD6:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR_LCD6.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS_2.get().getDefaultState();
				}
				break;
			case PSD_2_LCD7:
				switch (this.item) {
					case PSD_APG_DOOR:
						return Blocks.PSD_DOOR_LCD7.get().getDefaultState();
					case PSD_APG_GLASS:
						return Blocks.PSD_GLASS_2.get().getDefaultState();
				}
				break;
		}
		return org.mtr.mapping.holder.Blocks.getAirMapped().getDefaultState();
	}

	public static boolean blocksNotReplaceable(ItemUsageContext context, int width, int height, @Nullable Block blacklistBlock) {
		final Direction facing = context.getPlayerFacing();
		final World world = context.getWorld();
		final BlockPos startingPos = context.getBlockPos().offset(context.getSide());
		for (int x = 0; x < width; ++x) {
			final BlockPos offsetPos = startingPos.offset(facing.rotateYClockwise(), x);
			if (blacklistBlock != null) {
				final boolean isBlacklistedBelow = world.getBlockState(offsetPos.down()).isOf(blacklistBlock);
				final boolean isBlacklistedAbove = world.getBlockState(offsetPos.up(height)).isOf(blacklistBlock);
				if (isBlacklistedBelow || isBlacklistedAbove) {
					return true;
				}
			}
			for (int y = 0; y < height; ++y) {
				if (world.getBlockState(offsetPos.up(y)).getBlock().equals(org.mtr.mapping.holder.Blocks.getAirMapped())) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	public enum EnumPSDAPGItem implements StringIdentifiable {
		PSD_APG_DOOR("psd_apg_door", true),
		PSD_APG_GLASS("psd_apg_glass", false);

		private final String name;
		private final boolean isDoor;

		EnumPSDAPGItem(String name, boolean isDoor) {
			this.name = name;
			this.isDoor = isDoor;
		}

		@Override
		@Nonnull
		public String asString2() {
			return this.name;
		}
	}

	public enum EnumPSDAPGType {
		PSD_1(true, false),
		PSD_2(true, false),
		PSD_2_LCD2(true, true),
		PSD_2_LCD3(true, true),
		PSD_2_LCD4(true, true),
		PSD_2_LCD5(true, true),
		PSD_2_LCD6(true, true),
		PSD_2_LCD7(true, true);

		private final boolean isPSD;
		private final boolean isLcd;

		EnumPSDAPGType(boolean isPSD, boolean isLcd) {
			this.isPSD = isPSD;
			this.isLcd = isLcd;
		}
	}
}
