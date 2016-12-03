package radixcore.modules;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import radixcore.math.Point3D;

/**
 *	Provides less verbose methods for getting, setting, and comparing blocks and block states in the world.
 */
public final class RadixBlocks 
{
	/**
	 * Sets the position provided to contain the provided block state.
	 * 
	 * @param 	world	The world to modify
	 * @param 	pos		The position in the world
	 * @param 	state	The state that this position should be set to
	 */
	public static void setBlock(World world, BlockPos pos, IBlockState state)
	{
		world.setBlockState(pos, state);
	}
	
	/**
	 * Sets the given block at point 3D with its default state.
	 * 
	 * @see #setBlock(World, int, int, int, IBlockState)
	 */
	public static void setBlock(World world, Point3D point, Block block)
	{
		setBlock(world, point.toBlockPos(), block.getDefaultState());
	}
	
	/**
	 * Sets the given block at x, y, z with its default state.
	 * 
	 * @see #setBlock(World, int, int, int, IBlockState)
	 */
	public static void setBlock(World world, int posX, int posY, int posZ, Block block)
	{
		setBlock(world, new BlockPos(posX, posY, posZ), block.getDefaultState());
	}
	
	/**
	 * Sets the given block at block position with its default state.
	 * 
	 * @see #setBlock(World, int, int, int, IBlockState)
	 */
	public static void setBlock(World world, BlockPos pos, Block block)
	{
		setBlock(world, pos, block.getDefaultState());
	}
	
	/**
	 * Sets the given block at block position with the provided property and property value
	 * 
	 * @see #setBlock(World, int, int, int, IBlockState)
	 */
	public static <T extends Comparable<T>> void setBlock(World world, BlockPos pos, Block block, IProperty<T> property, T value)
	{
		setBlock(world, pos, block.getDefaultState().withProperty(property, value));
	}
	
	/**
	 * Sets the given block at point 3D with the provided property and property value
	 * 
	 * @see #setBlock(World, int, int, int, IBlockState)
	 */
	public static <T extends Comparable<T>> void setBlock(World world, Point3D point, Block block, IProperty<T> property, T value)
	{
		setBlock(world, point.toBlockPos(), block, property, value);
	}
	
	/**
	 * Sets the given block at x, y, z with the provided property and property value
	 * 
	 * @see #setBlock(World, int, int, int, IBlockState)
	 */
	public static <T extends Comparable<T>> void setBlock(World world, int posX, int posY, int posZ, Block block, IProperty<T> property, T value)
	{
		setBlock(world, new BlockPos(posX, posY, posZ), block.getDefaultState().withProperty(property, value));
	}
	
	/*******************************************************************************************************/
	
	/**
	 * Gets the block type at the given position.
	 * 
	 * @param 	world	The world containing the block.
	 * @param 	pos		The position of the block desired.
	 * 
	 * @return	Block which reflects the instance of the block at the given pos.
	 */
	public static Block getBlock(World world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock();
	}
	
	/**
	 * Gets the given block at point 3D.
	 * 
	 * @see #getBlock(World, BlockPos)
	 */
	public static Block getBlock(World world, Point3D point)
	{
		return getBlock(world, point.toBlockPos());
	}

	/**
	 * Gets the given block at x, y, z.
	 * 
	 * @see #getBlock(World, BlockPos)
	 */
	public static Block getBlock(World world, int posX, int posY, int posZ)
	{
		return getBlock(world, new BlockPos(posX, posY, posZ));
	}
	
	/*******************************************************************************************************/
	
	/**
	 * Gets the actual state of the block at the provided position.
	 * 
	 * @param 	world	World containing the block
	 * @param 	pos		Position of the block
	 * 
	 * @return IBlockState object which reflects the actual state of the block at pos.
	 */
	public static IBlockState getBlockState(World world, BlockPos pos)
	{
		return world.getBlockState(pos).getActualState(world, pos);
	}
	
	/**
	 * Gets the actual block state at point 3D.
	 * 
	 * @see #getBlockState(World, BlockPos)
	 */
	public static IBlockState getBlockState(World world, Point3D point)
	{
		return getBlockState(world, point.toBlockPos());
	}

	/**
	 * Gets the actual block state at x, y, z.
	 * 
	 * @see #getBlockState(World, BlockPos)
	 */
	public static IBlockState getBlockState(World world, int posX, int posY, int posZ)
	{
		return getBlockState(world, new BlockPos(posX, posY, posZ));
	}
	
	/*******************************************************************************************************/
	
	/**
	 * Compares the given block state with the state of the block at the provided position. If equal, true is returned. Otherwise false.
	 * 
	 * @param world		The world containing the block.
	 * @param pos		The position of the block.
	 * @param property	The property to use for comparison.
	 * @param value		The value of the property, used for comparison.
	 * 
	 * @return	True or false from .equals() call of the block's actual state and the state given to this method.
	 */
	public static <T extends Comparable<T>> boolean blockHasState(World world, BlockPos pos, IProperty<T> property, T value)
	{
		IBlockState state = world.getBlockState(pos).getActualState(world, pos);
		
		if (state.equals(world.getBlockState(pos).getBlock().getDefaultState().withProperty(property, value)))
		{
			return true;
		}
		
		else
		{
			return false;
		}
	}
	
	/**
	 * Gets the given block at point 3D.
	 * 
	 * @see #blockHasState(World, BlockPos, IProperty, Comparable)
	 */
	public static <T extends Comparable<T>> boolean blockHasState(World world, Point3D point, IProperty<T> property, T value)
	{
		return blockHasState(world, point.toBlockPos(), property, value);
	}

	/**
	 * Gets the given block at x, y, z.
	 * 
	 * @see #blockHasState(World, BlockPos, IProperty, Comparable)
	 */
	public static <T extends Comparable<T>> boolean blockHasState(World world, int posX, int posY, int posZ, IProperty<T> property, T value)
	{
		return blockHasState(world, new BlockPos(posX, posY, posZ), property, value);
	}
	
	private RadixBlocks()
	{
	}
}
