package com.mtrpsdlcd.render;

import com.mtrpsdlcd.Constants;

import com.mtrpsdlcd.block.MyPSDTopLcd2;

import com.mtrpsdlcd.block.MyPSDTopLcd3;

import com.mtrpsdlcd.block.MyPSDTopLcd4;

import com.mtrpsdlcd.block.MyPSDTopLcd5;

import com.mtrpsdlcd.block.MyPSDTopLcd6;

import com.mtrpsdlcd.block.MyPSDTopLcd7;

import com.mtrpsdlcd.block.MyPSDTopLcd8;

import com.mtrpsdlcd.block.MyPSDTopLcd9;

import com.mtrpsdlcd.block.MyPSDTopLcd10;

import com.mtrpsdlcd.block.MyPSDTopLcd11;

import com.mtrpsdlcd.block.MyPSDTopLcd12;

import com.mtrpsdlcd.block.MyPSDTopLcd13;

import com.mtrpsdlcd.block.MyPSDTopLcd14;

import com.mtrpsdlcd.block.MyPSDTopLcd15;

import com.mtrpsdlcd.block.MyPSDTopLcd16;

import com.mtrpsdlcd.block.entity.MyPSDTopBE;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE;

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

import org.mtr.mod.Init;

import org.mtr.mapping.mapper.GraphicsHolder;

import org.mtr.mod.block.BlockPSDAPGDoorBase;

import org.mtr.mod.block.BlockPSDAPGGlassEndBase;

import org.mtr.mod.block.BlockPSDTop;

import org.mtr.mod.block.IBlock;

import org.mtr.mod.client.DynamicTextureCache;

import org.mtr.mod.client.MinecraftClientData;

import org.mtr.mod.client.IDrawing;

import org.mtr.mod.config.Config;

import org.mtr.mod.data.ArrivalsCacheClient;

import org.mtr.mod.data.IGui;

import org.mtr.mod.render.MainRenderer;

import org.mtr.mod.render.WorkerThread;

import org.mtr.mod.render.QueuedRenderLayer;

import org.mtr.mod.render.RenderRouteBase;

import org.mtr.mod.render.StoredMatrixTransformations;

import org.mtr.core.data.SimplifiedRoute;

import org.mtr.core.data.SimplifiedRoutePlatform;

import java.util.HashMap;

import java.util.HashSet;

import java.util.Map;

