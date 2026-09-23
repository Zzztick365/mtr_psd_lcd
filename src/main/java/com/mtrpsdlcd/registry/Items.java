package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.item.ItemPSDBase;
import com.mtrpsdlcd.item.ItemPSDTopModule;
import com.mtrpsdlcd.item.MyPSDTool;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.registry.ItemRegistryObject;

public final class Items {

	public static final ItemRegistryObject PSD_TOOL = ModRegistry.registerItem("psd_tool", itemSettings -> new Item(new MyPSDTool(itemSettings)), ItemGroups.MAIN);

	static {

		Blocks.register();
	}

	public static final ItemRegistryObject PSD_GLASS_LCD14 = ModRegistry.registerItem("psd_glass_lcd14", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD14, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_GLASS_LCD17 = ModRegistry.registerItem("psd_glass_lcd_3", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD17, itemSettings)), ItemGroups.MAIN);

	public static final ItemRegistryObject PSD_DOOR = ModRegistry.registerItem("psd_door", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_1, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_2 = ModRegistry.registerItem("psd_door_2", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD3 = ModRegistry.registerItem("psd_door_lcd3", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD3, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD2 = ModRegistry.registerItem("psd_door_lcd2", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD2, itemSettings)), ItemGroups.MAIN);

	// ================= ⑩-⑮ LCD4 / LCD5 / LCD6 / LCD7 / LCD19 / LCD15 =================
	public static final ItemRegistryObject PSD_DOOR_LCD4 = ModRegistry.registerItem("psd_door_lcd4", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD4, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD5 = ModRegistry.registerItem("psd_door_lcd5", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD5, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD6 = ModRegistry.registerItem("psd_door_lcd6", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD6, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD7 = ModRegistry.registerItem("psd_door_lcd7", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD7, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD19 = ModRegistry.registerItem("psd_door_lcd19", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD19, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_DOOR_LCD15 = ModRegistry.registerItem("psd_door_lcd15", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD15, itemSettings)), ItemGroups.MAIN);

	public static final ItemRegistryObject PSD_TOP_LCD8 = ModRegistry.registerItem("psd_top_lcd8", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD8, true, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_TOP_LCD9 = ModRegistry.registerItem("psd_top_lcd9", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD9, true, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_TOP_LCD10 = ModRegistry.registerItem("psd_top_lcd10", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD10, true, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_TOP_LCD11 = ModRegistry.registerItem("psd_top_lcd11", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD11, true, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_TOP_LCD16 = ModRegistry.registerItem("psd_top_lcd16", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD16, true, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_TOP_LCD12 = ModRegistry.registerItem("psd_top_lcd12", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD12, false, itemSettings)), ItemGroups.MAIN);
	public static final ItemRegistryObject PSD_TOP_LCD18 = ModRegistry.registerItem("psd_top_lcd18", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD18, false, itemSettings)), ItemGroups.MAIN);

	public static final ItemRegistryObject PSD_GLASS = ModRegistry.registerItemHidden("psd_glass", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_1, itemSettings)));
	public static final ItemRegistryObject PSD_GLASS_2 = ModRegistry.registerItemHidden("psd_glass_2", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_2, itemSettings)));

	public static final ItemRegistryObject PSD_GLASS_LCD21 = ModRegistry.registerItemHidden("psd_glass_lcd21", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD21, itemSettings)));
	public static final ItemRegistryObject PSD_GLASS_LCD22 = ModRegistry.registerItemHidden("psd_glass_lcd22", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD22, itemSettings)));
	public static final ItemRegistryObject PSD_TOP_LCD20 = ModRegistry.registerItemHidden("psd_top_lcd20", itemSettings -> new Item(new ItemPSDTopModule(Blocks.PSD_TOP_LCD20, false, itemSettings)));

	public static final ItemRegistryObject PSD_GLASS_LCD13 = ModRegistry.registerItemHidden("psd_glass_lcd13", itemSettings -> new Item(new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD13, itemSettings)));

	public static void register() {
	}
}
