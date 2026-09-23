package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.render.RenderPSDTop;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mod.render.RenderPSDAPGDoor;

public final class BlockEntityRenderers {
	public static void registerClient() {
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 0));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_2, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 1));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD2, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 1));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD15, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 1));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD3, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 0));

		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD4, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 0));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD5, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 1));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD6, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 0));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD7, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 1));

		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD2, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD3, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD4, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD5, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD6, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD7, RenderPSDTop::new);

		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD8, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD9, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD10, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD11, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD12, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD13, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD14, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD15, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD16, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD17, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD18, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD20, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD21, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD22, RenderPSDTop::new);
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_DOOR_LCD19, dispatcher -> new RenderPSDAPGDoor((BlockEntityRenderer.Argument) dispatcher, 0));
		ModRegistryClient.registerBlockEntityRenderer(BlockEntities.PSD_TOP_LCD19, RenderPSDTop::new);
	}
}
