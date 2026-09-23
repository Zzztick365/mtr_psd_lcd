package com.mtrpsdlcd.client;

import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.packet.PacketCustomText;
import com.mtrpsdlcd.registry.ModRegistryClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mod.Init;

public class CustomTextScreen extends Screen {
	private final BlockPos topPos;
	private final String initialText;
	private String currentImagePath;
	private TextFieldWidget textField;
	private volatile String pickedImagePath = null;
	private volatile String pickerError = null;
	private volatile boolean pickingImage = false;

	public CustomTextScreen(BlockPos topPos, String initialText, String initialImagePath) {
		super(Text.literal("自定义文字 / 图片"));
		this.topPos = topPos;
		this.initialText = initialText;
		this.currentImagePath = initialImagePath == null ? "" : initialImagePath;
	}

	@Override
	protected void init() {
		final int centerX = width / 2;
		final int centerY = height / 2;
		textField = new TextFieldWidget(textRenderer, centerX - 130, centerY - 44, 260, 20, Text.literal("文字"));
		textField.setMaxLength(64);
		textField.setText(readInitialText());
		addDrawableChild(textField);
		setInitialFocus(textField);
		addDrawableChild(ButtonWidget.builder(Text.literal("选择图片"), button -> pickImageFile()).dimensions(centerX - 130, centerY - 8, 175, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.literal("清除图片"), button -> {
			currentImagePath = "";
			PSDCustomText.clearSelectedLibraryImage();
			final String text = textField.getText();
			ModRegistryClient.sendPacketToServer(new PacketCustomText(topPos, text, ""));
			ModRegistryClient.applyCustomTextLocally(topPos, text, "");
			Init.LOGGER.info("[LCD14] 已清除自定义图片（本屏 + 全局记录）：" + topPos);
		}).dimensions(centerX + 50, centerY - 8, 80, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("保存"), button -> {
			final String text = textField.getText();
			ModRegistryClient.sendPacketToServer(new PacketCustomText(topPos, text, currentImagePath));
			ModRegistryClient.applyCustomTextLocally(topPos, text, currentImagePath);
			writeTextFile(text);

			if (currentImagePath == null || currentImagePath.isEmpty()) {
				PSDCustomText.clearSelectedLibraryImage();
			} else {
				PSDCustomText.setSelectedLibraryImage(currentImagePath);
			}
			close();
		}).dimensions(centerX - 130, centerY + 40, 125, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("取消"), button -> close()).dimensions(centerX + 5, centerY + 40, 125, 20).build());
	}

	private String readInitialText() {
		final String fromBlock = initialText == null ? "" : initialText;
		if (!fromBlock.isEmpty() && !PSDCustomText.DEFAULT_TEXT.equals(fromBlock)) {
			return fromBlock;
		}
		try {
			final java.io.File file = new java.io.File(PSDCustomText.getLibraryGroupDir(), "text.txt");
			if (file.isFile()) {
				final String fromFile = new String(java.nio.file.Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8).trim();
				if (!fromFile.isEmpty()) {
					return fromFile;
				}
			}
		} catch (Throwable ignored) {
		}
		return fromBlock;
	}

	private void writeTextFile(String text) {
		try {
			java.nio.file.Files.write(new java.io.File(PSDCustomText.getLibraryGroupDir(), "text.txt").toPath(), (text == null ? "" : text).getBytes(java.nio.charset.StandardCharsets.UTF_8));
		} catch (Throwable t) {
			Init.LOGGER.error("[LCD14] 写入 text.txt 失败", t);
		}
	}

	private void pickImageFile() {
		if (pickingImage) {
			return;
		}
		pickingImage = true;
		pickerError = null;
		final Thread thread = new Thread(() -> {
			String result = null;
			String error = null;
			try {
				result = openAwtFileDialog();
			} catch (Throwable t) {
				error = "系统对话框失败：" + describeThrowable(t) + " [headless=" + java.awt.GraphicsEnvironment.isHeadless() + "]";
				try {
					result = openSwingFileChooser();
					error = null;
				} catch (Throwable t2) {
					error = error + "；Swing 兜底也失败：" + describeThrowable(t2);
				}
			}
			if (result != null && !result.isEmpty()) {
				final String imported = importToLibrary(result);
				if (imported != null && !imported.isEmpty()) {
					pickedImagePath = imported;
				}
				Init.LOGGER.info("[LCD14] 选择图片 file=" + result + " → 入库=" + imported);
			} else if (error != null) {
				pickerError = error;
				Init.LOGGER.error("[LCD14] 图片选择器失败：" + error);
			}
			pickingImage = false;
		}, "psd-lcd-image-picker");
		thread.setDaemon(true);
		thread.start();
	}

	private static final int MAX_IMAGE_SIDE = 1920;

	private static int[] readImageSize(java.io.File file) {
		try {
			final java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(file);
			return image == null ? null : new int[]{image.getWidth(), image.getHeight()};
		} catch (Throwable t) {
			return null;
		}
	}

