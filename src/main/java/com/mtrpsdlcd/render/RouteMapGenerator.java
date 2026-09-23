/*
 * Decompiled with CFR 0.152.
 */
package com.mtrpsdlcd.render;

import com.mtrpsdlcd.Constants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import com.mtrpsdlcd.block.PSDCustomText;
import org.mtr.core.data.Data;
import org.mtr.core.data.Platform;
import org.mtr.core.data.Route;
import org.mtr.core.data.SimplifiedRoute;
import org.mtr.core.data.SimplifiedRoutePlatform;
import org.mtr.core.data.Station;
import org.mtr.core.operation.ArrivalResponse;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mod.data.ArrivalsCacheClient;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MathHelper;
import org.mtr.mapping.holder.NativeImage;
import org.mtr.mapping.holder.NativeImageFormat;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import org.mtr.mod.Init;
import org.mtr.mod.client.DynamicTextureCache;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.config.Config;
import org.mtr.mod.data.IGui;
import org.mtr.mod.generated.lang.TranslationProvider;

public class RouteMapGenerator
implements IGui {
    private static int scale;
    private static int lineSize;
    private static int lineSpacing;
    private static int fontSizeBig;
    private static int fontSizeSmall;

    private static final float LINE_Y_BASE_RATIO = 0.68f;
    private static final float WIDTH_INSET_RATIO = 0.115f;
    private static final float STATION_NAME_ANGLE = 45.0f;
    private static final float STATION_NAME_FONT_SCALE = 0.92f;
    public static final int PIXEL_SCALE = 4;
    private static final int MIN_VERTICAL_SIZE = 5;
    private static final String LOGO_RESOURCE = "textures/block/sign/logo.png";
    private static final String EXIT_RESOURCE = "textures/block/sign/exit_letter_blank.png";
    private static final String ARROW_RESOURCE = "textures/block/sign/arrow.png";
    private static final String CIRCLE_RESOURCE = "textures/block/sign/circle.png";
    private static final String TEMP_CIRCULAR_MARKER_CLOCKWISE;
    private static final String TEMP_CIRCULAR_MARKER_ANTICLOCKWISE;
    private static final int PIXEL_RESOLUTION = 24;

    private static boolean perRoute = false;

    public static void setConstants() {
        scale = (int)Math.pow(2.0, Config.getClient().getDynamicTextureResolution() + 5);
        lineSize = scale / 8;
        lineSpacing = lineSize * 3 / 2;
        fontSizeBig = lineSize * 2;
        fontSizeSmall = fontSizeBig / 2;
    }

    public static NativeImage generatePixelatedText(String text, int textColor, int maxWidth, double cjkSizeRatio, boolean fullPixel) {
        try {
            int scale = fullPixel ? 1 : 4;
            int newMaxWidth = maxWidth / scale;
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(text, dimensions, newMaxWidth, Integer.MAX_VALUE, (int)Math.round(24.0 * (cjkSizeRatio > 0.0 ? cjkSizeRatio + 1.0 : 1.0)), (int)Math.round(24.0 * (cjkSizeRatio < 0.0 ? 1.0 - cjkSizeRatio : 1.0)), 0, IGui.HorizontalAlignment.CENTER);
            int width = Math.min(newMaxWidth, dimensions[0]) * scale;
            int height = dimensions[1] * scale;
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            RouteMapGenerator.drawStringPixelated(nativeImage, pixels, dimensions, textColor, fullPixel);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateColorStrip(long platformId) {
        try {
            IntArrayList colors = RouteMapGenerator.getRouteStream(platformId, (simplifiedRoute, currentStationIndex) -> {});
            if (colors.isEmpty()) {
                NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), 1, 1, false);
                nativeImage.setPixelColor(0, 0, 0);
                return nativeImage;
            }
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), 1, colors.size(), false);
            for (int i = 0; i < colors.size(); ++i) {
                RouteMapGenerator.drawPixelSafe(nativeImage, 0, i, 0xFF000000 | colors.getInt(i));
            }
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateStationName(String stationName, float aspectRatio) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            int height = scale * 2;
            int width = Math.round((float)height * aspectRatio);
            int padding = scale / 16;
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(stationName, dimensions, width - padding * 2, height - padding * 2, fontSizeBig * 2, fontSizeSmall * 2, padding, IGui.HorizontalAlignment.CENTER);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            RouteMapGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateTallStationName(int textColor, String stationName, int stationColor, float aspectRatio) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            int width = Math.round((float)scale * 1.6f);
            int height = Math.round((float)width / aspectRatio);
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(IGui.formatVerticalChinese(stationName), dimensions, width, height, fontSizeBig * 2, fontSizeSmall * 2, 0, IGui.HorizontalAlignment.CENTER);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            RouteMapGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0xFF000000 | stationColor, textColor, false);
            RouteMapGenerator.clearColor(nativeImage, RouteMapGenerator.invertColor(0xFF000000 | stationColor));
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateStationNameEntrance(int textColor, String stationName, float aspectRatio) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            int size = scale * 2;
            int width = Math.round((float)size * aspectRatio);
            int padding = scale / 16;
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(stationName, dimensions, width - size - padding, size - padding * 2, fontSizeBig * 3, fontSizeSmall * 3, padding, IGui.HorizontalAlignment.LEFT);
            int xOffset = (width - dimensions[0] - size) / 2;
            int fakeBackgroundColor = textColor == -16777216 ? textColor + 65793 : 0;
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, size, false);
            nativeImage.fillRect(0, 0, width, size, fakeBackgroundColor);
            RouteMapGenerator.drawResource(nativeImage, LOGO_RESOURCE, xOffset, 0, size, size, false, 0.0f, 1.0f, 0, true);
            RouteMapGenerator.drawString(nativeImage, pixels, size + xOffset, size / 2, dimensions, IGui.HorizontalAlignment.LEFT, IGui.VerticalAlignment.CENTER, fakeBackgroundColor, textColor, false);
            RouteMapGenerator.clearColor(nativeImage, RouteMapGenerator.invertColor(fakeBackgroundColor));
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateSingleRowStationName(long platformId, float aspectRatio) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(RouteMapGenerator.getStationName(platformId).replace("|", " | "), dimensions, fontSizeBig, fontSizeSmall);
            int padding = dimensions[1] / 2;
            int height = dimensions[1] + padding;
            int width = Math.max(Math.round((float)height * aspectRatio), dimensions[0] + padding);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, -1);
            RouteMapGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -16777216, false);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateSignText(String text, IGui.HorizontalAlignment horizontalAlignment, float paddingScale, int backgroundColor, int textColor) {
        try {
            int height = scale;
            int padding = Math.round((float)height * paddingScale);
            int tileSize = height - padding * 2;
            int tilePadding = tileSize / 4;
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(text, dimensions, Integer.MAX_VALUE, (int)((float)tileSize * 1.25f), tileSize * 3 / 5, tileSize * 3 / 10, tilePadding, horizontalAlignment);
            int width = dimensions[0] - tilePadding * 2;
            if (width <= 0 || height <= 0) {
                return null;
            }
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            RouteMapGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, backgroundColor, textColor, false);
            RouteMapGenerator.clearColor(nativeImage, RouteMapGenerator.invertColor(backgroundColor));
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateLiftPanel(String text, int textColor) {
        try {
            int width = Math.round((float)scale * 1.5f);
            int height = fontSizeSmall * 2 * text.split("\\|").length;
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(text.toUpperCase(Locale.ENGLISH), dimensions, width, height, fontSizeSmall * 2, fontSizeSmall * 2, 0, IGui.HorizontalAlignment.CENTER);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            RouteMapGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, -16777216, textColor, false);
            RouteMapGenerator.clearColor(nativeImage, RouteMapGenerator.invertColor(-16777216));
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateExitSignLetter(String exitLetter, String exitNumber, int backgroundColor) {
        try {
            int size = scale / 2;
            boolean noNumber = exitNumber.isEmpty();
            int textSize = size * 7 / 8;
            int[] dimensions1 = new int[2];
            byte[] pixels1 = DynamicTextureCache.instance.getTextPixels(exitLetter, dimensions1, noNumber ? textSize : textSize * 2 / 3, textSize, textSize, size, size, IGui.HorizontalAlignment.CENTER);
            int[] dimensions2 = new int[2];
            byte[] pixels2 = noNumber ? null : DynamicTextureCache.instance.getTextPixels(exitNumber, dimensions2, textSize / 3, textSize, textSize / 2, textSize / 2, size, IGui.HorizontalAlignment.CENTER);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), size, size, false);
            nativeImage.fillRect(0, 0, size, size, backgroundColor);
            RouteMapGenerator.drawResource(nativeImage, EXIT_RESOURCE, 0, 0, size, size, false, 0.0f, 1.0f, 0, true);
            RouteMapGenerator.drawString(nativeImage, pixels1, size / 2 - (noNumber ? 0 : textSize / 6 - size / 32), size / 2, dimensions1, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            if (!noNumber) {
                RouteMapGenerator.drawString(nativeImage, pixels2, size / 2 + textSize / 3 - size / 32, size / 2 + textSize / 8, dimensions2, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            }
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateRouteSquare(int color, String routeName, IGui.HorizontalAlignment horizontalAlignment) {
        try {
            int padding = scale / 32;
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(routeName, dimensions, Integer.MAX_VALUE, (int)((float)(fontSizeBig + fontSizeSmall) * 1.25f), fontSizeBig, fontSizeSmall, padding, horizontalAlignment);
            int width = dimensions[0] + padding * 2;
            int height = dimensions[1] + padding * 2;
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, RouteMapGenerator.invertColor(0xFF000000 | color));
            RouteMapGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateDirectionArrow(long platformId, boolean hasLeft, boolean hasRight, IGui.HorizontalAlignment horizontalAlignment, boolean showToString, float paddingScale, float aspectRatio, int backgroundColor, int textColor, int transparentColor) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            int circleX;
            ObjectArrayList<String> destinations = new ObjectArrayList<String>();
            IntArrayList colors = RouteMapGenerator.getRouteStream(platformId, (simplifiedRoute, currentStationIndex) -> destinations.add((switch (simplifiedRoute.getCircularState()) {
                case CLOCKWISE -> TEMP_CIRCULAR_MARKER_CLOCKWISE;
                case ANTICLOCKWISE -> TEMP_CIRCULAR_MARKER_ANTICLOCKWISE;
                default -> "";
            }) + simplifiedRoute.getPlatforms().get((int)currentStationIndex).getDestination()));
            boolean isTerminating = destinations.isEmpty();
            boolean leftToRight = horizontalAlignment == IGui.HorizontalAlignment.CENTER ? hasLeft || !hasRight : horizontalAlignment != IGui.HorizontalAlignment.RIGHT;
            int height = scale;
            int width = Math.round((float)height * aspectRatio);
            int padding = Math.round((float)height * paddingScale);
            int tileSize = height - padding * 2;
            if (width <= 0 || height <= 0) {
                return null;
            }
            DynamicTextureCache clientCache = DynamicTextureCache.instance;
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, RouteMapGenerator.invertColor(backgroundColor));
            if (isTerminating) {
                circleX = (int)horizontalAlignment.getOffset(0.0f, tileSize - width);
            } else {
                String destinationString = IGui.mergeStations(destinations);
                boolean isClockwise = destinationString.startsWith(TEMP_CIRCULAR_MARKER_CLOCKWISE);
                boolean isAnticlockwise = destinationString.startsWith(TEMP_CIRCULAR_MARKER_ANTICLOCKWISE);
                if (!(destinationString = destinationString.replace(TEMP_CIRCULAR_MARKER_CLOCKWISE, "").replace(TEMP_CIRCULAR_MARKER_ANTICLOCKWISE, "")).isEmpty()) {
                    if (isClockwise) {
                        destinationString = IGui.insertTranslation(TranslationProvider.GUI_MTR_CLOCKWISE_VIA_CJK, TranslationProvider.GUI_MTR_CLOCKWISE_VIA, 1, destinationString);
                    } else if (isAnticlockwise) {
                        destinationString = IGui.insertTranslation(TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA_CJK, TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA, 1, destinationString);
                    } else if (showToString) {
                        destinationString = IGui.insertTranslation(TranslationProvider.GUI_MTR_TO_CJK, TranslationProvider.GUI_MTR_TO, 1, destinationString);
                    }
                }
                int tilePadding = tileSize / 4;
                int leftSize = ((hasLeft ? 1 : 0) + (leftToRight ? 1 : 0)) * (tileSize + tilePadding);
                int rightSize = ((hasRight ? 1 : 0) + (leftToRight ? 0 : 1)) * (tileSize + tilePadding);
                int[] dimensionsDestination = new int[2];
                byte[] pixelsDestination = clientCache.getTextPixels(destinationString, dimensionsDestination, width - leftSize - rightSize - padding * (showToString ? 2 : 1), (int)((float)tileSize * 1.25f), tileSize * 3 / 5, tileSize * 3 / 10, tilePadding, leftToRight ? IGui.HorizontalAlignment.LEFT : IGui.HorizontalAlignment.RIGHT);
                int leftPadding = (int)horizontalAlignment.getOffset(0.0f, leftSize + rightSize + dimensionsDestination[0] - tilePadding * 2 - width);
                RouteMapGenerator.drawString(nativeImage, pixelsDestination, leftPadding + leftSize - tilePadding, height / 2, dimensionsDestination, IGui.HorizontalAlignment.LEFT, IGui.VerticalAlignment.CENTER, backgroundColor, textColor, false);
                if (hasLeft) {
                    RouteMapGenerator.drawResource(nativeImage, ARROW_RESOURCE, leftPadding, padding, tileSize, tileSize, false, 0.0f, 1.0f, textColor, false);
                }
                if (hasRight) {
                    RouteMapGenerator.drawResource(nativeImage, ARROW_RESOURCE, leftPadding + leftSize + dimensionsDestination[0] - tilePadding * 2 + rightSize - tileSize, padding, tileSize, tileSize, true, 0.0f, 1.0f, textColor, false);
                }
                circleX = leftPadding + leftSize + (leftToRight ? -tileSize - tilePadding : dimensionsDestination[0] - tilePadding);
            }
            for (int i = 0; i < colors.size(); ++i) {
                RouteMapGenerator.drawResource(nativeImage, CIRCLE_RESOURCE, circleX, padding, tileSize, tileSize, false, (float)i / (float)colors.size(), ((float)i + 1.0f) / (float)colors.size(), colors.getInt(i), false);
            }
            Platform platform = (Platform)MinecraftClientData.getInstance().platformIdMap.get(platformId);
            if (platform != null) {
                int[] dimensionsPlatformNumber = new int[2];
                byte[] pixelsPlatformNumber = clientCache.getTextPixels(platform.getName(), dimensionsPlatformNumber, tileSize, (int)((float)tileSize * 1.25f * 3.0f / 4.0f), tileSize * 3 / 4, tileSize * 3 / 4, 0, IGui.HorizontalAlignment.CENTER);
                RouteMapGenerator.drawString(nativeImage, pixelsPlatformNumber, circleX + tileSize / 2, padding + tileSize / 2, dimensionsPlatformNumber, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            }
            if (transparentColor != 0) {
                RouteMapGenerator.clearColor(nativeImage, RouteMapGenerator.invertColor(transparentColor));
            }
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateRouteMap(long platformId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite) {
        return RouteMapGenerator.generateRouteMap(platformId, -1L, vertical, flip, aspectRatio, transparentWhite);
    }

    public static NativeImage generateRouteMap(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            RouteMapGenerator.perRoute = routeId >= 0L;
            ObjectArrayList routeDetails = new ObjectArrayList();

            if (routeId >= 0L) {
                RouteMapGenerator.getRouteStreamForStation(platformId, (simplifiedRoute, currentStationIndex) -> routeDetails.add(new ObjectIntImmutablePair<SimplifiedRoute>((SimplifiedRoute)simplifiedRoute, (int)currentStationIndex)));
            } else {
                RouteMapGenerator.getRouteStream(platformId, (simplifiedRoute, currentStationIndex) -> routeDetails.add(new ObjectIntImmutablePair<SimplifiedRoute>((SimplifiedRoute)simplifiedRoute, (int)currentStationIndex)));
            }

            if (routeId >= 0L) {
                routeDetails.removeIf(routeDetail -> ((SimplifiedRoute)((ObjectIntImmutablePair)routeDetail).left()).getId() != routeId);
            }
            int routeCount = routeDetails.size();
            if (routeCount > 0) {
                float heightScale;
                float widthScale;
                int height;
                int width;
                float yOffset;
                float extraPadding;
                float rawHeight;
                int routeIndex;
                DynamicTextureCache clientCache = DynamicTextureCache.instance;
                ObjectArrayList<LongArrayList> stationsIdsBefore = new ObjectArrayList<LongArrayList>();
                ObjectArrayList<LongArrayList> stationsIdsAfter = new ObjectArrayList<LongArrayList>();
                ObjectArrayList<Int2ObjectAVLTreeMap<StationPosition>> stationPositions = new ObjectArrayList<Int2ObjectAVLTreeMap<StationPosition>>();
                IntAVLTreeSet colors = new IntAVLTreeSet();
                int[] colorIndices = new int[routeCount];
                int colorIndex = -1;
                int previousColor = -1;
                for (routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                    stationsIdsBefore.add(new LongArrayList());
                    stationsIdsAfter.add(new LongArrayList());
                    stationPositions.add(new Int2ObjectAVLTreeMap());
                    ObjectIntImmutablePair routeDetail = (ObjectIntImmutablePair)routeDetails.get(routeIndex);
                    ObjectArrayList<SimplifiedRoutePlatform> simplifiedRoutePlatforms = ((SimplifiedRoute)routeDetail.left()).getPlatforms();
                    int currentIndex = routeDetail.rightInt();
                    for (int stationIndex = 0; stationIndex < simplifiedRoutePlatforms.size(); ++stationIndex) {
                        if (stationIndex == currentIndex) continue;
                        long stationId = simplifiedRoutePlatforms.get(stationIndex).getStationId();
                        if (stationIndex < currentIndex) {
                            stationsIdsBefore.get(stationsIdsBefore.size() - 1).add(0, stationId);
                            continue;
                        }
                        ((LongArrayList)stationsIdsAfter.get(stationsIdsAfter.size() - 1)).add(stationId);
                    }
                    int color2 = ((SimplifiedRoute)routeDetail.left()).getColor();
                    colors.add(color2);
                    if (color2 != previousColor) {
                        previousColor = color2;
                    }
                    colorIndices[routeIndex] = ++colorIndex;
                }
                for (routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                    ((Int2ObjectAVLTreeMap)stationPositions.get(routeIndex)).put(0, new StationPosition(0.0f, RouteMapGenerator.getLineOffset(routeIndex, colorIndices), true));
                }
                float[] bounds = new float[3];
                RouteMapGenerator.setup(stationPositions, flip ? stationsIdsBefore : stationsIdsAfter, colorIndices, bounds, flip, true);
                float xOffset = bounds[0] + 0.5f;
                RouteMapGenerator.setup(stationPositions, flip ? stationsIdsAfter : stationsIdsBefore, colorIndices, bounds, !flip, false);
                float rawHeightPart = Math.abs(bounds[1]) + (vertical ? 0.6f : 1.0f);
                float rawWidth = xOffset + bounds[0] + 0.5f;
                float rawHeightTotal = rawHeightPart + bounds[2] + (vertical ? 0.6f : 1.0f);
                if (vertical && rawHeightTotal < 5.0f) {
                    rawHeight = 5.0f;
                    extraPadding = (5.0f - rawHeightTotal) / 2.0f;
                    yOffset = rawHeightPart + extraPadding;
                } else {
                    rawHeight = rawHeightTotal;
                    extraPadding = 0.0f;
                    yOffset = rawHeightPart;
                }
                if (rawWidth / rawHeight > aspectRatio) {
                    width = Math.round(rawWidth * (float)scale);
                    height = Math.round((float)width / aspectRatio);
                    widthScale = 1.0f;
                    heightScale = (float)height / rawHeight / (float)scale;
                } else {
                    height = Math.round(rawHeight * (float)scale);
                    width = Math.round((float)height * aspectRatio);
                    heightScale = 1.0f;
                    widthScale = (float)width / rawWidth / (float)scale;
                }
                if (width <= 0 || height <= 0) {
                    return null;
                }
                NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
                nativeImage.fillRect(0, 0, width, height, -1);
                Object2ObjectOpenHashMap<String, ObjectOpenHashSet> stationPositionsGrouped = new Object2ObjectOpenHashMap<String, ObjectOpenHashSet>();
                for (int routeIndex2 = 0; routeIndex2 < routeCount; ++routeIndex2) {
                    SimplifiedRoute simplifiedRoute2 = (SimplifiedRoute)((ObjectIntImmutablePair)routeDetails.get(routeIndex2)).left();
                    int currentIndex = ((ObjectIntImmutablePair)routeDetails.get(routeIndex2)).rightInt();
                    Int2ObjectAVLTreeMap<StationPosition> routeStationPositions = stationPositions.get(routeIndex2);
                    for (int stationIndex = 0; stationIndex < simplifiedRoute2.getPlatforms().size(); ++stationIndex) {
                        StationPosition stationPosition = routeStationPositions.get(stationIndex - currentIndex);
                        if (stationIndex < simplifiedRoute2.getPlatforms().size() - 1) {
                            RouteMapGenerator.drawLine(nativeImage, stationPosition, routeStationPositions.get(stationIndex + 1 - currentIndex), widthScale, heightScale, xOffset, yOffset, stationIndex < currentIndex ? -5592406 : 0xFF000000 | simplifiedRoute2.getColor());
                        }
                        SimplifiedRoutePlatform simplifiedRoutePlatform = simplifiedRoute2.getPlatforms().get(stationIndex);
                        String key2 = String.format("%s||%s", simplifiedRoutePlatform.getStationName(), simplifiedRoutePlatform.getStationId());
                        if (stationPosition.isCommon && !stationPositionsGrouped.getOrDefault(key2, new ObjectOpenHashSet()).stream().noneMatch(stationPosition2 -> ((StationPositionGrouped)stationPosition2).stationPosition.x == stationPosition.x)) continue;
                        IntArrayList interchangeColors = new IntArrayList();
                        ObjectArrayList<String> interchangeNames = new ObjectArrayList<String>();
                        simplifiedRoutePlatform.forEach((color, interchangeRouteNamesForColor) -> {
                            if (!colors.contains(color)) {
                                interchangeColors.add(color);
                                interchangeRouteNamesForColor.forEach(interchangeNames::add);
                            }
                        });
                        Data.put(stationPositionsGrouped, key2, new StationPositionGrouped(stationPosition, stationIndex - currentIndex, interchangeColors, interchangeNames), ObjectOpenHashSet::new);
                    }
                }
                int maxStringWidth = (int)((double)scale * 0.9 * (double)((vertical ? heightScale : widthScale) / 2.0f + extraPadding / (float)routeCount));
                stationPositionsGrouped.forEach((key, stationPositionGroupedSet) -> stationPositionGroupedSet.forEach(stationPositionGrouped -> {
                    int lines;
                    StationPositionGrouped g = (StationPositionGrouped) stationPositionGrouped;
                    int x = Math.round((g.stationPosition.x + xOffset) * (float)scale * widthScale);
                    int y = Math.round((g.stationPosition.y + yOffset) * (float)scale * heightScale);
                    int n = lines = g.stationPosition.isCommon ? colorIndices[colorIndices.length - 1] : 0;
                    boolean textBelow = vertical || (g.stationPosition.isCommon ? Math.abs(g.stationOffset) % 2 == 0 : (float)y >= yOffset * (float)scale);
                    boolean currentStation = g.stationOffset == 0;
                    boolean passed = g.stationOffset < 0;
                    IntArrayList interchangeColors = g.interchangeColors;

                    if (!interchangeColors.isEmpty() && !currentStation) {
                        int lineHeight = lineSize * 2;
                        int lineWidth = (int)Math.ceil((float)lineSize / (float)interchangeColors.size());
                        for (int i = 0; i < interchangeColors.size(); ++i) {
                            for (int drawX = 0; drawX < lineWidth; ++drawX) {
                                for (int drawY = 0; drawY < lineHeight; ++drawY) {
                                    RouteMapGenerator.drawPixelSafe(nativeImage, x + drawX + lineWidth * i - lineWidth * interchangeColors.size() / 2, y + (textBelow ? -1 : lines * lineSpacing) + (textBelow ? -drawY : drawY), passed ? -5592406 : 0xFF000000 | interchangeColors.getInt(i));
                                }
                            }
                        }
                        int[] dimensions = new int[2];
                        byte[] pixels = clientCache.getTextPixels(IGui.mergeStations(g.interchangeNames), dimensions, maxStringWidth - (vertical ? lineHeight : 0), (int)((float)(fontSizeBig + fontSizeSmall) * 1.25f / 2.0f), fontSizeBig / 2, fontSizeSmall / 2, 0, vertical ? IGui.HorizontalAlignment.LEFT : IGui.HorizontalAlignment.CENTER);
                        RouteMapGenerator.drawString(nativeImage, pixels, x, y + (textBelow ? -1 - lineHeight : lines * lineSpacing + lineHeight), dimensions, IGui.HorizontalAlignment.CENTER, textBelow ? IGui.VerticalAlignment.BOTTOM : IGui.VerticalAlignment.TOP, 0, passed ? -5592406 : -16777216, vertical);
                    }
                    RouteMapGenerator.drawStation(nativeImage, x, y, heightScale, lines, passed);
                    int[] dimensions = new int[2];
                    byte[] pixels = clientCache.getTextPixels(key.split("\\|\\|")[0], dimensions, maxStringWidth, (int)((float)(fontSizeBig + fontSizeSmall) * 1.25f), fontSizeBig, fontSizeSmall, fontSizeSmall / 4, vertical ? IGui.HorizontalAlignment.RIGHT : IGui.HorizontalAlignment.CENTER);
                    RouteMapGenerator.drawString(nativeImage, pixels, x, y + (textBelow ? lines * lineSpacing : -1) + (textBelow ? 1 : -1) * lineSize * 5 / 4, dimensions, IGui.HorizontalAlignment.CENTER, textBelow ? IGui.VerticalAlignment.TOP : IGui.VerticalAlignment.BOTTOM, currentStation ? -16777216 : 0, passed ? -5592406 : (currentStation ? -1 : -16777216), vertical);
                }));
                if (transparentWhite) {
                    RouteMapGenerator.clearColor(nativeImage, -1);
                }
                return nativeImage;
            }
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), 1, 1, false);
            nativeImage.setPixelColor(0, 0, transparentWhite ? 0 : -1);
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
        }
        return null;
    }

    // ==========================================================================================
    // ==========================================================================================

    // ==========================================================================================

    public static final float TRAIN1_V1 = 0.035f;
    public static final float TRAIN1_V2 = 0.475f;
    public static final float TRAIN2_V1 = 0.525f;
    public static final float TRAIN2_V2 = 0.965f;

    public static final float BLOCK_U1 = 0.050f;
    public static final float BLOCK_U2 = 0.197f;
    public static final float PANEL_ROUTE_U1 = 0.200f;
    public static final float PANEL_ROUTE_U2 = 0.728f;

    public static final float PANEL_BG_U1 = PANEL_ROUTE_U1;
    public static final float PANEL_BG_U2 = PANEL_ROUTE_U2;
    public static final float TRAIN_U1 = 0.733f;
    public static final float TRAIN_U2 = 0.948f;

    private static final float ROW_BLOCK_PAD_V = 0.30f;
    private static final float ROW_ROUTE_LINE_V = 0.62f;
    private static final float SCREEN_NAME_FONT_SCALE = 0.75f;
    private static final float SCREEN_NAME_FONT_MIN = 0.60f;

    private static final int SPAN_AFTER = 3;
    private static final int SPAN_BEFORE = 1;
    private static final int SPAN_BEFORE_TERMINUS = 2;

    private static final String[] TRAIN_TITLE = { "本班列车 the train", "下一班列车 the next train" };
    // ==========================================================================================

    // ==========================================================================================
    private static final Map<String, NativeImage> SCREEN_CANVASES = new HashMap<>();
    private static final Map<String, byte[]> TEXT_PIXELS_CACHE = new HashMap<>();
    private static final Map<String, int[]> TEXT_DIMS_CACHE = new HashMap<>();

    private static int lerpColorAbgr(int colorTop, int colorBottom, float t) {
        final float k = Math.max(0.0f, Math.min(1.0f, t));
        final int a1 = colorTop >>> 24 & 0xFF;
        final int b1 = colorTop >>> 16 & 0xFF;
        final int g1 = colorTop >>> 8 & 0xFF;
        final int r1 = colorTop & 0xFF;
        final int a2 = colorBottom >>> 24 & 0xFF;
        final int b2 = colorBottom >>> 16 & 0xFF;
        final int g2 = colorBottom >>> 8 & 0xFF;
        final int r2 = colorBottom & 0xFF;
        final int a = Math.round(a1 + (a2 - a1) * k);
        final int b = Math.round(b1 + (b2 - b1) * k);
        final int g = Math.round(g1 + (g2 - g1) * k);
        final int r = Math.round(r1 + (r2 - r1) * k);
        return a << 24 | b << 16 | g << 8 | r;
    }

    private static byte[] getTextPixelsCached(DynamicTextureCache clientCache, String text, int[] dims, int maxWidth, int maxHeight, int fontSizeBig, int fontSizeSmall, int padding, IGui.HorizontalAlignment alignment) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        final String key = text + "|" + maxWidth + "|" + maxHeight + "|" + fontSizeBig + "|" + fontSizeSmall + "|" + padding + "|" + alignment;
        final byte[] cached = TEXT_PIXELS_CACHE.get(key);
        if (cached != null) {
            final int[] cachedDims = TEXT_DIMS_CACHE.get(key);
            if (cachedDims != null) {
                dims[0] = cachedDims[0];
                dims[1] = cachedDims[1];
            }
            return cached;
        }
        final byte[] pixels = clientCache.getTextPixels(text, dims, maxWidth, maxHeight, fontSizeBig, fontSizeSmall, padding, alignment);
        if (pixels != null) {
            if (TEXT_PIXELS_CACHE.size() > 400) {
                TEXT_PIXELS_CACHE.clear();
                TEXT_DIMS_CACHE.clear();
            }
            TEXT_PIXELS_CACHE.put(key, pixels);
            TEXT_DIMS_CACHE.put(key, new int[] { dims[0], dims[1] });
        }
        return pixels;
    }

    private static final int ARRIVING_SECONDS = 15;

    private static final int SCREEN_BG_TOP = 0xFFFBF4E4;
    private static final int SCREEN_BG_BOTTOM = 0xFFF5B83F;
    private static final float SCREEN_RESOLUTION_SCALE = 2.0f;
    private static final int SCREEN_MAX_TEXTURE_HEIGHT = 1024;
    private static final float GOLDEN_RATIO = 1.618f;
    private static final float COLOR_BLOCK_SCALE = 0.70f;
    private static final float COLOR_BLOCK_CENTER_V = 0.66f;
    public static final float BLUE_BOX_V1 = 0.04f;
    public static final float BLUE_BOX_V2 = 0.30f;
    private static final int BLUE_BOX_COLOR = 0xFFFF0000;

    public static final float CUSTOM_IMAGE_U = 0.30f;

    // ==========================================================================================

    // ==========================================================================================
    private static final Map<String, NativeImage> CUSTOM_IMAGE_SOURCES = new HashMap<>();
    private static final java.util.Set<String> CUSTOM_IMAGE_SOURCE_FAILED = new java.util.HashSet<>();

    private static NativeImage getCustomImageSource(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        final String key = path + "@" + PSDCustomText.imageVersion(path);
        final NativeImage cached = CUSTOM_IMAGE_SOURCES.get(key);
        if (cached != null) {
            return cached;
        }
        if (CUSTOM_IMAGE_SOURCE_FAILED.contains(key)) {
            return null;
        }
        try {
            final java.io.File file = new java.io.File(path);
            if (!file.isFile()) {
                CUSTOM_IMAGE_SOURCE_FAILED.add(key);
                Init.LOGGER.error("[LCD14] 自定义图片不存在：" + path);
                return null;
            }
            final NativeImage source = NativeImage.read(new java.io.FileInputStream(file));
            final int sw = source.getWidth();
            final int sh = source.getHeight();
            if (sw <= 0 || sh <= 0) {
                source.close();
                CUSTOM_IMAGE_SOURCE_FAILED.add(key);
                return null;
            }
            final int maxSide = 512;
            final float scale = Math.min(1.0f, Math.min((float)maxSide / (float)sw, (float)maxSide / (float)sh));
            final NativeImage scaled;
            if (scale < 1.0f) {
                final int tw = Math.max(1, Math.round((float)sw * scale));
                final int th = Math.max(1, Math.round((float)sh * scale));
                scaled = new NativeImage(NativeImageFormat.getAbgrMapped(), tw, th, false);
                source.resizeSubRectTo(0, 0, sw, sh, scaled);
                source.close();
            } else {
                scaled = source;
            }
            if (CUSTOM_IMAGE_SOURCES.size() > 8) {
                CUSTOM_IMAGE_SOURCES.clear();
                CUSTOM_IMAGE_SOURCE_FAILED.clear();
            }
            CUSTOM_IMAGE_SOURCES.put(key, scaled);
            if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] 自定义图片已加载 path={} 版本={} 尺寸={}x{}", path, PSDCustomText.imageVersion(path), scaled.getWidth(), scaled.getHeight());
            return scaled;
        } catch (Exception e) {
            CUSTOM_IMAGE_SOURCE_FAILED.add(key);
            Init.LOGGER.error("[LCD14] 自定义图片加载失败 path=" + path, e);
        }
        return null;
    }

    private static final Map<String, int[]> CUSTOM_IMAGE_AREA_PIXELS = new HashMap<>();

    private static final boolean IMAGE_SWAP_RB = false;

    private static int[] getCustomImageAreaPixels(String path, int rw, int rh) {
        final String key = path + "@" + PSDCustomText.imageVersion(path) + "@" + rw + "x" + rh;
        final int[] cached = CUSTOM_IMAGE_AREA_PIXELS.get(key);
        if (cached != null) {
            return cached;
        }
        final NativeImage source = getCustomImageSource(path);
        if (source == null) {
            return null;
        }
        final int sw = source.getWidth();
        final int sh = source.getHeight();
        if (sw <= 0 || sh <= 0) {
            return null;
        }
        final int[] pixels = new int[rw * rh];
        for (int y = 0; y < rh; ++y) {
            final int sy0 = y * sh / rh;
            final int sy1 = Math.max(sy0 + 1, (y + 1) * sh / rh);
            for (int x = 0; x < rw; ++x) {
                final int sx0 = x * sw / rw;
                final int sx1 = Math.max(sx0 + 1, (x + 1) * sw / rw);
                long sumR = 0;
                long sumG = 0;
                long sumB = 0;
                int count = 0;
                for (int sy = sy0; sy < sy1; ++sy) {
                    for (int sx = sx0; sx < sx1; ++sx) {
                        final int raw = source.getColor(sx, sy);
                        final int c0 = raw & 0xFF;
                        final int c1 = raw >> 8 & 0xFF;
                        final int c2 = raw >> 16 & 0xFF;
                        sumR += IMAGE_SWAP_RB ? c2 : c0;
                        sumG += c1;
                        sumB += IMAGE_SWAP_RB ? c0 : c2;
                        ++count;
                    }
                }
                if (count <= 0) {
                    count = 1;
                }
                pixels[y * rw + x] = 0xFF000000 | ((int)(sumR / count) << 16) | ((int)(sumG / count) << 8) | (int)(sumB / count);
            }
        }
        if (CUSTOM_IMAGE_AREA_PIXELS.size() > 64) {
            CUSTOM_IMAGE_AREA_PIXELS.clear();
        }
        CUSTOM_IMAGE_AREA_PIXELS.put(key, pixels);
        return pixels;
    }

    private static void drawCustomImage(NativeImage nativeImage, int x1, int y1, int x2, int y2, String path) {
        final int rw = x2 - x1;
        final int rh = y2 - y1;
        if (rw <= 0 || rh <= 0) {
            return;
        }
        final int[] pixels = (path == null || path.isEmpty()) ? null : getCustomImageAreaPixels(path, rw, rh);
        for (int y = 0; y < rh; ++y) {
            for (int x = 0; x < rw; ++x) {
                RouteMapGenerator.drawPixelSafe(nativeImage, x1 + x, y1 + y, pixels == null ? 0xFF000000 : pixels[y * rw + x]);
            }
        }
    }

    public static float customImageUFraction(float aspectRatio) {
        final float rowH = TRAIN1_V2 - TRAIN1_V1;
        final float boxVFrac = rowH * (BLUE_BOX_V2 - BLUE_BOX_V1);
        return aspectRatio <= 0.01f ? boxVFrac : boxVFrac / aspectRatio;
    }
    private static final float BADGE_R_SCALE = 0.42f;
    private static final float LINE_SCALE = 0.75f;
    private static final float BADGE_GAP_SCALE = 0.30f;

    private static float lineScale = 1.0f;

    private static void drawRectOutline(NativeImage nativeImage, int x1, int y1, int x2, int y2, int thickness, int color) {
        if (x2 <= x1 || y2 <= y1) {
            return;
        }
        final int t = Math.max(1, thickness);
        nativeImage.fillRect(x1, y1, x2 - x1 + 1, t, color);
        nativeImage.fillRect(x1, y2 - t + 1, x2 - x1 + 1, t, color);
        nativeImage.fillRect(x1, y1, t, y2 - y1 + 1, color);
        nativeImage.fillRect(x2 - t + 1, y1, t, y2 - y1 + 1, color);
    }

    private static SimplifiedRoute findRouteByName(String routeName) {
        if (routeName == null || routeName.isEmpty()) {
            return null;
        }
        final String cjkTarget = getCjkOnly(routeName).trim();
        SimplifiedRoute fallback = null;
        for (final Object routeObj : MinecraftClientData.getInstance().simplifiedRoutes) {
            final SimplifiedRoute route = (SimplifiedRoute)routeObj;
            final String name = route.getName();
            if (name == null || name.isEmpty()) {
                continue;
            }
            if (name.equals(routeName)) {
                return route;
            }
            final String cjkName = getCjkOnly(name).trim();
            if (!cjkTarget.isEmpty() && cjkName.equals(cjkTarget)) {
                return route;
            }
            if (fallback == null && !cjkName.isEmpty() && !cjkTarget.isEmpty() && (cjkName.contains(cjkTarget) || cjkTarget.contains(cjkName))) {
                fallback = route;
            }
        }
        return fallback;
    }

    private static SimplifiedRoute findRouteById(long routeId) {
        for (final Object routeObj : MinecraftClientData.getInstance().simplifiedRoutes) {
            final SimplifiedRoute route = (SimplifiedRoute)routeObj;
            if (route.getId() == routeId) {
                return route;
            }
        }
        return null;
    }

    public static NativeImage generateCustomSingleRouteMap(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite) {
        return generateCustomSingleRouteMap(platformId, routeId, vertical, flip, aspectRatio, transparentWhite, null);
    }

    public static NativeImage generateCustomSingleRouteMap(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite, String canvasKey) {
        return generateCustomSingleRouteMap(platformId, routeId, vertical, flip, aspectRatio, transparentWhite, canvasKey, PSDCustomText.DEFAULT_TEXT);
    }

    public static NativeImage generateCustomSingleRouteMap(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite, String canvasKey, String customText) {
        return generateCustomSingleRouteMap(platformId, routeId, vertical, flip, aspectRatio, transparentWhite, canvasKey, customText, "");
    }

    public static NativeImage generateCustomSingleRouteMap(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite, String canvasKey, String customText, String customImagePath) {
        final long _RT_T0 = System.currentTimeMillis();
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            final DynamicTextureCache clientCache = DynamicTextureCache.instance;
            final long stationId = RouteMapGenerator.getStationIdForPlatformStatic(platformId);

            final float resScale = Math.min(SCREEN_RESOLUTION_SCALE, (float)SCREEN_MAX_TEXTURE_HEIGHT / (float)(scale * 2));
            final int height = Math.round((float)(scale * 2) * resScale);
            final int width = Math.max(Math.round((float)height * aspectRatio), Math.round((float)(scale * 2) * resScale));

            NativeImage reuseCanvas = null;
            if (canvasKey != null) {
                final NativeImage cachedCanvas = SCREEN_CANVASES.get(canvasKey);
                if (cachedCanvas != null && cachedCanvas.getWidth() == width && cachedCanvas.getHeight() == height) {
                    reuseCanvas = cachedCanvas;
                }
            }
            final NativeImage nativeImage = reuseCanvas != null ? reuseCanvas : new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            if (reuseCanvas == null && canvasKey != null) {
                final NativeImage oldCanvas = SCREEN_CANVASES.put(canvasKey, nativeImage);
                if (oldCanvas != null && oldCanvas != nativeImage) {
                    try {
                        oldCanvas.close();
                    } catch (Exception ignored) {
                    }
                }
            }

            for (int gy = 0; gy < height; ++gy) {
                nativeImage.fillRect(0, gy, width, 1, lerpColorAbgr(SCREEN_BG_TOP, SCREEN_BG_BOTTOM, height <= 1 ? 0.0f : (float)gy / (float)(height - 1)));
            }

            final float[] rowV1s = { TRAIN1_V1, TRAIN2_V1 };
            final float[] rowV2s = { TRAIN1_V2, TRAIN2_V2 };
            for (int k = 0; k < 2; ++k) {
                final int wy1 = (int)(height * rowV1s[k]);
                final int wy2 = (int)(height * rowV2s[k]);
                final int rowHi = wy2 - wy1;
                nativeImage.fillRect((int)(width * PANEL_BG_U1), wy1, (int)(width * (PANEL_BG_U2 - PANEL_BG_U1)), Math.max(1, rowHi), -1);
                nativeImage.fillRect((int)(width * BLOCK_U1), wy1 + (int)(rowHi * BLUE_BOX_V1), (int)(width * (BLOCK_U2 - BLOCK_U1)), Math.max(1, (int)(rowHi * (BLUE_BOX_V2 - BLUE_BOX_V1))), -1);

                nativeImage.fillRect((int)(width * TRAIN_U1), wy1, (int)(width * (TRAIN_U2 - TRAIN_U1)), Math.max(1, rowHi), -1);
            }

            final ObjectArrayList<ArrivalResponse> arrivals = RouteMapGenerator.requestArrivalsForPlatform(platformId);
            ArrivalResponse first = null;
            ArrivalResponse second = null;
            for (final ArrivalResponse ar : arrivals) {
                if (first == null || ar.getArrival() < first.getArrival()) {
                    second = first;
                    first = ar;
                } else if (second == null || ar.getArrival() < second.getArrival()) {
                    second = ar;
                }
            }
            final ArrivalResponse[] trains = { first, second };
            final int frameThickness = Math.max(1, Math.round((float)scale * resScale / 64.0f));

            final int baseLineSize = lineSize;
            final int baseLineSpacing = lineSpacing;
            final int baseFontSizeBig = fontSizeBig;
            final int baseFontSizeSmall = fontSizeSmall;
            lineSize = Math.max(2, Math.round((float)baseLineSize * resScale));
            lineSpacing = Math.max(2, Math.round((float)baseLineSpacing * resScale));
            fontSizeBig = Math.max(6, Math.round((float)baseFontSizeBig * resScale));
            fontSizeSmall = Math.max(4, Math.round((float)baseFontSizeSmall * resScale));
            boolean needRetry = false;
            try {
                for (int k = 0; k < 2; ++k) {
                    needRetry |= RouteMapGenerator.drawTrainRow(nativeImage, clientCache, width, height, k, trains[k], stationId, platformId, flip, frameThickness, customText, aspectRatio, customImagePath);
                }
            } finally {
                lineSize = baseLineSize;
                lineSpacing = baseLineSpacing;
                fontSizeBig = baseFontSizeBig;
                fontSizeSmall = baseFontSizeSmall;
            }
            if (transparentWhite) {
                RouteMapGenerator.clearColor(nativeImage, -1);
            }

            if (needRetry) {
                return null;
            }
            if (Constants.DEBUG_LOG) Init.LOGGER.info("[RTE] single-route 生成耗时={}ms platformId={} routeId={}", System.currentTimeMillis() - _RT_T0, platformId, routeId);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
        }
        return null;
    }

    private static boolean drawTrainRow(NativeImage nativeImage, DynamicTextureCache clientCache, int width, int height, int row, ArrivalResponse arrival, long stationId, long platformId, boolean flip, int frameThickness, String customText, float currentAspectRatio, String customImagePath) {
        final float rowV1 = row == 0 ? TRAIN1_V1 : TRAIN2_V1;
        final float rowV2 = row == 0 ? TRAIN1_V2 : TRAIN2_V2;
        final int y1 = (int)(height * rowV1);
        final int y2 = (int)(height * rowV2);
        if (y2 <= y1) {
            return false;
        }
        final int mapX1 = (int)(width * PANEL_ROUTE_U1);
        final int mapX2 = (int)(width * PANEL_ROUTE_U2);

        final int blueX1 = (int)(width * BLOCK_U1);
        final int blueX2 = (int)(width * BLOCK_U2);
        final int blueY1 = y1 + (int)((y2 - y1) * BLUE_BOX_V1);
        final int blueY2 = y1 + (int)((y2 - y1) * BLUE_BOX_V2);

        final boolean hasImage = customImagePath != null && !customImagePath.isEmpty();
        final int imageX2 = hasImage ? blueX1 + Math.round((float)width * customImageUFraction(currentAspectRatio)) : blueX1;
        if (hasImage) {
            RouteMapGenerator.drawCustomImage(nativeImage, blueX1, blueY1, imageX2, blueY2, customImagePath);
        }
        if (customText != null && !customText.isEmpty()) {

            final int textX1 = imageX2;
            RouteMapGenerator.drawTextLine(nativeImage, clientCache, customText, Math.max(8, (blueX2 - textX1) - 8), Math.max(6, (blueY2 - blueY1) - 6),
                    Math.max(6, (int)((float)(blueY2 - blueY1) * 0.55f)), (textX1 + blueX2) / 2, (blueY1 + blueY2) / 2, IGui.HorizontalAlignment.CENTER, -16777216);
        }
        if (arrival == null) {
            return false;
        }

        SimplifiedRoute route = RouteMapGenerator.findRouteById(arrival.getRouteId());
        int currentIndex = route == null ? -1 : RouteMapGenerator.getStationIndexOnRoute(route, stationId, platformId);
        if (route == null || currentIndex < 0) {
            final SimplifiedRoute byName = RouteMapGenerator.findRouteByName(arrival.getRouteName());
            if (byName != null) {
                final int byNameIndex = RouteMapGenerator.getStationIndexOnRoute(byName, stationId, platformId);
                if (byNameIndex >= 0) {
                    route = byName;
                    currentIndex = byNameIndex;
                }
            }
        }
        if (route == null || currentIndex < 0) {

            Init.LOGGER.error("[LCD14] 行{} 找不到可用线路：routeId={} 到站名='{}' 目的地='{}'（该行不绘制）", row, arrival.getRouteId(), arrival.getRouteName(), arrival.getDestination());
            return false;
        }

        final int routeColor = route.getColor() != 0 ? route.getColor() : arrival.getRouteColor();
        final String routeName = route.getName() == null || route.getName().isEmpty() ? arrival.getRouteName() : route.getName();
        if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] 行{} 匹配检查 routeId={} 查到线路='{}'(#{} , id={}) 到站携带='{}'(#{}) 目的地='{}'",
                row, arrival.getRouteId(), route.getName(), Integer.toHexString(route.getColor()), route.getId(),
                arrival.getRouteName(), Integer.toHexString(arrival.getRouteColor()), arrival.getDestination());

        RouteMapGenerator.drawRouteColorBlock(nativeImage, clientCache, width, y1, y2, routeColor, routeName);

        boolean needRetry = false;
        final ObjectArrayList<SimplifiedRoutePlatform> platforms = route.getPlatforms();
        if (currentIndex >= 0 && !platforms.isEmpty()) {
            needRetry = RouteMapGenerator.drawRouteMapInBand(nativeImage, clientCache, width, platforms, route, currentIndex, flip, mapX1, mapX2, y1, y2, frameThickness, routeColor);
        }

        RouteMapGenerator.drawTrainInfoText(nativeImage, clientCache, width, y1, y2, arrival, row);
        return needRetry;
    }

    private static void drawRouteColorBlock(NativeImage nativeImage, DynamicTextureCache clientCache, int width, int rowY1, int rowY2, int routeColor, String routeName) {
        final int rowH = rowY2 - rowY1;
        final int fullW = (int)((BLOCK_U2 - BLOCK_U1) * width);
        final int blockW = Math.max(8, (int)(fullW * COLOR_BLOCK_SCALE));
        if (blockW <= 0 || rowH <= 0) {
            return;
        }
        int blockH = Math.round((float)blockW / GOLDEN_RATIO);
        blockH = Math.max((int)(rowH * 0.15f), Math.min((int)(rowH * 0.6f), blockH));
        final int boxCenterX = (int)((BLOCK_U1 + BLOCK_U2) * 0.5f * width);
        final int x1 = boxCenterX - blockW / 2;
        final int x2 = x1 + blockW;
        final int centerY = rowY1 + (int)(rowH * COLOR_BLOCK_CENTER_V);
        int y1 = centerY - blockH / 2;
        y1 = Math.max(rowY1 + (int)(rowH * BLUE_BOX_V2) + 2, Math.min(rowY2 - blockH - 2, y1));
        final int y2 = y1 + blockH;
        final int routeColorRgb = routeColor;

        for (int by = y1; by < y2; ++by) {
            for (int bx = x1; bx < x2; ++bx) {
                RouteMapGenerator.drawPixelSafe(nativeImage, bx, by, 0xFF000000 | routeColorRgb);
            }
        }
        final int r = routeColorRgb >> 16 & 0xFF;
        final int g = routeColorRgb >> 8 & 0xFF;
        final int b = routeColorRgb & 0xFF;
        final int textColor = (int)(0.299 * (double)r + 0.587 * (double)g + 0.114 * (double)b) > 150 ? -16777216 : -1;
        final int cx = (x1 + x2) / 2;
        final String cjkName = getCjkOnly(routeName);
        final String latinName = RouteMapGenerator.getLatinOnly(routeName);
        if (latinName.isEmpty()) {

            RouteMapGenerator.drawTextLine(nativeImage, clientCache, cjkName, blockW * 9 / 10, (int)(blockH * 0.55f), (int)(blockH * 0.42f), cx, y1 + blockH / 2, IGui.HorizontalAlignment.CENTER, textColor);
        } else {

            final int cjkSize = (int)(blockH * 0.44f);
            final int latinSize = Math.max(4, (int)(cjkSize * 0.5f));
            final int cjkCy = y1 + (int)(blockH * 0.34f);
            final int latinCy = y1 + (int)(blockH * 0.74f);
            RouteMapGenerator.drawTextLine(nativeImage, clientCache, cjkName, blockW * 9 / 10, (int)(blockH * 0.38f), cjkSize, cx, cjkCy, IGui.HorizontalAlignment.CENTER, textColor);
            RouteMapGenerator.drawTextLine(nativeImage, clientCache, latinName, blockW * 9 / 10, (int)(blockH * 0.24f), latinSize, cx, latinCy, IGui.HorizontalAlignment.CENTER, textColor);
        }
    }

    private static boolean drawRouteMapInBand(NativeImage nativeImage, DynamicTextureCache clientCache, int width, ObjectArrayList<SimplifiedRoutePlatform> platforms, SimplifiedRoute simplifiedRoute, int currentIndex, boolean flip, int routeX1, int routeX2, int bandY1, int bandY2, int frameThickness, int routeColor) {

        lineScale = LINE_SCALE;
        try {
            return drawRouteMapInBandScaled(nativeImage, clientCache, width, platforms, simplifiedRoute, currentIndex, flip, routeX1, routeX2, bandY1, bandY2, frameThickness, routeColor);
        } finally {
            lineScale = 1.0f;
        }
    }

    private static boolean drawRouteMapInBandScaled(NativeImage nativeImage, DynamicTextureCache clientCache, int width, ObjectArrayList<SimplifiedRoutePlatform> platforms, SimplifiedRoute simplifiedRoute, int currentIndex, boolean flip, int routeX1, int routeX2, int bandY1, int bandY2, int frameThickness, int routeColor) {
        final int n = platforms.size();
        if (n == 0 || currentIndex < 0 || currentIndex >= n) {
            return false;
        }

        final int spanStart;
        final int spanEnd;
        if (currentIndex <= 0) {
            spanStart = 0;
            spanEnd = Math.min(n - 1, SPAN_AFTER);
        } else if (currentIndex >= n - 1) {
            spanStart = Math.max(0, currentIndex - SPAN_BEFORE_TERMINUS);
            spanEnd = n - 1;
        } else {
            spanStart = currentIndex - SPAN_BEFORE;
            spanEnd = Math.min(n - 1, currentIndex + SPAN_AFTER);
        }
        final int count = spanEnd - spanStart + 1;

        final int innerX1 = routeX1 + frameThickness + 2;
        final int innerX2 = routeX2 - frameThickness - 2;
        final int innerY1 = bandY1 + frameThickness + 2;
        final int innerY2 = bandY2 - frameThickness - 2;
        final int availH = Math.max(8, innerY2 - innerY1);
        final int availW = Math.max(8, innerX2 - innerX1);
        final int dotR = Math.max(2, (int)(lineSize * BADGE_R_SCALE));
        final int lineHalf = Math.max(1, Math.round((float)lineSize * LINE_SCALE / 2.0f));
        final int badgeGap = Math.max(3, Math.round((float)lineSize * BADGE_GAP_SCALE));
        final int belowNeed = lineHalf + badgeGap + dotR * 2 + 3;

        final String[] names = new String[count];
        final byte[][] pixels = new byte[count][];
        final int[][] dimsAll = new int[count][];
        final int[][] dims2x = new int[count][];
        int maxNameW = 1;
        int maxNameH = 1;
        float fontScale = SCREEN_NAME_FONT_SCALE;
        for (int attempt = 0; attempt < 6; ++attempt) {
            maxNameW = 1;
            maxNameH = 1;
            final int fSB = Math.max(5, (int)(fontSizeBig * fontScale));
            final int fSS = Math.max(4, (int)(fontSizeSmall * fontScale));
            for (int k = 0; k < count; ++k) {
                names[k] = RouteMapGenerator.getFullStationName(platforms.get(spanStart + k));
                final int[] dims2 = new int[2];

                pixels[k] = names[k].isEmpty() ? null : RouteMapGenerator.getTextPixelsCached(clientCache, names[k], dims2, availW * 2, (int)((float)(fSB + fSS) * 1.25f) * 2, fSB * 2, fSS * 2, Math.max(1, fSS / 2), IGui.HorizontalAlignment.CENTER);
                dims2x[k] = dims2;
                final int[] dims = { Math.max(1, dims2[0] / 2), Math.max(1, dims2[1] / 2) };
                dimsAll[k] = dims;
                if (pixels[k] != null) {
                    maxNameW = Math.max(maxNameW, dims[0]);
                    maxNameH = Math.max(maxNameH, dims[1]);
                }
            }
            final int above = lineSize + (int)(maxNameW * 0.35f) + (int)(0.354f * (float)(maxNameW + maxNameH)) + 3;
            if (above + belowNeed <= availH || fontScale <= SCREEN_NAME_FONT_MIN) {
                break;
            }
            fontScale = Math.max(SCREEN_NAME_FONT_MIN, fontScale * 0.9f);
        }

        final int lineY = Math.max(innerY1 + lineSize + 2, innerY2 - belowNeed);
        final int aboveNeed = lineY - (innerY1 + 2);

        final int nameRightOvershoot = lineSize * 3 / 2 + (int)(0.354f * (float)(maxNameW + maxNameH)) + 2;
        int stationX1 = innerX1;
        int stationX2 = Math.max(innerX1 + 1, innerX2 - nameRightOvershoot);
        int spacing = count > 1 ? Math.max(1, (stationX2 - stationX1) / (count - 1)) : 0;
        if (count > 1) {
            final int inset = Math.max(nameRightOvershoot, spacing / 2 + 2);
            stationX1 = innerX1 + inset;
            stationX2 = Math.max(stationX1 + 1, innerX2 - inset);
            spacing = Math.max(1, (stationX2 - stationX1) / (count - 1));
        }
        final int half = Math.max(1, spacing / 2);
        final boolean terminus = currentIndex >= n - 1;
        final int[] xs = new int[count];
        for (int k = 0; k < count; ++k) {
            xs[k] = count == 1 ? (innerX1 + innerX2) / 2 : stationX1 + Math.round((float)(stationX2 - stationX1) * (float)k / (float)(count - 1));
            if (flip) {
                xs[k] = stationX1 + stationX2 - xs[k];
            }
        }

        if (spanStart > 0 && !terminus) {
            final int headX = flip ? xs[0] + half : xs[0] - half;
            RouteMapGenerator.drawLine(nativeImage, Math.min(xs[0], headX), lineY, Math.abs(headX - xs[0]), 0, Math.abs(headX - xs[0]), -5592406);
        }
        for (int k = 0; k < count - 1; ++k) {
            final int idx = spanStart + k;
            final int segLen = Math.abs(xs[k + 1] - xs[k]);
            final int segColor = idx < currentIndex ? -5592406 : (0xFF000000 | routeColor);
            RouteMapGenerator.drawLine(nativeImage, Math.min(xs[k], xs[k + 1]), lineY, segLen, 0, segLen, segColor);
        }
        if (spanEnd < n - 1) {
            final int lastX = xs[count - 1];
            final int tailX = flip ? lastX - half : lastX + half;
            RouteMapGenerator.drawLine(nativeImage, Math.min(lastX, tailX), lineY, Math.abs(tailX - lastX), 0, Math.abs(tailX - lastX), 0xFF000000 | routeColor);
        }

        boolean needRetry = false;
        final int fSB = Math.max(5, (int)(fontSizeBig * fontScale));
        final int fSS = Math.max(4, (int)(fontSizeSmall * fontScale));
        for (int k = 0; k < count; ++k) {
            final int i = spanStart + k;
            final int x = xs[k];
            final boolean passed = i < currentIndex;
            final boolean currentStation = i == currentIndex;
            final IntArrayList interchangeColors = new IntArrayList();
            final ObjectArrayList<String> interchangeNames = new ObjectArrayList<String>();
            final ObjectArrayList<String> interchangeNumbers = new ObjectArrayList<String>();
            RouteMapGenerator.collectInterchange(platforms.get(i), simplifiedRoute, interchangeColors, interchangeNames, interchangeNumbers);
            RouteMapGenerator.drawCustomStation(nativeImage, x, lineY, 1.0f, 0, passed, interchangeColors, 0xFF000000 | routeColor);
            if (!interchangeColors.isEmpty()) {
                RouteMapGenerator.drawInterchangeDotsOnly(nativeImage, x, lineY + lineHalf + badgeGap + dotR, interchangeColors, interchangeNumbers, passed, innerY2, dotR);
                for (final String nn : interchangeNumbers) {
                    if (nn.isEmpty()) {
                        needRetry = true;
                    }
                }
            }

            final int textColor = passed ? -5592406 : (currentStation ? (0xFF000000 | routeColor) : -16777216);
            if (pixels[k] != null) {
                final float nameAngle = -STATION_NAME_ANGLE;
                final int nameCx = x + lineSize * 3 / 2;
                final int nameCy = lineY - lineSize - (int)(dimsAll[k][0] * 0.35f);
                RouteMapGenerator.drawStringRotatedAA(nativeImage, pixels[k], dims2x[k], nameCx, nameCy, dimsAll[k][0], dimsAll[k][1], textColor, nameAngle);
            }
        }
        return needRetry;
    }

    private static void drawTrainInfoText(NativeImage nativeImage, DynamicTextureCache clientCache, int width, int y1, int y2, ArrivalResponse arrival, int row) {
        final int x1 = (int)(width * TRAIN_U1) + 5;
        final int x2 = (int)(width * TRAIN_U2) - 5;
        final int blockW = x2 - x1;
        final int blockH = y2 - y1 - 4;
        final int y1i = y1 + 2;
        if (blockW <= 0 || blockH <= 0) {
            return;
        }
        final int lineH = blockH / 4;
        final int leftX = x1 + (int)(blockW * 0.05f);
        final int cx = (x1 + x2) / 2;
        final int small = (int)(lineH * 0.55f);
        final int big = (int)(lineH * 0.78f);
        final String destination = arrival.getDestination();
        final String destRaw = destination == null ? "" : destination;
        final String destCjk = RouteMapGenerator.containsCjk(destRaw) ? getCjkOnly(destRaw) : "";
        final String destLatin = RouteMapGenerator.getLatinOnly(destRaw);

        RouteMapGenerator.drawTextLine(nativeImage, clientCache, row >= 0 && row < TRAIN_TITLE.length ? TRAIN_TITLE[row] : "", blockW, lineH, small, leftX, y1i + lineH / 2, IGui.HorizontalAlignment.LEFT, -16777216);

        final String arrivalTimeText = RouteMapGenerator.getArrivalTimeText(arrival);
        final boolean arriving = arrivalTimeText != null && arrivalTimeText.startsWith("列车进站");
        RouteMapGenerator.drawTextLine(nativeImage, clientCache, arrivalTimeText, blockW, lineH, big, cx, y1i + lineH + lineH / 2, IGui.HorizontalAlignment.CENTER, arriving ? 0xFFFF0000 : -16777216);

        if (!destCjk.isEmpty()) {
            RouteMapGenerator.drawTextLine(nativeImage, clientCache, "开往 " + destCjk, blockW, lineH, small, leftX, y1i + lineH * 2 + lineH / 2, IGui.HorizontalAlignment.LEFT, -16777216);
        }

        if (!destLatin.isEmpty()) {
            RouteMapGenerator.drawTextLine(nativeImage, clientCache, "to " + destLatin, blockW, lineH, small, leftX, y1i + lineH * 3 + lineH / 2, IGui.HorizontalAlignment.LEFT, -16777216);
        }
    }

    private static void drawTextLine(NativeImage nativeImage, DynamicTextureCache clientCache, String text, int maxWidth, int maxHeight, int fontSize, int x, int cy, IGui.HorizontalAlignment alignment, int textColor) {
        if (text == null || text.isEmpty() || maxWidth <= 0 || maxHeight <= 0 || fontSize <= 0) {
            return;
        }
        final int[] dims = new int[2];
        final int latinSize = Math.max(1, fontSize * 2 / 3);
        final byte[] px = RouteMapGenerator.getTextPixelsCached(clientCache, text, dims, maxWidth, maxHeight, fontSize, latinSize, Math.max(1, latinSize / 4), alignment);
        if (px != null) {
            RouteMapGenerator.drawString(nativeImage, px, x, cy, dims, alignment, IGui.VerticalAlignment.CENTER, 0, textColor, false);
        }
    }

    private static String getArrivalTimeText(ArrivalResponse arrival) {
        final long seconds = (arrival.getArrival() - ArrivalsCacheClient.INSTANCE.getMillisOffset() - System.currentTimeMillis()) / 1000L;
        if (seconds > 60L) {
            final long minutes = seconds / 60L;
            return minutes + " 分钟进站 " + minutes + " min";
        }
        if (seconds > ARRIVING_SECONDS) {
            return seconds + " 秒进站 " + seconds + " sec";
        }
        return "列车进站，请注意安全";
    }

    private static ObjectArrayList<ArrivalResponse> requestArrivalsForPlatform(long platformId) {
        final LongArrayList pids = new LongArrayList();
        pids.add(platformId);
        final ObjectArrayList<ArrivalResponse> arrivals = ArrivalsCacheClient.INSTANCE.requestArrivals(pids);
        return arrivals == null ? new ObjectArrayList<ArrivalResponse>() : arrivals;
    }

    private static boolean containsCjk(String text) {
        if (text == null) {
            return false;
        }
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) >= 0x2E80) {
                return true;
            }
        }
        return false;
    }

    private static String getLatinOnly(String name) {
        if (name == null) {
            return "";
        }
        final int idx = name.indexOf('|');
        if (idx >= 0) {
            return name.substring(idx + 1);
        }
        return RouteMapGenerator.containsCjk(name) ? "" : name;
    }

    private static void collectInterchange(SimplifiedRoutePlatform sp, SimplifiedRoute currentRoute, IntArrayList colors, ObjectArrayList<String> names, ObjectArrayList<String> numbers) {
        final long stationId = sp.getStationId();
        for (final Object routeObj : MinecraftClientData.getInstance().simplifiedRoutes) {
            final SimplifiedRoute route = (SimplifiedRoute)routeObj;
            if (route.getId() == currentRoute.getId()) {
                continue;
            }
            if (routeHasStation(route, stationId) && route.getColor() != currentRoute.getColor() && !colors.contains(route.getColor())) {
                colors.add(route.getColor());
                names.add(route.getName());

                String number = "";
                try {
                    for (final SimplifiedRoutePlatform rsp : route.getPlatforms()) {
                        if (rsp.getStationId() == stationId) {
                            final LongArrayList pids = new LongArrayList();
                            pids.add(rsp.getPlatformId());
                            final ObjectArrayList<ArrivalResponse> arrs = ArrivalsCacheClient.INSTANCE.requestArrivals(pids);

                            for (final ArrivalResponse ar : arrs) {
                                if (ar.getRouteId() == route.getId()) {
                                    final String raw = ar.getRouteNumber();
                                    number = RouteMapGenerator.extractLeadingNumber(RouteMapGenerator.getCjkOnly(raw));
                                    if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] 换乘点 routeId={} routeName={} 匹配该线车 raw路号='{}' 数字='{}'", route.getId(), route.getName(), raw, number);
                                    break;
                                }
                            }
                            if (number.isEmpty()) {
                                if (Constants.DEBUG_LOG) Init.LOGGER.info("[LCD14] 换乘点 routeId={} routeName={} 无匹配该线到站车(月台到站数={})", route.getId(), route.getName(), arrs.size());
                            }
                            break;
                        }
                    }
                } catch (Exception ex) {

                    Init.LOGGER.error("[LCD14] 换乘路号异常 routeId=" + route.getId(), ex);
                }
                if (number.isEmpty()) {
                    number = RouteMapGenerator.extractLeadingNumber(RouteMapGenerator.getCjkOnly(route.getName()));
                }
                numbers.add(number);
            }
        }
    }

    private static void drawInterchangeDotsOnly(NativeImage nativeImage, int cx, int cy, IntArrayList colors, ObjectArrayList<String> numbers, boolean passed, int canvasHeight) {
        drawInterchangeDotsOnly(nativeImage, cx, cy, colors, numbers, passed, canvasHeight, Math.max(2, (int)(lineSize * BADGE_R_SCALE)));
    }

    private static void drawInterchangeDotsOnly(NativeImage nativeImage, int cx, int cy, IntArrayList colors, ObjectArrayList<String> numbers, boolean passed, int canvasHeight, int dotRParam) {
        final int dotR = Math.max(2, dotRParam);
        final int gap = dotR * 3;
        final int perRow = 2;
        final int rows = (colors.size() + perRow - 1) / perRow;

        final int availH = Math.max(0, canvasHeight - cy - dotR - 2);
        int rowGap = dotR * 2 + 2;
        if (rows > 1) { rowGap = Math.min(rowGap, Math.max(2, availH / rows)); }
        final DynamicTextureCache clientCache = DynamicTextureCache.instance;
        final int labelDown = (int)(dotR * 0.5f);
        for (int c = 0; c < colors.size(); ++c) {
            final int row = c / perRow;
            final int col = c % perRow;
            final int nInRow = (row == rows - 1) ? (colors.size() - perRow * row) : perRow;
            final int rowW = (nInRow - 1) * gap;
            final int dx = cx - rowW / 2 + col * gap;
            final int dy = cy + row * rowGap;
            final int color = passed ? -5592406 : 0xFF000000 | colors.getInt(c);

            for (int ox = -dotR; ox <= dotR; ++ox) {
                for (int oy = -dotR; oy <= dotR; ++oy) {
                    if (ox * ox + oy * oy <= dotR * dotR) {
                        RouteMapGenerator.drawPixelSafe(nativeImage, dx + ox, dy + oy, color);
                    }
                }
            }

            final String rawNumber = c < numbers.size() ? numbers.get(c) : "";
            final String number = extractLeadingNumber(rawNumber);
            if (number.isEmpty()) continue;
            final int[] dims = new int[2];

            final int numFont = Math.max(4, (int)((float)dotR * 1.15f));
            byte[] px = clientCache.getTextPixels(number, dims, dotR * 4, (int)((float)(numFont + numFont) * 1.25f), numFont, numFont, Math.max(1, numFont / 4), IGui.HorizontalAlignment.CENTER);
            if (px != null) {

                RouteMapGenerator.drawString(nativeImage, px, dx, dy, dims, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            }
        }
    }

    private static String extractLeadingNumber(String text) {
        if (text == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);
            if (ch >= '0' && ch <= '9') {
                sb.append(ch);
            } else if (sb.length() > 0) {
                break;
            }
        }
        return sb.toString();
    }

    private static String getCjkOnly(String name) {
        if (name == null) {
            return "";
        }
        final int idx = name.indexOf('|');
        return idx >= 0 ? name.substring(0, idx) : name;
    }

    private static void drawCustomStation(NativeImage nativeImage, int x, int y, float heightScale, int lines, boolean passed, IntArrayList interchangeColors, int color) {
        final int ringColor = passed ? -5592406 : color;

        RouteMapGenerator.drawStation(nativeImage, x, y, heightScale, lines, passed);
        if (interchangeColors != null && !interchangeColors.isEmpty()) {

            final int innerRadius = Math.max(1, (int)((double)lineSize * lineScale * 0.5));
            for (int offsetX = -innerRadius; offsetX < innerRadius; ++offsetX) {
                for (int offsetY = -innerRadius; offsetY < innerRadius; ++offsetY) {
                    final double squareSum = ((double)offsetX + 0.5) * ((double)offsetX + 0.5) + ((double)offsetY + 0.5) * ((double)offsetY + 0.5);
                    if (squareSum <= (double)(innerRadius * innerRadius) && squareSum > 0.5 * (double)innerRadius * (double)innerRadius) {
                        RouteMapGenerator.drawPixelSafe(nativeImage, x + offsetX, y + offsetY, ringColor);
                    }
                }
            }
        }
    }

    private static void drawStationNameBox(NativeImage nativeImage, int x, int y, int[] textDimensions, int bgColor, int textColor, boolean vertical) {
        final int w = textDimensions[vertical ? 1 : 0] + 2;
        final int h = textDimensions[vertical ? 0 : 1] + 2;
        for (int dx = -w / 2; dx <= w / 2; ++dx) {
            for (int dy = -h / 2; dy <= h / 2; ++dy) {
                RouteMapGenerator.drawPixelSafe(nativeImage, x + dx, y + dy, bgColor);
            }
        }
    }

    private static void drawStationNameBoxRotated(NativeImage nativeImage, int x, int y, int[] textDimensions, int bgColor, float degrees) {
        final int w = textDimensions[0] + 2;
        final int h = textDimensions[1] + 2;
        final double rad = Math.toRadians(degrees);
        final double cos = Math.cos(rad);
        final double sin = Math.sin(rad);
        final double bboxRx = (Math.abs(w * cos) + Math.abs(h * sin)) / 2.0;
        final double bboxRy = (Math.abs(w * sin) + Math.abs(h * cos)) / 2.0;
        final int rx = (int) Math.ceil(bboxRx);
        final int ry = (int) Math.ceil(bboxRy);
        for (int dy = -ry; dy <= ry; ++dy) {
            for (int dx = -rx; dx <= rx; ++dx) {
                final double u = dx * cos + dy * sin;
                final double v = -dx * sin + dy * cos;
                if (Math.abs(u) <= w / 2.0 && Math.abs(v) <= h / 2.0) {
                    RouteMapGenerator.drawPixelSafe(nativeImage, x + dx, y + dy, bgColor);
                }
            }
        }
    }

    private static String getFullStationName(SimplifiedRoutePlatform sp) {
        return sp.getStationName();
    }

    public static void scrollTextLightRail(GraphicsHolder graphicsHolder, int rows, float availableWidth, float availableHeight, int imageWidth, int imageHeight) {
        float scale = availableHeight / (float)imageHeight * (float)rows;
        int delayTime = 3000;
        int slideTime = 8;
        int totalTime = 3000 + (int)Math.floor(availableWidth / scale) * 8;
        int totalStep = (int)(System.currentTimeMillis() % (long)(totalTime * rows));
        int step = totalStep % totalTime;
        int row = totalStep / totalTime;
        float xOffset = (availableWidth - (float)imageWidth * scale) / 2.0f;
        float x = xOffset - (float)Math.max(0, step - 3000) * scale / 8.0f;
        IDrawing.drawTexture(graphicsHolder, Math.max(x, 0.0f), 0.0f, (float)imageWidth * scale + Math.min(x, 0.0f), availableHeight, Math.max(-x, 0.0f) / (float)imageWidth / scale, (float)row / (float)rows, 1.0f, (float)(row + 1) / (float)rows, Direction.UP, -1, GraphicsHolder.getDefaultLight());
    }

    private static void setup(ObjectArrayList<Int2ObjectAVLTreeMap<StationPosition>> stationPositions, ObjectArrayList<LongArrayList> stationsIdLists, int[] colorIndices, float[] bounds, boolean passed, boolean reverse) {
        int passedMultiplier = passed ? -1 : 1;
        int reverseMultiplier = reverse ? -1 : 1;
        bounds[0] = 0.0f;
        LongArrayList commonStationIds = new LongArrayList();
        stationsIdLists.get(0).forEach(stationId -> {
            if (stationId != 0L && !commonStationIds.contains(stationId) && stationsIdLists.stream().allMatch(stationsIds -> stationsIds.contains(stationId))) {
                commonStationIds.add(stationId);
            }
        });
        int positionXOffset = 0;
        int routeCount = stationsIdLists.size();
        int[] traverseIndex = new int[routeCount];
        for (int commonStationIndex = 0; commonStationIndex <= commonStationIds.size(); ++commonStationIndex) {
            int routeIndex;
            boolean lastStation = commonStationIndex == commonStationIds.size();
            long commonStationId = lastStation ? -1L : commonStationIds.getLong(commonStationIndex);
            int intermediateSegmentsMaxCount = 0;
            int[] intermediateSegmentsCounts = new int[routeCount];
            for (int routeIndex2 = 0; routeIndex2 < routeCount; ++routeIndex2) {
                intermediateSegmentsCounts[routeIndex2] = (lastStation ? stationsIdLists.get(routeIndex2).size() : stationsIdLists.get(routeIndex2).indexOf(commonStationId) + 1) - traverseIndex[routeIndex2];
                intermediateSegmentsMaxCount = Math.max(intermediateSegmentsMaxCount, intermediateSegmentsCounts[routeIndex2]);
            }
            IntArrayList routesIndicesInSection = new IntArrayList();
            for (routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                if (lastStation && intermediateSegmentsCounts[routeIndex] <= 0) continue;
                routesIndicesInSection.add(routeIndex);
            }
            for (routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                if (intermediateSegmentsCounts[routeIndex] <= 0) continue;
                float increment = (float)intermediateSegmentsMaxCount / (float)intermediateSegmentsCounts[routeIndex];
                for (int j = 0; j < intermediateSegmentsCounts[routeIndex] - (lastStation ? 0 : 1); ++j) {
                    float stationX = (float)positionXOffset + increment * (float)(j + 1);
                    bounds[0] = Math.max(bounds[0], stationX / 2.0f);
                    float stationY = (float)routesIndicesInSection.indexOf(routeIndex) - (float)(routesIndicesInSection.size() - 1) / 2.0f + RouteMapGenerator.getLineOffset(routeIndex, colorIndices);
                    bounds[1] = Math.min(bounds[1], stationY);
                    bounds[2] = Math.max(bounds[2], stationY);
                    stationPositions.get(routeIndex).put(passedMultiplier * (j + traverseIndex[routeIndex] + 1), new StationPosition((float)reverseMultiplier * stationX / 2.0f, stationY, false));
                }
                int n = routeIndex;
                traverseIndex[n] = traverseIndex[n] + intermediateSegmentsCounts[routeIndex];
            }
            if (lastStation) continue;
            positionXOffset += intermediateSegmentsMaxCount;
            for (routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                float stationY = RouteMapGenerator.getLineOffset(routeIndex, colorIndices);
                bounds[1] = Math.min(bounds[1], stationY);
                bounds[2] = Math.max(bounds[2], stationY);
                stationPositions.get(routeIndex).put(passedMultiplier * traverseIndex[routeIndex], new StationPosition((float)(reverseMultiplier * positionXOffset) / 2.0f, stationY, true));
            }
            bounds[0] = (float)positionXOffset / 2.0f;
        }
    }

    private static float getLineOffset(int routeIndex, int[] colorIndices) {
        return (float)lineSpacing / (float)scale * ((float)colorIndices[routeIndex] - (float)colorIndices[colorIndices.length - 1] / 2.0f);
    }

    public static IntArrayList getRouteStream(long platformId, BiConsumer<SimplifiedRoute, Integer> nonTerminatingCallback) {
        IntArrayList colors = new IntArrayList();
        IntArrayList terminatingColors = new IntArrayList();
        MinecraftClientData.getInstance().simplifiedRoutes.stream().filter(simplifiedRoute -> simplifiedRoute.getPlatformIndex(platformId) >= 0 && !simplifiedRoute.getName().isEmpty()).sorted().forEach(simplifiedRoute -> {
            int currentStationIndex = simplifiedRoute.getPlatformIndex(platformId);
            if (currentStationIndex < simplifiedRoute.getPlatforms().size() - 1) {
                nonTerminatingCallback.accept((SimplifiedRoute)simplifiedRoute, currentStationIndex);
                if (!colors.contains(simplifiedRoute.getColor())) {
                    colors.add(simplifiedRoute.getColor());
                }
            } else if (!terminatingColors.contains(simplifiedRoute.getColor())) {
                terminatingColors.add(simplifiedRoute.getColor());
            }
        });
        if (colors.isEmpty()) {
            colors.addAll(terminatingColors);
        }
        return colors;
    }

    private static IntArrayList getRouteStreamForStation(long platformId, BiConsumer<SimplifiedRoute, Integer> nonTerminatingCallback) {
        IntArrayList colors = new IntArrayList();
        IntArrayList terminatingColors = new IntArrayList();
        final long stationId = RouteMapGenerator.getStationIdForPlatform(platformId);
        MinecraftClientData.getInstance().simplifiedRoutes.stream().filter(simplifiedRoute -> (stationId >= 0L ? RouteMapGenerator.routeHasStation((SimplifiedRoute)simplifiedRoute, stationId) : simplifiedRoute.getPlatformIndex(platformId) >= 0) && !simplifiedRoute.getName().isEmpty()).sorted().forEach(simplifiedRoute -> {
            int currentStationIndex = stationId >= 0L ? RouteMapGenerator.getStationIndex((SimplifiedRoute)simplifiedRoute, stationId) : simplifiedRoute.getPlatformIndex(platformId);
            if (currentStationIndex < simplifiedRoute.getPlatforms().size() - 1) {
                nonTerminatingCallback.accept((SimplifiedRoute)simplifiedRoute, currentStationIndex);
                if (!colors.contains(simplifiedRoute.getColor())) {
                    colors.add(simplifiedRoute.getColor());
                }
            } else if (!terminatingColors.contains(simplifiedRoute.getColor())) {
                terminatingColors.add(simplifiedRoute.getColor());
            }
        });
        if (colors.isEmpty()) {
            colors.addAll(terminatingColors);
        }
        return colors;
    }

    private static long getStationIdForPlatform(long platformId) {
        if (platformId < 0) {
            return -1L;
        }
        for (final Object routeObj : MinecraftClientData.getInstance().simplifiedRoutes) {
            final SimplifiedRoute route = (SimplifiedRoute)routeObj;
            for (final SimplifiedRoutePlatform sp : route.getPlatforms()) {
                if (sp.getPlatformId() == platformId) {
                    return sp.getStationId();
                }
            }
        }
        return -1L;
    }

    public static ObjectArrayList<Long> collectRouteIdsForStation(long platformId) {
        final ObjectArrayList<Long> ids = new ObjectArrayList<Long>();
        final long stationId = RouteMapGenerator.getStationIdForPlatformStatic(platformId);
        for (final Object routeObj : MinecraftClientData.getInstance().simplifiedRoutes) {
            final SimplifiedRoute route = (SimplifiedRoute)routeObj;
            final int currentIndex = RouteMapGenerator.getStationIndexOnRoute(route, stationId, platformId);
            if (currentIndex >= 0) {
                ids.add(route.getId());
            }
        }
        return ids;
    }

    public static long getStationIdForPlatformStatic(long platformId) {
        return RouteMapGenerator.getStationIdForPlatform(platformId);
    }

    public static int getStationIndexOnRoute(SimplifiedRoute route, long stationId, long platformId) {
        if (stationId >= 0L) {
            return RouteMapGenerator.routeHasStation(route, stationId) ? RouteMapGenerator.getStationIndex(route, stationId) : -1;
        }
        return route.getPlatformIndex(platformId);
    }

    private static boolean routeHasStation(SimplifiedRoute route, long stationId) {
        for (final SimplifiedRoutePlatform sp : route.getPlatforms()) {
            if (sp.getStationId() == stationId) {
                return true;
            }
        }
        return false;
    }

    private static int getStationIndex(SimplifiedRoute route, long stationId) {
        for (int i = 0; i < route.getPlatforms().size(); i++) {
            if (route.getPlatforms().get(i).getStationId() == stationId) {
                return i;
            }
        }
        return route.getPlatformIndex(route.getPlatforms().isEmpty() ? -1L : route.getPlatforms().get(0).getPlatformId());
    }

    private static String getStationName(long platformId) {
        Platform platform = (Platform)MinecraftClientData.getInstance().platformIdMap.get(platformId);
        Station station = platform == null ? null : (Station)platform.area;
        return station == null ? "" : station.getName();
    }

    private static void drawLine(NativeImage nativeImage, StationPosition stationPosition1, StationPosition stationPosition2, float widthScale, float heightScale, float xOffset, float yOffset, int color) {
        int x1 = Math.round((stationPosition1.x + xOffset) * (float)scale * widthScale);
        int x2 = Math.round((stationPosition2.x + xOffset) * (float)scale * widthScale);
        int y1 = Math.round((stationPosition1.y + yOffset) * (float)scale * heightScale);
        int y2 = Math.round((stationPosition2.y + yOffset) * (float)scale * heightScale);
        int xChange = x2 - x1;
        int yChange = y2 - y1;
        int xChangeAbs = Math.abs(xChange);
        int yChangeAbs = Math.abs(yChange);
        int changeDifference = Math.abs(yChangeAbs - xChangeAbs);
        if (xChangeAbs > yChangeAbs) {
            boolean y1OffsetGreater = Math.abs((float)y1 - yOffset * (float)scale) > Math.abs((float)y2 - yOffset * (float)scale);
            RouteMapGenerator.drawLine(nativeImage, x1, y1, x2 - x1, y1OffsetGreater ? 0 : y2 - y1, y1OffsetGreater ? changeDifference : yChangeAbs, color);
            RouteMapGenerator.drawLine(nativeImage, x2, y2, x1 - x2, y1OffsetGreater ? y1 - y2 : 0, y1OffsetGreater ? yChangeAbs : changeDifference, color);
        } else {
            int halfXChangeAbs = xChangeAbs / 2;
            RouteMapGenerator.drawLine(nativeImage, x1, y1, x2 - x1, y2 - y1, halfXChangeAbs, color);
            RouteMapGenerator.drawLine(nativeImage, x2, y2, x1 - x2, y1 - y2, halfXChangeAbs, color);
            RouteMapGenerator.drawLine(nativeImage, (x1 + x2) / 2, y1 + (int)Math.copySign(halfXChangeAbs, y2 - y1), 0, y2 - y1, changeDifference, color);
        }
    }

    private static void drawLine(NativeImage nativeImage, int x, int y, int directionX, int directionY, int length, int color) {
        int xWidth;
        final int scaledLine = Math.max(2, Math.round((float)lineSize * lineScale));
        int halfLineHeight = scaledLine / 2;
        int n = xWidth = directionX == 0 ? halfLineHeight : 0;
        int yWidth = directionX == 0 ? 0 : (directionY == 0 ? halfLineHeight : Math.round((float)scaledLine * MathHelper.getSquareRootOfTwoMapped() / 2.0f));
        int yMin = y - halfLineHeight - (directionY < 0 ? length : 0) + 1;
        int yMax = y + halfLineHeight + (directionY > 0 ? length : 0) - 1;
        int drawOffset = directionX != 0 && directionY != 0 ? halfLineHeight : 0;
        for (int i = -drawOffset; i < Math.abs(length) + drawOffset; ++i) {
            int drawX = x + (directionX == 0 ? 0 : (int)Math.copySign(i, directionX)) + (directionX < 0 ? -1 : 0);
            int drawY = y + (directionY == 0 ? 0 : (int)Math.copySign(i, directionY)) + (directionY < 0 ? -1 : 0);
            for (int xOffset = 0; xOffset < xWidth; ++xOffset) {
                RouteMapGenerator.drawPixelSafe(nativeImage, drawX - xOffset - 1, drawY, color);
                RouteMapGenerator.drawPixelSafe(nativeImage, drawX + xOffset, drawY, color);
            }
            for (int yOffset = 0; yOffset < yWidth; ++yOffset) {
                RouteMapGenerator.drawPixelSafe(nativeImage, drawX, Math.max(drawY - yOffset, yMin) - 1, color);
                RouteMapGenerator.drawPixelSafe(nativeImage, drawX, Math.min(drawY + yOffset, yMax), color);
            }
        }
    }

    private static void drawStation(NativeImage nativeImage, int x, int y, float heightScale, int lines, boolean passed) {
        final int r = Math.max(2, Math.round((float)lineSize * lineScale));
        for (int offsetX = -r; offsetX < r; ++offsetX) {
            for (int offsetY = -r; offsetY < r; ++offsetY) {
                int i;
                int extraOffsetY = offsetY > 0 ? (int)((float)(lines * lineSpacing) * heightScale) : 0;
                int repeatDraw = offsetY == 0 ? (int)((float)(lines * lineSpacing) * heightScale) : 0;
                double squareSum = ((double)offsetX + 0.5) * ((double)offsetX + 0.5) + ((double)offsetY + 0.5) * ((double)offsetY + 0.5);
                if (squareSum <= 0.5 * (double)r * (double)r) {
                    for (i = 0; i <= repeatDraw; ++i) {
                        RouteMapGenerator.drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, -1);
                    }
                    continue;
                }
                if (!(squareSum <= (double)(r * r))) continue;
                for (i = 0; i <= repeatDraw; ++i) {
                    RouteMapGenerator.drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, passed ? -5592406 : -16777216);
                }
            }
        }
    }

    private static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, int backgroundColor, int textColor, boolean rotate90) {
        int drawY;
        int drawX;
        if ((backgroundColor >> 24 & 0xFF) > 0) {
            for (drawX = 0; drawX < textDimensions[rotate90 ? 1 : 0]; ++drawX) {
                for (drawY = 0; drawY < textDimensions[rotate90 ? 0 : 1]; ++drawY) {
                    RouteMapGenerator.drawPixelSafe(nativeImage, (int)horizontalAlignment.getOffset(drawX + x, textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset(drawY + y, textDimensions[rotate90 ? 0 : 1]), backgroundColor);
                }
            }
        }
        drawX = 0;
        drawY = rotate90 ? textDimensions[0] - 1 : 0;
        for (int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
            RouteMapGenerator.blendPixel(nativeImage, (int)horizontalAlignment.getOffset(x + drawX, textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset(y + drawY, textDimensions[rotate90 ? 0 : 1]), ((pixels[i] & 0xFF) << 24) + (textColor & 0xFFFFFF));
            if (rotate90) {
                if (--drawY >= 0) continue;
                drawY = textDimensions[0] - 1;
                ++drawX;
                continue;
            }
            if (++drawX != textDimensions[0]) continue;
            drawX = 0;
            ++drawY;
        }
    }

    private static void drawStringRotated(NativeImage nativeImage, byte[] pixels, int cx, int cy, int[] textDimensions, int textColor, float degrees) {
        final int w = textDimensions[0];
        final int h = textDimensions[1];
        final double rad = Math.toRadians(degrees);
        final double cos = Math.cos(rad);
        final double sin = Math.sin(rad);

        final double bboxRx = (Math.abs(w * cos) + Math.abs(h * sin)) / 2.0;
        final double bboxRy = (Math.abs(w * sin) + Math.abs(h * cos)) / 2.0;
        final double srcCx = w / 2.0;
        final double srcCy = h / 2.0;
        final int rx = (int) Math.ceil(bboxRx);
        final int ry = (int) Math.ceil(bboxRy);
        for (int dy = -ry; dy <= ry; ++dy) {
            for (int dx = -rx; dx <= rx; ++dx) {

                final double u = dx * cos + dy * sin;
                final double v = -dx * sin + dy * cos;
                final int sx = (int) Math.round(srcCx + u);
                final int sy = (int) Math.round(srcCy + v);
                if (sx < 0 || sx >= w || sy < 0 || sy >= h) {
                    continue;
                }
                final int alpha = pixels[sy * w + sx] & 0xFF;
                if (alpha > 0) {
                    RouteMapGenerator.blendPixel(nativeImage, cx + dx, cy + dy, (alpha << 24) + (textColor & 0xFFFFFF));
                }
            }
        }
    }

    private static void drawStringRotatedAA(NativeImage nativeImage, byte[] pixels2x, int[] dims2x, int cx, int cy, int dispW, int dispH, int textColor, float degrees) {
        if (pixels2x == null || dims2x == null || dims2x[0] <= 0 || dims2x[1] <= 0 || dispW <= 0 || dispH <= 0) {
            return;
        }
        final int w2 = dims2x[0];
        final int h2 = dims2x[1];
        final double rad = Math.toRadians(degrees);
        final double cos = Math.cos(rad);
        final double sin = Math.sin(rad);
        final double bboxRx = (Math.abs(dispW * cos) + Math.abs(dispH * sin)) / 2.0;
        final double bboxRy = (Math.abs(dispW * sin) + Math.abs(dispH * cos)) / 2.0;
        final double srcCx = dispW / 2.0;
        final double srcCy = dispH / 2.0;
        final int rx = (int) Math.ceil(bboxRx) + 1;
        final int ry = (int) Math.ceil(bboxRy) + 1;
        for (int dy = -ry; dy <= ry; ++dy) {
            for (int dx = -rx; dx <= rx; ++dx) {
                final double u = dx * cos + dy * sin;
                final double v = -dx * sin + dy * cos;
                final double sxD = srcCx + u;
                final double syD = srcCy + v;
                final int baseX = (int) Math.floor(sxD * 2.0);
                final int baseY = (int) Math.floor(syD * 2.0);
                int aSum = 0;
                int aCnt = 0;
                for (int ox = 0; ox < 2; ++ox) {
                    for (int oy = 0; oy < 2; ++oy) {
                        final int sx = baseX + ox;
                        final int sy = baseY + oy;
                        if (sx >= 0 && sx < w2 && sy >= 0 && sy < h2) {
                            aSum += pixels2x[sy * w2 + sx] & 0xFF;
                            ++aCnt;
                        }
                    }
                }
                if (aCnt > 0 && aSum > 0) {
                    final int alpha = aSum / aCnt;
                    if (alpha > 0) {
                        RouteMapGenerator.blendPixel(nativeImage, cx + dx, cy + dy, (alpha << 24) + (textColor & 0xFFFFFF));
                    }
                }
            }
        }
    }

    private static void drawStringPixelated(NativeImage nativeImage, byte[] pixels, int[] textDimensions, int textColor, boolean fullPixel) {
        int yOffset = (textDimensions[1] * (fullPixel ? 1 : 4) - nativeImage.getHeight()) / 2;
        int drawX = 0;
        int drawY = 0;
        for (int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
            if ((pixels[i] & 0xFF) > 127) {
                if (fullPixel) {
                    RouteMapGenerator.drawPixelSafe(nativeImage, drawX, drawY - yOffset, textColor);
                } else {
                    for (int j = 0; j < 3; ++j) {
                        for (int k = 0; k < 3; ++k) {
                            RouteMapGenerator.drawPixelSafe(nativeImage, drawX * 4 + j, drawY * 4 + k - yOffset, textColor);
                        }
                    }
                }
            }
            if (++drawX != textDimensions[0]) continue;
            drawX = 0;
            ++drawY;
        }
    }

    private static void drawResource(NativeImage nativeImage, String resource, int x, int y, int width, int height, boolean flipX, float v1, float v2, int color, boolean useActualColor) {
        ResourceManagerHelper.readResource(new Identifier("mtr", resource), inputStream2 -> {
            try {
                NativeImage nativeImageResource = NativeImage.read(NativeImageFormat.getAbgrMapped(), inputStream2);
                int resourceWidth = nativeImageResource.getWidth();
                int resourceHeight = nativeImageResource.getHeight();
                for (int drawX = 0; drawX < width; ++drawX) {
                    for (int drawY = Math.round(v1 * (float)height); drawY < Math.round(v2 * (float)height); ++drawY) {
                        int newColor;
                        float pixelX = (float)drawX / (float)width * (float)resourceWidth;
                        float pixelY = (float)drawY / (float)height * (float)resourceHeight;
                        int floorX = (int)pixelX;
                        int floorY = (int)pixelY;
                        int ceilX = floorX + 1;
                        int ceilY = floorY + 1;
                        float percentX1 = (float)ceilX - pixelX;
                        float percentY1 = (float)ceilY - pixelY;
                        float percentX2 = pixelX - (float)floorX;
                        float percentY2 = pixelY - (float)floorY;
                        int pixel1 = nativeImageResource.getColor(MathHelper.clamp(floorX, 0, resourceWidth - 1), MathHelper.clamp(floorY, 0, resourceHeight - 1));
                        int pixel2 = nativeImageResource.getColor(MathHelper.clamp(ceilX, 0, resourceWidth - 1), MathHelper.clamp(floorY, 0, resourceHeight - 1));
                        int pixel3 = nativeImageResource.getColor(MathHelper.clamp(floorX, 0, resourceWidth - 1), MathHelper.clamp(ceilY, 0, resourceHeight - 1));
                        int pixel4 = nativeImageResource.getColor(MathHelper.clamp(ceilX, 0, resourceWidth - 1), MathHelper.clamp(ceilY, 0, resourceHeight - 1));
                        if (useActualColor) {
                            newColor = RouteMapGenerator.invertColor(pixel1);
                        } else {
                            float luminance1 = (float)(pixel1 >> 24 & 0xFF) * percentX1 * percentY1;
                            float luminance2 = (float)(pixel2 >> 24 & 0xFF) * percentX2 * percentY1;
                            float luminance3 = (float)(pixel3 >> 24 & 0xFF) * percentX1 * percentY2;
                            float luminance4 = (float)(pixel4 >> 24 & 0xFF) * percentX2 * percentY2;
                            newColor = (color & 0xFFFFFF) + ((int)(luminance1 + luminance2 + luminance3 + luminance4) << 24);
                        }
                        RouteMapGenerator.blendPixel(nativeImage, (flipX ? width - drawX - 1 : drawX) + x, drawY + y, newColor);
                    }
                }
            }
            catch (Exception e) {
                Init.LOGGER.error("", (Throwable)e);
            }
        });
    }

    private static void blendPixel(NativeImage nativeImage, int x, int y, int color) {
        float percent;
        if (Utilities.isBetween(x, 0.0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0.0, nativeImage.getHeight() - 1) && (percent = (float)(color >> 24 & 0xFF) / 255.0f) > 0.0f) {
            int existingPixel = nativeImage.getColor(x, y);
            boolean existingTransparent = (existingPixel >> 24 & 0xFF) == 0;
            int r1 = existingTransparent ? 255 : existingPixel & 0xFF;
            int g1 = existingTransparent ? 255 : existingPixel >> 8 & 0xFF;
            int b1 = existingTransparent ? 255 : existingPixel >> 16 & 0xFF;
            int r2 = color >> 16 & 0xFF;
            int g2 = color >> 8 & 0xFF;
            int b2 = color & 0xFF;
            float inversePercent = 1.0f - percent;
            int finalColor = 0xFF000000 | ((int)((float)r1 * inversePercent + (float)r2 * percent) << 16) + ((int)((float)g1 * inversePercent + (float)g2 * percent) << 8) + (int)((float)b1 * inversePercent + (float)b2 * percent);
            RouteMapGenerator.drawPixelSafe(nativeImage, x, y, finalColor);
        }
    }

    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween(x, 0.0, nativeImage.getWidth() - 1) && Utilities.isBetween(y, 0.0, nativeImage.getHeight() - 1)) {
            nativeImage.setPixelColor(x, y, RouteMapGenerator.invertColor(color));
        }
    }

    private static int invertColor(int color) {
        return ((color & 0xFF000000) != 0 ? -16777216 : 0) + ((color & 0xFF) << 16) + (color & 0xFF00) + ((color & 0xFF0000) >> 16);
    }

    private static void clearColor(NativeImage nativeImage, int color) {
        for (int x = 0; x < nativeImage.getWidth(); ++x) {
            for (int y = 0; y < nativeImage.getHeight(); ++y) {
                if (nativeImage.getColor(x, y) != color) continue;
                nativeImage.setPixelColor(x, y, 0);
            }
        }
    }

    static {
        TEMP_CIRCULAR_MARKER_CLOCKWISE = String.format("temp_circular_marker_%s_clockwise", Init.randomString());
        TEMP_CIRCULAR_MARKER_ANTICLOCKWISE = String.format("temp_circular_marker_%s_anticlockwise", Init.randomString());
    }

    private static class StationPosition {
        private final float x;
        private final float y;
        private final boolean isCommon;

        private StationPosition(float x, float y, boolean isCommon) {
            this.x = x;
            this.y = y;
            this.isCommon = isCommon;
        }
    }

    private static class StationPositionGrouped {
        private final StationPosition stationPosition;
        private final int stationOffset;
        private final IntArrayList interchangeColors;
        private final ObjectArrayList<String> interchangeNames;

        private StationPositionGrouped(StationPosition stationPosition, int stationOffset, IntArrayList interchangeColors, ObjectArrayList<String> interchangeNames) {
            this.stationPosition = stationPosition;
            this.stationOffset = stationOffset;
            this.interchangeColors = interchangeColors;
            this.interchangeNames = interchangeNames;
        }
    }
}
