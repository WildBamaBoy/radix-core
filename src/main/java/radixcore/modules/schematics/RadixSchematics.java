package radixcore.modules.schematics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import radixcore.core.RadixCore;
import radixcore.math.Point3D;
import radixcore.modules.RadixBlocks;

public final class RadixSchematics 
{
	public static Point3D getPoint3DWithValue(Map<Point3D, BlockObj> schematicData, Point3D point)
	{
		for (Map.Entry<Point3D, BlockObj> entry : schematicData.entrySet())
		{
			if (entry.getKey().equals(point))
			{
				return entry.getKey();
			}
		}

		return null;
	}

	public static int countOccurencesOfBlockObj(Map<Point3D, BlockObj> schematicData, BlockObj searchBlock)
	{
		int count = 0;

		for (BlockObj block : schematicData.values())
		{
			if (block.equals(searchBlock))
			{
				count++;
			}
		}

		return count;
	}

	public static SortedMap<Point3D, BlockObj> readSchematic(String location)
	{
		Point3D origin = null;
		Point3D offset = null;

		SortedMap<Point3D, BlockObj> map = new TreeMap<Point3D, BlockObj>();

		NBTTagCompound nbtdata = null;
		
		try
		{
			nbtdata = CompressedStreamTools.readCompressed(RadixSchematics.class.getResourceAsStream(location));
		}
		
		catch (IOException e)
		{
			RadixCore.getLogger().error("Failed to load schematic: " + location);
			return map;
		}
		
		short width = nbtdata.getShort("Width");
		short height = nbtdata.getShort("Height");
		short length = nbtdata.getShort("Length");

		byte[] blockIds = nbtdata.getByteArray("Blocks");
		byte[] data	= nbtdata.getByteArray("Data");
		byte[] addIds = new byte[0];
		short[] blocks = new short[blockIds.length];

		if (nbtdata.hasKey("AddBlocks")) 
		{
			addIds = nbtdata.getByteArray("AddBlocks");
		}

		try 
		{
			int originX = nbtdata.getInteger("WEOriginX");
			int originY = nbtdata.getInteger("WEOriginY");
			int originZ = nbtdata.getInteger("WEOriginZ");
			Point3D min = new Point3D(originX, originY, originZ);

			int offsetX = nbtdata.getInteger("WEOffsetX");
			int offsetY = nbtdata.getInteger("WEOffsetY");
			int offsetZ = nbtdata.getInteger("WEOffsetZ");
			offset = new Point3D(offsetX, offsetY, offsetZ);

			origin = new Point3D(min.iX() - offset.iX(), min.iY() - offset.iY(), min.iZ() - offset.iZ());
		} 

		catch (Exception ignore) 
		{
			origin = Point3D.ZERO;
		}

		for (int index = 0; index < blockIds.length; index++) 
		{
			if ((index >> 1) >= addIds.length) 
			{
				blocks[index] = (short) (blockIds[index] & 0xFF);
			} 

			else 
			{
				if ((index & 1) == 0) 
				{
					blocks[index] = (short) (((addIds[index >> 1] & 0x0F) << 8) + (blockIds[index] & 0xFF));
				} 

				else 
				{
					blocks[index] = (short) (((addIds[index >> 1] & 0xF0) << 4) + (blockIds[index] & 0xFF));
				}
			}
		}

		for (int x = 0; x < width; ++x) 
		{
			for (int y = 0; y < height; ++y) 
			{
				for (int z = 0; z < length; ++z) 
				{
					int index = y * width * length + z * width + x;
					Point3D point = new Point3D(x + offset.iX(), y + offset.iY() - 1, z + offset.iZ());
					BlockObj block = new BlockObj(Block.getBlockById(blocks[index]), data[index]);

					map.put(point, block);
				}
			}
		}

		return map;
	}

	public static void spawnStructureRelativeToPlayer(String location, EntityPlayer player)
	{
		spawnStructureRelativeToPoint(location, new Point3D(player.posX, player.posY + 1, player.posZ), player.world);
	}

	public static void spawnStructureRelativeToPoint(String location, Point3D point, World world)
	{
		Map<Point3D, BlockObj> schemBlocks = readSchematic(location);
		Map<Point3D, BlockObj> torchMap = new HashMap<Point3D, BlockObj>();
		Map<Point3D, BlockObj> doorMap = new HashMap<Point3D, BlockObj>();

		for (Map.Entry<Point3D, BlockObj> entry : schemBlocks.entrySet())
		{
			if (entry.getValue().getBlock() == Blocks.TORCH)
			{
				torchMap.put(entry.getKey(), entry.getValue());
			}

			else if (entry.getValue().getBlock() == Blocks.OAK_DOOR)
			{
				doorMap.put(entry.getKey(), entry.getValue());
			}

			else
			{
				Point3D blockPoint = entry.getKey();

				int x = blockPoint.iX() + point.iX();
				int y = blockPoint.iY() + point.iY();
				int z = blockPoint.iZ() + point.iZ();

				Block currentBlockAtPoint = RadixBlocks.getBlock(world, x, y, z);
				BlockObj blockObj = entry.getValue();
				IBlockState state = blockObj.getBlock().getStateFromMeta(blockObj.getMeta());

				//Properly remove tall grass.
				if (currentBlockAtPoint == Blocks.TALLGRASS)
				{
					RadixBlocks.setBlock(world, x, y + 1, z, Blocks.AIR);
					RadixBlocks.setBlock(world, x, y, z, Blocks.AIR);
				}

				//Don't set double plants, causes spawning of a peony which crashes the game with Waila.
				if (blockObj.getBlock() == Blocks.DOUBLE_PLANT)
				{
					continue;
				}

				world.setBlockState(new BlockPos(x, y, z), state);
			}
		}

		for (Map.Entry<Point3D, BlockObj> entry : torchMap.entrySet())
		{
			Point3D blockPoint = entry.getKey();

			int x = blockPoint.iX() + point.iX();
			int y = blockPoint.iY() + point.iY();
			int z = blockPoint.iZ() + point.iZ();

			BlockObj blockObj = entry.getValue();
			IBlockState state = blockObj.getBlock().getStateFromMeta(blockObj.getMeta());

			world.setBlockState(new BlockPos(x, y, z), state);
		}

		for (Map.Entry<Point3D, BlockObj> entry : doorMap.entrySet())
		{
			Point3D blockPoint = entry.getKey();

			int x = blockPoint.iX() + point.iX();
			int y = blockPoint.iY() + point.iY();
			int z = blockPoint.iZ() + point.iZ();

			BlockObj blockObj = entry.getValue();
			IBlockState state = blockObj.getBlock().getStateFromMeta(blockObj.getMeta());

			world.setBlockState(new BlockPos(x, y, z), state);
		}
	}
}
