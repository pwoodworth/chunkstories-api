package io.xol.chunkstories.api.voxel.models;

import io.xol.chunkstories.api.voxel.VoxelSides.Corners;
import io.xol.chunkstories.api.voxel.models.ChunkRenderer.ChunkRenderContext.VoxelLighter;

//(c) 2015-2017 XolioWare Interactive
//http://chunkstories.xyz
//http://xol.io

import io.xol.chunkstories.api.voxel.textures.VoxelTexture;

/**
 * Provides you with means to draw geometry in a buffer, which is then laid out in a specific format by *LayoutBaker in the
 * {@link io.xol.chunkstories.api.voxel.models.layout} package.
 * This class groups methods common to both VoxelBakerCubic and VoxelBakerHighpoly
 */
public interface VoxelBakerCommon
{
	//public void beginVertex(coordinates) // <-- Implemented in subclasses
	
	/** Provides the 4-bit sun and voxel light levels, as well as an "ao" term. */
	public void setVoxelLight(byte sunLight, byte blockLight, byte ao);

	/** Automatically obtains those values from a specific corner */
	public void setVoxelLightAuto(VoxelLighter voxelLighter, Corners corner);
	
	/** Selects a specific texture */
	public void usingTexture(VoxelTexture voxelTexture);
	
	/** Texture coordinates WITHIN the specified VoxelTexture ( atlas/array texture stuff is handled internally ) */
	public void setTextureCoordinates(float s, float t);
	
	/** Defines the normal for this vertex */
	public void setNormal(float x, float y, float z);
	
	/** Enables/disable the wavy grass effect */
	public void setWavyFlag(boolean wavy);
	
	/** Emmits the vertex based on the providen data */
	public abstract void endVertex();
	
	/** Reset any previously modified value/flag to it's default. */
	public void reset();
}
