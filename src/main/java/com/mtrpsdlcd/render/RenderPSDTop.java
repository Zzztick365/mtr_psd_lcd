package com.mtrpsdlcd.render;

import com.mtrpsdlcd.block.MyPSDTopLcd2;
import com.mtrpsdlcd.block.MyPSDTopLcd3;
import com.mtrpsdlcd.block.MyPSDTopLcd4;
import com.mtrpsdlcd.block.MyPSDTopLcd5;
import com.mtrpsdlcd.block.MyPSDTopLcd6;
import com.mtrpsdlcd.block.MyPSDTopLcd7;
import com.mtrpsdlcd.block.entity.MyPSDTopBE;
import org.mtr.core.operation.ArrivalResponse;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.AbstractTexture;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MathHelper;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageBackedTexture;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.BlockPSDAPGGlassEndBase;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.DynamicTextureCache;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.ArrivalsCacheClient;
import org.mtr.mod.data.IGui;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.RenderRouteBase;
import org.mtr.mod.render.StoredMatrixTransformations;

import java.util.HashMap;
import java.util.Map;

public class RenderPSDTop extends RenderRouteBase<MyPSDTopBE> {

	private RenderRouteBase.RenderType lastRenderType = RenderRouteBase.RenderType.NONE;

	private World lastWorld;
	private BlockPos lastPos;

	private static final Map<String, Identifier> PLATFORM_ARROW_TEXTURES = new HashMap<>();
	private static final Map<String, Long> PLATFORM_ARROW_TIMES = new HashMap<>();

	private static final Map<String, Object[]> ROUTE_BADGE_TEXTURES = new HashMap<>();
	private static final Map<String, Identifier> LED_TEXT_TEXTURES = new HashMap<>();
	private static final Map<String, Integer> LED_TEXT_WIDTHS = new HashMap<>();
	private static final Map<String, Integer> LED_TEXT_HEIGHTS = new HashMap<>();
	private static final Map<String, Object[]> MTR_STATION_TEXTURES = new HashMap<>();

	private static final float END_FRONT_OFFSET = 1.0f / (MathHelper.getSquareRootOfTwoMapped() * 16.0f);
	private static final float BOTTOM_DIAGONAL_OFFSET = ((float) Math.sqrt(3.0) - 1.0f) / 32.0f;
	private static final float ROOT_TWO_SCALED = MathHelper.getSquareRootOfTwoMapped() / 16.0f;
	private static final float BOTTOM_END_DIAGONAL_OFFSET = END_FRONT_OFFSET - BOTTOM_DIAGONAL_OFFSET / MathHelper.getSquareRootOfTwoMapped();
	private static final float COLOR_STRIP_START = 0.90625f;
	private static final float COLOR_STRIP_END = 0.9375f;

	private static final float SCALE = 90.0f;

	private static final float LED_Z_OFFSET = 0.06f;

	private static final float LED_PANEL_EDGE = 0.05f;
	private static final float LED_PANEL_Y1 = 0.68f;
	private static final float LED_PANEL_Y2 = 0.90f;
	private static final float ROUTE_Y1 = 0.71f;
	private static final float ROUTE_Y2 = 0.87f;
	private static final float ROUTE_PAD = 0.03f;
	private static final float TEXT_GAP = 0.08f;

	private static final String PLATFORM_LABEL = "站台";
	private static final float PLATFORM_TEXT_GAP = 0.08f;
	private static final String PLATFORM_DEST_PREFIX = "往 ";
	private static final float TEXT_Y = 0.73f;
	private static final float TEXT_SCALE = 1.0f;
	private static final int COLOR_PANEL = 0xFFFFFFFF;
	private static final int COLOR_TEXT = 0xFF000000;
	private static final int COLOR_ARRIVED = 0xFF00E676;
	private static final int COLOR_ROUTE_TEXT = 0xFFFFFFFF;

	private static final float SONG_Z_OFFSET = -0.0005f;

	private static final Map<String, String> STATION_EN_FALLBACK = Map.of("安城镇", "AnChengZhen", "巡河坊", "XunHeFang");

	private static int invertColor(int color) {
		return ((color & 0xFF000000) != 0 ? 0xFF000000 : 0) + ((color & 0xFF) << 16) + (color & 0xFF00) + ((color & 0xFF0000) >> 16);
	}

	public RenderPSDTop(BlockEntityRenderer.Argument dispatcher) {

		super(dispatcher, 1.95f, 3.0f, 6.0f, 0.125f, true, 3, BlockPSDTop.ARROW_DIRECTION);
	}

