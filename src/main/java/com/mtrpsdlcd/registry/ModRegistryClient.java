package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Constants;
import com.mtrpsdlcd.block.PSDCustomText;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.registry.PacketHandler;
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

	public static void sendPacketToServer(PacketHandler packet) {
		REGISTRY_CLIENT.sendPacketToServer(packet);
	}

	public static void setupPackets(String channel) {
		REGISTRY_CLIENT.setupPackets(Constants.id(channel));
	}

	public static void applyCustomTextLocally(BlockPos topPos, String text, String imagePath) {
		final net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
		if (client.world == null) {
			return;
		}
		PSDCustomText.apply(new World(client.world), topPos, text, imagePath);
	}

	public static void register() {
		Blocks.registerClient();
		BlockEntityRenderers.registerClient();
		REGISTRY_CLIENT.init();
		setupPackets("packet");
	}
}
