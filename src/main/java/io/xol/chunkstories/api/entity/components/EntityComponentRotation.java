package io.xol.chunkstories.api.entity.components;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.xol.chunkstories.api.entity.Entity;
import io.xol.chunkstories.api.math.vector.dp.Vector3dm;
import io.xol.chunkstories.api.math.vector.sp.Vector2fm;
import io.xol.chunkstories.api.serialization.StreamSource;
import io.xol.chunkstories.api.serialization.StreamTarget;
import io.xol.chunkstories.api.world.WorldMaster;

//(c) 2015-2017 XolioWare Interactive
//http://chunkstories.xyz
//http://xol.io

public class EntityComponentRotation extends EntityComponent
{
	private float rotationHorizontal = 0f;
	private float rotationVertical = 0f;
	
	private Vector2fm rotationImpulse = new Vector2fm();
	
	public EntityComponentRotation(Entity entity, EntityComponent previous)
	{
		super(entity, previous);
	}

	public float getHorizontalRotation()
	{
		return rotationHorizontal;
	}
	
	public float getVerticalRotation()
	{
		return rotationVertical;
	}

	/**
	 * @return A vector3d for the direction
	 */
	public Vector3dm getDirectionLookingAt()
	{
		Vector3dm direction = new Vector3dm();

		double a = ((-getHorizontalRotation()) / 360f * 2 * Math.PI);
		double b = ((getVerticalRotation()) / 360f * 2 * Math.PI);
		direction.setX(-Math.sin(a) * Math.cos(b));
		direction.setY(-Math.sin(b));
		direction.setZ(-Math.cos(a) * Math.cos(b));

		return direction.normalize();
	}
	
	public void setRotation(double horizontalAngle, double verticalAngle)
	{
		this.rotationHorizontal = (float)(360 + horizontalAngle) % 360;
		this.rotationVertical = (float)verticalAngle;
		
		if (rotationVertical > 90)
			rotationVertical = 90;
		if (rotationVertical < -90)
			rotationVertical = -90;
		
		this.pushComponentEveryone();
	}

	public void addRotation(double d, double e)
	{
		setRotation(rotationHorizontal + d, rotationVertical + e);
	}
	
	@Override
	protected void push(StreamTarget destinator, DataOutputStream dos) throws IOException
	{
		dos.writeFloat(rotationHorizontal);
		dos.writeFloat(rotationVertical);
	}

	@Override
	protected void pull(StreamSource from, DataInputStream dis) throws IOException
	{
		rotationHorizontal = dis.readFloat();
		rotationVertical = dis.readFloat();

		//Position updates received by the server should be told to everyone but the controller
		if(entity.getWorld() instanceof WorldMaster)
			this.pushComponentEveryoneButController();
	}

	/**
	 * Sends the view flying about
	 */
	public void applyInpulse(double inpulseHorizontal, double inpulseVertical)
	{
		rotationImpulse.add(new Vector2fm((float)inpulseHorizontal, (float)inpulseVertical));
	}
	
	/**
	 * Reduces the acceleration and returns it
	 */
	public Vector2fm tickInpulse()
	{
		rotationImpulse.scale(0.50f);
		if(rotationImpulse.length() < 0.05)
			rotationImpulse.set(0.0f, 0.0f);
		return rotationImpulse;
	}

}
