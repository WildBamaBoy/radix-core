/*******************************************************************************
 * Point3D.java
 * Copyright (c) 2014 Radix-Shock Entertainment.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.radixshock.radixcore.logic;

/**
 * Used to store a group of 3D coordinates and easily move them around.
 */
public class Point3D
{
	/** The x coordinate value. */
    public double posX;

    /** The y coordinate value. */
    public double posY;
    
    /** The z coordiante value. */
    public double posZ;

    /**
     * Constructor
     * 
     * @param	posX	The x coordinate value.
     * @param	posY	The y coordinate value.
     * @param	posZ	The z coordinate value.
     */
    public Point3D(double posX, double posY, double posZ)
    {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }
    
    /**
     * Gets string representation of the Coordinates object.
     * 
     * @return	"x, y, z" as string representation of the coordinates stored in this object.
     */
    public String toString()
    {
    	return posX + ", " + posY + ", " + posZ;
    }
}