	@Override
	protected RenderRouteBase.RenderType getRenderType(World world, BlockPos pos, BlockState state) {
		final BlockPSDTop.EnumPersistent persistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
		final RenderRouteBase.RenderType renderType;
		if (persistent == BlockPSDTop.EnumPersistent.NONE) {
			final Block blockBelow = world.getBlockState(pos.down()).getBlock();
			if (blockBelow.data instanceof BlockPSDAPGDoorBase) {
				renderType = RenderRouteBase.RenderType.ARROW;
			} else if (!(blockBelow.data instanceof BlockPSDAPGGlassEndBase)) {
				renderType = RenderRouteBase.RenderType.ROUTE;
			} else {
				renderType = RenderRouteBase.RenderType.NONE;
			}
		} else {
			renderType = persistent == BlockPSDTop.EnumPersistent.ARROW ? RenderRouteBase.RenderType.ARROW : (persistent == BlockPSDTop.EnumPersistent.ROUTE ? RenderRouteBase.RenderType.ROUTE : RenderRouteBase.RenderType.NONE);
		}
		lastRenderType = renderType;
		lastWorld = world;
		lastPos = pos;

		DynamicTextureCache.instance.getRouteSquare(0, "1", IGui.HorizontalAlignment.CENTER);

		return RenderRouteBase.RenderType.NONE;
	}

