/*******************************************************************************
 * RadixEvents.java
 * Copyright (c) 2014 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package radixcore.core;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import radixcore.data.AbstractPlayerData;
import radixcore.data.IWatchable;
import radixcore.packets.PacketDataSyncReq;
import radixcore.update.UpdateChecker;

/**
 * Defines events handles by RadixCore.
 */
public class RadixEvents
{
	@SubscribeEvent
	public void playerLoggedInEventHandler(PlayerLoggedInEvent event)
	{		
		for (ModMetadataEx exData : RadixCore.registeredMods)
		{
			if (RadixCore.allowUpdateChecking && event.player.getEntityWorld().isRemote)
			{
				try
				{
					new Thread(new UpdateChecker(exData, event.player)).start();
				}

				catch (Exception e)
				{
					RadixCore.getLogger().error("Unexpected exception while starting update checker for " + exData.name + ". Error was " + e.getMessage());
					continue;
				}
			}
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

	@SubscribeEvent
	public void worldSaveEventHandler(WorldEvent.Unload event)
	{
		if (!event.getWorld().isRemote)
		{
			for (ModMetadataEx metadata : RadixCore.registeredMods)
			{
				if (metadata.playerDataMap != null)
				{
					for (AbstractPlayerData data : metadata.playerDataMap.values())
					{
						if (data != null) //Crashes on extensively modded games, I'm not clear on why this may happen. Simple workaround now, may revisit later.
						{
							data.saveDataToFile();
						}

						else
						{
							RadixCore.getLogger().error("Skipping save of null player data for mod " + metadata.modId + ". You may notice a few oddities. Please report if so.");
						}
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void clientTickEventHandler(ClientTickEvent event)
	{
		RadixCore.getPacketHandler().processPackets(Side.CLIENT);
	}
	
	@SubscribeEvent
	public void serverTickEventHandler(ServerTickEvent event)
	{
		RadixCore.getPacketHandler().processPackets(Side.SERVER);
	}
}
