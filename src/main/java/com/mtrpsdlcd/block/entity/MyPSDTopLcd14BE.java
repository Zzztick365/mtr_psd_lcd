package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;

public class MyPSDTopLcd14BE extends MyPSDTopBE {

	private static final String TAG_ROUTE_INDEX = "routeIndex";

	private int routeIndex = 0;

	public MyPSDTopLcd14BE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP_LCD14.get(), pos, state);
	}

	public MyPSDTopLcd14BE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public int getRouteIndex() {
		return routeIndex;
	}

	public void setRouteIndex(int index) {
		this.routeIndex = index;
		markDirty2();
	}

	@Override
	public void writeCompoundTag(CompoundTag compoundTag) {
		super.writeCompoundTag(compoundTag);
		compoundTag.putInt(TAG_ROUTE_INDEX, routeIndex);
	}

	@Override
	public void readCompoundTag(CompoundTag compoundTag) {
		super.readCompoundTag(compoundTag);
		routeIndex = compoundTag.contains(TAG_ROUTE_INDEX) ? compoundTag.getInt(TAG_ROUTE_INDEX) : 0;
	}
}
