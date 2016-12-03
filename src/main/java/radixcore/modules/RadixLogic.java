package radixcore.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import radixcore.math.Point3D;

/**
 * Contains various methods normally used within AI operations, mostly pertaining to the surrounding area
 * within a certain point in a world. Includes ability to get lists of entities, nearest and farthest blocks,
 * lists of blocks, etc.
 */
public final class RadixLogic
{
	private RadixLogic()
	{
	}
	
	/**
	 * Gets an entity of the specified type located at the XYZ coordinates in the specified world.
	 * If an entity isn't at those exact coordinates, an area of 5 blocks around that point is scanned
	 * for the entity.
	 * 
	 * @param type The type of entity to get.
	 * @param world The world the entity is in.
	 * @param x The X position of the entity.
	 * @param y The Y position of the entity.
	 * @param z The Z position of the entity.
	 * @return The entity located at the specified XYZ coordinates. Null if one was not found.
	 */
	public static <E extends Entity> E getEntityOfTypeAtXYZ(Class<E> type, World world, int x, int y, int z)
	{
		// Scan all loaded entities to see if one is at the provided coordinates.
		for (Entity entity : world.loadedEntityList)
		{
			if (type.isInstance(type))
			{
				final int posX = (int) entity.posX;
				final int posY = (int) entity.posY;
				final int posZ = (int) entity.posZ;

				if (x == posX && y == posY && z == posZ)
				{
					return (E) entity;
				}
			}
		}

		// If the above fails, search for and return the nearest entity to the point that was clicked.
		Entity nearestEntity = null;

		for (final Object obj : getEntitiesWithinDistance(type, world, x, y, z, 5))
		{
			if (type.isInstance(obj))
			{
				if (nearestEntity == null)
				{
					nearestEntity = (Entity) obj;
				}

				else
				{
					final Entity otherEntity = (Entity) obj;

					final double nearestEntityDistance = RadixMath.getDistanceToXYZ(nearestEntity.posX, nearestEntity.posY, nearestEntity.posZ, x, y, z);
					final double nearestCandidateDistance =  RadixMath.getDistanceToXYZ(otherEntity.posX, otherEntity.posY, otherEntity.posZ, x, y, z);

					// In the very rare occurrence that either distance is
					// exactly 1.0, that entity is perfectly
					// in between four blocks, and is most likely the reason
					// that this code is running in the first place.
					if (nearestEntityDistance == 1.0)
					{
						return (E) nearestEntity;
					}

					else if (nearestCandidateDistance == 1.0)
					{
						return (E) otherEntity;
					}

					else if (nearestCandidateDistance < nearestEntityDistance)
					{
						nearestEntity = otherEntity;
					}
				}
			}
		}

		return (E) nearestEntity;
	}

	/**
	 * @see #getEntityOfTypeAtXYZ(Class, World, int, int, int)
	 */
	public static <E extends Entity> E getEntityOfTypeAtXYZ(Class<E> type, World world, Point3D point)
	{
		return getEntityOfTypeAtXYZ(type, world, point.iX(), point.iY(), point.iZ());
	}

