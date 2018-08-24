//
// This file is a part of the Chunk Stories API codebase
// Check out README.md for more information
// Website: http://chunkstories.xyz
//

package io.xol.chunkstories.api.net.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.xol.chunkstories.api.Location;
import io.xol.chunkstories.api.client.net.ClientPacketsProcessor;
import io.xol.chunkstories.api.entity.Entity;
import io.xol.chunkstories.api.entity.Subscriber;
import io.xol.chunkstories.api.entity.traits.Trait;
import io.xol.chunkstories.api.entity.traits.serializable.TraitSerializable;
import io.xol.chunkstories.api.exceptions.UnknownComponentException;
import io.xol.chunkstories.api.net.PacketDestinator;
import io.xol.chunkstories.api.net.PacketReceptionContext;
import io.xol.chunkstories.api.net.PacketSender;
import io.xol.chunkstories.api.net.PacketSendingContext;
import io.xol.chunkstories.api.net.PacketWorld;
import io.xol.chunkstories.api.player.Player;
import io.xol.chunkstories.api.server.RemotePlayer;
import io.xol.chunkstories.api.world.World;
import io.xol.chunkstories.api.world.WorldMaster;

import javax.annotation.Nullable;

public class PacketEntity extends PacketWorld {
	private Entity entity;
	@Nullable
	private TraitSerializable updateSpecificComponent;

	public PacketEntity(World world) {
		super(world);
	}

	public PacketEntity(Entity entityToUpdate) {
		super(entityToUpdate.getWorld());
		this.entity = entityToUpdate;
	}

	public PacketEntity(Entity entity, @Nullable TraitSerializable component) {
		this(entity);
		this.updateSpecificComponent = component;
	}

	@Override
	public void send(PacketDestinator destinator, DataOutputStream out, PacketSendingContext context) throws IOException {
		long entityUUID = entity.getUUID();
		short entityTypeID = (short) entity.getWorld().getContentTranslator().getIdForEntity(entity);

		boolean hideEntity = entity.getTraitLocation().wasRemoved();
		if (destinator instanceof Subscriber)
			hideEntity |= !entity.getSubscribers().contains(destinator);

		// System.out.println("telling "+destinator+" about "+entity +"
		// (hide:"+hideEntity+", reg="+entity.subscribers.isRegistered(destinator)+")");

		out.writeLong(entityUUID);
		out.writeShort(entityTypeID);

		out.writeBoolean(hideEntity);

		if (!hideEntity) { // don't push components when all we want is to hide the entity from the
							// player's view
			if (updateSpecificComponent == null) {
				// No specific component specified ? Update all of them.

				// can't use shorter method because of exceptions handling >:(
				// entity.components.all().forEach(c -> c.pushComponentInStream(destinator,
				// out));
				for (Trait trait : entity.getTraits().all()) {
					if (trait instanceof TraitSerializable) {
						((TraitSerializable) trait).pushComponentInStream(destinator, out);
					}
				}

			} else {
				updateSpecificComponent.pushComponentInStream(destinator, out);
			}
		}

		// Write a -1 to mark the end of the components updates
		out.writeInt(-1);
	}

	public void process(PacketSender sender, DataInputStream in, PacketReceptionContext processor) throws IOException, UnknownComponentException {
		long entityUUID = in.readLong();
		short entityTypeID = in.readShort();

		boolean hideEntity = in.readBoolean();

		if (entityTypeID == -1)
			return;

		World world = processor.getWorld();
		if (world == null)
			return;

		// System.out.println("received packet entity");
		Entity entity = world.getEntityByUUID(entityUUID);

		boolean addToWorld = false;
		// TODO this should be done explicitely by dedicated packet/packet flags
		// Create an entity if the servers tells you to do so
		if (entity == null) {
			if (world instanceof WorldMaster && sender instanceof RemotePlayer) {
				((Player) sender).sendMessage("You are sending packets to the server about a removed entity. Ignoring those.");
				return;
			} else if (!hideEntity) {
				entity = processor.getWorld().getContentTranslator().getEntityForId(entityTypeID).create(new Location(world, 0, 0, 0)); // This is technically

				entity.setUUID(entityUUID);

				addToWorld = true;
			}
		}

		int componentId = in.readInt();
		// Loop throught all components
		while (componentId >= 0) {
			Trait trait = entity.getTraits().byId()[componentId];
			if (trait instanceof TraitSerializable) {
				((TraitSerializable) trait).tryPull(sender, in);
			}
			componentId = in.readInt();
		}

		// Add to world if it was missing and we didn't receive the despawn flag
		if (addToWorld && !hideEntity) {
			// Only the WorldMaster is allowed to spawn new entities in the world
			if (processor instanceof ClientPacketsProcessor)
				processor.getWorld().addEntity(entity);
		}

		if (hideEntity && entity != null) {
			world.removeEntity(entity);
		}
	}
}
