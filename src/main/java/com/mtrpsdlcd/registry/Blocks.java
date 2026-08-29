package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.block.MyPSDDoor;
import com.mtrpsdlcd.block.MyPSDDoorLcd2;
import com.mtrpsdlcd.block.MyPSDDoorLcd3;
import com.mtrpsdlcd.block.MyPSDDoorLcd4;
import com.mtrpsdlcd.block.MyPSDDoorLcd5;
import com.mtrpsdlcd.block.MyPSDDoorLcd6;
import com.mtrpsdlcd.block.MyPSDDoorLcd7;
import com.mtrpsdlcd.block.MyPSDGlass;
import com.mtrpsdlcd.block.MyPSDLCD;
import com.mtrpsdlcd.block.MyPSDTop;
import com.mtrpsdlcd.block.MyPSDTopLcd2;
import com.mtrpsdlcd.block.MyPSDTopLcd3;
import com.mtrpsdlcd.block.MyPSDTopLcd4;
import com.mtrpsdlcd.block.MyPSDTopLcd5;
import com.mtrpsdlcd.block.MyPSDTopLcd6;
import com.mtrpsdlcd.block.MyPSDTopLcd7;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.registry.BlockRegistryObject;

public final class Blocks {

	public static final BlockRegistryObject PSD_LCD = ModRegistry.registerBlockItem("psd_lcd", () -> new Block(new MyPSDLCD()), ItemGroups.MAIN);

	public static final BlockRegistryObject PSD_DOOR = ModRegistry.registerBlock("psd_door", () -> new Block(new MyPSDDoor(0)));
	public static final BlockRegistryObject PSD_GLASS = ModRegistry.registerBlock("psd_glass", () -> new Block(new MyPSDGlass(0)));
	public static final BlockRegistryObject PSD_DOOR_2 = ModRegistry.registerBlock("psd_door_2", () -> new Block(new MyPSDDoor(1)));
	public static final BlockRegistryObject PSD_GLASS_2 = ModRegistry.registerBlock("psd_glass_2", () -> new Block(new MyPSDGlass(1)));

	public static final BlockRegistryObject PSD_DOOR_LCD2 = ModRegistry.registerBlock("psd_door_lcd2", () -> new Block(new MyPSDDoorLcd2()));
	public static final BlockRegistryObject PSD_TOP_LCD2 = ModRegistry.registerBlock("psd_top_lcd2", () -> new Block(new MyPSDTopLcd2()));
	public static final BlockRegistryObject PSD_DOOR_LCD3 = ModRegistry.registerBlock("psd_door_lcd3", () -> new Block(new MyPSDDoorLcd3()));
	public static final BlockRegistryObject PSD_TOP_LCD3 = ModRegistry.registerBlock("psd_top_lcd3", () -> new Block(new MyPSDTopLcd3()));

	public static final BlockRegistryObject PSD_DOOR_LCD4 = ModRegistry.registerBlock("psd_door_lcd4", () -> new Block(new MyPSDDoorLcd4()));
	public static final BlockRegistryObject PSD_TOP_LCD4 = ModRegistry.registerBlock("psd_top_lcd4", () -> new Block(new MyPSDTopLcd4()));
	public static final BlockRegistryObject PSD_DOOR_LCD5 = ModRegistry.registerBlock("psd_door_lcd5", () -> new Block(new MyPSDDoorLcd5()));
	public static final BlockRegistryObject PSD_TOP_LCD5 = ModRegistry.registerBlock("psd_top_lcd5", () -> new Block(new MyPSDTopLcd5()));
	public static final BlockRegistryObject PSD_DOOR_LCD6 = ModRegistry.registerBlock("psd_door_lcd6", () -> new Block(new MyPSDDoorLcd6()));
	public static final BlockRegistryObject PSD_TOP_LCD6 = ModRegistry.registerBlock("psd_top_lcd6", () -> new Block(new MyPSDTopLcd6()));
	public static final BlockRegistryObject PSD_DOOR_LCD7 = ModRegistry.registerBlock("psd_door_lcd7", () -> new Block(new MyPSDDoorLcd7()));
	public static final BlockRegistryObject PSD_TOP_LCD7 = ModRegistry.registerBlock("psd_top_lcd7", () -> new Block(new MyPSDTopLcd7()));

	public static final BlockRegistryObject PSD_TOP = ModRegistry.registerBlock("psd_top", () -> new Block(new MyPSDTop()));

	public static void register() {
	}

	public static void registerClient() {
		ModRegistryClient.registerBlockRenderType(RenderLayer.getCutout(), PSD_DOOR, PSD_GLASS, PSD_DOOR_2, PSD_GLASS_2, PSD_DOOR_LCD2, PSD_DOOR_LCD3, PSD_DOOR_LCD4, PSD_DOOR_LCD5, PSD_DOOR_LCD6, PSD_DOOR_LCD7);
	}
}