	/**
	 * Returns the closest entity at a given point in the world, within a distance. If a class type is provided,
	 * only entities of that type will be considered and returned. If an entity is provided to be excluded,
	 * that specific entity instance will not be returned if it meets the search criteria.
	 * 
	 * @param point 			The point around which a bounding box will be created to encapsulate entities for consideration.
	 * @param world 			The world in which the entity will be searched for.
	 * @param range 			The maximum distance away from the provided point to search for an entity.
	 * @param type 				Null accepted. Limits returned entities to those of the provided type or those that are subclasses of this type. All entities are considered if this is null.
	 * @param entityExcluded 	Null accepted. 
	 * 
	 * @return Entity of <b>type</b> if type is provided. <b>Entity</b> if not. <b>Null</b> if no entity is found.
	 */
	public static <E extends Entity> E getClosestEntity(Point3D point, World world, int range, @Nullable Class<E> type, @Nullable Entity entityExcluded)
	{
		final List<Entity> validEntities = new ArrayList();
		final double posX = point.dX();
		final double posY = point.dY();
		final double posZ = point.dZ();
		final List<Entity> entitiesAroundMe = world.getEntitiesWithinAABBExcludingEntity(entityExcluded, 
				new AxisAlignedBB(
						posX - range, posY - range, posZ - range, 
						posX + range, posY + range, posZ + range));
		
		int indexToReturn = -1;
		double lastMinDistance = 100.0D;

		if (type == null)
		{
			type = (Class<E>) Entity.class;
		}

		// Search for entities of the provided type
		for (final Entity entityNearMe : entitiesAroundMe)
		{
			if (type.isInstance(entityNearMe))
			{
				validEntities.add(entityNearMe);
			}
		}

		// Find the closest entity to the given point
		for (int i = 0; i < validEntities.size(); i++)
		{
			Entity entity = validEntities.get(i);
			double distance = RadixMath.getDistanceToXYZ(point.dX(), point.dY(), point.dZ(), entity.posX, entity.posY, entity.posZ);

			if (distance < lastMinDistance)
			{
				lastMinDistance = distance;
				indexToReturn = i;
			}
		}

		if (indexToReturn == -1)
		{
			return null;
		}
		
		else
		{
			return (E)validEntities.get(indexToReturn);
		}
	}

	/**
	 * Excludes <b>entityOrigin</b> from return value
	 * @see #getClosestEntity(Point3D, World, int, Class, Entity)
	 */
	public static <E extends Entity> E getClosestEntityExclusive(Entity entityOrigin, int range, @Nullable Class<E> type)
	{
		return getClosestEntity(Point3D.fromEntityPosition(entityOrigin), entityOrigin.worldObj, range, type, entityOrigin);
	}

	/**
	 * Includes <b>entityOrigin</b> with return value
	 * @see #getClosestEntity(Point3D, World, int, Class, Entity)
	 */
	public static <E extends Entity> E getClosestEntityInclusive(Entity entityOrigin, int range, @Nullable Class<E> type)
	{
		return getClosestEntity(Point3D.fromEntityPosition(entityOrigin), entityOrigin.worldObj, range, type, null);
	}

	/**
	 * Gets the point of the closest block of a given type to the provided origin in a world.
	 * 
	 * @param origin			The point around which to search for the block.
	 * @param world				The world in which to search. 
	 * @param horizontalRange	The horizontal range of the search area from the origin.
	 * @param verticalRange		The vertical range of the search area from the origin.
	 * @param blockType			The type of block to retrieve.
	 * 
	 * @return Point3D object containing the location of the closest block requested. Point3D with noneFlag set if none found.
	 */
	public static Point3D getNearestBlock(Point3D origin, World world, int horizontalRange, int verticalRange, Block blockType)
	{
		Point3D returnPoint = Point3D.NONE;
		double closest = 100.0D;

		for (Point3D point : getNearbyBlocks(origin, world, blockType, horizontalRange, verticalRange))
		{
			double distance = RadixMath.getDistanceToXYZ(origin.dX(), origin.dY(), origin.dZ(), point.dX(), point.dY(), point.dZ());

			if (distance < closest)
			{
				closest = distance;
				returnPoint = point;
			}
		}

		return returnPoint;
	}
	
	/**
	 * Gets the nearest block of type around the provided entity, using a horizontal and vertical range.
	 * 
	 * @see #getNearestBlock(Point3D, World, int, int, Block)
	 */
	public static Point3D getNearestBlock(Entity entity, int horizontalRange, int verticalRange, Block blockType)
	{
		return getNearestBlock(Point3D.fromEntityPosition(entity), entity.worldObj, horizontalRange, verticalRange, blockType);
	}

	/**
	 * Gets the nearest block of type around the provided entity, using a horizontal range and assuming a 3 block vertical range.
	 * 
	 * @see #getNearestBlock(Point3D, World, int, int, Block)
	 */
	public static Point3D getNearestBlock(Entity entity, int horizontalRange, Block blockType)
	{
		return getNearestBlock(Point3D.fromEntityPosition(entity), entity.worldObj, horizontalRange, 3, blockType);
	}
	