import java.util.Set;

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

	private static final Map<String, Identifier> PER_ROUTE_TEXTURES = new HashMap<>();

	private static final Map<String, Long> PER_ROUTE_TIMES = new HashMap<>();

	private static final Set<String> CUSTOM_ROUTE_GENERATING = new HashSet<>();

	private static final Set<String> PLATFORM_ARROW_GENERATING = new HashSet<>();

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

	private static final float GLASS_BORDER = 0.02f;

	private static final float GLASS_BORDER_Z = 0.015f;

	private static final float ROUTE_Y1 = 0.71f;

	private static final float ROUTE_Y2 = 0.87f;

	private static final float ROUTE_PAD = 0.03f;

	private static final float TEXT_GAP = 0.08f;

	private void drawGlassRouteSplit(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {

		final ObjectArrayList<SimplifiedRoute> routes = new ObjectArrayList<>();

		for (final SimplifiedRoute route : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {

			for (final SimplifiedRoutePlatform sp : route.getPlatforms()) {

				if (sp.getPlatformId() == platformId) {

					routes.add(route);

					break;

				}

			}

		}

		if (routes.size() <= 1) {

			drawGlassRoute(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);

			return;

		}

		final float leftX = leftBlocks == 0 ? sidePadding : 0.0f;

		final float rightX = 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f);

		final float topY = topPadding;

		final float bottomY = 1.0f - bottomPadding;

		final float rowH = (bottomY - topY) / routes.size();

		for (int i = 0; i < routes.size(); i++) {

			drawRouteLine(storedMatrixTransformations, routes.get(i), topY + i * rowH, rowH, leftX, rightX, facing, light);

		}

	}

	private void drawRouteLine(StoredMatrixTransformations storedMatrixTransformations, SimplifiedRoute route, float y0, float rowH, float leftX, float rightX, Direction facing, int light) {

		final int routeColor = 0xFF000000 | route.getColor();

		final float lineY = y0 + rowH * 0.40f;

		final Identifier circleTex = new Identifier("mtr", "textures/block/sign/circle.png");

		final ObjectArrayList<SimplifiedRoutePlatform> plats = route.getPlatforms();

		final int count = Math.max(plats.size(), 1);

		final float stepX = (rightX - leftX) / count;

		MainRenderer.scheduleRender(new Identifier("mtr", "textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (gh, off) -> {

			storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

			IDrawing.drawTexture(gh, leftX, lineY - 0.012f, 0.0f, rightX, lineY + 0.012f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, routeColor, GraphicsHolder.getDefaultLight());

			gh.pop();

		});

		MainRenderer.scheduleRender(circleTex, false, QueuedRenderLayer.EXTERIOR, (gh, off) -> {

			storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

			final float r = 0.022f;

			for (int i = 0; i < count; i++) {

				final float cx = leftX + stepX * (i + 0.5f);

				IDrawing.drawTexture(gh, cx - r, lineY - r, 0.0f, cx + r, lineY + r, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, -1, light);

			}

			gh.pop();

		});

		MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (gh, off) -> {

			storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

			gh.push();

			gh.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);

			final float cx0 = leftX * SCALE + stepX * SCALE / 2.0f;

			final float nameY = (lineY + 0.022f) * SCALE;

			for (int i = 0; i < count; i++) {

				drawTextCentered(gh, getCjkText(plats.get(i).getStationName()), cx0 + i * stepX * SCALE, nameY, TEXT_SCALE, COLOR_TEXT, 0.20f * SCALE);

			}

			gh.pop();

			gh.pop();

		});

	}

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

	private static boolean isStandaloneModule(Block block) {

		return block.data instanceof MyPSDTopLcd8 || block.data instanceof MyPSDTopLcd9 || block.data instanceof MyPSDTopLcd10 || block.data instanceof MyPSDTopLcd11 || block.data instanceof MyPSDTopLcd12 || block.data instanceof MyPSDTopLcd13 || block.data instanceof MyPSDTopLcd14;

	}

	private static boolean isDoubleTopModule(Block block) {

		return isStandaloneModule(block) && !(block.data instanceof MyPSDTopLcd12);

	}

	private float findNearbyDoorValue(World world, BlockPos pos, Direction facing) {

		final BlockPos[] scan = {

				pos.down(),

				pos.up(),

				pos.offset(facing.rotateYClockwise()), pos.offset(facing.rotateYClockwise()).down(),

				pos.offset(facing.rotateYCounterclockwise()), pos.offset(facing.rotateYCounterclockwise()).down()

		};

		for (final BlockPos p : scan) {

			final BlockEntity doorBE = world.getBlockEntity(p);

			if (doorBE != null && doorBE.data instanceof BlockPSDAPGDoorBase.BlockEntityBase) {

				return (float) ((BlockPSDAPGDoorBase.BlockEntityBase) doorBE.data).getDoorValue();

			}

		}

		return -1f;

	}

	private void drawGlassRoute(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {

		final float width = (leftBlocks + rightBlocks + 1) - sidePadding * 2.0f;

		final float routeHeight = 1.0f - topPadding - bottomPadding;

		final int arrowDirection = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);

		final DynamicTextureCache.DynamicResource route = DynamicTextureCache.instance.getRouteMap(platformId, false, arrowDirection == 2, width / routeHeight, true);

		if (route != null) {

			MainRenderer.scheduleRender(route.identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, offset) -> {

				storedMatrixTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);

				IDrawing.drawTexture(graphicsHolderNew, leftBlocks == 0 ? sidePadding : 0.0f, topPadding, 0.0f, 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f), 1.0f - bottomPadding, 0.0f,

						(leftBlocks - (leftBlocks == 0 ? 0.0f : sidePadding)) / width, 0.0f,

						(width - rightBlocks + (rightBlocks == 0 ? 0.0f : sidePadding)) / width, 1.0f, facing.getOpposite(), color, light);

				graphicsHolderNew.pop();

			});

		}

		final DynamicTextureCache.DynamicResource colorStrip = DynamicTextureCache.instance.getColorStrip(platformId);

		if (colorStrip != null && colorStrip.width == 1) {

			MainRenderer.scheduleRender(colorStrip.identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, offset) -> {

				storedMatrixTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);

				IDrawing.drawTexture(graphicsHolderNew, leftBlocks == 0 ? sidePadding : 0.0f, COLOR_STRIP_START, 0.0f, 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f), COLOR_STRIP_END, 0.0f, facing, -1, light);

				graphicsHolderNew.pop();

			});

		}

	}

	private static final float GROOVE_SCREEN_V1 = 0.056f;
	private static final float GROOVE_SCREEN_V2 = 0.874f;

	private static final int GROOVE_SCREEN_BG_COLOR = 0xFFEDEDED;

	private static final float GROOVE_STRIP_V1 = COLOR_STRIP_START;
	private static final float GROOVE_STRIP_V2 = COLOR_STRIP_END;

	private static final float GROOVE_SCREEN_MAP_Z = 0.08f;

	private static final float GROOVE_SCREEN_LED_Z = 0.10f;

	private void drawGroovedScreen(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light, int routeIndex) {

		final float width = (leftBlocks + rightBlocks + 1) - sidePadding * 2.0f;

		final float screenH = GROOVE_SCREEN_V2 - GROOVE_SCREEN_V1;

		final int routeState = routeIndex;

		final int routeIdx = routeState;

		final boolean leftDir = detectRouteDirectionLeft(platformId) ^ com.mtrpsdlcd.block.PSDCustomText.readDirectionFlip(lastWorld, lastPos);

		MainRenderer.scheduleRender(new Identifier("mtr", "textures/block/white.png"), false, QueuedRenderLayer.LIGHT, (gh, off) -> {

			storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

			IDrawing.drawTexture((GraphicsHolder) gh, leftBlocks == 0 ? sidePadding : 0.0f, GROOVE_SCREEN_V1, GROOVE_SCREEN_LED_Z, 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f), GROOVE_SCREEN_V2, GROOVE_SCREEN_LED_Z, 0.0f, 0.0f, 1.0f, 1.0f, facing, 0xFFFFFFFF, light);

			gh.pop();

		});

		if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] drawGroovedScreen platformId={} routeIndex={} leftBlocks={} rightBlocks={} persistent={}", platformId, routeIndex, leftBlocks, rightBlocks, IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT));

		final long routeId = getRouteIdByIndex(platformId, routeIdx);

		if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] routeState={} routeIdx={} leftDir={} -> routeId={}", routeState, routeIdx, leftDir, routeId);

		if (routeId == -1L) {

			return;

		}

		final Identifier routeIdentifier = getCustomRouteTexture(platformId, routeId, false, leftDir, width / screenH, true, "", getRefreshSignature(platformId), getCustomText(), getCustomImagePath());

		if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] routeId={} tex={}", routeId, routeIdentifier);

		if (routeIdentifier != null) {

			MainRenderer.scheduleRender(routeIdentifier, false, QueuedRenderLayer.EXTERIOR, (gh, off) -> {

				storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

				IDrawing.drawTexture(gh, leftBlocks == 0 ? sidePadding : 0.0f, GROOVE_SCREEN_V1, GROOVE_SCREEN_MAP_Z, 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f), GROOVE_SCREEN_V2, GROOVE_SCREEN_MAP_Z,

						(leftBlocks - (leftBlocks == 0 ? 0.0f : sidePadding)) / width, 0.0f,

						(width - rightBlocks + (rightBlocks == 0 ? 0.0f : sidePadding)) / width, 1.0f, facing.getOpposite(), -1, light);

				gh.pop();

			});

		}

	}

	private void drawGroovedColorStrip(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int light) {

		final DynamicTextureCache.DynamicResource colorStrip = DynamicTextureCache.instance.getColorStrip(platformId);

		if (colorStrip != null && colorStrip.width == 1) {

			MainRenderer.scheduleRender(colorStrip.identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, offset) -> {

				storedMatrixTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);

				IDrawing.drawTexture((GraphicsHolder) graphicsHolderNew, leftBlocks == 0 ? sidePadding : 0.0f, GROOVE_STRIP_V1, 0.0f, 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f), GROOVE_STRIP_V2, 0.0f, facing, -1, light);

				graphicsHolderNew.pop();

			});

		}

	}

	// ==========================================================================================

	// ==========================================================================================

	private String getCustomImagePath() {
		if (lastWorld == null || lastPos == null) {
			return "";
		}

		final String selected = com.mtrpsdlcd.block.PSDCustomText.getSelectedLibraryImage();
		if (selected != null && !selected.isEmpty()) {
			return selected;
		}

		if (com.mtrpsdlcd.block.PSDCustomText.isLibrarySelectionCleared()) {
			return "";
		}
		final String fromLibrary = com.mtrpsdlcd.block.PSDCustomText.findFirstLibraryImage();
		if (fromLibrary != null && !fromLibrary.isEmpty()) {
			return fromLibrary;
		}
		return com.mtrpsdlcd.block.PSDCustomText.readImage(lastWorld, lastPos);
	}

	private String getCustomText() {
		if (lastWorld == null || lastPos == null) {
			return com.mtrpsdlcd.block.PSDCustomText.DEFAULT_TEXT;
		}
		return com.mtrpsdlcd.block.PSDCustomText.read(lastWorld, lastPos);
	}

	private int getLcd14RouteIndex(BlockState state) {

		if (lastWorld == null || lastPos == null) {

			return 0;

		}

		final BlockEntity be = lastWorld.getBlockEntity(lastPos);

		if (be != null && be.data instanceof MyPSDTopLcd14BE) {

			return ((MyPSDTopLcd14BE) be.data).getRouteIndex();

		}

		return 0;

	}

	private long getRouteIdByIndex(long platformId, int index) {

		final ObjectArrayList<Long> routeIds = RouteMapGenerator.collectRouteIdsForStation(platformId);

		if (routeIds.isEmpty()) {

			return -1L;

		}

		if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] getRouteIdByIndex platformId={} 收集到线路数={} index={}", platformId, routeIds.size(), index);

		int n = index % routeIds.size();

		if (n < 0) {

			n += routeIds.size();

		}

		return routeIds.get(n);

	}

	private boolean detectRouteDirectionLeft(long platformId) {

		try {

			final LongArrayList platformIds = new LongArrayList();

			platformIds.add(platformId);

			final ObjectArrayList<ArrivalResponse> arrivals = ArrivalsCacheClient.INSTANCE.requestArrivals(platformIds);

			if (arrivals.isEmpty()) {

				return false;

			}

			final ArrivalResponse next = arrivals.get(0);

			final SimplifiedRoute route = MinecraftClientData.getInstance().simplifiedRouteIdMap.get(next.getRouteId());

			if (route == null || next.getDestination() == null) {

				return false;

			}

			final String destCjk = getCjkText(next.getDestination());

			int destIdx = -1;

			int thisIdx = -1;

			final ObjectArrayList<SimplifiedRoutePlatform> plats = route.getPlatforms();

			for (int i = 0; i < plats.size(); ++i) {

				final SimplifiedRoutePlatform sp = plats.get(i);

				if (destIdx == -1 && destCjk.equals(getCjkText(sp.getStationName()))) {

					destIdx = i;

				}

				if (thisIdx == -1 && sp.getPlatformId() == platformId) {

					thisIdx = i;

				}

			}

			if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] 自动方向 destCjk={} destIdx={} thisIdx={} -> 朝左={}", destCjk, destIdx, thisIdx, (destIdx >= 0 && thisIdx >= 0 && destIdx > thisIdx));

			return destIdx >= 0 && thisIdx >= 0 && destIdx > thisIdx;

		} catch (Exception e) {

			Init.LOGGER.error("", (Throwable)e);

			return false;

		}

	}

	private void drawSingleRouteRows(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {

		final ObjectArrayList<SimplifiedRoute> routes = new ObjectArrayList<>();

		for (final SimplifiedRoute route : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {

			for (final SimplifiedRoutePlatform sp : route.getPlatforms()) {

				if (sp.getPlatformId() == platformId) {

					routes.add(route);

					break;

				}

			}

		}

		if (routes.size() <= 1) {

			drawGlassRoute(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);

			return;

		}

		final float leftX = 0.04f;

		final float rightX = 1.0f - leftX;

		final float rowH = 0.46f;

		for (int i = 0; i < routes.size(); i++) {

			final float y0 = 0.03f + i * rowH;

			drawSingleRouteRow(storedMatrixTransformations, routes.get(i), y0, rowH, leftX, rightX, facing, light);

		}

	}

	private void drawSingleRouteRow(StoredMatrixTransformations storedMatrixTransformations, SimplifiedRoute route, float y0, float rowH, float leftX, float rightX, Direction facing, int light) {

		final float lineY = y0 + rowH / 2.0f;

		MainRenderer.scheduleRender(new Identifier("mtr", "textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (gh, off) -> {

			storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

			IDrawing.drawTexture(gh, leftX, lineY, 0.0f, rightX, lineY + 0.025f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, 0xFF000000 | route.getColor(), GraphicsHolder.getDefaultLight());

			gh.pop();

		});

		final ObjectArrayList<SimplifiedRoutePlatform> plats = route.getPlatforms();

		MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (gh, off) -> {

			storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

			gh.push();

			gh.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);

			float x = (leftX + 0.03f) * SCALE;

			final float y = lineY * SCALE - 4.5f * TEXT_SCALE;

			for (final SimplifiedRoutePlatform sp : plats) {

				final String name = getCjkText(sp.getStationName());

				drawTextLeft(gh, name, x, y, TEXT_SCALE, COLOR_TEXT, 0.16f * SCALE);

				x += GraphicsHolder.getTextWidth(name) * TEXT_SCALE + 0.05f * SCALE;

			}

			gh.pop();

			gh.pop();

		});

	}

	private void drawMultiRouteRows(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {

		final ObjectArrayList<SimplifiedRoute> routes = new ObjectArrayList<>();

		for (final SimplifiedRoute route : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {

			for (final SimplifiedRoutePlatform sp : route.getPlatforms()) {

				if (sp.getPlatformId() == platformId) {

					routes.add(route);

					break;

				}

			}

		}

		if (routes.isEmpty()) {

			drawGlassRoute(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);

			return;

		}

		final float leftX = 0.05f;

		final float rowH = 0.14f;

		final float startY = 0.06f;

		final float w = 1.0f - 2.0f * leftX;

		for (int i = 0; i < routes.size(); i++) {

			final SimplifiedRoute route = routes.get(i);

			final float y = startY + i * rowH;

			final String name = route.getName();

			MainRenderer.scheduleRender(new Identifier("mtr", "textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (gh, off) -> {

				storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

				IDrawing.drawTexture(gh, leftX, y, 0.0f, leftX + 0.10f, y + rowH - 0.015f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, 0xFF000000 | route.getColor(), GraphicsHolder.getDefaultLight());

				gh.pop();

			});

			MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (gh, off) -> {

				storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

				gh.push();

				gh.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);

				drawTextLeft(gh, name, (leftX + 0.12f) * SCALE, (y + rowH / 2.0f) * SCALE - 4.5f * TEXT_SCALE, TEXT_SCALE, COLOR_TEXT, w * SCALE);

				gh.pop();

				gh.pop();

			});

		}

	}

	private void drawColorStrip(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int light) {

		final DynamicTextureCache.DynamicResource colorStrip = DynamicTextureCache.instance.getColorStrip(platformId);

		if (colorStrip != null && colorStrip.width == 1) {

			MainRenderer.scheduleRender(colorStrip.identifier, false, QueuedRenderLayer.EXTERIOR, (graphicsHolderNew, offset) -> {

				storedMatrixTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);

				IDrawing.drawTexture(graphicsHolderNew, leftBlocks == 0 ? sidePadding : 0.0f, COLOR_STRIP_START, 0.0f, 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f), COLOR_STRIP_END, 0.0f, facing, -1, light);

				graphicsHolderNew.pop();

			});

		}

	}

	public RenderPSDTop(BlockEntityRenderer.Argument dispatcher) {

		super(dispatcher, 1.95f, 3.0f, 6.0f, 0.125f, true, 3, BlockPSDTop.ARROW_DIRECTION);

	}

	@Override

	protected RenderRouteBase.RenderType getRenderType(World world, BlockPos pos, BlockState state) {

		final BlockPSDTop.EnumPersistent persistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);

		final RenderRouteBase.RenderType renderType;

		if (isStandaloneModule(state.getBlock())) {

			renderType = state.getBlock().data instanceof MyPSDTopLcd12 ? RenderRouteBase.RenderType.NONE : RenderRouteBase.RenderType.ARROW;

		} else if (persistent == BlockPSDTop.EnumPersistent.NONE) {

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

		final boolean standaloneModule = isStandaloneModule(state.getBlock());

		if (!(currentBlockBelow.data instanceof BlockPSDAPGDoorBase) && !standaloneModule) {

			return;

		}

		if (state.getBlock().data instanceof MyPSDTopLcd12) {

			if (IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.ROUTE) {

				drawGlassRoute(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);

			}

			return;

		}

		if (state.getBlock().data instanceof MyPSDTopLcd13) {

			if (IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.ROUTE) {

				drawPerRouteMaps(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);

			}

			drawColorStrip(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, light);

			return;

		}

		if (state.getBlock().data instanceof MyPSDTopLcd14) {

			final int routeIndex = getLcd14RouteIndex(state);

			if (IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.ROUTE) {

				drawGroovedScreen(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light, routeIndex);

			}

			drawGroovedColorStrip(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, light);

			return;

		}

		final boolean isNotPersistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == BlockPSDTop.EnumPersistent.NONE;

		final boolean airLeft = isNotPersistent && IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT);

		final boolean airRight = isNotPersistent && IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT);

		final float glassX1 = airLeft ? 0.625f : 0.0f;

		final float glassX2 = airRight ? 0.375f : 1.0f;

		MainRenderer.scheduleRender(new Identifier("mtr", "textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (borderGh, borderOff) -> {

			storedMatrixTransformations.transform((GraphicsHolder) borderGh, (Vector3d) borderOff);

			final float bz = GLASS_BORDER_Z;

			IDrawing.drawTexture(borderGh, glassX1, LED_PANEL_Y2 - GLASS_BORDER, bz, glassX2, LED_PANEL_Y2, bz, 0.0f, 0.0f, 1.0f, 1.0f, facing, COLOR_PANEL, light);

			IDrawing.drawTexture(borderGh, glassX1, LED_PANEL_Y1, bz, glassX2, LED_PANEL_Y1 + GLASS_BORDER, bz, 0.0f, 0.0f, 1.0f, 1.0f, facing, COLOR_PANEL, light);

			if (leftBlocks == 0) {

				IDrawing.drawTexture(borderGh, glassX1, LED_PANEL_Y1, bz, glassX1 + GLASS_BORDER, LED_PANEL_Y2, bz, 0.0f, 0.0f, 1.0f, 1.0f, facing, COLOR_PANEL, light);

			}

			if (rightBlocks == 0) {

				IDrawing.drawTexture(borderGh, glassX2 - GLASS_BORDER, LED_PANEL_Y1, bz, glassX2, LED_PANEL_Y2, bz, 0.0f, 0.0f, 1.0f, 1.0f, facing, COLOR_PANEL, light);

			}

			borderGh.pop();

		});

		MainRenderer.scheduleRender(new Identifier("mtr", "textures/block/transparent.png"), false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (glassGh, glassOff) -> {

			storedMatrixTransformations.transform((GraphicsHolder) glassGh, (Vector3d) glassOff);

			IDrawing.drawTexture(glassGh, glassX1, LED_PANEL_Y1, 0.0f, glassX2, LED_PANEL_Y2, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, -1, light);

			glassGh.pop();

		});

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

				final float startY = (LED_PANEL_Y1 - 2.0f * lineH - 0.03f);

				final float colStep = badgeW + 0.16f;

				MainRenderer.scheduleRender(new Identifier("mtr:textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (badgeGh, badgeOff) -> {

					storedMatrixTransformations.transform((GraphicsHolder) badgeGh, (Vector3d) badgeOff);

					for (int i = 0; i < lineCount; i++) {

						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(i);

						final int col = i / 2;

						final int row = i % 2;

						final float x = col == 0 ? leftX : 0.5f;

						final float y = startY + row * lineH;

						IDrawing.drawTexture(badgeGh, x, y, 0.0f, x + badgeW, y + badgeH, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, 0xFF000000 | nl.routeColor, GraphicsHolder.getDefaultLight());

					}

					badgeGh.pop();

				});

				MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (nGh, nOff) -> {

					storedMatrixTransformations.transform((GraphicsHolder) nGh, (Vector3d) nOff);

					nGh.push();

					nGh.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);

					for (int i = 0; i < lineCount; i++) {

						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(i);

						final int col = i / 2;

						final int row = i % 2;

						final float x = col == 0 ? leftX : 0.5f;

						final float y = startY + row * lineH;

						final String nextCjk = getCjkText(nl.nextStation);

						float textRight = (x + badgeW + 0.03f) * SCALE;

						final float textCenterY = (y + badgeH / 2.0f) * SCALE;

						if (!nl.routeName.isEmpty()) {

							final String routeNameCjk = getCjkText(nl.routeName);

							final float routeScale = badgeH * 0.8f / 0.1f;

							drawTextLeft(nGh, routeNameCjk, textRight, textCenterY - 4.5f * routeScale, routeScale, 0xFF000000, (1 + rightBlocks) * SCALE * 0.7f);

							textRight += routeNameCjk.length() * routeScale * 9.0f + 0.03f * SCALE;

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

			if (lcdMulti != null && !lcdMulti.nextLines.isEmpty() && !(state.getBlock().data instanceof MyPSDTopLcd15) && !(state.getBlock().data instanceof MyPSDTopLcd16)) {

				final ObjectArrayList<PSDTopTextureGenerator.LcdNextLine> nextLines = lcdMulti.nextLines;

				final int lineCount = nextLines.size();

				final float lineH = 0.10f;

				final float badgeH = 0.065f;

				final float badgeW = 0.11f;

				final float leftX = LED_PANEL_EDGE;

				final float startY = (LED_PANEL_Y1 - 2.0f * lineH - 0.03f);

				final float colStep = badgeW + 0.16f;

				MainRenderer.scheduleRender(new Identifier("mtr:textures/block/white.png"), false, QueuedRenderLayer.EXTERIOR, (badgeGh, badgeOff) -> {

					storedMatrixTransformations.transform((GraphicsHolder) badgeGh, (Vector3d) badgeOff);

					for (int i = 0; i < lineCount; i++) {

						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(lineCount - 1 - i);

						final int col = i / 2;

						final int row = i % 2;

						final float x = col == 0 ? leftX : 1.00f;

						final float y = startY + row * lineH;

						IDrawing.drawTexture(badgeGh, x, y, 0.0f, x + badgeW, y + badgeH, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing, 0xFF000000 | nl.routeColor, GraphicsHolder.getDefaultLight());

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

				if (lcdMulti != null && !lcdMulti.nextLines.isEmpty() && !(state.getBlock().data instanceof MyPSDTopLcd15) && !(state.getBlock().data instanceof MyPSDTopLcd16)) {

					final ObjectArrayList<PSDTopTextureGenerator.LcdNextLine> nextLines = lcdMulti.nextLines;

					final int lineCount = nextLines.size();

					final float lineH = 0.10f;

					final float badgeH = 0.065f;

					final float badgeW = 0.11f;

					final float leftX = LED_PANEL_EDGE;

					final float startY = (LED_PANEL_Y1 - 2.0f * lineH - 0.03f);

					final float colStep = badgeW + 0.16f;

					for (int i = 0; i < lineCount; i++) {

						final PSDTopTextureGenerator.LcdNextLine nl = nextLines.get(lineCount - 1 - i);

						final int col = i / 2;

						final int row = i % 2;

						final float x = col == 0 ? leftX : 1.00f;

						final float y = startY + row * lineH;

						final String nextCjk = getCjkText(nl.nextStation);

						float textRight = (x + badgeW + 0.03f) * SCALE;

						final float textCenterY = (y + badgeH / 2.0f) * SCALE;

						if (!nl.routeName.isEmpty()) {

							final String routeNameCjk = getCjkText(nl.routeName);

							final float routeScale = badgeH * 0.8f / 0.1f;

							drawTextLeft(lcdGh, routeNameCjk, textRight, textCenterY - 4.5f * routeScale, routeScale, 0xFF000000, (1 + rightBlocks) * SCALE * 0.7f);

							textRight += routeNameCjk.length() * routeScale * 9.0f + 0.03f * SCALE;

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

		final String routeNameText = arrival == null ? "" : getCjkText(arrival.getRouteName());

		final String badgeText = routeNameText.isEmpty() ? routeNumber : routeNameText;

		final String destinationText = arrival == null ? "" : getDestinationText(arrival.getDestination());

		final int routeColor = arrival == null ? 0 : 0xFF000000 | arrival.getRouteColor();

		final boolean hasRoute = arrival != null && !badgeText.isEmpty();

		final float routeWidth = hasRoute ? (GraphicsHolder.getTextWidth(badgeText) + ROUTE_PAD * 2 * SCALE) * 1.4f : 0.0f;

				final long departureRemaining = arrival == null ? Long.MAX_VALUE : (arrival.getDeparture() - ArrivalsCacheClient.INSTANCE.getMillisOffset() - System.currentTimeMillis()) / 1000;

				final boolean stopped = arrivalRemaining <= 5 && departureRemaining > -5;

		final float rowWidth = (1 + rightBlocks) * SCALE;

		final float colWidth = rowWidth / 3.0f;

		final StoredMatrixTransformations ledTransformations = storedMatrixTransformations.copy();

		ledTransformations.add(graphicsHolderNew -> graphicsHolderNew.translate(0.0, 0.0, LED_Z_OFFSET));

		final float routeTextWidthPx = hasRoute ? GraphicsHolder.getTextWidth(badgeText) : 0.0f;

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

			final float nearbyDoorValue;

			if (doorStateBelow.getBlock().data instanceof BlockPSDAPGDoorBase && IBlock.getStatePropertySafe(doorStateBelow, IBlock.SIDE) == IBlock.EnumSide.LEFT) {

				nearbyDoorValue = isCurrentDoorOpen(currentPos) ? 1f : 0f;

			} else if (isDoubleTopModule(state.getBlock())) {

				nearbyDoorValue = findNearbyDoorValue(lastWorld, currentPos, rawFacing);

			} else {

				nearbyDoorValue = -1f;

			}

			if (leftBlocks == 0 && (nearbyDoorValue >= 0f || isDoubleTopModule(state.getBlock()))) {

				final String msg;

				final int msgColor;

				if (nearbyDoorValue >= 0f) {

					final boolean open = nearbyDoorValue > 0.1f;

					msg = open ? "车门开启，请注意安全" : "车门关闭，请勿倚靠车门";

					msgColor = open ? COLOR_ARRIVED : 0xFFFF0000;

				} else {

					msg = "车辆进站，请注意安全";

					msgColor = 0xFFFFA500;

				}

				final float centerX = (1 + rightBlocks) * SCALE / 2.0f;

				MainRenderer.scheduleRender(QueuedRenderLayer.TEXT, (graphicsHolderNew, offset) -> {

					ledTransformations.transform((GraphicsHolder) graphicsHolderNew, (Vector3d) offset);

					graphicsHolderNew.translate(0.0, 0.0, -0.002f);

					graphicsHolderNew.push();

					graphicsHolderNew.scale(1.0f / SCALE, 1.0f / SCALE, 1.0f / SCALE);

					drawTextCentered(graphicsHolderNew, msg, centerX, (LED_PANEL_Y1 + (LED_PANEL_Y2 - LED_PANEL_Y1) / 2.0f) * SCALE - 4.5f * TEXT_SCALE, TEXT_SCALE, msgColor, 2.0f * SCALE);

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

						drawTextCentered(graphicsHolderNew, badgeText, cx1, (ROUTE_Y1 + ROUTE_Y2) / 2.0f * SCALE - 4.5f * TEXT_SCALE, TEXT_SCALE, COLOR_ROUTE_TEXT, colWidth);

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

		if (state.getBlock().data instanceof MyPSDTopLcd12 || state.getBlock().data instanceof MyPSDTopLcd13 || state.getBlock().data instanceof MyPSDTopLcd14) {

			return 0.0f;

		}

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

		if (cached != null) {

			PLATFORM_ARROW_TIMES.put(key, System.currentTimeMillis());

			return cached;

		}

		if (PLATFORM_ARROW_GENERATING.contains(key)) {

			return null;

		}

		PLATFORM_ARROW_GENERATING.add(key);

		PSDTopTextureGenerator.setConstants();

		MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> DynamicTextureCache.instance.getRouteSquare(0, "1", IGui.HorizontalAlignment.CENTER));

		MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {

			try {

				PSDTopTextureGenerator.setConstants();

				final NativeImage image = PSDTopTextureGenerator.generateDirectionArrow(platformId, hasLeft, hasRight, IGui.HorizontalAlignment.CENTER, true, 0.25f, ratio, -1, -16777216, -1, showPlatform);

				if (image == null) {

					return;

				}

				final NativeImageBackedTexture texture = new NativeImageBackedTexture(image);

				final Identifier identifier = new Identifier("mtr_psd_lcd", "platform_arrow_" + Math.abs(key.hashCode()));

				MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, AbstractTexture.cast(texture));

				PLATFORM_ARROW_TEXTURES.put(key, identifier);

				PLATFORM_ARROW_TIMES.put(key, System.currentTimeMillis());

			} catch (Exception e) {

				Init.LOGGER.error("Failed to generate platform arrow: " + key, e);

			} finally {

				PLATFORM_ARROW_GENERATING.remove(key);

			}

		});

		return null;

	}

	private static final Map<String, NativeImage> MTR_STATION_PENDING = new java.util.concurrent.ConcurrentHashMap<>();
	private static final java.util.Set<String> MTR_STATION_GENERATING = java.util.concurrent.ConcurrentHashMap.newKeySet();

	private Object[] getMtrStationCompositeEntry(String stationName, float aspectRatio) {
		final String key = "mtrstation_" + stationName + "_" + aspectRatio;

		final NativeImage pendingImage = MTR_STATION_PENDING.remove(key);
		if (pendingImage != null) {
			final NativeImageBackedTexture texture = new NativeImageBackedTexture(pendingImage);
			final Identifier identifier = new Identifier("mtr_psd_lcd", "mtr_station_" + Math.abs(key.hashCode()));
			MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, AbstractTexture.cast(texture));
			final Object[] entry = new Object[]{identifier, pendingImage.getWidth(), pendingImage.getHeight()};
			MTR_STATION_TEXTURES.put(key, entry);
			return entry;
		}
		final Object[] cached = MTR_STATION_TEXTURES.get(key);
		if (cached != null) {
			return cached;
		}

		if (MTR_STATION_GENERATING.add(key)) {
			RouteMapGenerator.setConstants();
			MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> DynamicTextureCache.instance.getRouteSquare(0, "1", IGui.HorizontalAlignment.CENTER));
			MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {
				try {
					PSDTopTextureGenerator.setConstants();
					final NativeImage image = PSDTopTextureGenerator.getMTRStationCompositeImage(stationName, aspectRatio);
					if (image != null) {
						MTR_STATION_PENDING.put(key, image);
					}
				} catch (Exception e) {
					Init.LOGGER.error("MTR station composite failed", e);
				} finally {
					MTR_STATION_GENERATING.remove(key);
				}
			});
		}
		return null;
	}

	private Identifier getPerRouteTexture(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite) {

		final String key = "perroute_" + platformId + "_" + routeId + "_" + (int)(aspectRatio * 1000) + "_" + flip;

		final Identifier cached = PER_ROUTE_TEXTURES.get(key);

		if (cached != null) {

			PER_ROUTE_TIMES.put(key, System.currentTimeMillis());

			return cached;

		}

		RouteMapGenerator.setConstants();

		DynamicTextureCache.instance.getRouteSquare(0, "1", IGui.HorizontalAlignment.CENTER);

		final NativeImage image = RouteMapGenerator.generateRouteMap(platformId, routeId, vertical, flip, aspectRatio, transparentWhite);

		if (image == null) {

			return null;

		}

		final NativeImageBackedTexture texture = new NativeImageBackedTexture(image);

		final Identifier identifier = new Identifier("mtr_psd_lcd", "per_route_" + Math.abs(key.hashCode()));

		MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, AbstractTexture.cast(texture));

		PER_ROUTE_TEXTURES.put(key, identifier);

		PER_ROUTE_TIMES.put(key, System.currentTimeMillis());

		return identifier;

	}

	// ==========================================================================================

	// ==========================================================================================
	private static final Map<String, Identifier> LIVE_TEXTURE = new HashMap<>();
	private static final Map<String, NativeImageBackedTexture> LIVE_DYNAMIC = new HashMap<>();
	private static final Map<String, String> LIVE_CONTENT = new HashMap<>();
	private static final Map<String, NativeImage> LIVE_PENDING_IMAGE = new HashMap<>();
	private static final Map<String, String> LIVE_PENDING_CONTENT = new HashMap<>();
	private static final Set<String> LIVE_GENERATING = new HashSet<>();
	private static final Map<String, NativeImage> LIVE_IMAGE_REF = new HashMap<>();

	private static String getRefreshSignature(long platformId) {
		final LongArrayList pids = new LongArrayList();
		pids.add(platformId);
		final ObjectArrayList<ArrivalResponse> arrivals = ArrivalsCacheClient.INSTANCE.requestArrivals(pids);
		final long offset = ArrivalsCacheClient.INSTANCE.getMillisOffset();
		final long now = System.currentTimeMillis();
		final java.util.TreeSet<String> parts = new java.util.TreeSet<>();
		if (arrivals != null) {
			for (final ArrivalResponse ar : arrivals) {
				final long seconds = (ar.getArrival() - offset - now) / 1000L;
				final long bucket = seconds > 60L ? 60L : 1L;
				parts.add(ar.getRouteId() + ":" + (seconds / bucket) + ":" + ar.getDestination() + ":" + ar.getRouteName());
			}
		}
		final StringBuilder sb = new StringBuilder();
		for (final String part : parts) {
			sb.append(part).append(';');
		}
		return sb.toString();
	}

	private Identifier getCustomRouteTexture(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite, String slotDiscriminator, String refreshSignature, String customText, String customImagePath) {
		final String slotKey = "customroute_" + platformId + "_" + (int)(aspectRatio * 1000.0f) + "_" + flip + slotDiscriminator;
		final String contentKey = routeId + "#" + refreshSignature + "#" + transparentWhite + "#" + customText + "#" + customImagePath
				+ "#" + com.mtrpsdlcd.block.PSDCustomText.imageVersion(customImagePath);

		final NativeImage ready = LIVE_PENDING_IMAGE.remove(slotKey);
		if (ready != null) {
			NativeImageBackedTexture dynamic = LIVE_DYNAMIC.get(slotKey);
			if (dynamic == null) {
				dynamic = new NativeImageBackedTexture(ready);
				final Identifier identifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("custom_route_" + Math.abs(slotKey.hashCode()), dynamic);
				LIVE_TEXTURE.put(slotKey, identifier);
				LIVE_DYNAMIC.put(slotKey, dynamic);
				LIVE_IMAGE_REF.put(slotKey, ready);
				if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] 实时纹理首次注册 slot={} 尺寸={}x{}", slotKey, ready.getWidth(), ready.getHeight());
			} else {

				if (LIVE_IMAGE_REF.get(slotKey) != ready) {
					dynamic.setImage(ready);
					LIVE_IMAGE_REF.put(slotKey, ready);
				}
				dynamic.upload();
			}
			LIVE_CONTENT.put(slotKey, LIVE_PENDING_CONTENT.remove(slotKey));
		}

		if (!contentKey.equals(LIVE_CONTENT.get(slotKey)) && !LIVE_GENERATING.contains(slotKey)) {
			LIVE_GENERATING.add(slotKey);
			if (LIVE_TEXTURE.get(slotKey) == null) {
				RouteMapGenerator.setConstants();
				MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> DynamicTextureCache.instance.getRouteSquare(0, "1", IGui.HorizontalAlignment.CENTER));
			}
			MainRenderer.WORKER_THREAD.scheduleDynamicTextures(() -> {
				try {
					RouteMapGenerator.setConstants();
					final NativeImage image = RouteMapGenerator.generateCustomSingleRouteMap(platformId, routeId, vertical, flip, aspectRatio, transparentWhite, slotKey, customText, customImagePath);
					if (image != null) {

						final NativeImage stale = LIVE_PENDING_IMAGE.put(slotKey, image);
						if (stale != null && stale != image) {
							try {
								stale.close();
							} catch (Exception ignored) {
							}
						}
						LIVE_PENDING_CONTENT.put(slotKey, contentKey);
					}
				} catch (Exception e) {
					Init.LOGGER.error("Failed to generate custom route map: " + slotKey, e);
				} finally {
					LIVE_GENERATING.remove(slotKey);
				}
			});
		}

		return LIVE_TEXTURE.get(slotKey);
	}

	private void drawPerRouteMaps(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {

		final ObjectArrayList<SimplifiedRoute> routes = new ObjectArrayList<>();

		final long stationId = getStationIdForPlatform(platformId);

		for (final SimplifiedRoute route : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {

			for (final SimplifiedRoutePlatform sp : route.getPlatforms()) {

				if (stationId >= 0L ? sp.getStationId() == stationId : sp.getPlatformId() == platformId) {

					routes.add(route);

					break;

				}

			}

		}

		if (routes.size() <= 1) {

			drawGlassRoute(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);

			return;

		}

		final float routeHeight = 1.0f - topPadding - bottomPadding;

		final float width = (leftBlocks + rightBlocks + 1) - sidePadding * 2.0f;

		final int arrowDirection = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);

		final boolean flip = arrowDirection == 2;

		final float n = routes.size();

		final float rowH = routeHeight / n;

		for (int i = 0; i < routes.size(); i++) {

			final SimplifiedRoute route = routes.get(i);

			final Identifier tex = getCustomRouteTexture(platformId, route.getId(), false, flip, width / rowH, true, "_r" + route.getId(), "", com.mtrpsdlcd.block.PSDCustomText.DEFAULT_TEXT, "");

			if (tex != null) {

				final float y0 = topPadding + i * rowH;

				MainRenderer.scheduleRender(tex, false, QueuedRenderLayer.EXTERIOR, (gh, off) -> {

					storedMatrixTransformations.transform((GraphicsHolder) gh, (Vector3d) off);

					IDrawing.drawTexture(gh, leftBlocks == 0 ? sidePadding : 0.0f, y0, 0.0f, 1.0f - (rightBlocks == 0 ? sidePadding : 0.0f), y0 + rowH, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, facing.getOpposite(), color, light);

					gh.pop();

				});

			}

		}

	}

	private long getStationIdForPlatform(long platformId) {

		if (platformId < 0) {

			return -1L;

		}

		for (final SimplifiedRoute route : MinecraftClientData.getInstance().simplifiedRouteIdMap.values()) {

			for (final SimplifiedRoutePlatform sp : route.getPlatforms()) {

				if (sp.getPlatformId() == platformId) {

					return sp.getStationId();

				}

			}

		}

		return -1L;

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
