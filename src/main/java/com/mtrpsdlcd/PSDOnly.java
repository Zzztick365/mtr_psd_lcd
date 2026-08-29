package com.mtrpsdlcd;

import com.mtrpsdlcd.registry.ModRegistry;
import net.fabricmc.api.ModInitializer;

public class PSDOnly implements ModInitializer {
	@Override
	public void onInitialize() {
		ModRegistry.register();
	}
}
