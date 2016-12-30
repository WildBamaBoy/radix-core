/*******************************************************************************
 * AbstractPacket.java
 * Copyright (c) 2014 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package radixcore.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class defines a basic packet that will be sent across the network. Extending this
 * will also enable you to run your processing logic on the game thread rather than the network thread.
 * 
 * You must still implement <code>IMessage</code> and <code>MessageHandler<(packet type), IMessage></code>
 */
public abstract class AbstractPacket
{
	public EntityPlayer getPlayer(MessageContext context)
	{
		EntityPlayer player = null;

		if (context.side == Side.CLIENT)
		{
			return getPlayerClient();
		}

		else
		{
			player = context.getServerHandler().playerEntity;
		}

		return player;
	}

	@SideOnly(Side.CLIENT)
	public EntityPlayer getPlayerClient()
	{
		return Minecraft.getMinecraft().player;
	}
	
	/**
	 * Runs the packet logic on the main game thread. If using this, add your packet to your processing queue
	 * from onMessage.
	 */
	public abstract void processOnGameThread(IMessageHandler message, MessageContext context);
}
