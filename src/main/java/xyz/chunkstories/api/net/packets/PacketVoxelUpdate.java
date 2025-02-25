//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package xyz.chunkstories.api.net.packets;

import xyz.chunkstories.api.client.net.ClientPacketsProcessor;
import xyz.chunkstories.api.exceptions.world.WorldException;
import xyz.chunkstories.api.net.PacketDestinator;
import xyz.chunkstories.api.net.PacketReceptionContext;
import xyz.chunkstories.api.voxel.Voxel;
import xyz.chunkstories.api.voxel.VoxelFormat;
import xyz.chunkstories.api.voxel.components.VoxelComponent;
import xyz.chunkstories.api.world.World;
import xyz.chunkstories.api.world.chunk.ChunkCell;
import xyz.chunkstories.api.net.PacketSender;
import xyz.chunkstories.api.net.PacketSendingContext;
import xyz.chunkstories.api.net.PacketWorld;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map.Entry;

/** Describes a voxel change, including to the VoxelComponents */
public class PacketVoxelUpdate extends PacketWorld {
	public PacketVoxelUpdate(World world) {
		super(world);
	}

	public PacketVoxelUpdate(ChunkCell context) {
		super(context.getWorld());
		this.context = context;
	}

	public PacketVoxelUpdate(ChunkCell context, @Nullable VoxelComponent componentToUpdate) {
		super(context.getWorld());
		this.context = context;
		this.componentToUpdate = componentToUpdate;
	}

	private ChunkCell context;

	@Nullable
	private VoxelComponent componentToUpdate;

	@SuppressWarnings("deprecation")
	@Override
	public void send(PacketDestinator destinator, DataOutputStream out, PacketSendingContext sendingContext) throws IOException {
		out.writeInt(context.getX());
		out.writeInt(context.getY());
		out.writeInt(context.getZ());
		out.writeInt(context.getData());

		if (componentToUpdate == null) {
			for (Entry<String, VoxelComponent> entry : context.getComponents().getAllVoxelComponents()) {
				out.writeByte((byte) 0x01);
				out.writeUTF(entry.getKey());
				entry.getValue().push(destinator, out);
			}
		} else {
			out.writeByte((byte) 0x01);
			out.writeUTF(componentToUpdate.getName());
			componentToUpdate.push(destinator, out);
		}

		// No further information
		out.writeByte((byte) 0x00);
	}

	public void process(PacketSender sender, DataInputStream in, PacketReceptionContext processor) throws IOException {
		if (processor instanceof ClientPacketsProcessor) {
			ClientPacketsProcessor cpp = (ClientPacketsProcessor) processor;

			int x = in.readInt();
			int y = in.readInt();
			int z = in.readInt();
			int data = in.readInt();

			Voxel voxel = world.getContentTranslator().getVoxelForId(VoxelFormat.id(data));

			byte nextComponent = in.readByte();

			try {
				ChunkCell context = cpp.getWorld().getChunkWorldCoordinates(x, y, z).poke(x, y, z, voxel, VoxelFormat.sunlight(data),
						VoxelFormat.blocklight(data), VoxelFormat.meta(data), null);

				while (nextComponent != 0) {
					String componentName = in.readUTF();
					context.getComponents().getVoxelComponent(componentName).pull(sender, in);
					nextComponent = in.readByte();
				}

			} catch (WorldException e) {
				// Maybe the world wasn't ready ?
				// Edge case: what happens we receive an update for a chunk we haven't received
				// the data yet ?
				// The best option would be to delay the process but this is complicated for a
				// rare instance
				// Maybe one day
			}
		} else {
			// Fail hard
		}
	}
}
