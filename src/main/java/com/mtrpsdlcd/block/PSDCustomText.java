package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopBE;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.BlockPSDGlass;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;

public final class PSDCustomText {
	public static final String DEFAULT_TEXT = "地铁轨交";

	public static final String LIBRARY_SUB_DIR = "1";

	public static final String SELECTED_NONE = "@none";

	private static long libraryScanTime = 0L;
	private static String selectedImage = "";
	private static String libraryNewestImage = "";
	private static long libraryNewestTime = 0L;
	private static boolean selectedCleared = false;

	public static java.io.File getLibraryGroupDir() {
		final java.io.File dir = new java.io.File(new java.io.File(net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().toFile(), "psd_lcd_images"), LIBRARY_SUB_DIR);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}

	private static synchronized void scanLibrary() {
		final long now = System.currentTimeMillis();
		if (now - libraryScanTime < 3000L) {
			return;
		}
		libraryScanTime = now;
		selectedImage = "";
		libraryNewestImage = "";
		libraryNewestTime = 0L;
		selectedCleared = false;
		long markerTime = 0L;
		boolean markerIsNone = false;
		try {
			final java.io.File marker = new java.io.File(getLibraryGroupDir(), "selected.txt");
			if (marker.isFile()) {
				markerTime = marker.lastModified();
				final String name = new String(java.nio.file.Files.readAllBytes(marker.toPath()), java.nio.charset.StandardCharsets.UTF_8).trim();

				if (SELECTED_NONE.equalsIgnoreCase(name) || name.isEmpty()) {
					markerIsNone = true;
				} else {
					final java.io.File image = new java.io.File(name);
					final java.io.File resolved = image.isAbsolute() ? image : new java.io.File(getLibraryGroupDir(), name);
					if (resolved.isFile()) {
						selectedImage = resolved.getAbsolutePath();
					}
				}
			}
			final java.io.File[] files = getLibraryGroupDir().listFiles();
			if (files != null) {
				for (final java.io.File file : files) {
					final String name = file.getName().toLowerCase(java.util.Locale.ROOT);
					if (file.isFile() && (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".gif"))) {
						if (file.lastModified() >= libraryNewestTime) {
							libraryNewestTime = file.lastModified();
							libraryNewestImage = file.getAbsolutePath();
						}
					}
				}
			}
		} catch (Throwable ignored) {
		}

