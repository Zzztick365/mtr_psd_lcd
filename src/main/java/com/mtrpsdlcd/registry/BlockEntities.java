package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.block.entity.MyPSDDoorBE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd2BE;
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

	public static void register() {
	}
}
