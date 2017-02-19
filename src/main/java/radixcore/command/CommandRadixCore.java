package radixcore.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockSand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import radixcore.constant.Font.Color;
import radixcore.constant.Font.Format;
import radixcore.core.RadixCore;
import radixcore.math.Point3D;
import radixcore.modules.RadixBlocks;
import radixcore.modules.RadixLogic;
import radixcore.modules.RadixMath;
import radixcore.modules.RadixReflect;
import radixcore.packets.PacketTest;

public class CommandRadixCore extends CommandBase 
{
	@Override
	public String getName() 
	{
		return "radixcore";
	}

	@Override
	public String getUsage(ICommandSender sender) 
	{
		return "/radixcore <subcommand> <arguments>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		String subcommand = args[0];
		EntityPlayer player = null;
		
		if (sender instanceof EntityPlayer)
		{
			player = (EntityPlayer)sender;
		}
		
		else
		{
			throw new CommandException("RadixCore commands cannot be used through rcon or the server console.");
		}
		
		if (subcommand == null || subcommand.isEmpty())
		{
			throw new CommandException("An invalid argument was provided.");
		}
		
		if (subcommand.equals("testing"))
		{
			if (args[1].equals("on"))
			{
				addMessage("Testing mode enabled.", player);
				RadixCore.isTesting = true;
			}

			else if (args[1].equals("off"))
			{
				addMessage("Testing mode disabled.", player);
				RadixCore.isTesting = false;
			}
			
			else if (args[1].equals("runIngameTests"))
			{
				try
				{
					addMessage("Running in-game tests...", player);
					
					Point3D testPoint;
					testPoint = new Point3D(1.0D, 1.1D, 1.0D);
					
					assertTrue(testPoint.dX() == 1.0D);
					assertTrue(testPoint.dY() == 1.1D);
					assertTrue(testPoint.dZ() == 1.0D);
					
					assertTrue(testPoint.fX() == 1.0F);
					assertTrue(testPoint.fY() == 1.1F);
					assertTrue(testPoint.fZ() == 1.0F);
					
					assertTrue(testPoint.iX() == 1);
					assertTrue(testPoint.iY() == 1);
					assertTrue(testPoint.iZ() == 1);
					
					testPoint.set(3, 4, 5);
					
					assertTrue(testPoint.iX() == 3);
					assertTrue(testPoint.iY() == 4);
					assertTrue(testPoint.iZ() == 5);
					
					assertTrue(testPoint.fX() == 3.0F);
					assertTrue(testPoint.fY() == 4.0F);
					assertTrue(testPoint.fZ() == 5.0F);
					
					assertTrue(testPoint.dX() == 3.0D);
					assertTrue(testPoint.dY() == 4.0D);
					assertTrue(testPoint.dZ() == 5.0D);
					
					/* Conversion to and from block pos */
					BlockPos pos = testPoint.toBlockPos();
					assertTrue(pos.getX() == 3);
					assertTrue(pos.getY() == 4);
					assertTrue(pos.getZ() == 5);
					
					Point3D convertedPos = Point3D.fromBlockPos(pos);
					assertTrue(convertedPos.iX() == 3);
					assertTrue(convertedPos.iY() == 4);
					assertTrue(convertedPos.iZ() == 5);
					
					/* Saving and loading */
					NBTTagCompound nbt = new NBTTagCompound();
					testPoint.writeToNBT("TestPoint", nbt);
					
					Point3D readPoint = Point3D.readFromNBT("TestPoint", nbt);
					assertTrue(readPoint.equals(testPoint));
					
					/* Grabbing nearest/furthest points */
					testPoint = Point3D.fromEntityPosition(player);
					
					List<Point3D> testList = new ArrayList<Point3D>();
					testList.add(testPoint.setNew(testPoint.iX() + 10, testPoint.iY(), testPoint.iZ()));
					testList.add(testPoint.setNew(testPoint.iX(), testPoint.iY(), testPoint.iZ() + 5));
					testList.add(testPoint.setNew(testPoint.iX(), testPoint.iY() + 3, testPoint.iZ()));
					
					Point3D nearestPoint = Point3D.getNearestPointInList(testPoint, testList);
					assertTrue(nearestPoint.iY() == testPoint.iY() + 3);
					
					Point3D furthestPoint = Point3D.getFurthestPointInList(testPoint, testList);
					assertTrue(furthestPoint.iX() == testPoint.iX() + 10);
					
					passTest("3D Points", player);
				}
				
				catch (AssertionError e)
				{
					e.printStackTrace();
					failTest("3D Points", player);
				}
				
				/* Logic testing */
				try
				{
					Point3D playerPos = Point3D.fromEntityPosition(player);
					EntityPlayer testEntity = null;
					
					testEntity = RadixLogic.getEntityOfTypeAtXYZ(EntityPlayer.class, player.world, playerPos);
					assertTrue(testEntity == player);
					
					testEntity = RadixLogic.getClosestEntityInclusive(player, 10, null);
					assertTrue(testEntity == player);
					
					testEntity = RadixLogic.getClosestEntityExclusive(player, 10, EntityPlayer.class);
					assertTrue(testEntity == null);
					
					testEntity = RadixLogic.getClosestEntity(playerPos, player.world, 10, null, null);
					assertTrue(testEntity == player);
					
					for (int i = 0; i < 1000; i++)
					{
						assertTrue(RadixLogic.getBooleanWithProbability(100));
						assertFalse(RadixLogic.getBooleanWithProbability(0));
					}
					
					//Clear any obsidian around, just in case.
					for (Point3D point : RadixLogic.getNearbyBlocks(player, Blocks.OBSIDIAN, 10))
					{
						player.world.setBlockToAir(point.toBlockPos());
					}
					
					Point3D nearest = RadixLogic.getNearestBlock(player, 10, Blocks.OBSIDIAN);
					
					assertTrue(nearest.isNone());
					
					player.world.setBlockState(player.getPosition().add(5, 0, 0), Blocks.OBSIDIAN.getDefaultState());
					nearest = RadixLogic.getNearestBlock(player, 10, Blocks.OBSIDIAN);
					
					assertFalse(nearest.isNone());
					
					//Clear again to place two for testing nearest and farthest
					for (Point3D point : RadixLogic.getNearbyBlocks(player, Blocks.OBSIDIAN, 10))
					{
						player.world.setBlockToAir(point.toBlockPos());
					}
					
					BlockPos playerBlockPos = player.getPosition();
					
					player.world.setBlockState(playerBlockPos.add(4, 0, 0), Blocks.OBSIDIAN.getDefaultState());
					player.world.setBlockState(playerBlockPos.add(8, 0, 0), Blocks.OBSIDIAN.getDefaultState());
					player.world.setBlockState(playerBlockPos.add(9, 0, 0), Blocks.OBSIDIAN.getDefaultState());
					
					nearest = RadixLogic.getNearestBlock(player, 10, 3, Blocks.OBSIDIAN);
					Point3D farthest = RadixLogic.getFarthestBlock(player, 10, 3, Blocks.OBSIDIAN);
					
					assertTrue(nearest.dX() == playerBlockPos.getX() + 4);
					assertTrue(farthest.dX() == playerBlockPos.getX() + 9);
					
					for (Point3D point : RadixLogic.getNearbyBlocks(player, Blocks.OBSIDIAN, 10))
					{
						player.world.setBlockToAir(point.toBlockPos());
					}
					
					//Blocks returned from getNearbyBlocks should be in a cube.
					for (int cubeSize = 1; cubeSize < 10; cubeSize++)
					{
						List<Point3D> blocks = RadixLogic.getNearbyBlocks(player, null, cubeSize, cubeSize);
						assertTrue(blocks.size() == Math.pow((cubeSize + cubeSize + 1), 3));
					}
					
					//Test getting entities
					EntityHorse newHorse = new EntityHorse(player.world);
					newHorse.setPosition(player.posX + 5, player.posY, player.posZ);
					player.world.spawnEntity(newHorse);
					
					List<EntityHorse> horses = RadixLogic.getEntitiesWithinDistance(EntityHorse.class, player, 10);

					assertTrue(horses.size() >= 1);
					newHorse.setDead();
					
					passTest("Logic", player);
				}
				
				catch (AssertionError e)
				{
					e.printStackTrace();
					failTest("Logic", player);
				}
				
				try
				{
					for (int i = 0; i < 1000; i++)
					{
						assertTrue(RadixMath.isWithinRange(i, 0, 1000));
						assertTrue(RadixMath.isWithinRange(RadixMath.getNumberInRange(i, i * 2), i, i * 2));
						assertTrue(RadixMath.isWithinRange(RadixMath.getNumberInRange(i, i * 2), i, i * 2));
					}
					
					assertTrue(RadixMath.getDistanceToXYZ(
							player.posX, player.posY, player.posZ, 
							player.posX + 2, player.posY + 2, player.posZ + 3.5) == 4.5F);
					
					assertTrue(RadixMath.getHighestNumber(2.0F, 3.05F, 3.5F, 3.6F, 3.68F, 3.685F) == 3.685F);
					assertTrue(RadixMath.getLowestNumber(2.005F, 2.0F, 1.98F, 1.987F, 1.9885F) == 1.98F);
					
					passTest("Math", player);
				}
				
				catch (AssertionError e)
				{
					failTest("Math", player);
				}
				
				try
				{
					passTest("Reflection", player);
					
					InventoryEnderChest enderChest = RadixReflect.getInstanceObjectOfTypeFromClass(InventoryEnderChest.class, EntityPlayer.class, player);
					assertTrue(enderChest != null);
					
					DataParameter<Float> absorptionParam = RadixReflect.getStaticObjectOfTypeFromClass(DataParameter.class, EntityPlayer.class);
					assertTrue(absorptionParam != null);
				}
				
				catch (AssertionError e)
				{
					failTest("Reflection", player);
				}
				
				try
				{
					passTest("Blocks", player);
					
					BlockPos testPos = new BlockPos(player.posX + 5, player.posY, player.posZ);
					
					RadixBlocks.setBlock(player.world, testPos, Blocks.BEDROCK);
					assertTrue(RadixBlocks.getBlock(player.world, testPos) == Blocks.BEDROCK);
					RadixBlocks.setBlock(player.world, testPos, Blocks.AIR);
					assertTrue(RadixBlocks.getBlock(player.world, testPos) == Blocks.AIR);
					
					// Verbose block setting and checking
					RadixBlocks.setBlock(player.world, testPos, 
							Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND));
					
					assertTrue(RadixBlocks.getBlock(player.world, testPos) == Blocks.SAND);
					assertTrue(RadixBlocks.blockHasState(player.world, testPos, BlockSand.VARIANT, BlockSand.EnumType.RED_SAND));
					assertFalse(RadixBlocks.blockHasState(player.world, testPos, BlockSand.VARIANT, BlockSand.EnumType.SAND));
					
					// Less verbose block setting and checking
					RadixBlocks.setBlock(player.world, testPos, Blocks.SAND, BlockSand.VARIANT, BlockSand.EnumType.RED_SAND);
					assertTrue(RadixBlocks.getBlock(player.world, testPos) == Blocks.SAND);
					assertFalse(RadixBlocks.blockHasState(player.world, testPos, BlockSand.VARIANT, BlockSand.EnumType.SAND));
					assertTrue(RadixBlocks.blockHasState(player.world, testPos, BlockSand.VARIANT, BlockSand.EnumType.RED_SAND));
					
					RadixBlocks.setBlock(player.world, testPos, Blocks.AIR);
				}
				
				catch (AssertionError e)
				{
					failTest("Blocks", player);
				}
				
				RadixCore.getPacketHandler().sendPacketToPlayer(new PacketTest(PacketTest.Type.REQ), player);
			}
		}
	}
	
	private void addMessage(String message, EntityPlayer player)
	{
		player.sendMessage(new TextComponentString(Color.GOLD + "[" + Color.DARKRED + "RadixCore" + Color.GOLD + "] " + Format.RESET + message));
	}
	
	private void passTest(String testName, EntityPlayer player)
	{
		addMessage("- " + testName + ": " + Color.GREEN + "[PASS]", player);
	}
	
	private void failTest(String testName, EntityPlayer player)
	{
		addMessage("- " + testName + ": " + Color.RED + "[FAIL]", player);
	}
	
	private void assertTrue(boolean expression) throws AssertionError
	{
		if (!expression)
		{
			throw new AssertionError();
		}
	}
	
	private void assertFalse(boolean expression) throws AssertionError
	{
		if (expression)
		{
			throw new AssertionError();
		}
	}
}
