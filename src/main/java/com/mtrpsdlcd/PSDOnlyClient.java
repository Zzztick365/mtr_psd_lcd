package com.mtrpsdlcd;

import com.mtrpsdlcd.registry.ModRegistryClient;
import net.fabricmc.api.ClientModInitializer;

public class PSDOnlyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		try {
			System.setProperty("java.awt.headless", "false");
		} catch (Throwable ignored) {
		}
		ModRegistryClient.register();
	}
}
