package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.block.MyPSDDoor;
import com.mtrpsdlcd.block.MyPSDDoorLcd2;
import com.mtrpsdlcd.block.MyPSDDoorLcd3;
import com.mtrpsdlcd.block.MyPSDDoorLcd4;
import com.mtrpsdlcd.block.MyPSDDoorLcd5;
import com.mtrpsdlcd.block.MyPSDDoorLcd6;
import com.mtrpsdlcd.block.MyPSDDoorLcd7;
import com.mtrpsdlcd.block.MyPSDGlass;
import com.mtrpsdlcd.block.MyPSDGlassLcd13;
import com.mtrpsdlcd.block.MyPSDGlassLcd14;
import com.mtrpsdlcd.block.MyPSDLCD;
import com.mtrpsdlcd.block.MyPSDTop;
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
import com.mtrpsdlcd.block.MyPSDDoorLcd15;
import com.mtrpsdlcd.block.MyPSDTopLcd15;
import com.mtrpsdlcd.block.MyPSDTopLcd16;
import com.mtrpsdlcd.block.MyPSDGlassLcd17;
import com.mtrpsdlcd.block.MyPSDTopLcd17;
import com.mtrpsdlcd.block.MyPSDTopLcd18;
import com.mtrpsdlcd.block.MyPSDTopLcd20;
import com.mtrpsdlcd.block.MyPSDGlassLcd21;
import com.mtrpsdlcd.block.MyPSDTopLcd21;
import com.mtrpsdlcd.block.MyPSDGlassLcd22;
import com.mtrpsdlcd.block.MyPSDTopLcd22;
import com.mtrpsdlcd.block.MyPSDDoorLcd19;
import com.mtrpsdlcd.block.MyPSDTopLcd19;
import com.mtrpsdlcd.block.MyPSDPillar;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.registry.BlockRegistryObject;

public final class Blocks {

	public static final BlockRegistryObject PSD_LCD = ModRegistry.registerBlockItem("psd_lcd", () -> new Block(new MyPSDLCD()), ItemGroups.MAIN);

	public static final BlockRegistryObject PSD_DOOR = ModRegistry.registerBlock("psd_door", () -> new Block(new MyPSDDoor(0)));
	public static final BlockRegistryObject PSD_GLASS = ModRegistry.registerBlock("psd_glass", () -> new Block(new MyPSDGlass(0)));
	public static final BlockRegistryObject PSD_DOOR_2 = ModRegistry.registerBlock("psd_door_2", () -> new Block(new MyPSDDoor(1)));
	public static final BlockRegistryObject PSD_GLASS_2 = ModRegistry.registerBlock("psd_glass_2", () -> new Block(new MyPSDGlass(1)));
	public static final BlockRegistryObject PSD_GLASS_LCD13 = ModRegistry.registerBlock("psd_glass_lcd13", () -> new Block(new MyPSDGlassLcd13()));
	public static final BlockRegistryObject PSD_GLASS_LCD14 = ModRegistry.registerBlock("psd_glass_lcd14", () -> new Block(new MyPSDGlassLcd14()));

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

	public static final BlockRegistryObject PSD_TOP_LCD8 = ModRegistry.registerBlock("psd_top_lcd8", () -> new Block(new MyPSDTopLcd8()));
	public static final BlockRegistryObject PSD_TOP_LCD9 = ModRegistry.registerBlock("psd_top_lcd9", () -> new Block(new MyPSDTopLcd9()));
	public static final BlockRegistryObject PSD_TOP_LCD10 = ModRegistry.registerBlock("psd_top_lcd10", () -> new Block(new MyPSDTopLcd10()));
	public static final BlockRegistryObject PSD_TOP_LCD11 = ModRegistry.registerBlock("psd_top_lcd11", () -> new Block(new MyPSDTopLcd11()));

	public static final BlockRegistryObject PSD_TOP_LCD12 = ModRegistry.registerBlock("psd_top_lcd12", () -> new Block(new MyPSDTopLcd12()));

