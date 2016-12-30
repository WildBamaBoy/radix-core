package radixcore.packets;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.core.RadixCore;
import radixcore.data.DataWatcherEx;
import radixcore.data.IWatchable;
import radixcore.data.WatchedObjectEx;
import radixcore.network.ByteBufIO;
import radixcore.util.RadixExcept;

public class PacketDataSync extends AbstractPacket implements IMessage, IMessageHandler<PacketDataSync, IMessage>
{
	private int entityId;
	private Map dataWatcherData;
	
	public PacketDataSync()
	{
	}

	public PacketDataSync(int entityId, DataWatcherEx dataWatcherEx)
	{
		this.entityId = entityId;
		this.dataWatcherData = dataWatcherEx.getWatchedDataMap();
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		this.entityId = byteBuf.readInt();
		this.dataWatcherData = (Map) ByteBufIO.readObject(byteBuf);
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(this.entityId);
		ByteBufIO.writeObject(byteBuf, this.dataWatcherData);		
	}

	@Override
	public IMessage onMessage(PacketDataSync packet, MessageContext context)
	{
		RadixCore.getPacketHandler().addPacketForProcessing(context.side, packet, context);
		return null;
	}

	@Override
	public void processOnGameThread(IMessageHandler message, MessageContext context) 
	{
		PacketDataSync packet = (PacketDataSync)message;
		IWatchable entity = (IWatchable)this.getPlayerClient().getEntityWorld().getEntityByID(packet.entityId);
		
		try
		{
			DataWatcherEx dataWatcherEx = entity.getDataWatcherEx();
			
			for (Object obj : packet.dataWatcherData.values())
			{
				WatchedObjectEx recvObject = (WatchedObjectEx)obj;
				WatchedObjectEx currentObject = dataWatcherEx.getWatchedObject(recvObject.getDataValueId());
				
				currentObject.setObject(recvObject.getObject());
			}
		}
		
		catch (Throwable e)
		{
			RadixExcept.logErrorCatch(e, "Unexpected error while processing received sync data.");
		}
		
	}
}