	@Override
	protected void renderAdditionalUnmodified(StoredMatrixTransformations storedMatrixTransformations, BlockState state, Direction facing, int light) {
		final boolean airLeft = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT);
		final boolean airRight = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT);
		final boolean persistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) != BlockPSDTop.EnumPersistent.NONE;
		if (!airLeft && !airRight || persistent) {
			return;
		}
		MainRenderer.scheduleRender(new Identifier("mtr", "textures/block/psd_top.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
			storedMatrixTransformations.transform((GraphicsHolder) graphicsHolder, (Vector3d) offset);
			if (airLeft) {
				IDrawing.drawTexture(graphicsHolder, -0.125f, 0.0f, 0.5f, 0.5f, 0.0f, -0.125f, 0.5f, 1.0f, -0.125f, -0.125f, 1.0f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, facing, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f - END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, -0.25f - END_FRONT_OFFSET, 0.0625f, 0.25f - END_FRONT_OFFSET, -0.25f - END_FRONT_OFFSET, 1.0f, 0.25f - END_FRONT_OFFSET, 0.5f - END_FRONT_OFFSET, 1.0f, -0.5f - END_FRONT_OFFSET, 0.0f, 0.0f, 1.0f, 0.9375f, facing.getOpposite(), -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5f - BOTTOM_END_DIAGONAL_OFFSET, -0.25f - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25f - BOTTOM_END_DIAGONAL_OFFSET, -0.25f - END_FRONT_OFFSET, 0.0625f, 0.25f - END_FRONT_OFFSET, 0.5f - END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, 0.0f, 0.9375f, 1.0f, 0.96875f, facing.getOpposite(), -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f, 0.0f, -0.5f, -0.25f, 0.0f, 0.25f, -0.25f - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25f - BOTTOM_END_DIAGONAL_OFFSET, 0.5f - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5f - BOTTOM_END_DIAGONAL_OFFSET, 0.0f, 0.96875f, 1.0f, 1.0f, facing.getOpposite(), -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f, 0.003125f, -0.125f, -0.125f, 0.003125f, 0.5f, -0.125f, 0.003125f, 0.125f, 0.5f, 0.003125f, -0.5f, 0.125f, 0.125f, 0.1875f, 0.1875f, facing, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f, 0.996875f, -0.5f, -0.125f, 0.996875f, 0.125f, -0.125f, 0.996875f, 0.5f, 0.5f, 0.996875f, -0.125f, 0.125f, 0.125f, 0.1875f, 0.1875f, Direction.UP, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f - END_FRONT_OFFSET, 0.996875f, -0.5f - END_FRONT_OFFSET, -0.125f - ROOT_TWO_SCALED, 0.996875f, 0.125f, -0.125f, 0.996875f, 0.125f, 0.5f, 0.996875f, -0.5f, 0.125f, 0.125f, 0.1875f, 0.1875f, Direction.UP, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f, 0.0625f, -0.5f, 0.5f - END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, 0.5f - END_FRONT_OFFSET, 1.0f, -0.5f - END_FRONT_OFFSET, 0.5f, 1.0f, -0.5f, 0.9375f, 0.0f, 1.0f, 0.9375f, facing, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.5f, 0.0f, -0.5f, 0.5f - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5f - BOTTOM_END_DIAGONAL_OFFSET, 0.5f - END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, 0.5f, 0.0625f, -0.5f, 0.9375f, 0.9375f, 1.0f, 1.0f, facing, -1, light);
			}
			if (airRight) {
				IDrawing.drawTexture(graphicsHolder, -0.5f, 0.0f, -0.125f, 0.125f, 0.0f, 0.5f, 0.125f, 1.0f, 0.5f, -0.5f, 1.0f, -0.125f, 0.0f, 0.0f, 1.0f, 1.0f, facing, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.25f + END_FRONT_OFFSET, 0.0625f, 0.25f - END_FRONT_OFFSET, -0.5f + END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, -0.5f + END_FRONT_OFFSET, 1.0f, -0.5f - END_FRONT_OFFSET, 0.25f + END_FRONT_OFFSET, 1.0f, 0.25f - END_FRONT_OFFSET, 0.0f, 0.0f, 1.0f, 0.9375f, facing.getOpposite(), -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.25f + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25f - BOTTOM_END_DIAGONAL_OFFSET, -0.5f + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5f - BOTTOM_END_DIAGONAL_OFFSET, -0.5f + END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, 0.25f + END_FRONT_OFFSET, 0.0625f, 0.25f - END_FRONT_OFFSET, 0.0f, 0.9375f, 1.0f, 0.96875f, facing.getOpposite(), -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.25f, 0.0f, 0.25f, -0.5f, 0.0f, -0.5f, -0.5f + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5f - BOTTOM_END_DIAGONAL_OFFSET, 0.25f + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25f - BOTTOM_END_DIAGONAL_OFFSET, 0.0f, 0.96875f, 1.0f, 1.0f, facing.getOpposite(), -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.125f, 0.003125f, 0.5f, -0.5f, 0.003125f, -0.125f, -0.5f, 0.003125f, -0.5f, 0.125f, 0.003125f, 0.125f, 0.125f, 0.125f, 0.1875f, 0.1875f, facing, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.125f, 0.996875f, 0.125f, -0.5f, 0.996875f, -0.5f, -0.5f, 0.996875f, -0.125f, 0.125f, 0.996875f, 0.5f, 0.125f, 0.125f, 0.1875f, 0.1875f, Direction.UP, -1, light);
				IDrawing.drawTexture(graphicsHolder, 0.125f + ROOT_TWO_SCALED, 0.996875f, 0.125f, -0.5f + END_FRONT_OFFSET, 0.996875f, -0.5f - END_FRONT_OFFSET, -0.5f, 0.996875f, -0.5f, 0.125f, 0.996875f, 0.125f, 0.125f, 0.125f, 0.1875f, 0.1875f, Direction.UP, -1, light);
				IDrawing.drawTexture(graphicsHolder, -0.5f + END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, -0.5f, 0.0625f, -0.5f, -0.5f, 1.0f, -0.5f, -0.5f + END_FRONT_OFFSET, 1.0f, -0.5f - END_FRONT_OFFSET, 0.0f, 0.0f, 0.0625f, 0.9375f, facing, -1, light);
				IDrawing.drawTexture(graphicsHolder, -0.5f + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5f - BOTTOM_END_DIAGONAL_OFFSET, -0.5f, 0.0f, -0.5f, -0.5f, 0.0625f, -0.5f, -0.5f + END_FRONT_OFFSET, 0.0625f, -0.5f - END_FRONT_OFFSET, 0.0f, 0.9375f, 0.0625f, 1.0f, facing, -1, light);
			}
			graphicsHolder.pop();
		});
	}

	@Override
	protected void renderAdditional(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {

		final boolean lcd = state.getBlock().data instanceof MyPSDTopLcd2 || state.getBlock().data instanceof MyPSDTopLcd3;

		final boolean lcd45 = state.getBlock().data instanceof MyPSDTopLcd4 || state.getBlock().data instanceof MyPSDTopLcd5;

		final boolean lcd67 = state.getBlock().data instanceof MyPSDTopLcd6 || state.getBlock().data instanceof MyPSDTopLcd7;

		if (lastWorld == null || lastPos == null) {
			return;
		}

		final Direction rawFacing = facing.getOpposite();

		final BlockPos currentPos = lastPos.offset(rawFacing.rotateYClockwise(), leftBlocks);
		final Block currentBlockBelow = lastWorld.getBlockState(currentPos.down()).getBlock();
		if (!(currentBlockBelow.data instanceof BlockPSDAPGDoorBase)) {

			return;
		}

		final boolean isNotPersistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.NONE;
		final boolean airLeft = isNotPersistent && IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT);
		final boolean airRight = isNotPersistent && IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT);

		final DynamicTextureCache.DynamicResource colorStrip = DynamicTextureCache.instance.getColorStrip(platformId);
		final boolean colorStripReady = colorStrip != null && colorStrip.width == 1;
		if (colorStripReady) {
		MainRenderer.scheduleRender(colorStrip.identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolder, offset) -> {
			storedMatrixTransformations.transform((GraphicsHolder) graphicsHolder, (Vector3d) offset);
			IDrawing.drawTexture(graphicsHolder, airLeft ? 0.625f : 0.0f, COLOR_STRIP_START, 0.0f, airRight ? 0.375f : 1.0f, COLOR_STRIP_END, 0.0f, facing, color, light);
			if (airLeft) {
				IDrawing.drawTexture(graphicsHolder, END_FRONT_OFFSET, COLOR_STRIP_START, -0.625f - END_FRONT_OFFSET, 0.75f + END_FRONT_OFFSET, COLOR_STRIP_END, 0.125f - END_FRONT_OFFSET, facing, -1, light);
			}
			if (airRight) {
				IDrawing.drawTexture(graphicsHolder, 0.25f - END_FRONT_OFFSET, COLOR_STRIP_START, 0.125f - END_FRONT_OFFSET, 1.0f - END_FRONT_OFFSET, COLOR_STRIP_END, -0.625f - END_FRONT_OFFSET, facing, -1, light);
			}
			graphicsHolder.pop();
		});
		}

		if (!lcd && (lastRenderType == RenderRouteBase.RenderType.ARROW)) {
			final int arrowDirection2 = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
			final boolean hasLeft = (arrowDirection2 & 1) > 0;
			final boolean hasRight = (arrowDirection2 & 2) > 0;
			final float panelWidth = (leftBlocks + 1 + rightBlocks) - 2.0f * 0.125f / 16.0f;
			final float panelHeight = 1.0f - 3.0f / 16.0f - 6.0f / 16.0f;
			final boolean showPlatform = !(lcd45 || lcd67);
			final Identifier arrowTex = getPlatformArrowTexture(platformId, hasLeft, hasRight, panelWidth / panelHeight, showPlatform);
			if (arrowTex != null) {
				MainRenderer.scheduleRender(arrowTex, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, offset) -> {
					storedMatrixTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);

					final float y1 = 3.0f / 16.0f - (lcd67 ? panelHeight * 0.21f : 0.0f);
					final float y2 = 1.0f - 6.0f / 16.0f - (lcd67 ? panelHeight * 0.21f : 0.0f);
					IDrawing.drawTexture(graphicsHolderNew, leftBlocks == 0 ? 0.125f / 16.0f : 0.0f, y1, 0.0f, 1.0f - (rightBlocks == 0 ? 0.125f / 16.0f : 0.0f), y2, 0.0f, (leftBlocks - (leftBlocks == 0 ? 0.0f : 0.125f / 16.0f)) / panelWidth, 0.0f, (panelWidth - rightBlocks + (rightBlocks == 0 ? 0.0f : 0.125f / 16.0f)) / panelWidth, 1.0f, facing, color, light);
					graphicsHolderNew.pop();
				});
			}
		}

		if (lcd67 && leftBlocks == 0 && lastRenderType == RenderRouteBase.RenderType.ARROW) {
			final PSDTopTextureGenerator.LcdInfoMulti nMulti = PSDTopTextureGenerator.getLcdInfoMulti(platformId);
			if (nMulti != null && !nMulti.nextLines.isEmpty()) {
				final ObjectArrayList<PSDTopTextureGenerator.LcdNextLine> nextLines = nMulti.nextLines;
				final int lineCount = nextLines.size();
				final float lineH = 0.10f;
				final float badgeH = 0.065f;
				final float badgeW = 0.11f;
				final float leftX = LED_PANEL_EDGE;

				final float startY = (LED_PANEL_Y1 - lineH * lineCount - 0.03f);
				MainRenderer.scheduleRender(new Identifier("mtr:textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (badgeGh, badgeOff) -> {
					storedMatrixTransformations.transform((GraphicsHolder) badgeGh, (Vector3d) badgeOff);
					for (int i = 0; i < lineCount; i++) {
						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(lineCount - 1 - i);
						final float y = startY + i * lineH;
						IDrawing.drawTexture(badgeGh, leftX, y, 0.0f, leftX + badgeW, y + badgeH, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, 0xFF000000 | nl.routeColor, GraphicsHolder.getDefaultLight());
					}
					badgeGh.pop();
				});
				MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (nGh, nOff) -> {
					storedMatrixTransformations.transform((GraphicsHolder) nGh, (Vector3d) nOff);
					nGh.push();
					nGh.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);
					for (int i = 0; i < lineCount; i++) {
						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(lineCount - 1 - i);
						final float y = startY + i * lineH;
						final String nextCjk = getCjkText(nl.nextStation);
						float textRight = (leftX + badgeW + 0.03f) * SCALE;
						final float textCenterY = (y + badgeH / 2.0f) * SCALE;
						if (!nl.routeName.isEmpty()) {
							final float routeScale = badgeH * 0.8f / 0.1f;
							drawTextLeft(nGh, nl.routeName, textRight, textCenterY - 4.5f * routeScale, routeScale, 0xFF000000, (1 + rightBlocks) * SCALE * 0.7f);
							textRight += nl.routeName.length() * routeScale * 9.0f + 0.03f * SCALE;
						}
						if (!nextCjk.isEmpty()) {
							final float nextScale = badgeH * 0.8f / 0.1f;
							drawTextLeft(nGh, "下一站：" + nextCjk, textRight, textCenterY - 4.5f * nextScale, nextScale, COLOR_TEXT, (1 + rightBlocks) * SCALE * 0.7f);
						}
					}
					nGh.pop();
					nGh.pop();
				});
			}
		}

		if (lcd && leftBlocks == 0 && lastRenderType == RenderRouteBase.RenderType.ARROW) {

			final LongArrayList lcdPlatformIds = new LongArrayList();
			lcdPlatformIds.add(platformId);
			final ObjectArrayList<ArrivalResponse> lcdArrivals = ArrivalsCacheClient.INSTANCE.requestArrivals(lcdPlatformIds);
			final ArrivalResponse lcdArrival = lcdArrivals.isEmpty() ? null : lcdArrivals.get(0);
			final PSDTopTextureGenerator.LcdInfoMulti lcdMulti = PSDTopTextureGenerator.getLcdInfoMulti(platformId);
			final String stationName = lcdMulti == null ? (lcdArrival == null ? "" : lcdArrival.getPlatformName()) : lcdMulti.currentStation;
			final String stationCjk = getCjkText(stationName);
			final String[] stationParts = getLcdStationParts(stationName);

			String stationLatin = stationParts[1];
			if (stationLatin.isEmpty()) {
				stationLatin = STATION_EN_FALLBACK.getOrDefault(stationCjk, "");
			}
			if (stationLatin.isEmpty() && lcdArrival != null) {
				stationLatin = getEnText(lcdArrival.getPlatformName());
			}

			final float arrowY1 = 3.0f / 16.0f;
			final float arrowY2 = 1.0f - 6.0f / 16.0f;
			final float panelH = arrowY2 - arrowY1;
			final float centerX = (1 + rightBlocks) * SCALE / 2.0f;

			if (lcdMulti != null && !lcdMulti.nextLines.isEmpty()) {
				final ObjectArrayList<PSDTopTextureGenerator.LcdNextLine> nextLines = lcdMulti.nextLines;
				final int lineCount = nextLines.size();
				final float lineH = 0.10f;
				final float badgeH = 0.065f;
				final float badgeW = 0.11f;
				final float leftX = LED_PANEL_EDGE;
				final float startY = (LED_PANEL_Y1 - lineH * lineCount - 0.03f);
				MainRenderer.scheduleRender(new Identifier("mtr:textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (badgeGh, badgeOff) -> {
					storedMatrixTransformations.transform((GraphicsHolder) badgeGh, (Vector3d) badgeOff);

					for (int i = 0; i < lineCount; i++) {
						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(lineCount - 1 - i);
						final float y = startY + i * lineH;
						IDrawing.drawTexture(badgeGh, leftX, y, 0.0f, leftX + badgeW, y + badgeH, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, 0xFF000000 | nl.routeColor, GraphicsHolder.getDefaultLight());
					}
					badgeGh.pop();
				});
			}

			if (!stationCjk.isEmpty() || !stationLatin.isEmpty()) {

				final float cjkH = panelH * 0.30f;

				final float songH = cjkH * 4.0f * 1.25f;
				final float stationAspect = (1.0f + rightBlocks) / songH;
				final Object[] songTex = getMtrStationCompositeEntry(stationName, stationAspect);
				if (songTex != null) {
					final float songW = songH * ((Integer) songTex[1] / (float) (Integer) songTex[2]);

					final float songCenterY = arrowY1 + panelH * 0.24f;
					final float songY = songCenterY - songH / 2.0f;
					final Identifier songId = (Identifier) songTex[0];
					MainRenderer.scheduleRender(songId, false, QueuedRenderLayer.EXTERIOR, (songGh, songOff) -> {
						storedMatrixTransformations.transform((GraphicsHolder) songGh, (Vector3d) songOff);

						songGh.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);
						drawTextTexture(songGh, songId, centerX - songW * SCALE / 2.0f, songY * SCALE, songW * SCALE, songH * SCALE, facing, GraphicsHolder.getDefaultLight());
						songGh.pop();
					});
				}
			}

			MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (lcdGh, lcdOff) -> {
				storedMatrixTransformations.transform((GraphicsHolder) lcdGh, (Vector3d) lcdOff);
				lcdGh.push();
				lcdGh.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);

				if (lcdMulti != null && !lcdMulti.nextLines.isEmpty()) {
					final ObjectArrayList<PSDTopTextureGenerator.LcdNextLine> nextLines = lcdMulti.nextLines;
					final int lineCount = nextLines.size();
					final float lineH = 0.10f;
					final float badgeH = 0.065f;
					final float badgeW = 0.11f;
					final float leftX = LED_PANEL_EDGE;
					final float startY = (LED_PANEL_Y1 - lineH * lineCount - 0.03f);
					for (int i = 0; i < lineCount; i++) {
						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(lineCount - 1 - i);
						final float y = startY + i * lineH;
						final String nextCjk = getCjkText(nl.nextStation);
						float textRight = (leftX + badgeW + 0.03f) * SCALE;

						final float textCenterY = (y + badgeH / 2.0f) * SCALE;
						if (!nl.routeName.isEmpty()) {
							final float routeScale = badgeH * 0.8f / 0.1f;
							drawTextLeft(lcdGh, nl.routeName, textRight, textCenterY - 4.5f * routeScale, routeScale, 0xFF000000, (1 + rightBlocks) * SCALE * 0.7f);
							textRight += nl.routeName.length() * routeScale * 9.0f + 0.03f * SCALE;
						}
						if (!nextCjk.isEmpty()) {
							final float nextScale = badgeH * 0.8f / 0.1f;
							drawTextLeft(lcdGh, "下一站：" + nextCjk, textRight, textCenterY - 4.5f * nextScale, nextScale, COLOR_TEXT, (1 + rightBlocks) * SCALE * 0.7f);
						}
					}
				}
				lcdGh.pop();
				lcdGh.pop();
			});
		}

		final LongArrayList platformIds = new LongArrayList();
		platformIds.add(platformId);
		final ObjectArrayList<ArrivalResponse> arrivals = ArrivalsCacheClient.INSTANCE.requestArrivals(platformIds);
		final ArrivalResponse arrival = arrivals.isEmpty() ? null : arrivals.get(0);
		final long arrivalRemaining = arrival == null ? Long.MAX_VALUE : (arrival.getArrival() - ArrivalsCacheClient.INSTANCE.getMillisOffset() - System.currentTimeMillis()) / 1000;
		final int textColor = arrivalRemaining <= 0 ? COLOR_ARRIVED : COLOR_TEXT;
		final String routeNumber = arrival == null ? "" : getCjkText(arrival.getRouteNumber());
		final String destinationText = arrival == null ? "" : getDestinationText(arrival.getDestination());
		final int routeColor = arrival == null ? 0 : 0xFF000000 | arrival.getRouteColor();
		final boolean hasRoute = arrival != null && !routeNumber.isEmpty();
		final float routeWidth = hasRoute ? (GraphicsHolder.getTextWidth(routeNumber) + ROUTE_PAD * 2 * SCALE) * 1.4f : 0.0f;

				final long departureRemaining = arrival == null ? Long.MAX_VALUE : (arrival.getDeparture() - ArrivalsCacheClient.INSTANCE.getMillisOffset() - System.currentTimeMillis()) / 1000;
				final boolean stopped = arrivalRemaining <= 5 && departureRemaining > -5;

		final float rowWidth = (1 + rightBlocks) * SCALE;
		final float colWidth = rowWidth / 3.0f;

		final StoredMatrixTransformations ledTransformations = storedMatrixTransformations.copy();
		ledTransformations.add(graphicsHolderNew -> graphicsHolderNew.translate(0.0, 0.0, LED_Z_OFFSET));

		final float routeTextWidthPx = hasRoute ? GraphicsHolder.getTextWidth(routeNumber) : 0.0f;
		final float badgeHalfW = hasRoute ? (routeTextWidthPx / 2.0f + ROUTE_PAD * SCALE) / SCALE : 0.0f;
		final float badgeCenterX = (colWidth / 2.0f) / SCALE;
		final float badgeX1 = Math.max(badgeCenterX - badgeHalfW, 0.0f);
		final float badgeX2 = Math.min(badgeCenterX + badgeHalfW, 1.0f);
		MainRenderer.scheduleRender(new Identifier("mtr:textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, offset) -> {
			ledTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);
			if (leftBlocks == 0 && hasRoute && !stopped) {

				IDrawing.drawTexture(graphicsHolderNew, badgeX1, LED_PANEL_Y1, 0.0f, badgeX2, LED_PANEL_Y2, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, routeColor, GraphicsHolder.getDefaultLight());
				if (badgeX1 > 0.0f) {
					IDrawing.drawTexture(graphicsHolderNew, 0.0f, LED_PANEL_Y1, 0.0f, badgeX1, LED_PANEL_Y2, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, COLOR_PANEL, GraphicsHolder.getDefaultLight());
				}
				if (badgeX2 < 1.0f) {
					IDrawing.drawTexture(graphicsHolderNew, badgeX2, LED_PANEL_Y1, 0.0f, 1.0f, LED_PANEL_Y2, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, COLOR_PANEL, GraphicsHolder.getDefaultLight());
				}
			} else {
				IDrawing.drawTexture(graphicsHolderNew, 0.0f, LED_PANEL_Y1, 0.0f, 1.0f, LED_PANEL_Y2, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, COLOR_PANEL, GraphicsHolder.getDefaultLight());
			}
			graphicsHolderNew.pop();
		});

		if (stopped) {

			final BlockState doorStateBelow = lastWorld.getBlockState(currentPos.down());
			if (doorStateBelow.getBlock().data instanceof BlockPSDAPGDoorBase && IBlock.getStatePropertySafe(doorStateBelow, IBlock.SIDE) == IBlock.EnumSide.LEFT) {
				final boolean open = isCurrentDoorOpen(currentPos);
				final String msg = open ? "车门开启，请注意安全" : "车门关闭，请勿倚靠车门";
				final int msgColor = open ? COLOR_ARRIVED : 0xFFFF0000;
				final float centerX = SCALE;
				MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (graphicsHolderNew, offset) -> {
					ledTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);
					graphicsHolderNew.translate(0.0, 0.0, -0.002f);
					graphicsHolderNew.push();
					graphicsHolderNew.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);
					drawTextCentered(graphicsHolderNew, msg, centerX, TEXT_Y * SCALE, TEXT_SCALE, msgColor, 2.0f * SCALE);
					graphicsHolderNew.pop();

					graphicsHolderNew.pop();
				});
			}
		} else {

			if (leftBlocks == 0) {
				final boolean infoPage = (System.currentTimeMillis() / 5000) % 2 == 1;
				final int carCount = arrival == null ? 0 : arrival.getCarCount();
				final float cx1 = colWidth / 2.0f;
				final float cx2 = colWidth * 1.5f;
				final float cx3 = colWidth * 2.5f;
				MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (graphicsHolderNew, offset) -> {
					ledTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);
					graphicsHolderNew.translate(0.0, 0.0, -0.002f);
					graphicsHolderNew.push();
					graphicsHolderNew.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);
					final float textY = TEXT_Y * SCALE;

					if (hasRoute) {
						drawTextCentered(graphicsHolderNew, routeNumber, cx1, (ROUTE_Y1 + ROUTE_Y2) / 2.0f * SCALE - 4.5f * TEXT_SCALE, TEXT_SCALE, COLOR_ROUTE_TEXT, colWidth);
					}
					if (infoPage) {

						drawTextCentered(graphicsHolderNew, "请注意安全", cx2, textY, TEXT_SCALE, COLOR_TEXT, colWidth);
						final String weatherTime = getWeatherIcon() + " " + getRealTime();
						drawTextCentered(graphicsHolderNew, weatherTime, cx3, textY, TEXT_SCALE, COLOR_TEXT, colWidth);
					} else {

						if (!destinationText.isEmpty()) {
							drawTextCentered(graphicsHolderNew, destinationText, cx2, textY, TEXT_SCALE, textColor, colWidth);
						}
						if (carCount > 0) {
							drawTextCentered(graphicsHolderNew, carCount + "节", cx3, textY, TEXT_SCALE, COLOR_TEXT, colWidth);
						}
					}
					graphicsHolderNew.pop();

					graphicsHolderNew.pop();
				});
			}
		}
	}

	@Override
	protected float getAdditionalOffset(BlockState state) {
		return IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.NONE ? 0.0f : 0.46875f;
	}

	private String getCjkText(String text) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		final int index = text.indexOf('|');
		return (index >= 0 ? text.substring(0, index) : text).trim();
	}

	private String getEnText(String text) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		final int index = text.indexOf('|');
		return (index >= 0 && index + 1 < text.length()) ? text.substring(index + 1).trim() : "";
	}

	private float approxTextWidth(String text, int fontSize) {
		if (text == null || text.isEmpty()) {
			return 0;
		}
		float width = 0;
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			if (c == ' ') {
				width += fontSize * 0.3f;
			} else if (c >= 0x4E00 && c <= 0x9FFF || c >= 0x3400 && c <= 0x4DBF || c >= 0xF900 && c <= 0xFAFF) {
				width += fontSize;
			} else {
				width += fontSize * 0.55f;
			}
		}
		return width;
	}

	private Identifier getPlatformArrowTexture(long platformId, boolean hasLeft, boolean hasRight, float ratio, boolean showPlatform) {
		final String key = platformId + "_" + hasLeft + "_" + hasRight + "_" + ratio + "_" + showPlatform;
		final Identifier cached = PLATFORM_ARROW_TEXTURES.get(key);
		final Long time = PLATFORM_ARROW_TIMES.get(key);
		if (cached != null && time != null && System.currentTimeMillis() - time < 10000) {
			return cached;
		}

		PSDTopTextureGenerator.setConstants();

		DynamicTextureCache.instance.getRouteSquare(0, "1", IGui.HorizontalAlignment.CENTER);
		final NativeImage image = PSDTopTextureGenerator.generateDirectionArrow(platformId, hasLeft, hasRight, IGui.HorizontalAlignment.CENTER, true, 0.25f, ratio, -1, -16777216, -1, showPlatform);
		if (image == null) {
			return null;
		}
		final NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
		final Identifier identifier = new Identifier("mtr_psd_lcd", "platform_arrow_" + Math.abs(key.hashCode()));
		MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, AbstractTexture.cast(texture));
		PLATFORM_ARROW_TEXTURES.put(key, identifier);
		PLATFORM_ARROW_TIMES.put(key, System.currentTimeMillis());
		return identifier;
	}

	private Object[] getMtrStationCompositeEntry(String stationName, float aspectRatio) {
		final String key = "mtrstation_" + stationName + "_" + aspectRatio;
		final Object[] cached = MTR_STATION_TEXTURES.get(key);
		if (cached != null) {
			return cached;
		}
		PSDTopTextureGenerator.setConstants();
		DynamicTextureCache.instance.getRouteSquare(0, "1", IGui.HorizontalAlignment.CENTER);
		final NativeImage image = PSDTopTextureGenerator.getMTRStationCompositeImage(stationName, aspectRatio);
		if (image == null) {
			return null;
		}
		final NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
		final Identifier identifier = new Identifier("mtr_psd_lcd", "mtr_station_" + Math.abs(key.hashCode()));
		MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, AbstractTexture.cast(texture));
		final Object[] entry = new Object[]{identifier, image.getWidth(), image.getHeight()};
		MTR_STATION_TEXTURES.put(key, entry);
		return entry;
	}

	private Identifier getLedPixelText(String text, int textColor) {
		final String key = "ledtext_" + text + "_" + textColor;
		final Identifier cached = LED_TEXT_TEXTURES.get(key);
		if (cached != null) {
			return cached;
		}
		final NativeImage image = PSDTopTextureGenerator.generatePixelatedText(text, textColor, 4096, 0.0, false);
		if (image == null) {
			return null;
		}
		final NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
		final Identifier identifier = new Identifier("mtr_psd_lcd", "led_text_" + Math.abs(key.hashCode()));
		MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, AbstractTexture.cast(texture));
		LED_TEXT_TEXTURES.put(key, identifier);
		LED_TEXT_WIDTHS.put(key, image.getWidth());
		LED_TEXT_HEIGHTS.put(key, image.getHeight());
		return identifier;
	}

	private float getLedTextWidth(String text, int textColor) {
		final Integer v = LED_TEXT_WIDTHS.get("ledtext_" + text + "_" + textColor);
		return v == null ? 1 : v;
	}

	private float getLedTextHeight(String text, int textColor) {
		final Integer v = LED_TEXT_HEIGHTS.get("ledtext_" + text + "_" + textColor);
		return v == null ? 1 : v;
	}

	private Object[] getRouteBadgeTexture(String routeName, int routeColor) {
		final String key = "routebadge_" + routeName + "_" + routeColor;
		final Object[] cached = ROUTE_BADGE_TEXTURES.get(key);
		if (cached != null) {
			return cached;
		}
		PSDTopTextureGenerator.setConstants();
		final NativeImage image = PSDTopTextureGenerator.generateRouteBadge(routeName, routeColor);
		if (image == null) {
			return null;
		}
		final NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
		final Identifier identifier = new Identifier("mtr_psd_lcd", "route_badge_" + Math.abs(key.hashCode()));
		MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, AbstractTexture.cast(texture));
		final Object[] entry = new Object[]{identifier, image.getWidth(), image.getHeight()};
		ROUTE_BADGE_TEXTURES.put(key, entry);
		return entry;
	}

	private void drawTextTexture(GraphicsHolder graphicsHolder, Identifier texture, float x, float y, float w, float h, Direction facing, int light) {
		IDrawing.drawTexture(graphicsHolder, x, y, 0.0f, x + w, y + h, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, -1, light);
	}

	private boolean isCurrentDoorOpen(BlockPos pos) {
		if (lastWorld == null || pos == null) {
			return false;
		}
		final BlockEntity doorBE = lastWorld.getBlockEntity(pos.down());
		if (doorBE == null || !(doorBE.data instanceof BlockPSDAPGDoorBase.BlockEntityBase)) {
			return false;
		}
		return ((BlockPSDAPGDoorBase.BlockEntityBase) doorBE.data).getDoorValue() > 0.1;
	}

	private boolean isDoorOpen() {
		if (lastWorld == null || lastPos == null) {
			return false;
		}
		final BlockEntity doorBE = lastWorld.getBlockEntity(lastPos.down());
		if (doorBE == null || !(doorBE.data instanceof BlockPSDAPGDoorBase.BlockEntityBase)) {
			return false;
		}
		return ((BlockPSDAPGDoorBase.BlockEntityBase) doorBE.data).getDoorValue() > 0.1;
	}

	private String getWeatherIcon() {
		final ClientWorld world = MinecraftClient.getInstance().getWorldMapped();
		if (world == null) {
			return "";
		}
		if (world.isThundering()) {
			return "⛈";
		} else if (world.isRaining()) {
			return "🌧";
		}
		return "☀";
	}

	private String getRealTime() {
		final java.time.LocalTime now = java.time.LocalTime.now();
		return String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond());
	}

	private String getDestinationText(String destination) {
		if (destination == null || destination.isEmpty()) {
			return "";
		}
		final String[] split = destination.split("\\|");
		final String cjk = split[0].trim();
		if (split.length > 1 && !split[1].trim().isEmpty()) {
			return cjk + " " + split[1].trim();
		}
		return cjk;
	}

	private String[] getLcdStationParts(String text) {
		if (text == null || text.isEmpty()) {
			return new String[]{"", ""};
		}
		final String[] split = text.split("\\|");
		final String cjk = split[0].trim();
		final String latin = split.length > 1 ? split[1].trim() : "";
		return new String[]{cjk, latin};
	}

	private void drawTextCentered(GraphicsHolder graphicsHolder, String text, float centerX, float y, float scale, int color, float maxWidth) {
		graphicsHolder.push();
		final int textWidth = GraphicsHolder.getTextWidth(text);
		final float drawWidth = textWidth * scale;
		graphicsHolder.translate(centerX - Math.min(drawWidth, maxWidth) / 2.0f, y, 0.0);
		if (drawWidth > maxWidth) {
			graphicsHolder.scale(maxWidth / drawWidth, 1.0f, 1.0f);
		}
		graphicsHolder.scale(scale, scale, 1.0f);
		graphicsHolder.drawText(text, 0, 0, color, false, GraphicsHolder.getDefaultLight());
		graphicsHolder.pop();
	}

	private void drawTextLeft(GraphicsHolder graphicsHolder, String text, float x, float y, float scale, int color, float maxWidth) {
		graphicsHolder.push();
		final int textWidth = GraphicsHolder.getTextWidth(text);
		final float drawWidth = textWidth * scale;
		graphicsHolder.translate(x, y, 0.0);
		if (drawWidth > maxWidth) {
			graphicsHolder.scale(maxWidth / drawWidth, 1.0f, 1.0f);
		}
		graphicsHolder.scale(scale, scale, 1.0f);
		graphicsHolder.drawText(text, 0, 0, color, false, GraphicsHolder.getDefaultLight());
		graphicsHolder.pop();
	}
}
