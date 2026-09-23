package com.mtrpsdlcd.packet;

import com.mtrpsdlcd.block.PSDCustomText;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public final class PacketCustomText extends PacketHandler {
	private final int x;
	private final int y;
	private final int z;
	private final String text;
	private final String imagePath;

	public PacketCustomText(PacketBufferReceiver receiver) {
		this(receiver.readInt(), receiver.readInt(), receiver.readInt(), receiver.readString(), receiver.readString());
	}

	public PacketCustomText(BlockPos pos, String text, String imagePath) {
		this(pos.getX(), pos.getY(), pos.getZ(), text, imagePath);
	}

	private PacketCustomText(int x, int y, int z, String text, String imagePath) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.text = text == null ? "" : text;
		this.imagePath = imagePath == null ? "" : imagePath;
	}

	@Override
	public void write(PacketBufferSender sender) {
		sender.writeInt(x);
		sender.writeInt(y);
		sender.writeInt(z);
		sender.writeString(text);
		sender.writeString(imagePath);
	}

	@Override
	public void runServer(MinecraftServer server, ServerPlayerEntity player) {
		final World world = World.cast(player.getServerWorld());
		final BlockPos topPos = new BlockPos(x, y, z);
		PSDCustomText.apply(world, topPos, text, imagePath);

		com.mtrpsdlcd.registry.ModRegistry.sendPacketToClient(player, new PacketCustomText(topPos, text, imagePath));
	}
}
