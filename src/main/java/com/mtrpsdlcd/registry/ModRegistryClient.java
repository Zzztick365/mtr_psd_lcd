package com.mtrpsdlcd.registry;

import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.registry.BlockEntityTypeRegistryObject;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.registry.RegistryClient;

import java.util.function.Function;

public class ModRegistryClient {
	public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(ModRegistry.REGISTRY);

	public static <T extends BlockEntityTypeRegistryObject<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRenderer.Argument, BlockEntityRenderer<U>> rendererInstance) {
		REGISTRY_CLIENT.registerBlockEntityRenderer(blockEntityType, rendererInstance);
	}

	public static void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject... blocks) {
		for (BlockRegistryObject block : blocks) {
			REGISTRY_CLIENT.registerBlockRenderType(renderLayer, block);
		}
	}

	public static void register() {
		Blocks.registerClient();
		BlockEntityRenderers.registerClient();
		REGISTRY_CLIENT.init();
	}
}
