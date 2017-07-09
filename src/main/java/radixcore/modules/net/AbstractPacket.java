/*******************************************************************************
 * AbstractPacket.java
 * Copyright (c) 2014 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package radixcore.modules.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class defines a basic packet that will be sent across the network. Extending this
 * will also enable you to run your processing logic on the game thread rather than the network thread.
 */
public abstract class AbstractPacket<REQ extends IMessage> implements IMessage, IMessageHandler<REQ, IMessage>
{
	protected static AbstractPacketHandler packetHandler;
	
	public EntityPlayer getPlayer(MessageContext context)
	{
		EntityPlayer player = null;

		if (context.side == Side.CLIENT)
		{
			return getPlayerClient();
		}

		else
		{
			player = context.getServerHandler().player;
		}

		return player;
	}

	@SideOnly(Side.CLIENT)
	public EntityPlayer getPlayerClient()
	{
		return Minecraft.getMinecraft().player;
	}
	
	public static void setPacketHandler(AbstractPacketHandler handler)
	{
		packetHandler = handler;
	}
	
	@Override
	public final IMessage onMessage(REQ packet, MessageContext context)
	{
		packetHandler.addPacketForProcessing(context.side, (AbstractPacket) packet, context);
		return null;
	}
	
	/**
	 * Runs the packet logic on the main game thread. If using this, add your packet to your processing queue
	 * from onMessage.
	 */
	public abstract void processOnGameThread(REQ message, MessageContext context);
}
