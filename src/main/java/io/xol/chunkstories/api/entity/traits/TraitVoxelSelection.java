package io.xol.chunkstories.api.entity.traits;

import io.xol.chunkstories.api.Location;
import io.xol.chunkstories.api.entity.Entity;

/** Any entity with this trait is able to select blocks */
public abstract class TraitVoxelSelection extends Trait {

	public TraitVoxelSelection(Entity entity) {
		super(entity);
	}

	/** 
	 * Performs a raytrace and returns a location or null if out of bounds
	 * @param inside Do we return the position where we collide or do we return the empty space next to the face we hit ?
	 * @param overwriteInstaDestructibleBlocks Would we return a non-air cell provided the voxel in that cell is instantaneously destructible/replaceable ?
	 * @return
	 */
	public abstract Location getBlockLookingAt(boolean inside, boolean overwriteInstaDestructibleBlocks);
}
