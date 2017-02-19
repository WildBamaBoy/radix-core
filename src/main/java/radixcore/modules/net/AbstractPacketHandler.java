/*******************************************************************************
 * AbstractPacketHandler.java
 * Copyright (c) 2014 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package radixcore.modules.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class provides a simple set of methods used to manage registering and sending packets to/from the client and server.
 * 
 * <p>In order to mitigate issues with certain oddities on Minecraft 1.8's threaded networking system,
 * the AbstractPacketHandler implements a simple processing queue. On a packet's onMessage() method, add it
 * to your mod's packet handler with addPacketForProcessing(). In your mod's client and server tick handlers,
 * call processPackets().
 * 
 * <p>This will process the packets on the main client/server thread (as they were in 1.7.10) rather than on a 
 * thread used exclusively by the networking system.
 * 
 * <p>See RadixEvents for implementation.
 */
public abstract class AbstractPacketHandler
{
	private List<QueuedPacket> serverQueuedPackets;
	private List<QueuedPacket> clientQueuedPackets;
	protected SimpleNetworkWrapper wrapper;
	private int idCounter;

	public AbstractPacketHandler(String modId)
	{
		wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
		registerPackets();
		clientQueuedPackets = new ArrayList<QueuedPacket>();
		serverQueuedPackets = new ArrayList<QueuedPacket>();
	}

	public abstract void registerPackets();

	/**
	 * Registers a packet with the underlying SimpleNetworkWrapper.
	 * 
	 * @param 	packetClass		The packet's class.
	 * @param 	processorSide	The side that will receive and process this packet.
	 */
	protected final void registerPacket(Class packetClass, Side processorSide)
	{
		try
		{
			Method setPacketHandler = packetClass.getMethod("setPacketHandler", AbstractPacketHandler.class);
			setPacketHandler.invoke(null, this);
		}
		
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e)
		{
			throw new RuntimeException(e);
		}
		
		wrapper.registerMessage(packetClass, packetClass, idCounter, processorSide);
		idCounter++;
	}

	/**
	 * Sends the provided packet to all players.
	 * 
	 * @param packet The packet to be sent.
	 */
	public final void sendPacketToAllPlayers(IMessage packet)
	{
		wrapper.sendToAll(packet);
	}

	/**
	 * Sends the provided packet to all players except the provided player.
	 * 
	 * @param packet The packet to be sent.
	 * @param player The player that will not receive the packet.
	 */
	public final void sendPacketToAllPlayersExcept(IMessage packet, EntityPlayerMP player)
	{
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		final PlayerList playerList = server.getPlayerList();

		for (int index = 0; index < playerList.getCurrentPlayerCount(); ++index)
		{
			final EntityPlayerMP playerInList = (EntityPlayerMP) playerList.getPlayers().get(index);

			if (!playerInList.getName().equals(player.getName()))
			{
				wrapper.sendTo(packet, playerInList);
			}
		}
	}

	/**
	 * Sends the provided packet to the provided player.
	 * 
	 * @param packet The packet to be sent.
	 * @param player The player that will receive the packet.
	 */
	public final void sendPacketToPlayer(IMessage packet, EntityPlayer player)
	{
		if (player != null)
		{
			wrapper.sendTo(packet, (EntityPlayerMP) player);
		}
	}

	/**
	 * Sends the provided packet to everyone within a certain range of the provided point.
	 * 
	 * @param packet The packet to be sent.
	 * @param point The point around which to send the packet.
	 */
	public final void sendPacketToAllAround(IMessage packet, NetworkRegistry.TargetPoint point)
	{
		wrapper.sendToAllAround(packet, point);
	}

	/**
	 * Sends the provided packet to everyone within the supplied dimension.
	 * 
	 * @param packet The packet to be sent.
	 * @param dimensionId The dimension id.
	 */
	public final void sendPacketToDimension(IMessage packet, int dimensionId)
	{
		wrapper.sendToDimension(packet, dimensionId);
	}

	/**
	 * Sends this message to the server.
	 * 
	 * @param packet The packet to be sent.
	 */
	public final void sendPacketToServer(IMessage packet)
	{
		wrapper.sendToServer(packet);
	}

	/**
	 * Processes all packets stored in the queue. Locks the queue while packets are being processed.
	 * Call this from your client and server tick handlers fairly often to keep things up-to-date.
	 * 
	 * @param 	side	The side that will be processing packets.
	 */
	public final void processPackets(Side side)
	{
		List<QueuedPacket> queuedPackets = side.isClient() ? clientQueuedPackets : serverQueuedPackets;

		synchronized (queuedPackets)
		{
			while (!queuedPackets.isEmpty())
			{
				QueuedPacket queuedObject = queuedPackets.get(0);
				AbstractPacket packet = queuedObject.getPacket();
				packet.processOnGameThread((IMessage) queuedObject.getMessage(), queuedObject.getContext());
				queuedPackets.remove(0);
			}
		}
	}

	/**
	 * Adds a packet to the processing queue. Thread-safe.
	 * 
	 * @param	side	The side that will process this packet.
	 * @param 	packet	An instance of the packet that will be processed.
	 * @param 	context	The MessageContext of the packet from its onMessage() method.
	 */
	public final void addPacketForProcessing(Side side, AbstractPacket packet, MessageContext context)
	{
		Side currentSide = context.side;
		List<QueuedPacket> queuedPackets = currentSide.isClient() ? clientQueuedPackets : serverQueuedPackets;
		
		synchronized (queuedPackets)
		{
			queuedPackets.add(new QueuedPacket(packet, (IMessageHandler)packet, context));
		}
	}
}