	/**
	 * Gets the point of the farthest block of a given type from the provided origin in a world.
	 * 
	 * @param origin			The point around which to search for the block.
	 * @param world				The world in which to search. 
	 * @param horizontalRange	The horizontal range of the search area from the origin.
	 * @param verticalRange		The vertical range of the search area from the origin.
	 * @param blockType			The type of block to retrieve.
	 * 
	 * @return Point3D object containing the location of the closest block requested. Point3D with noneFlag set if none found.
	 */
	public static Point3D getFarthestBlock(Point3D origin, World world, int horizontalRange, int verticalRange, Block blockType)
	{
		Point3D returnPoint = Point3D.NONE;
		double farthest = 0.0D;

		for (Point3D point : getNearbyBlocks(origin, world, blockType, horizontalRange, verticalRange))
		{
			double distance = RadixMath.getDistanceToXYZ(origin.dX(), origin.dY(), origin.dZ(), point.dX(), point.dY(), point.dZ());

			if (distance > farthest)
			{
				farthest = distance;
				returnPoint = point;
			}
		}

		return returnPoint;
	}

	/**
	 * Gets the farthest block of type around the provided entity, using a horizontal and vertical range.
	 * 
	 * @see #getFarthestBlock(Point3D, World, int, int, Block)
	 */
	public static Point3D getFarthestBlock(Entity entity, int horizontalRange, int verticalRange, Block blockType)
	{
		return getFarthestBlock(Point3D.fromEntityPosition(entity), entity.worldObj, horizontalRange, verticalRange, blockType);
	}

	/**
	 * Gets the nearest block of type around the provided entity, using a horizontal range and assuming a 3 block vertical range.
	 * 
	 * @see #getFarthestBlock(Point3D, World, int, int, Block)
	 */
	public static Point3D getFarthestBlock(Entity entity, int horizontalRange, Block blockType)
	{
		return getFarthestBlock(Point3D.fromEntityPosition(entity), entity.worldObj, horizontalRange, 3, blockType);
	}
	
	/**
	 * Gets the blocks of a provided type around an origin in a world.
	 * 
	 * @param origin			The point around which to search for the block.
	 * @param world				The world in which to search. 
	 * @param filter			The type of block desired, <b>null</b> if all blocks should be included.
	 * @param horizontalRange	The horizontal range of the search area from the origin.
	 * @param verticalRange		The vertical range of the search area from the origin.
	 * 
	 * @return 	List of Point3D objects representing the position of the provided <b>filter</b> block.
	 * 			If no filter, the position of all blocks in the range will be returned.
	 */
	public static List<Point3D> getNearbyBlocks(Point3D origin, World world, @Nullable Block filter, int horizontalRange, int verticalRange)
	{
		final int x = origin.iX();
		final int y = origin.iY();
		final int z = origin.iZ();
	
		int xMov = 0 - horizontalRange;
		int yMov = verticalRange;
		int zMov = 0 - horizontalRange;
	
		final List<Point3D> pointsList = new ArrayList<Point3D>();
	
		while (true)
		{
			final Block currentBlock = RadixBlocks.getBlock(world, x + xMov, y + yMov, z + zMov);
	
			if (filter == null || (filter != null && currentBlock == filter))
			{
				pointsList.add(new Point3D(x + xMov, y + yMov, z + zMov));
			}
			
			if (zMov == horizontalRange && xMov == horizontalRange && yMov == verticalRange * -1)
			{
				break;
			}
	
			if (zMov == horizontalRange && xMov == horizontalRange)
			{
				yMov--;
				xMov = 0 - horizontalRange;
				zMov = 0 - horizontalRange;
				continue;
			}
	
			if (xMov == horizontalRange)
			{
				zMov++;
				xMov = 0 - horizontalRange;
				continue;
			}
	
			xMov++;
		}
	
		return pointsList;
	}

	/**
	 * Gets nearby blocks using an entity as the origin, with a horizontal range and assumed vertical range of 3.
	 * 
	 * @see #getNearbyBlocks(Point3D, World, Block, int, int)
	 */
	public static List<Point3D> getNearbyBlocks(Entity entity, @Nullable Block filter, int verticalRange)
	{
		return getNearbyBlocks(Point3D.fromEntityPosition(entity), entity.worldObj, filter, verticalRange, 3);
	}
	
