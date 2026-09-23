package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.block.entity.MyPSDDoorBE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd2BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd15BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd3BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd4BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd5BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd6BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd7BE;
import com.mtrpsdlcd.block.entity.MyPSDTopBE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd2BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd3BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd4BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd5BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd6BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd7BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd8BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd9BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd10BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd11BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd12BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd13BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd15BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd16BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd17BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd18BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd20BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd21BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd22BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd19BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd19BE;
import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;

public final class BlockEntities {
	public static final BlockEntityTypeRegistryObject<MyPSDDoorBE> PSD_DOOR = ModRegistry.registerBlockEntity("psd_door", (blockPos, blockState) -> new MyPSDDoorBE(BlockEntities.PSD_DOOR.get(), blockPos, blockState), Blocks.PSD_DOOR);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorBE> PSD_DOOR_2 = ModRegistry.registerBlockEntity("psd_door_2", (blockPos, blockState) -> new MyPSDDoorBE(BlockEntities.PSD_DOOR_2.get(), blockPos, blockState), Blocks.PSD_DOOR_2);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd2BE> PSD_DOOR_LCD2 = ModRegistry.registerBlockEntity("psd_door_lcd2", (blockPos, blockState) -> new MyPSDDoorLcd2BE(BlockEntities.PSD_DOOR_LCD2.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD2);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd3BE> PSD_DOOR_LCD3 = ModRegistry.registerBlockEntity("psd_door_lcd3", (blockPos, blockState) -> new MyPSDDoorLcd3BE(BlockEntities.PSD_DOOR_LCD3.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD3);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd4BE> PSD_DOOR_LCD4 = ModRegistry.registerBlockEntity("psd_door_lcd4", (blockPos, blockState) -> new MyPSDDoorLcd4BE(BlockEntities.PSD_DOOR_LCD4.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD4);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd5BE> PSD_DOOR_LCD5 = ModRegistry.registerBlockEntity("psd_door_lcd5", (blockPos, blockState) -> new MyPSDDoorLcd5BE(BlockEntities.PSD_DOOR_LCD5.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD5);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd6BE> PSD_DOOR_LCD6 = ModRegistry.registerBlockEntity("psd_door_lcd6", (blockPos, blockState) -> new MyPSDDoorLcd6BE(BlockEntities.PSD_DOOR_LCD6.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD6);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd7BE> PSD_DOOR_LCD7 = ModRegistry.registerBlockEntity("psd_door_lcd7", (blockPos, blockState) -> new MyPSDDoorLcd7BE(BlockEntities.PSD_DOOR_LCD7.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD7);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP = ModRegistry.registerBlockEntity("psd_top", MyPSDTopBE::new, Blocks.PSD_TOP);

	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD2 = ModRegistry.registerBlockEntity("psd_top_lcd2", MyPSDTopLcd2BE::new, Blocks.PSD_TOP_LCD2);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD3 = ModRegistry.registerBlockEntity("psd_top_lcd3", MyPSDTopLcd3BE::new, Blocks.PSD_TOP_LCD3);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD4 = ModRegistry.registerBlockEntity("psd_top_lcd4", MyPSDTopLcd4BE::new, Blocks.PSD_TOP_LCD4);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD5 = ModRegistry.registerBlockEntity("psd_top_lcd5", MyPSDTopLcd5BE::new, Blocks.PSD_TOP_LCD5);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD6 = ModRegistry.registerBlockEntity("psd_top_lcd6", MyPSDTopLcd6BE::new, Blocks.PSD_TOP_LCD6);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD7 = ModRegistry.registerBlockEntity("psd_top_lcd7", MyPSDTopLcd7BE::new, Blocks.PSD_TOP_LCD7);

	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD8 = ModRegistry.registerBlockEntity("psd_top_lcd8", MyPSDTopLcd8BE::new, Blocks.PSD_TOP_LCD8);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD9 = ModRegistry.registerBlockEntity("psd_top_lcd9", MyPSDTopLcd9BE::new, Blocks.PSD_TOP_LCD9);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD10 = ModRegistry.registerBlockEntity("psd_top_lcd10", MyPSDTopLcd10BE::new, Blocks.PSD_TOP_LCD10);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD11 = ModRegistry.registerBlockEntity("psd_top_lcd11", MyPSDTopLcd11BE::new, Blocks.PSD_TOP_LCD11);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD12 = ModRegistry.registerBlockEntity("psd_top_lcd12", MyPSDTopLcd12BE::new, Blocks.PSD_TOP_LCD12);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD13 = ModRegistry.registerBlockEntity("psd_top_lcd13", MyPSDTopLcd13BE::new, Blocks.PSD_TOP_LCD13);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD14 = ModRegistry.registerBlockEntity("psd_top_lcd14", MyPSDTopLcd14BE::new, Blocks.PSD_TOP_LCD14);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd15BE> PSD_DOOR_LCD15 = ModRegistry.registerBlockEntity("psd_door_lcd15", (blockPos, blockState) -> new MyPSDDoorLcd15BE(BlockEntities.PSD_DOOR_LCD15.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD15);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD15 = ModRegistry.registerBlockEntity("psd_top_lcd15", MyPSDTopLcd15BE::new, Blocks.PSD_TOP_LCD15);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD16 = ModRegistry.registerBlockEntity("psd_top_lcd16", MyPSDTopLcd16BE::new, Blocks.PSD_TOP_LCD16);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD17 = ModRegistry.registerBlockEntity("psd_top_lcd_3", MyPSDTopLcd17BE::new, Blocks.PSD_TOP_LCD17);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD18 = ModRegistry.registerBlockEntity("psd_top_lcd18", MyPSDTopLcd18BE::new, Blocks.PSD_TOP_LCD18);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD20 = ModRegistry.registerBlockEntity("psd_top_lcd20", MyPSDTopLcd20BE::new, Blocks.PSD_TOP_LCD20);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD21 = ModRegistry.registerBlockEntity("psd_top_lcd21", MyPSDTopLcd21BE::new, Blocks.PSD_TOP_LCD21);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD22 = ModRegistry.registerBlockEntity("psd_top_lcd22", MyPSDTopLcd22BE::new, Blocks.PSD_TOP_LCD22);
	public static final BlockEntityTypeRegistryObject<MyPSDDoorLcd19BE> PSD_DOOR_LCD19 = ModRegistry.registerBlockEntity("psd_door_lcd19", (blockPos, blockState) -> new MyPSDDoorLcd19BE(BlockEntities.PSD_DOOR_LCD19.get(), blockPos, blockState), Blocks.PSD_DOOR_LCD19);
	public static final BlockEntityTypeRegistryObject<MyPSDTopBE> PSD_TOP_LCD19 = ModRegistry.registerBlockEntity("psd_top_lcd19", MyPSDTopLcd19BE::new, Blocks.PSD_TOP_LCD19);

	public static void register() {
	}
}
