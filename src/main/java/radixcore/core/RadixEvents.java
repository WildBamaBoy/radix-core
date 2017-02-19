/*******************************************************************************
 * RadixEvents.java
 * Copyright (c) 2014 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package radixcore.core;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import radixcore.constant.Font.Color;
import radixcore.constant.Font.Format;
import radixcore.modules.client.RadixRender;
import radixcore.modules.datawatcher.IWatchable;
import radixcore.packets.PacketDataSyncReq;
import radixcore.packets.PacketPostLogin;

/**
 * Defines events handled by RadixCore.
 */
public class RadixEvents
{
	@SubscribeEvent
	public void renderGameOverlayEventHandler(RenderGameOverlayEvent.Text event)
	{
		if (RadixCore.isTesting)
		{
			RadixRender.drawTextPopup("RADIXCORE RENDER HELPER TEST", 5, 5);
		}
	}
	
	@SubscribeEvent
	public void rightClickBlockEventHandler(PlayerInteractEvent.RightClickBlock event)
	{
		if (RadixCore.isTesting)
		{
			EntityPlayer player = event.getEntityPlayer();
			World world = player.getEntityWorld();
			BlockPos blockPos = event.getPos();
			IBlockState state = world.getBlockState(blockPos);
			
			StringBuilder message = new StringBuilder();
			
			message.append(state.toString());
			message.append(" @");
			message.append(blockPos.toString().replace("BlockPos", ""));
			
			
			player.sendMessage(new TextComponentString(Color.GOLD + "[" + Color.DARKRED + "RadixCore" + Color.GOLD + "] " + Format.RESET + message.toString()));
		}
	}
	
	@SubscribeEvent
	public void playerLoggedInEventHandler(PlayerLoggedInEvent event)
	{
		// By checking on the server and telling each client to check for updates,
		// a server admin can control whether or not update notifications appear
		// for their users.
		if (RadixCore.allowUpdateChecking && event.player.isServerWorld())
		{
			RadixCore.getPacketHandler().sendPacketToPlayer(new PacketPostLogin(), event.player);
		}
	}

	@SubscribeEvent
	public void entitySpawnedEvent(EntityJoinWorldEvent event)
	{
		if (event.getWorld().isRemote && event.getEntity() instanceof IWatchable)
		{
			RadixCore.getPacketHandler().sendPacketToServer(new PacketDataSyncReq(event.getEntity().getEntityId()));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void clientTickEventHandler(ClientTickEvent event)
	{
		for (ModMetadataEx exData : RadixCore.getRegisteredMods())
		{
			if (exData.packetHandler != null)
			{
				exData.packetHandler.processPackets(Side.CLIENT);
			}
		}
	}
	
	@SubscribeEvent
	public void serverTickEventHandler(ServerTickEvent event)
	{
		for (ModMetadataEx exData : RadixCore.getRegisteredMods())
		{
			if (exData.packetHandler != null)
			{
				exData.packetHandler.processPackets(Side.SERVER);
			}
		}
	}
}
