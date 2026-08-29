package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.item.ItemPSDBase;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.registry.ItemRegistryObject;

public final class Items {

	public static final ItemRegistryObject PSD_GLASS = ModRegistry.registerItem("psd_glass", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_1, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_GLASS_2 = ModRegistry.registerItem("psd_glass_2", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_2, itemSettings)), ItemGroups.MAIN);

	public static final ItemRegistryObject PSD_DOOR = ModRegistry.registerItem("psd_door", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_1, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_2 = ModRegistry.registerItem("psd_door_2", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2, itemSettings)), ItemGroups.MAIN);

	public static final ItemRegistryObject PSD_DOOR_LCD2 = ModRegistry.registerItem("psd_door_lcd2", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD2, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD3 = ModRegistry.registerItem("psd_door_lcd3", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD3, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD4 = ModRegistry.registerItem("psd_door_lcd4", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD4, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD5 = ModRegistry.registerItem("psd_door_lcd5", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD5, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD6 = ModRegistry.registerItem("psd_door_lcd6", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD6, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD7 = ModRegistry.registerItem("psd_door_lcd7", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD7, itemSettings)), ItemGroups.MAIN);

	public static void register() {
	}
}
