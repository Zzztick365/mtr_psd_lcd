package com.mtrpsdlcd;

import com.mtrpsdlcd.registry.ModRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PSDOnly implements ModInitializer {

	private static final String MOD_ID = "mtr_psd_lcd";

	@Override
	public void onInitialize() {

		try {
			System.setProperty("java.awt.headless", "false");
		} catch (Throwable ignored) {
		}

		enforceSingleVersion();

		ModRegistry.register();
	}

	private void enforceSingleVersion() {
		try {
			final Path modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");
			final List<String> sameIdJars = new ArrayList<>();
			if (Files.isDirectory(modsDir)) {
				try (final java.util.stream.Stream<Path> stream = Files.list(modsDir)) {
					for (final Path jar : stream.filter(p -> p.toString().toLowerCase().endsWith(".jar")).collect(Collectors.toList())) {
						if (modHasOurId(jar)) {
							sameIdJars.add(jar.getFileName().toString());
						}
					}
				}
			}
			if (sameIdJars.size() > 1) {
				throw new RuntimeException("检测到多个 Mtr_psd_LCD (mtr_psd_lcd) 版本同时存在，无法启动！请只保留一个版本。发现：" + String.join(", ", sameIdJars));
			}
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {

			System.err.println("[mtr_psd_lcd] 版式检测跳过（无法扫描 mods 目录）: " + e.getMessage());
		}
	}

	private boolean modHasOurId(final Path jar) {
		try (final ZipFile zip = new ZipFile(jar.toFile())) {
			final ZipEntry entry = zip.getEntry("fabric.mod.json");
			if (entry == null) {
				return false;
			}
			final String json = new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);

			return json.contains("\"" + MOD_ID + "\"") && json.matches("(?s).*\"id\"\\s*:\\s*\"" + MOD_ID + "\".*");
		} catch (final IOException | IllegalArgumentException e) {
			return false;
		}
	}
}