	private String importToLibrary(String sourcePath) {
		try {
			final java.io.File source = new java.io.File(sourcePath);
			if (!source.isFile()) {
				return sourcePath;
			}
			final int[] size = readImageSize(source);
			if (size == null) {
				pickerError = "图片读不出来（不是支持的图片格式？png/jpg/bmp/gif）";
				return null;
			}
			if (size[0] != size[1] || size[0] > MAX_IMAGE_SIDE) {
				pickerError = "图片不符合要求：需 1:1 正方形、边长 ≤ " + MAX_IMAGE_SIDE + "（这张是 " + size[0] + "×" + size[1] + "）";
				return null;
			}
			final java.io.File dir = PSDCustomText.getLibraryGroupDir();
			final String safeName = source.getName().replaceAll("[^A-Za-z0-9._\\u4e00-\\u9fa5-]", "_");
			final java.io.File target = new java.io.File(dir, safeName);

			if (source.getCanonicalFile().equals(target.getCanonicalFile())) {
				return target.getAbsolutePath();
			}
			java.nio.file.Files.copy(source.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			Init.LOGGER.info("[LCD14] 图片已入库（同名覆盖）：" + target.getAbsolutePath());
			return target.getAbsolutePath();
		} catch (Throwable t) {
			Init.LOGGER.error("[LCD14] 图片入库失败（改用原路径）", t);
			return sourcePath;
		}
	}

	private static String describeThrowable(Throwable t) {
		final StringBuilder sb = new StringBuilder();
		Throwable current = t;
		int depth = 0;
		while (current != null && depth++ < 5) {
			sb.append(current.getClass().getSimpleName());
			final String message = current.getMessage();
			if (message != null && !message.isEmpty()) {
				sb.append('(').append(message).append(')');
			}
			current = current.getCause();
			if (current != null) {
				sb.append(" <- ");
			}
		}
		return sb.toString();
	}

	private static String openAwtFileDialog() throws Exception {
		final String[] result = new String[1];
		java.awt.EventQueue.invokeAndWait(() -> {
			final java.awt.FileDialog dialog = new java.awt.FileDialog((java.awt.Frame)null, "选择图片");
			dialog.setMode(java.awt.FileDialog.LOAD);
			dialog.setMultipleMode(false);
			try {
				dialog.setAlwaysOnTop(true);
			} catch (Throwable ignored) {
			}
			try {
				dialog.setLocationRelativeTo(null);
			} catch (Throwable ignored) {
			}

			dialog.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowOpened(java.awt.event.WindowEvent event) {
					try {
						dialog.toFront();
						dialog.requestFocus();
						dialog.setAlwaysOnTop(true);
					} catch (Throwable ignored) {
					}
				}
			});
			dialog.setVisible(true);
			final String fileName = dialog.getFile();
			if (fileName != null && !fileName.isEmpty()) {
				final String directory = dialog.getDirectory();
				result[0] = (directory == null ? new java.io.File(fileName) : new java.io.File(directory, fileName)).getAbsolutePath();
			}
			dialog.dispose();
		});
		return result[0];
	}

	private static String openSwingFileChooser() throws Exception {
		final String[] result = new String[1];
		javax.swing.SwingUtilities.invokeAndWait(() -> {
			final javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
			chooser.setDialogTitle("选择图片");
			try {
				chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("图片文件 (png/jpg/bmp/gif)", "png", "jpg", "jpeg", "bmp", "gif"));
			} catch (Throwable ignored) {
			}
			if (chooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
				result[0] = chooser.getSelectedFile().getAbsolutePath();
			}
		});
		return result[0];
	}

	@Override
	public void tick() {
		super.tick();
		final String picked = pickedImagePath;
		if (picked != null) {
			pickedImagePath = null;
			currentImagePath = picked;
			pickerError = null;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textField != null && textField.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (textField != null && textField.charTyped(chr, modifiers)) {
			return true;
		}
		return super.charTyped(chr, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 84, 0xFFFFFF);
		final String imageLabel;
		if (currentImagePath == null || currentImagePath.isEmpty()) {
			imageLabel = "图片：无；也可直接把图丢进下面这个文件夹（则无需选图）";
		} else {
			imageLabel = "图片：" + new java.io.File(currentImagePath).getName();
		}
		context.drawCenteredTextWithShadow(textRenderer, Text.literal(imageLabel), width / 2, height / 2 - 70, 0xA0A0A0);

		context.drawCenteredTextWithShadow(textRenderer, Text.literal("图片要求：1:1 正方形（宽=高）、边长 ≤ " + MAX_IMAGE_SIDE + " 像素、png/jpg/bmp/gif"), width / 2, height / 2 - 58, 0x60A0FF);
		context.drawCenteredTextWithShadow(textRenderer, Text.literal("图片库：" + PSDCustomText.getLibraryGroupDir().getAbsolutePath()), width / 2, height / 2 + 22, 0x808080);
		if (pickerError != null) {
			context.drawCenteredTextWithShadow(textRenderer, Text.literal("图片选择器：" + pickerError), width / 2, height / 2 + 34, 0xFF5555);
		}
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