		selectedCleared = markerIsNone && (libraryNewestImage.isEmpty() || libraryNewestTime <= markerTime);
		if (markerIsNone && !selectedCleared) {
			selectedImage = libraryNewestImage;
		}
	}

	public static String getSelectedLibraryImage() {
		scanLibrary();
		return selectedImage;
	}

	public static boolean isLibrarySelectionCleared() {
		scanLibrary();
		return selectedCleared;
	}

	public static void setSelectedLibraryImage(String path) {
		try {
			final String name = (path == null || path.isEmpty()) ? "" : new java.io.File(path).getName();
			java.nio.file.Files.write(new java.io.File(getLibraryGroupDir(), "selected.txt").toPath(), name.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			libraryScanTime = 0L;
		} catch (Throwable ignored) {
		}
	}

	public static void clearSelectedLibraryImage() {
		try {
			java.nio.file.Files.write(new java.io.File(getLibraryGroupDir(), "selected.txt").toPath(), SELECTED_NONE.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			libraryScanTime = 0L;
		} catch (Throwable ignored) {
		}
	}

	public static String findFirstLibraryImage() {
		scanLibrary();
		return libraryNewestImage;
	}

	// ==========================================================================================

	// ==========================================================================================
	private static final java.util.Map<String, String> IMAGE_VERSION = new java.util.HashMap<>();
	private static final java.util.Map<String, Long> IMAGE_VERSION_TIME = new java.util.HashMap<>();

	public static String imageVersion(String path) {
		if (path == null || path.isEmpty()) {
			return "";
		}
		final long now = System.currentTimeMillis();
		final Long last = IMAGE_VERSION_TIME.get(path);
		if (last != null && now - last < 3000L) {
			final String cached = IMAGE_VERSION.get(path);
			if (cached != null) {
				return cached;
			}
		}
		String version = "";
		try {
			final java.io.File file = new java.io.File(path);
			if (file.isFile()) {
				version = file.lastModified() + ":" + file.length();
			}
		} catch (Throwable ignored) {
		}
		if (IMAGE_VERSION.size() > 64) {
			IMAGE_VERSION.clear();
			IMAGE_VERSION_TIME.clear();
		}
		IMAGE_VERSION.put(path, version);
		IMAGE_VERSION_TIME.put(path, now);
		return version;
	}

	// ==========================================================================================

	// ==========================================================================================
	public static boolean readDirectionFlip(World world, BlockPos pos) {
		final BlockPos topPos = resolveTopPos(world, pos);
		if (topPos == null) {
			return false;
		}
		final BlockEntity blockEntity = world.getBlockEntity(topPos);
		return blockEntity != null && blockEntity.data instanceof MyPSDTopBE && ((MyPSDTopBE) blockEntity.data).isDirectionFlip();
	}

	public static void applyDirectionFlip(World world, BlockPos topPos, boolean flip) {
		writeFlip(world, topPos, flip);
		final BlockState state = world.getBlockState(topPos);
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
		for (int dir = 0; dir < 2; ++dir) {
			final Direction direction = dir == 0 ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
			for (BlockPos pos = topPos.offset(direction); ; pos = pos.offset(direction)) {
				if (!isFamily(world.getBlockState(pos).getBlock().data)) {
					break;
				}
				writeFlip(world, pos, flip);
			}
		}
	}

	private static void writeFlip(World world, BlockPos pos, boolean flip) {
		final BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null && blockEntity.data instanceof MyPSDTopBE) {
			((MyPSDTopBE) blockEntity.data).setDirectionFlip(flip);
		}
	}

	private PSDCustomText() {
	}

	public static boolean isFamily(Object blockData) {
		return blockData instanceof MyPSDTopLcd14;
	}

	public static BlockPos resolveTopPos(World world, BlockPos pos) {
		final BlockState state = world.getBlockState(pos);
		if (isFamily(state.getBlock().data)) {
			return pos;
		}
		if (state.getBlock().data instanceof BlockPSDGlass) {
			final IBlock.DoubleBlockHalf half = IBlock.getStatePropertySafe(state, IBlock.HALF);
			final BlockPos topPos = half == IBlock.DoubleBlockHalf.LOWER ? pos.up(2) : pos.up(1);
			if (isFamily(world.getBlockState(topPos).getBlock().data)) {
				return topPos;
			}
		}
		return null;
	}

	public static String read(World world, BlockPos pos) {
		final BlockPos topPos = resolveTopPos(world, pos);
		if (topPos == null) {
			return DEFAULT_TEXT;
		}
		final BlockEntity blockEntity = world.getBlockEntity(topPos);
		if (blockEntity != null && blockEntity.data instanceof MyPSDTopBE) {
			return ((MyPSDTopBE) blockEntity.data).getCustomText();
		}
		return DEFAULT_TEXT;
	}

	public static String readImage(World world, BlockPos pos) {
		final BlockPos topPos = resolveTopPos(world, pos);
		if (topPos == null) {
			return "";
		}
		final BlockEntity blockEntity = world.getBlockEntity(topPos);
		if (blockEntity != null && blockEntity.data instanceof MyPSDTopBE) {
			return ((MyPSDTopBE) blockEntity.data).getCustomImagePath();
		}
		return "";
	}

	public static void apply(World world, BlockPos topPos, String text, String imagePath) {
		writeOne(world, topPos, text, imagePath);
		final BlockState state = world.getBlockState(topPos);
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
		for (int dir = 0; dir < 2; ++dir) {
			final Direction direction = dir == 0 ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
			for (BlockPos pos = topPos.offset(direction); ; pos = pos.offset(direction)) {
				if (!isFamily(world.getBlockState(pos).getBlock().data)) {
					break;
				}
				writeOne(world, pos, text, imagePath);
			}
		}
	}

	private static void writeOne(World world, BlockPos pos, String text, String imagePath) {
		final BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null && blockEntity.data instanceof MyPSDTopBE) {
			((MyPSDTopBE) blockEntity.data).setCustomText(text);
			((MyPSDTopBE) blockEntity.data).setCustomImagePath(imagePath);
		}
	}

	public static void apply(World world, BlockPos topPos, String text) {
		writeOne(world, topPos, text);
		final BlockState state = world.getBlockState(topPos);
		final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
		for (int dir = 0; dir < 2; ++dir) {
			final Direction direction = dir == 0 ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
			for (BlockPos pos = topPos.offset(direction); ; pos = pos.offset(direction)) {
				if (!isFamily(world.getBlockState(pos).getBlock().data)) {
					break;
				}
				writeOne(world, pos, text);
			}
		}
	}

	private static void writeOne(World world, BlockPos pos, String text) {
		final BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null && blockEntity.data instanceof MyPSDTopBE) {
			((MyPSDTopBE) blockEntity.data).setCustomText(text);
		}
	}
}
