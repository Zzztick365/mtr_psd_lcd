package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Constants;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.registry.CreativeModeTabHolder;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mapping.registry.Registry;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModRegistry {
	public static final Registry REGISTRY = new Registry();

	public static BlockRegistryObject registerBlock(String id, Supplier<Block> supplier) {
		return REGISTRY.registerBlock(Constants.id(id), supplier);
	}

	public static BlockRegistryObject registerBlockItem(String id, Supplier<Block> supplier, CreativeModeTabHolder itemGroup) {
		return REGISTRY.registerBlockWithBlockItem(Constants.id(id), supplier, new CreativeModeTabHolder[]{itemGroup});
	}

	public static ItemRegistryObject registerItem(String id, Function<ItemSettings, Item> callback, CreativeModeTabHolder itemGroup) {
		return REGISTRY.registerItem(Constants.id(id), callback, new CreativeModeTabHolder[]{itemGroup});
	}

	public static <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntity(String id, BiFunction<BlockPos, BlockState, T> constructor, BlockRegistryObject associatedBlock) {
		final Supplier[] supplierArray = new Supplier[1];
		supplierArray[0] = () -> associatedBlock.get();
		return REGISTRY.registerBlockEntityType(Constants.id(id), constructor, supplierArray);
	}

	public static void register() {
		Blocks.register();
		BlockEntities.register();
		Items.register();
		REGISTRY.init();
	}
}
