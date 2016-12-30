package radixcore.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.core.ModMetadataEx;
import radixcore.core.RadixCore;
import radixcore.data.AbstractPlayerData;
import radixcore.data.DataContainer;
import radixcore.data.DataWatcherEx;
import radixcore.data.IWatchable;
import radixcore.network.ByteBufIO;
import radixcore.util.RadixExcept;
import radixcore.util.RadixReflect;

public class PacketWatchedUpdateS extends AbstractPacket implements IMessage, IMessageHandler<PacketWatchedUpdateS, IMessage>
{
	private int entityId;
	private String modId;
	private int watchedId;
	private Object watchedValue;

	public PacketWatchedUpdateS()
	{
	}

	public PacketWatchedUpdateS(int entityId, int watchedId, Object watchedValue)
	{
		this.entityId = entityId;
		this.watchedId = watchedId;
		this.watchedValue = watchedValue;
	}

	public PacketWatchedUpdateS(String modId, int watchedId, Object watchedValue)
	{
		this.modId = modId;
		this.watchedId = watchedId;
		this.watchedValue = watchedValue;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		this.entityId = byteBuf.readInt();
		this.modId = (String) ByteBufIO.readObject(byteBuf);
		this.watchedId = byteBuf.readInt();
		this.watchedValue = ByteBufIO.readObject(byteBuf);
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(this.entityId);
		ByteBufIO.writeObject(byteBuf, this.modId);
		byteBuf.writeInt(this.watchedId);
		ByteBufIO.writeObject(byteBuf, this.watchedValue);
	}

	@Override
	public IMessage onMessage(PacketWatchedUpdateS packet, MessageContext context)
	{
		RadixCore.getPacketHandler().addPacketForProcessing(context.side, packet, context);
		return null;
	}

	@Override
	public void processOnGameThread(IMessageHandler message, MessageContext context) 
	{
		try
		{
			PacketWatchedUpdateS packet = (PacketWatchedUpdateS)message;
			
			EntityPlayer player = this.getPlayer(context);
			IWatchable watchable = null;

			if (packet.modId != null)
			{
				ModMetadataEx modData = null;

				for (ModMetadataEx data : RadixCore.getRegisteredMods())
				{
					if (data.modId.equals(packet.modId))
					{
						modData = data;
					}
				}

				if (modData != null)
				{
					if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
					{
						DataContainer container = RadixReflect.getStaticObjectOfTypeFromClass(DataContainer.class, modData.classContainingClientDataContainer);
						watchable = container.getPlayerData(AbstractPlayerData.class);
					}

					else
					{
						watchable = modData.getPlayerData(player);
					}
				}
			}

			else
			{
				watchable = (IWatchable)player.getEntityWorld().getEntityByID(packet.entityId);
			}

			if (watchable != null)
			{
				DataWatcherEx dataWatcherEx = watchable.getDataWatcherEx();
				boolean flag = !DataWatcherEx.allowClientSideModification;
				
				if (flag)
				{
					dataWatcherEx.allowClientSideModification = true;
				}
				
				dataWatcherEx.updateObject(packet.watchedId, packet.watchedValue, true); //Server-side received info from client. Dispatch to all other clients.
				
				if (flag)
				{
					dataWatcherEx.allowClientSideModification = false;
				}
			}
		}

		catch (Throwable e)
		{
			RadixExcept.logErrorCatch(e, "Non-fatal error caught while updating watched object server-side.");
		}
	}
}
