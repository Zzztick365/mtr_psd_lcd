package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.registry.BlockEntities;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mod.block.BlockPSDTop;

public class MyPSDTopBE extends BlockPSDTop.BlockEntityBase {

	private static final String TAG_CUSTOM_TEXT = "customText";
	private static final String TAG_CUSTOM_IMAGE = "customImage";

	private static final String TAG_DIRECTION_FLIP = "directionFlip";
	private String customText = PSDCustomText.DEFAULT_TEXT;
	private String customImagePath = "";
	private boolean directionFlip = false;

	public boolean isDirectionFlip() {
		return directionFlip;
	}

	public void setDirectionFlip(boolean flip) {
		this.directionFlip = flip;
		markDirty2();
	}

	public String getCustomText() {
		return customText == null || customText.isEmpty() ? PSDCustomText.DEFAULT_TEXT : customText;
	}

	public void setCustomText(String text) {
		this.customText = text == null ? "" : text;
		markDirty2();
	}

	public String getCustomImagePath() {
		return customImagePath == null ? "" : customImagePath;
	}

	public void setCustomImagePath(String path) {
		this.customImagePath = path == null ? "" : path;
		markDirty2();
	}

	@Override
	public void writeCompoundTag(CompoundTag compoundTag) {
		super.writeCompoundTag(compoundTag);
		compoundTag.putString(TAG_CUSTOM_TEXT, customText == null ? "" : customText);
		compoundTag.putString(TAG_CUSTOM_IMAGE, customImagePath == null ? "" : customImagePath);
		compoundTag.putBoolean(TAG_DIRECTION_FLIP, directionFlip);
	}

	@Override
	public void readCompoundTag(CompoundTag compoundTag) {
		super.readCompoundTag(compoundTag);
		customText = compoundTag.contains(TAG_CUSTOM_TEXT) ? compoundTag.getString(TAG_CUSTOM_TEXT) : PSDCustomText.DEFAULT_TEXT;
		customImagePath = compoundTag.contains(TAG_CUSTOM_IMAGE) ? compoundTag.getString(TAG_CUSTOM_IMAGE) : "";
		directionFlip = compoundTag.contains(TAG_DIRECTION_FLIP) && compoundTag.getBoolean(TAG_DIRECTION_FLIP);
	}
	public MyPSDTopBE(BlockPos pos, BlockState state) {
		this(BlockEntities.PSD_TOP.get(), pos, state);
	}

	protected MyPSDTopBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