	/**
	 * Gets nearby blocks using an entity as the origin, with a horizontal range and vertical range.
	 * 
	 * @see #getNearbyBlocks(Point3D, World, Block, int, int)
	 */
	public static List<Point3D> getNearbyBlocks(Entity entity, @Nullable Block filter, int horizontalRange, int verticalRange)
	{
		return getNearbyBlocks(Point3D.fromEntityPosition(entity), entity.worldObj, filter, horizontalRange, verticalRange);
	}
	
	/**
	 * Gets nearby blocks using a point as the origin, with a horizontal range and assumed vertical range of 3.
	 * 
	 * @see #getNearbyBlocks(Point3D, World, Block, int, int)
	 */
	public static List<Point3D> getNearbyBlocks(Point3D origin, World world, @Nullable Block filter, int horizontalRange)
	{
		return getNearbyBlocks(origin, world, filter, horizontalRange, 3);
	}
	
	/**
	 * Gets a list of entities found within the given coordinates.
	 * 
	 * @param filter			The types of entities that should be in the list.
	 * @param world				The world in which to locate entities.
	 * @param posX				X coordinate.
	 * @param posY				Y coordinate.
	 * @param posZ				Z coordinate.
	 * @param maxDistanceAway	Maximum size of the cube searched when grabbing entities.
	 * 
	 * @return A list containing entities of type <b>filter</b>
	 */
	public static <T extends Entity> List<T> getEntitiesWithinDistance(Class<T> filter, World world, double posX, double posY, double posZ, int maxDistanceAway)
	{
		return (List<T>) world.getEntitiesWithinAABB(filter, new AxisAlignedBB(posX - maxDistanceAway, posY - maxDistanceAway, posZ - maxDistanceAway, posX + maxDistanceAway, posY + maxDistanceAway, posZ + maxDistanceAway));
	}

	/**
	 * @see #getEntitiesWithinDistance(Class, World, double, double, double, int)
	 */
	public static <T extends Entity> List<T> getEntitiesWithinDistance(Class<T> filter, Entity origin, int maxDistanceAway)
	{
		return getEntitiesWithinDistance(filter, origin.worldObj, Point3D.fromEntityPosition(origin), maxDistanceAway);
	}

	/**
	 * @see #getEntitiesWithinDistance(Class, World, double, double, double, int)
	 */
	public static <T extends Entity> List<T> getEntitiesWithinDistance(Class<T> filter, World world, Point3D origin, int maxDistanceAway)
	{
		return getEntitiesWithinDistance(filter, world, origin.dX(), origin.dY(), origin.dZ(), maxDistanceAway);
	}

	/**
	 * Finds a y position given an x,y,z coordinate triple that is assumed to be the world's "ground". 
	 * 
	 * @param worldObj	The world in which blocks will be tested
	 * @param x			X coordinate
	 * @param y			Y coordinate, used as the starting height for finding ground.
	 * @param z			Z coordinate
	 * @return Integer representing the air block above the first non-air block given the provided ordered triples. 
	 */
	public static int getSpawnSafeTopLevel(World worldObj, int x, int y, int z)
	{
		Block block = Blocks.AIR;

		while (block == Blocks.AIR && y > 0)
		{
			y--;
			block = RadixBlocks.getBlock(worldObj, x, y, z);
		}

		return y + 1;
	}
	
	/**
	 * Assumes starting height of 256.
	 * 
	 * @see #getSpawnSafeTopLevel(World, int, int, int)
	 */
	public static int getSpawnSafeTopLevel(World worldObj, int x, int z) 
	{
		return getSpawnSafeTopLevel(worldObj, x, 256, z);
	}

	/**
	 * Generates true or false given a probability of true being generated.
	 * 
	 * @param probabilityOfTrue Integer in range [0 - 100]. Values beyond this range are clamped to be within the range. 
	 * 
	 * @return Boolean result of probability
	 */
	public static boolean getBooleanWithProbability(int probabilityOfTrue)
	{
		probabilityOfTrue = MathHelper.clamp_int(probabilityOfTrue, 0, 100);
		
		if (probabilityOfTrue <= 0)
		{
			return false;
		}
	
		else
		{
			return new Random().nextInt(100) + 1 <= probabilityOfTrue;
		}
	}
}