	public static final BlockRegistryObject PSD_TOP = ModRegistry.registerBlock("psd_top", () -> new Block(new MyPSDTop()));

	public static final BlockRegistryObject PSD_TOP_LCD13 = ModRegistry.registerBlock("psd_top_lcd13", () -> new Block(new MyPSDTopLcd13()));
	public static final BlockRegistryObject PSD_TOP_LCD14 = ModRegistry.registerBlock("psd_top_lcd14", () -> new Block(new MyPSDTopLcd14()));
	public static final BlockRegistryObject PSD_DOOR_LCD15 = ModRegistry.registerBlock("psd_door_lcd15", () -> new Block(new MyPSDDoorLcd15()));
	public static final BlockRegistryObject PSD_TOP_LCD15 = ModRegistry.registerBlock("psd_top_lcd15", () -> new Block(new MyPSDTopLcd15()));
	public static final BlockRegistryObject PSD_TOP_LCD16 = ModRegistry.registerBlock("psd_top_lcd16", () -> new Block(new MyPSDTopLcd16()));
	public static final BlockRegistryObject PSD_GLASS_LCD17 = ModRegistry.registerBlock("psd_glass_lcd_3", () -> new Block(new MyPSDGlassLcd17()));
	public static final BlockRegistryObject PSD_TOP_LCD17 = ModRegistry.registerBlock("psd_top_lcd_3", () -> new Block(new MyPSDTopLcd17()));
	public static final BlockRegistryObject PSD_TOP_LCD18 = ModRegistry.registerBlock("psd_top_lcd18", () -> new Block(new MyPSDTopLcd18()));
	public static final BlockRegistryObject PSD_TOP_LCD20 = ModRegistry.registerBlock("psd_top_lcd20", () -> new Block(new MyPSDTopLcd20()));
	public static final BlockRegistryObject PSD_GLASS_LCD21 = ModRegistry.registerBlock("psd_glass_lcd21", () -> new Block(new MyPSDGlassLcd21()));
	public static final BlockRegistryObject PSD_TOP_LCD21 = ModRegistry.registerBlock("psd_top_lcd21", () -> new Block(new MyPSDTopLcd21()));
	public static final BlockRegistryObject PSD_GLASS_LCD22 = ModRegistry.registerBlock("psd_glass_lcd22", () -> new Block(new MyPSDGlassLcd22()));
	public static final BlockRegistryObject PSD_TOP_LCD22 = ModRegistry.registerBlock("psd_top_lcd22", () -> new Block(new MyPSDTopLcd22()));
	public static final BlockRegistryObject PSD_DOOR_LCD19 = ModRegistry.registerBlock("psd_door_lcd19", () -> new Block(new MyPSDDoorLcd19()));
	public static final BlockRegistryObject PSD_TOP_LCD19 = ModRegistry.registerBlock("psd_top_lcd19", () -> new Block(new MyPSDTopLcd19()));

	public static final BlockRegistryObject PSD_PILLAR = ModRegistry.registerBlockItem("psd_pillar", () -> new Block(new MyPSDPillar()), ItemGroups.MAIN);

	public static void register() {
	}

	public static void registerClient() {
		ModRegistryClient.registerBlockRenderType(RenderLayer.getCutout(), PSD_DOOR, PSD_GLASS, PSD_DOOR_2, PSD_GLASS_2, PSD_DOOR_LCD2, PSD_DOOR_LCD3, PSD_DOOR_LCD4, PSD_DOOR_LCD5, PSD_DOOR_LCD6, PSD_DOOR_LCD7, PSD_GLASS_LCD13, PSD_GLASS_LCD14, PSD_DOOR_LCD15, PSD_GLASS_LCD17, PSD_GLASS_LCD21, PSD_GLASS_LCD22, PSD_DOOR_LCD19);
		ModRegistryClient.registerBlockRenderType(RenderLayer.getSolid(), PSD_PILLAR);
	}
}
