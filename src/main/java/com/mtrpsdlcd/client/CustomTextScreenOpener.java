package com.mtrpsdlcd.client;

import net.minecraft.client.MinecraftClient;
import org.mtr.mapping.holder.BlockPos;

public final class CustomTextScreenOpener {
	private CustomTextScreenOpener() {
	}

	public static void open(BlockPos topPos, String currentText, String currentImagePath) {
		MinecraftClient.getInstance().setScreen(new CustomTextScreen(topPos, currentText, currentImagePath));
	}
}
