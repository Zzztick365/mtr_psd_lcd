/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.mtr.core.data.Data
 *  org.mtr.core.data.SimplifiedRoute
 *  org.mtr.core.data.SimplifiedRoutePlatform
 *  org.mtr.core.data.Station
 *  org.mtr.core.tool.Utilities
 *  org.mtr.libraries.it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
 *  org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntAVLTreeSet
 *  org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntArrayList
 *  org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntList
 *  org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList
 *  org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair
 *  org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  org.mtr.mapping.holder.Direction
 *  org.mtr.mapping.holder.Identifier
 *  org.mtr.mapping.holder.MathHelper
 *  org.mtr.mapping.holder.NativeImage
 *  org.mtr.mapping.holder.NativeImageFormat
 *  org.mtr.mapping.mapper.GraphicsHolder
 *  org.mtr.mapping.mapper.ResourceManagerHelper
 *  org.mtr.mod.Init
 *  org.mtr.mod.client.IDrawing
 *  org.mtr.mod.client.MinecraftClientData
 *  org.mtr.mod.client.PSDTopTextureGenerator$1
 *  org.mtr.mod.client.PSDTopTextureGenerator$StationPosition
 *  org.mtr.mod.client.PSDTopTextureGenerator$StationPositionGrouped
 *  org.mtr.mod.config.Config
 *  org.mtr.mod.data.IGui
 *  org.mtr.mod.data.IGui$HorizontalAlignment
 *  org.mtr.mod.data.IGui$VerticalAlignment
 *  org.mtr.mod.generated.lang.TranslationProvider
 *  org.mtr.mod.generated.lang.TranslationProvider$TranslationHolder
 */
package com.mtrpsdlcd.render;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import org.mtr.core.data.Data;
import org.mtr.core.data.Platform;
import org.mtr.core.data.SimplifiedRoute;
import org.mtr.core.data.SimplifiedRoutePlatform;
import org.mtr.core.data.Station;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.ints.IntList;
import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
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

