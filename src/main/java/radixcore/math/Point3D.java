/*******************************************************************************
 * Point3D.java
 * Copyright (c) 2014 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package radixcore.math;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import radixcore.modules.RadixMath;

/**
 * Used to store a group of 3D coordinates and easily move them around.
 */
public final class Point3D implements Comparable
{
	public static final Point3D ZERO = new Point3D(0, 0, 0);
	public static final Point3D NONE = new Point3D(true);
	
	private int iPosX;
	private int iPosY;
	private int iPosZ;

	private float fPosX;
	private float fPosY;
	private float fPosZ;

	private double dPosX;
	private double dPosY;
	private double dPosZ;

	private boolean noneFlag;
	
	public Point3D(int posX, int posY, int posZ)
	{
		this((double)posX, (double)posY, (double)posZ);
	}

	public Point3D(float posX, float posY, float posZ)
	{
		this((double)posX, (double)posY, (double)posZ);
	}

	public Point3D(double posX, double posY, double posZ)
	{
		iPosX = (int) posX;
		iPosY = (int) posY;
		iPosZ = (int) posZ;

		fPosX = (float) posX;
		fPosY = (float) posY;
		fPosZ = (float) posZ;

		dPosX = posX;
		dPosY = posY;
		dPosZ = posZ;
	}
	
	private Point3D(boolean noneFlag)
	{
		this(0.0D, 0.0D, 0.0D);
		this.noneFlag = noneFlag;
	}
	
	public void set(int posX, int posY, int posZ)
	{
		fallthroughSet((double)posX, (double)posY, (double)posZ);
	}

	public void set(float posX, float posY, float posZ)
	{
		fallthroughSet((double)posX, (double)posY, (double)posZ);
	}
	
	public void set(double posX, double posY, double posZ)
	{
		fallthroughSet(posX, posY, posZ);
	}
	
	private void fallthroughSet(double posX, double posY, double posZ)
	{
		iPosX = (int) posX;
		iPosY = (int) posY;
		iPosZ = (int) posZ;

		fPosX = (float) posX;
		fPosY = (float) posY;
		fPosZ = (float) posZ;

		dPosX = posX;
		dPosY = posY;
		dPosZ = posZ;
	}
	
	public Point3D setNew(int posX, int posY, int posZ)
	{
		return new Point3D(posX, posY, posZ);
	}

	public Point3D setNew(float posX, float posY, float posZ)
	{
		return new Point3D(posX, posY, posZ);
	}
	
	public Point3D setNew(double posX, double posY, double posZ)
	{
		return new Point3D(posX, posY, posZ);
	}
	
	public double dX()
	{
		return dPosX;
	}
	
	public double dY()
	{
		return dPosY;
	}
	
	public double dZ()
	{
		return dPosZ;
	}
	
	public float fX()
	{
		return fPosX;
	}
	
	public float fY()
	{
		return fPosY;
	}
	
	public float fZ()
	{
		return fPosZ;
	}
	
	public int iX()
	{
		return iPosX;
	}
	
	public int iY()
	{
		return iPosY;
	}
	
	public int iZ()
	{
		return iPosZ;
	}
	
	public boolean isNone()
	{
		return noneFlag;
	}
	
	public static Point3D fromBlockPos(BlockPos pos)
	{
		return new Point3D(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static Point3D fromEntityPosition(Entity entity)
	{
		return new Point3D(entity.posX, entity.posY, entity.posZ);
	}
	
	public static Point3D getNearestPointInList(Point3D refPoint, List<Point3D> pointList)
	{
		Point3D returnPoint = null;
		double lastDistance = Double.MAX_VALUE;
		
		for (Point3D point : pointList)
		{
			double distanceTo = RadixMath.getDistanceToXYZ(refPoint.iPosX, refPoint.iPosY, refPoint.iPosZ, point.iPosX, point.iPosY, point.iPosZ);
			
			if (distanceTo < lastDistance)
			{
				returnPoint = point;
				lastDistance = distanceTo;
			}
		}
		
		return returnPoint;
	}
	
	public static Point3D getFurthestPointInList(Point3D refPoint, List<Point3D> pointList)
	{
		Point3D returnPoint = null;
		double lastDistance = 0.0D;
		
		for (Point3D point : pointList)
		{
			double distanceTo = RadixMath.getDistanceToXYZ(refPoint.iPosX, refPoint.iPosY, refPoint.iPosZ, point.iPosX, point.iPosY, point.iPosZ);
			
			if (distanceTo > lastDistance)
			{
				returnPoint = point;
				lastDistance = distanceTo;
			}
		}
		
		return returnPoint;
	}
	
	public void writeToNBT(String name, NBTTagCompound nbt)
	{
		nbt.setDouble(name + "dPosX", dPosX);
		nbt.setDouble(name + "dPosY", dPosY);
		nbt.setDouble(name + "dPosZ", dPosZ);
	}
	
	public static Point3D readFromNBT(String name, NBTTagCompound nbt)
	{
		double x = nbt.getDouble(name + "dPosX");
		double y = nbt.getDouble(name + "dPosY");
		double z = nbt.getDouble(name + "dPosZ");
		
		if (x == 0 && y == 0 && z == 0)
		{
			return ZERO;
		}
		
		else
		{
			return new Point3D(x, y, z);
		}
	}
	
	/**
	 * Gets string representation of the Coordinates object.
	 * 
	 * @return "x, y, z" as string representation of the coordinates stored in this object.
	 */
	@Override
	public String toString()
	{
		return dPosX + ", " + dPosY + ", " + dPosZ;
	}
	
	public BlockPos toBlockPos()
	{
		return new BlockPos(iPosX, iPosY, iPosZ);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Point3D)
		{
			final Point3D point = (Point3D)obj;
			return point.dPosX == this.dPosX && point.dPosY == this.dPosY && point.dPosZ == this.dPosZ;
		}
		
		return false;
	}

	@Override
	public int compareTo(Object obj) 
	{
		Point3D point = (Point3D)obj;
		
		if (this.iPosY > point.iPosY)
		{
			return 1;
		}
		
		else if (this.iPosY == point.iPosY)
		{
			if (this.iPosX > point.iPosX)
			{
				return 1;
			}
			
			else if (this.iPosX == point.iPosX)
			{
				if (this.iPosZ > point.iPosZ)
				{
					return 1;
				}
				
				else if (this.iPosZ == point.iPosZ)
				{
					return 0;
				}
				
				else
				{
					return -1;
				}
			}
			
			else
			{
				return -1;
			}
		}
		
		else
		{
			return -1;
		}
	}
}