public class PSDTopTextureGenerator
implements IGui {
    private static int scale;
    private static int lineSize;
    private static int lineSpacing;
    private static int fontSizeBig;
    private static int fontSizeSmall;
    public static final int PIXEL_SCALE = 4;
    private static final int MIN_VERTICAL_SIZE = 5;
    private static final String LOGO_RESOURCE = "textures/block/sign/logo.png";
    private static final String EXIT_RESOURCE = "textures/block/sign/exit_letter_blank.png";
    private static final String ARROW_RESOURCE = "textures/block/sign/arrow.png";
    private static final String CIRCLE_RESOURCE = "textures/block/sign/circle.png";

    public static final String PLATFORM_LABEL = "站台";
    private static final String TEMP_CIRCULAR_MARKER_CLOCKWISE;
    private static final String TEMP_CIRCULAR_MARKER_ANTICLOCKWISE;
    private static final int PIXEL_RESOLUTION = 24;

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
            PSDTopTextureGenerator.drawStringPixelated(nativeImage, pixels, dimensions, textColor, fullPixel);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateColorStrip(long platformId) {
        try {
            IntArrayList colors = PSDTopTextureGenerator.getRouteStream(platformId, (simplifiedRoute, currentStationIndex) -> {});
            if (colors.isEmpty()) {
                NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), 1, 1, false);
                nativeImage.setPixelColor(0, 0, 0);
                return nativeImage;
            }
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), 1, colors.size(), false);
            for (int i = 0; i < colors.size(); ++i) {
                PSDTopTextureGenerator.drawPixelSafe(nativeImage, 0, i, 0xFF000000 | colors.getInt(i));
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
            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
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
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(IGui.formatVerticalChinese((String)stationName), dimensions, width, height, fontSizeBig * 2, fontSizeSmall * 2, 0, IGui.HorizontalAlignment.CENTER);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0xFF000000 | stationColor, textColor, false);
            PSDTopTextureGenerator.clearColor(nativeImage, PSDTopTextureGenerator.invertColor(0xFF000000 | stationColor));
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
            PSDTopTextureGenerator.drawResource(nativeImage, LOGO_RESOURCE, xOffset, 0, size, size, false, 0.0f, 1.0f, 0, true);
            PSDTopTextureGenerator.drawString(nativeImage, pixels, size + xOffset, size / 2, dimensions, IGui.HorizontalAlignment.LEFT, IGui.VerticalAlignment.CENTER, fakeBackgroundColor, textColor, false);
            PSDTopTextureGenerator.clearColor(nativeImage, PSDTopTextureGenerator.invertColor(fakeBackgroundColor));
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
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(PSDTopTextureGenerator.getStationName(platformId).replace("|", " | "), dimensions, fontSizeBig, fontSizeSmall);
            int padding = dimensions[1] / 2;
            int height = dimensions[1] + padding;
            int width = Math.max(Math.round((float)height * aspectRatio), dimensions[0] + padding);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, -1);
            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -16777216, false);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generatePixelLine(String text, int backgroundColor, int textColor, float aspectRatio) {
        try {
            int[] dimensions = new int[2];
            byte[] pixels = DynamicTextureCache.instance.getTextPixels(text, dimensions, fontSizeBig, fontSizeSmall);
            int padding = dimensions[1] / 2;
            int height = Math.max(dimensions[1] + padding, 1);
            int width = Math.max(Math.round((float)height * aspectRatio), dimensions[0] + padding);
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, backgroundColor);
            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, textColor, false);
            return nativeImage;
        } catch (Exception e) {
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
            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, backgroundColor, textColor, false);
            PSDTopTextureGenerator.clearColor(nativeImage, PSDTopTextureGenerator.invertColor(backgroundColor));
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
            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, -16777216, textColor, false);
            PSDTopTextureGenerator.clearColor(nativeImage, PSDTopTextureGenerator.invertColor(-16777216));
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
            PSDTopTextureGenerator.drawResource(nativeImage, EXIT_RESOURCE, 0, 0, size, size, false, 0.0f, 1.0f, 0, true);
            PSDTopTextureGenerator.drawString(nativeImage, pixels1, size / 2 - (noNumber ? 0 : textSize / 6 - size / 32), size / 2, dimensions1, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            if (!noNumber) {
                PSDTopTextureGenerator.drawString(nativeImage, pixels2, size / 2 + textSize / 3 - size / 32, size / 2 + textSize / 8, dimensions2, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
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
            nativeImage.fillRect(0, 0, width, height, PSDTopTextureGenerator.invertColor(0xFF000000 | color));
            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateDirectionArrow(long platformId, boolean hasLeft, boolean hasRight, IGui.HorizontalAlignment horizontalAlignment, boolean showToString, float paddingScale, float aspectRatio, int backgroundColor, int textColor, int transparentColor, boolean showPlatform) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            int circleX;
            ObjectArrayList destinations = new ObjectArrayList();
            IntArrayList colors = PSDTopTextureGenerator.getRouteStream(platformId, (simplifiedRoute, currentStationIndex) -> {
                String circularMarker = "";
                switch (simplifiedRoute.getCircularState()) {
                    case CLOCKWISE:
                        circularMarker = TEMP_CIRCULAR_MARKER_CLOCKWISE;
                        break;
                    case ANTICLOCKWISE:
                        circularMarker = TEMP_CIRCULAR_MARKER_ANTICLOCKWISE;
                        break;
                    default:
                        break;
                }
                destinations.add((Object)(circularMarker + ((SimplifiedRoutePlatform)simplifiedRoute.getPlatforms().get(currentStationIndex.intValue())).getDestination()));
            });
            boolean isTerminating = destinations.isEmpty();
            boolean leftToRight = horizontalAlignment == IGui.HorizontalAlignment.CENTER ? hasLeft || !hasRight : horizontalAlignment != IGui.HorizontalAlignment.RIGHT;
            int height = scale;
            int width = Math.round((float)height * aspectRatio);
            int padding = Math.round((float)height * paddingScale);
            int tileSize = height - padding * 2;
            int tilePadding = tileSize / 4;
            if (width <= 0 || height <= 0) {
                return null;
            }
            DynamicTextureCache clientCache = DynamicTextureCache.instance;
            NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            nativeImage.fillRect(0, 0, width, height, PSDTopTextureGenerator.invertColor(backgroundColor));

            int[] dimensionsLabel = new int[2];
            byte[] pixelsLabel = null;
            int labelShift = 0;
            if (isTerminating) {
                circleX = (int)horizontalAlignment.getOffset(0.0f, (float)(tileSize - width));
            } else {
                String destinationString = IGui.mergeStations((List)destinations);
                boolean isClockwise = destinationString.startsWith(TEMP_CIRCULAR_MARKER_CLOCKWISE);
                boolean isAnticlockwise = destinationString.startsWith(TEMP_CIRCULAR_MARKER_ANTICLOCKWISE);
                if (!(destinationString = destinationString.replace(TEMP_CIRCULAR_MARKER_CLOCKWISE, "").replace(TEMP_CIRCULAR_MARKER_ANTICLOCKWISE, "")).isEmpty()) {
                    if (isClockwise) {
                        destinationString = IGui.insertTranslation((TranslationProvider.TranslationHolder)TranslationProvider.GUI_MTR_CLOCKWISE_VIA_CJK, (TranslationProvider.TranslationHolder)TranslationProvider.GUI_MTR_CLOCKWISE_VIA, (int)1, (String[])new String[]{destinationString});
                    } else if (isAnticlockwise) {
                        destinationString = IGui.insertTranslation((TranslationProvider.TranslationHolder)TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA_CJK, (TranslationProvider.TranslationHolder)TranslationProvider.GUI_MTR_ANTICLOCKWISE_VIA, (int)1, (String[])new String[]{destinationString});
                    } else if (showToString) {
                        destinationString = IGui.insertTranslation((TranslationProvider.TranslationHolder)TranslationProvider.GUI_MTR_TO_CJK, (TranslationProvider.TranslationHolder)TranslationProvider.GUI_MTR_TO, (int)1, (String[])new String[]{destinationString});
                    }
                }

                int leftSize = ((hasLeft ? 1 : 0) + (showPlatform && leftToRight ? 1 : 0)) * (tileSize + tilePadding);
                int rightSize = ((hasRight ? 1 : 0) + (showPlatform && !leftToRight ? 1 : 0)) * (tileSize + tilePadding);
                int[] dimensionsDestination = new int[2];
                byte[] pixelsDestination = clientCache.getTextPixels(destinationString, dimensionsDestination, width - leftSize - rightSize - padding * (showToString ? 2 : 1), (int)((float)tileSize * 1.25f), tileSize * 3 / 5, tileSize * 3 / 10, tilePadding, leftToRight ? IGui.HorizontalAlignment.LEFT : IGui.HorizontalAlignment.RIGHT);

                dimensionsLabel = new int[2];
                pixelsLabel = clientCache.getTextPixels(PSDTopTextureGenerator.PLATFORM_LABEL, dimensionsLabel, 4 * tileSize, (int)((float)tileSize * 1.25f * 3.0f / 4.0f), tileSize * 3 / 4, tileSize * 3 / 4, 0, IGui.HorizontalAlignment.LEFT);
                labelShift = (showPlatform && leftToRight) ? dimensionsLabel[0] + tilePadding : 0;
                int leftPadding = (int)horizontalAlignment.getOffset(0.0f, (float)(leftSize + rightSize + dimensionsDestination[0] + labelShift - tilePadding * 2 - width));

                if (!leftToRight) {
                    leftPadding -= tileSize;
                }
                PSDTopTextureGenerator.drawString(nativeImage, pixelsDestination, leftPadding + leftSize - tilePadding + labelShift, height / 2, dimensionsDestination, IGui.HorizontalAlignment.LEFT, IGui.VerticalAlignment.CENTER, backgroundColor, textColor, false);
                if (hasLeft) {
                    PSDTopTextureGenerator.drawResource(nativeImage, ARROW_RESOURCE, leftPadding, padding, tileSize, tileSize, false, 0.0f, 1.0f, textColor, false);
                }
                if (hasRight) {

                    final int arrowShift = showPlatform ? (leftToRight ? labelShift : (dimensionsLabel[0] + tilePadding)) : 0;
                    PSDTopTextureGenerator.drawResource(nativeImage, ARROW_RESOURCE, leftPadding + leftSize + dimensionsDestination[0] - tilePadding * 2 + rightSize - tileSize + arrowShift, padding, tileSize, tileSize, true, 0.0f, 1.0f, textColor, false);
                }
                circleX = leftPadding + leftSize + (leftToRight ? -tileSize - tilePadding : dimensionsDestination[0] - tilePadding);
            }

            if (showPlatform) {
                for (int i = 0; i < colors.size(); ++i) {
                    PSDTopTextureGenerator.drawResource(nativeImage, CIRCLE_RESOURCE, circleX, padding, tileSize, tileSize, false, (float)i / (float)colors.size(), ((float)i + 1.0f) / (float)colors.size(), colors.getInt(i), false);
                }
                Platform platform = (Platform)((Object)MinecraftClientData.getInstance().platformIdMap.get(platformId));
                if (platform != null) {
                    int[] dimensionsPlatformNumber = new int[2];
                    byte[] pixelsPlatformNumber = clientCache.getTextPixels(platform.getName(), dimensionsPlatformNumber, tileSize, (int)((float)tileSize * 1.25f * 3.0f / 4.0f), tileSize * 3 / 4, tileSize * 3 / 4, 0, IGui.HorizontalAlignment.CENTER);
                    PSDTopTextureGenerator.drawString(nativeImage, pixelsPlatformNumber, circleX + tileSize / 2, padding + tileSize / 2, dimensionsPlatformNumber, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);

                    if (pixelsLabel != null) {
                        PSDTopTextureGenerator.drawString(nativeImage, pixelsLabel, circleX + tileSize + tilePadding, padding + tileSize / 2, dimensionsLabel, IGui.HorizontalAlignment.LEFT, IGui.VerticalAlignment.CENTER, 0, -16777216, false);
                    }
                }
            }
            if (transparentColor != 0) {
                PSDTopTextureGenerator.clearColor(nativeImage, PSDTopTextureGenerator.invertColor(transparentColor));
            }
            return nativeImage;
        }
        catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage getMTRStationCompositeImage(String stationName, float aspectRatio) {
        try {
            if (stationName == null || stationName.isEmpty()) {
                return null;
            }
            final int height = scale * 2;
            final int width = Math.max(1, Math.round((float) height * aspectRatio));
            final int padding = scale / 16;
            final int[] dims = new int[2];
            final byte[] pixels = DynamicTextureCache.instance.getTextPixels(stationName, dims, width - padding * 2, height - padding * 2, fontSizeBig * 2, fontSizeSmall * 2, padding, IGui.HorizontalAlignment.CENTER);
            if (pixels == null) {
                return null;
            }
            final NativeImage img = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);
            img.fillRect(0, 0, width, height, 0);
            PSDTopTextureGenerator.drawString(img, pixels, width / 2, height / 2, dims, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -16777216, false);
            return img;
        } catch (Exception e) {
            Init.LOGGER.error("MTR station composite failed", e);
            return null;
        }
    }

    public static NativeImage generateRouteBadge(String routeName, int routeColor) {
        try {
            if (routeName == null || routeName.isEmpty()) {
                return null;
            }

            final int padding = scale / 16;
            final int height = scale / 6;
            int[] dimensions = new int[2];

            final byte[] pixels = DynamicTextureCache.instance.getTextPixels(routeName, dimensions, scale, (int)((float)height * 1.25f), height * 3 / 5, height * 3 / 10, 0, IGui.HorizontalAlignment.CENTER);
            if (pixels == null) {
                return null;
            }
            final int width = Math.max(dimensions[0] + padding * 2, scale / 5);
            final NativeImage nativeImage = new NativeImage(NativeImageFormat.getAbgrMapped(), width, height, false);

            nativeImage.fillRect(0, 0, width, height, routeColor);

            PSDTopTextureGenerator.drawString(nativeImage, pixels, width / 2, height / 2, dimensions, IGui.HorizontalAlignment.CENTER, IGui.VerticalAlignment.CENTER, 0, -1, false);
            return nativeImage;
        } catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static NativeImage generateRouteMap(long platformId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite) {
        if (aspectRatio <= 0.0f) {
            return null;
        }
        try {
            ObjectArrayList routeDetails = new ObjectArrayList();
            PSDTopTextureGenerator.getRouteStream(platformId, (simplifiedRoute, currentStationIndex) -> routeDetails.add((Object)new ObjectIntImmutablePair(simplifiedRoute, currentStationIndex.intValue())));
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
                ObjectArrayList stationsIdsBefore = new ObjectArrayList();
                ObjectArrayList stationsIdsAfter = new ObjectArrayList();
                ObjectArrayList stationPositions = new ObjectArrayList();
                IntAVLTreeSet colors = new IntAVLTreeSet();
                int[] colorIndices = new int[routeCount];
                int colorIndex = -1;
                int previousColor = -1;
                for (routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                    stationsIdsBefore.add((Object)new LongArrayList());
                    stationsIdsAfter.add((Object)new LongArrayList());
                    stationPositions.add((Object)new Int2ObjectAVLTreeMap());
                    ObjectIntImmutablePair routeDetail = (ObjectIntImmutablePair)routeDetails.get(routeIndex);
                    ObjectArrayList simplifiedRoutePlatforms = ((SimplifiedRoute)routeDetail.left()).getPlatforms();
                    int currentIndex = routeDetail.rightInt();
                    for (int stationIndex = 0; stationIndex < simplifiedRoutePlatforms.size(); ++stationIndex) {
                        if (stationIndex == currentIndex) continue;
                        long stationId = ((SimplifiedRoutePlatform)simplifiedRoutePlatforms.get(stationIndex)).getStationId();
                        if (stationIndex < currentIndex) {
                            ((LongArrayList)stationsIdsBefore.get(stationsIdsBefore.size() - 1)).add(0, stationId);
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
                    ((Int2ObjectAVLTreeMap)stationPositions.get(routeIndex)).put(0, (Object)new StationPosition(0.0f, PSDTopTextureGenerator.getLineOffset(routeIndex, colorIndices), true));
                }
                float[] bounds = new float[3];
                PSDTopTextureGenerator.setup((ObjectArrayList<Int2ObjectAVLTreeMap<StationPosition>>)stationPositions, (ObjectArrayList<LongArrayList>)(flip ? stationsIdsBefore : stationsIdsAfter), colorIndices, bounds, flip, true);
                float xOffset = bounds[0] + 0.5f;
                PSDTopTextureGenerator.setup((ObjectArrayList<Int2ObjectAVLTreeMap<StationPosition>>)stationPositions, (ObjectArrayList<LongArrayList>)(flip ? stationsIdsAfter : stationsIdsBefore), colorIndices, bounds, !flip, false);
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
                Object2ObjectOpenHashMap<String, ObjectOpenHashSet<StationPositionGrouped>> stationPositionsGrouped = new Object2ObjectOpenHashMap<>();
                for (int routeIndex2 = 0; routeIndex2 < routeCount; ++routeIndex2) {
                    SimplifiedRoute simplifiedRoute2 = (SimplifiedRoute)((ObjectIntImmutablePair)routeDetails.get(routeIndex2)).left();
                    int currentIndex = ((ObjectIntImmutablePair)routeDetails.get(routeIndex2)).rightInt();
                    Int2ObjectAVLTreeMap routeStationPositions = (Int2ObjectAVLTreeMap)stationPositions.get(routeIndex2);
                    for (int stationIndex = 0; stationIndex < simplifiedRoute2.getPlatforms().size(); ++stationIndex) {
                        StationPosition stationPosition = (StationPosition)routeStationPositions.get(stationIndex - currentIndex);
                        if (stationIndex < simplifiedRoute2.getPlatforms().size() - 1) {
                            PSDTopTextureGenerator.drawLine(nativeImage, stationPosition, (StationPosition)routeStationPositions.get(stationIndex + 1 - currentIndex), widthScale, heightScale, xOffset, yOffset, stationIndex < currentIndex ? -5592406 : 0xFF000000 | simplifiedRoute2.getColor());
                        }
                        SimplifiedRoutePlatform simplifiedRoutePlatform = (SimplifiedRoutePlatform)simplifiedRoute2.getPlatforms().get(stationIndex);
                        String key2 = String.format("%s||%s", simplifiedRoutePlatform.getStationName(), simplifiedRoutePlatform.getStationId());
                        if (stationPosition.isCommon && !stationPositionsGrouped.getOrDefault(key2, new ObjectOpenHashSet<>()).stream().noneMatch(stationPosition2 -> stationPosition2.stationPosition.x == stationPosition.x)) continue;
                        IntArrayList interchangeColors = new IntArrayList();
                        ObjectArrayList interchangeNames = new ObjectArrayList();
                        simplifiedRoutePlatform.forEach((color, interchangeRouteNamesForColor) -> {
                            if (!colors.contains(color)) {
                                interchangeColors.add(color);
                                interchangeRouteNamesForColor.forEach(arg_0 -> ((ObjectArrayList)interchangeNames).add(arg_0));
                            }
                        });
                        Data.put(stationPositionsGrouped, key2, new StationPositionGrouped(stationPosition, stationIndex - currentIndex, interchangeColors, interchangeNames), ObjectOpenHashSet::new);
                    }
                }
                int maxStringWidth = (int)((double)scale * 0.9 * (double)((vertical ? heightScale : widthScale) / 2.0f + extraPadding / (float)routeCount));
                stationPositionsGrouped.forEach((key, stationPositionGroupedSet) -> stationPositionGroupedSet.forEach(stationPositionGrouped -> {
                    int lines;
                    int x = Math.round((stationPositionGrouped.stationPosition.x + xOffset) * (float)scale * widthScale);
                    int y = Math.round((stationPositionGrouped.stationPosition.y + yOffset) * (float)scale * heightScale);
                    int n = lines = stationPositionGrouped.stationPosition.isCommon ? colorIndices[colorIndices.length - 1] : 0;
                    boolean textBelow = vertical || (stationPositionGrouped.stationPosition.isCommon ? Math.abs(stationPositionGrouped.stationOffset) % 2 == 0 : (float)y >= yOffset * (float)scale);
                    boolean currentStation = stationPositionGrouped.stationOffset == 0;
                    boolean passed = stationPositionGrouped.stationOffset < 0;
                    IntArrayList interchangeColors = stationPositionGrouped.interchangeColors;
                    if (!interchangeColors.isEmpty() && !currentStation) {
                        int lineHeight = lineSize * 2;
                        int lineWidth = (int)Math.ceil((float)lineSize / (float)interchangeColors.size());
                        for (int i = 0; i < interchangeColors.size(); ++i) {
                            for (int drawX = 0; drawX < lineWidth; ++drawX) {
                                for (int drawY = 0; drawY < lineHeight; ++drawY) {
                                    PSDTopTextureGenerator.drawPixelSafe(nativeImage, x + drawX + lineWidth * i - lineWidth * interchangeColors.size() / 2, y + (textBelow ? -1 : lines * lineSpacing) + (textBelow ? -drawY : drawY), passed ? -5592406 : 0xFF000000 | interchangeColors.getInt(i));
                                }
                            }
                        }
                        int[] dimensions = new int[2];
                        byte[] pixels = clientCache.getTextPixels(IGui.mergeStations((List)stationPositionGrouped.interchangeNames), dimensions, maxStringWidth - (vertical ? lineHeight : 0), (int)((float)(fontSizeBig + fontSizeSmall) * 1.25f / 2.0f), fontSizeBig / 2, fontSizeSmall / 2, 0, vertical ? IGui.HorizontalAlignment.LEFT : IGui.HorizontalAlignment.CENTER);
                        PSDTopTextureGenerator.drawString(nativeImage, pixels, x, y + (textBelow ? -1 - lineHeight : lines * lineSpacing + lineHeight), dimensions, IGui.HorizontalAlignment.CENTER, textBelow ? IGui.VerticalAlignment.BOTTOM : IGui.VerticalAlignment.TOP, 0, passed ? -5592406 : -16777216, vertical);
                    }
                    PSDTopTextureGenerator.drawStation(nativeImage, x, y, heightScale, lines, passed);
                    int[] dimensions = new int[2];
                    byte[] pixels = clientCache.getTextPixels(key.split("\\|\\|")[0], dimensions, maxStringWidth, (int)((float)(fontSizeBig + fontSizeSmall) * 1.25f), fontSizeBig, fontSizeSmall, fontSizeSmall / 4, vertical ? IGui.HorizontalAlignment.RIGHT : IGui.HorizontalAlignment.CENTER);
                    PSDTopTextureGenerator.drawString(nativeImage, pixels, x, y + (textBelow ? lines * lineSpacing : -1) + (textBelow ? 1 : -1) * lineSize * 5 / 4, dimensions, IGui.HorizontalAlignment.CENTER, textBelow ? IGui.VerticalAlignment.TOP : IGui.VerticalAlignment.BOTTOM, currentStation ? -16777216 : 0, passed ? -5592406 : (currentStation ? -1 : -16777216), vertical);
                }));
                if (transparentWhite) {
                    PSDTopTextureGenerator.clearColor(nativeImage, -1);
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
        IDrawing.drawTexture((GraphicsHolder)graphicsHolder, (float)Math.max(x, 0.0f), (float)0.0f, (float)((float)imageWidth * scale + Math.min(x, 0.0f)), (float)availableHeight, (float)(Math.max(-x, 0.0f) / (float)imageWidth / scale), (float)((float)row / (float)rows), (float)1.0f, (float)((float)(row + 1) / (float)rows), (Direction)Direction.UP, (int)-1, (int)GraphicsHolder.getDefaultLight());
    }

    private static void setup(ObjectArrayList<Int2ObjectAVLTreeMap<StationPosition>> stationPositions, ObjectArrayList<LongArrayList> stationsIdLists, int[] colorIndices, float[] bounds, boolean passed, boolean reverse) {
        int passedMultiplier = passed ? -1 : 1;
        int reverseMultiplier = reverse ? -1 : 1;
        bounds[0] = 0.0f;
        LongArrayList commonStationIds = new LongArrayList();
        ((LongArrayList)stationsIdLists.get(0)).forEach(stationId -> {
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
                intermediateSegmentsCounts[routeIndex2] = (lastStation ? ((LongArrayList)stationsIdLists.get(routeIndex2)).size() : ((LongArrayList)stationsIdLists.get(routeIndex2)).indexOf(commonStationId) + 1) - traverseIndex[routeIndex2];
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
                    float stationY = (float)routesIndicesInSection.indexOf(routeIndex) - (float)(routesIndicesInSection.size() - 1) / 2.0f + PSDTopTextureGenerator.getLineOffset(routeIndex, colorIndices);
                    bounds[1] = Math.min(bounds[1], stationY);
                    bounds[2] = Math.max(bounds[2], stationY);
                    ((Int2ObjectAVLTreeMap)stationPositions.get(routeIndex)).put(passedMultiplier * (j + traverseIndex[routeIndex] + 1), (Object)new StationPosition((float)reverseMultiplier * stationX / 2.0f, stationY, false));
                }
                int n = routeIndex;
                traverseIndex[n] = traverseIndex[n] + intermediateSegmentsCounts[routeIndex];
            }
            if (lastStation) continue;
            positionXOffset += intermediateSegmentsMaxCount;
            for (routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                float stationY = PSDTopTextureGenerator.getLineOffset(routeIndex, colorIndices);
                bounds[1] = Math.min(bounds[1], stationY);
                bounds[2] = Math.max(bounds[2], stationY);
                ((Int2ObjectAVLTreeMap)stationPositions.get(routeIndex)).put(passedMultiplier * traverseIndex[routeIndex], (Object)new StationPosition((float)(reverseMultiplier * positionXOffset) / 2.0f, stationY, true));
            }
            bounds[0] = (float)positionXOffset / 2.0f;
        }
    }

    private static float getLineOffset(int routeIndex, int[] colorIndices) {
        return (float)lineSpacing / (float)scale * ((float)colorIndices[routeIndex] - (float)colorIndices[colorIndices.length - 1] / 2.0f);
    }

    private static IntArrayList getRouteStream(long platformId, BiConsumer<SimplifiedRoute, Integer> nonTerminatingCallback) {
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
            colors.addAll((IntList)terminatingColors);
        }
        return colors;
    }

    private static String getStationName(long platformId) {
        Platform platform = (Platform)((Object)MinecraftClientData.getInstance().platformIdMap.get(platformId));
        Station station = platform == null ? null : (Station)platform.area;
        return station == null ? "" : station.getName();
    }

    public static LcdInfo getLcdInfo(long platformId) {
        try {

            final SimplifiedRoute[] matched = new SimplifiedRoute[1];
            final int[] currentIndex = { -1 };
            MinecraftClientData.getInstance().simplifiedRoutes.stream()
                    .filter(simplifiedRoute -> simplifiedRoute.getPlatformIndex(platformId) >= 0 && !simplifiedRoute.getName().isEmpty())
                    .sorted()
                    .findFirst()
                    .ifPresent(simplifiedRoute -> {
                        int idx = simplifiedRoute.getPlatformIndex(platformId);
                        if (idx < simplifiedRoute.getPlatforms().size() - 1) {
                            matched[0] = simplifiedRoute;
                            currentIndex[0] = idx;
                        }
                    });
            if (matched[0] == null) {
                return null;
            }
            final SimplifiedRoute route = matched[0];
            final int idx = currentIndex[0];
            final ObjectArrayList<SimplifiedRoutePlatform> platforms = route.getPlatforms();
            final String currentStation = platforms.get(idx).getStationName();
            final String nextStation = idx + 1 < platforms.size() ? platforms.get(idx + 1).getStationName() : "";
            return new LcdInfo(currentStation, nextStation, route.getName(), 0xFF000000 | route.getColor());
        } catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
    }

    public static final class LcdInfo {
        public final String currentStation;
        public final String nextStation;
        public final String routeName;
        public final int routeColor;

        public LcdInfo(String currentStation, String nextStation, String routeName, int routeColor) {
            this.currentStation = currentStation;
            this.nextStation = nextStation;
            this.routeName = routeName;
            this.routeColor = routeColor;
        }
    }

    public static final class LcdNextLine {
        public final String routeName;
        public final int routeColor;
        public final String nextStation;

        public LcdNextLine(String routeName, int routeColor, String nextStation) {
            this.routeName = routeName;
            this.routeColor = routeColor;
            this.nextStation = nextStation;
        }
    }

    public static final class LcdInfoMulti {
        public final String currentStation;
        public final ObjectArrayList<LcdNextLine> nextLines;

        public LcdInfoMulti(String currentStation, ObjectArrayList<LcdNextLine> nextLines) {
            this.currentStation = currentStation;
            this.nextLines = nextLines;
        }
    }

    public static LcdInfoMulti getLcdInfoMulti(long platformId) {
        try {
            final ObjectArrayList<LcdNextLine> nextLines = new ObjectArrayList<>();
            final ObjectArrayList<String> stationNames = new ObjectArrayList<>();
            MinecraftClientData.getInstance().simplifiedRoutes.stream()
                    .filter(simplifiedRoute -> !simplifiedRoute.getName().isEmpty())
                    .forEach(simplifiedRoute -> {
                        final int idx = simplifiedRoute.getPlatformIndex(platformId);
                        if (idx < 0) {
                            return;
                        }
                        final ObjectArrayList<SimplifiedRoutePlatform> platforms = simplifiedRoute.getPlatforms();

                        stationNames.add(platforms.get(idx).getStationName());
                        if (idx + 1 < platforms.size()) {
                            nextLines.add(new LcdNextLine(simplifiedRoute.getName(), 0xFF000000 | simplifiedRoute.getColor(), platforms.get(idx + 1).getStationName()));
                        }
                    });

            final String currentStation = stationNames.isEmpty() ? "" : stationNames.get(0);
            return new LcdInfoMulti(currentStation, nextLines);
        } catch (Exception e) {
            Init.LOGGER.error("", (Throwable)e);
            return null;
        }
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
            PSDTopTextureGenerator.drawLine(nativeImage, x1, y1, x2 - x1, y1OffsetGreater ? 0 : y2 - y1, y1OffsetGreater ? changeDifference : yChangeAbs, color);
            PSDTopTextureGenerator.drawLine(nativeImage, x2, y2, x1 - x2, y1OffsetGreater ? y1 - y2 : 0, y1OffsetGreater ? yChangeAbs : changeDifference, color);
        } else {
            int halfXChangeAbs = xChangeAbs / 2;
            PSDTopTextureGenerator.drawLine(nativeImage, x1, y1, x2 - x1, y2 - y1, halfXChangeAbs, color);
            PSDTopTextureGenerator.drawLine(nativeImage, x2, y2, x1 - x2, y1 - y2, halfXChangeAbs, color);
            PSDTopTextureGenerator.drawLine(nativeImage, (x1 + x2) / 2, y1 + (int)Math.copySign(halfXChangeAbs, y2 - y1), 0, y2 - y1, changeDifference, color);
        }
    }

    private static void drawLine(NativeImage nativeImage, int x, int y, int directionX, int directionY, int length, int color) {
        int xWidth;
        int halfLineHeight = lineSize / 2;
        int n = xWidth = directionX == 0 ? halfLineHeight : 0;
        int yWidth = directionX == 0 ? 0 : (directionY == 0 ? halfLineHeight : Math.round((float)lineSize * MathHelper.getSquareRootOfTwoMapped() / 2.0f));
        int yMin = y - halfLineHeight - (directionY < 0 ? length : 0) + 1;
        int yMax = y + halfLineHeight + (directionY > 0 ? length : 0) - 1;
        int drawOffset = directionX != 0 && directionY != 0 ? halfLineHeight : 0;
        for (int i = -drawOffset; i < Math.abs(length) + drawOffset; ++i) {
            int drawX = x + (directionX == 0 ? 0 : (int)Math.copySign(i, directionX)) + (directionX < 0 ? -1 : 0);
            int drawY = y + (directionY == 0 ? 0 : (int)Math.copySign(i, directionY)) + (directionY < 0 ? -1 : 0);
            for (int xOffset = 0; xOffset < xWidth; ++xOffset) {
                PSDTopTextureGenerator.drawPixelSafe(nativeImage, drawX - xOffset - 1, drawY, color);
                PSDTopTextureGenerator.drawPixelSafe(nativeImage, drawX + xOffset, drawY, color);
            }
            for (int yOffset = 0; yOffset < yWidth; ++yOffset) {
                PSDTopTextureGenerator.drawPixelSafe(nativeImage, drawX, Math.max(drawY - yOffset, yMin) - 1, color);
                PSDTopTextureGenerator.drawPixelSafe(nativeImage, drawX, Math.min(drawY + yOffset, yMax), color);
            }
        }
    }

    private static void drawStation(NativeImage nativeImage, int x, int y, float heightScale, int lines, boolean passed) {
        for (int offsetX = -lineSize; offsetX < lineSize; ++offsetX) {
            for (int offsetY = -lineSize; offsetY < lineSize; ++offsetY) {
                int i;
                int extraOffsetY = offsetY > 0 ? (int)((float)(lines * lineSpacing) * heightScale) : 0;
                int repeatDraw = offsetY == 0 ? (int)((float)(lines * lineSpacing) * heightScale) : 0;
                double squareSum = ((double)offsetX + 0.5) * ((double)offsetX + 0.5) + ((double)offsetY + 0.5) * ((double)offsetY + 0.5);
                if (squareSum <= 0.5 * (double)lineSize * (double)lineSize) {
                    for (i = 0; i <= repeatDraw; ++i) {
                        PSDTopTextureGenerator.drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, -1);
                    }
                    continue;
                }
                if (!(squareSum <= (double)(lineSize * lineSize))) continue;
                for (i = 0; i <= repeatDraw; ++i) {
                    PSDTopTextureGenerator.drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, passed ? -5592406 : -16777216);
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
                    PSDTopTextureGenerator.drawPixelSafe(nativeImage, (int)horizontalAlignment.getOffset((float)(drawX + x), (float)textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset((float)(drawY + y), (float)textDimensions[rotate90 ? 0 : 1]), backgroundColor);
                }
            }
        }
        drawX = 0;
        drawY = rotate90 ? textDimensions[0] - 1 : 0;
        for (int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
            PSDTopTextureGenerator.blendPixel(nativeImage, (int)horizontalAlignment.getOffset((float)(x + drawX), (float)textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset((float)(y + drawY), (float)textDimensions[rotate90 ? 0 : 1]), ((pixels[i] & 0xFF) << 24) + (textColor & 0xFFFFFF));
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

    private static void drawStringPixelated(NativeImage nativeImage, byte[] pixels, int[] textDimensions, int textColor, boolean fullPixel) {
        int yOffset = (textDimensions[1] * (fullPixel ? 1 : 4) - nativeImage.getHeight()) / 2;
        int drawX = 0;
        int drawY = 0;
        for (int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
            if ((pixels[i] & 0xFF) > 127) {
                if (fullPixel) {
                    PSDTopTextureGenerator.drawPixelSafe(nativeImage, drawX, drawY - yOffset, textColor);
                } else {
                    for (int j = 0; j < 3; ++j) {
                        for (int k = 0; k < 3; ++k) {
                            PSDTopTextureGenerator.drawPixelSafe(nativeImage, drawX * 4 + j, drawY * 4 + k - yOffset, textColor);
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
        ResourceManagerHelper.readResource((Identifier)new Identifier("mtr", resource), inputStream -> {
            try {
                NativeImage nativeImageResource = NativeImage.read((NativeImageFormat)NativeImageFormat.getAbgrMapped(), (InputStream)inputStream);
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
                        int pixel1 = nativeImageResource.getColor(MathHelper.clamp((int)floorX, (int)0, (int)(resourceWidth - 1)), MathHelper.clamp((int)floorY, (int)0, (int)(resourceHeight - 1)));
                        int pixel2 = nativeImageResource.getColor(MathHelper.clamp((int)ceilX, (int)0, (int)(resourceWidth - 1)), MathHelper.clamp((int)floorY, (int)0, (int)(resourceHeight - 1)));
                        int pixel3 = nativeImageResource.getColor(MathHelper.clamp((int)floorX, (int)0, (int)(resourceWidth - 1)), MathHelper.clamp((int)ceilY, (int)0, (int)(resourceHeight - 1)));
                        int pixel4 = nativeImageResource.getColor(MathHelper.clamp((int)ceilX, (int)0, (int)(resourceWidth - 1)), MathHelper.clamp((int)ceilY, (int)0, (int)(resourceHeight - 1)));
                        if (useActualColor) {
                            newColor = PSDTopTextureGenerator.invertColor(pixel1);
                        } else {
                            float luminance1 = (float)(pixel1 >> 24 & 0xFF) * percentX1 * percentY1;
                            float luminance2 = (float)(pixel2 >> 24 & 0xFF) * percentX2 * percentY1;
                            float luminance3 = (float)(pixel3 >> 24 & 0xFF) * percentX1 * percentY2;
                            float luminance4 = (float)(pixel4 >> 24 & 0xFF) * percentX2 * percentY2;
                            newColor = (color & 0xFFFFFF) + ((int)(luminance1 + luminance2 + luminance3 + luminance4) << 24);
                        }
                        PSDTopTextureGenerator.blendPixel(nativeImage, (flipX ? width - drawX - 1 : drawX) + x, drawY + y, newColor);
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
        if (Utilities.isBetween((double)x, (double)0.0, (double)(nativeImage.getWidth() - 1)) && Utilities.isBetween((double)y, (double)0.0, (double)(nativeImage.getHeight() - 1)) && (percent = (float)(color >> 24 & 0xFF) / 255.0f) > 0.0f) {
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
            PSDTopTextureGenerator.drawPixelSafe(nativeImage, x, y, finalColor);
        }
    }

    private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
        if (Utilities.isBetween((double)x, (double)0.0, (double)(nativeImage.getWidth() - 1)) && Utilities.isBetween((double)y, (double)0.0, (double)(nativeImage.getHeight() - 1))) {
            nativeImage.setPixelColor(x, y, PSDTopTextureGenerator.invertColor(color));
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
