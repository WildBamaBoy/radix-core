package radixcore.packets;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.modules.RadixNettyIO;
import radixcore.modules.datawatcher.DataWatcherEx;
import radixcore.modules.datawatcher.IWatchable;
import radixcore.modules.datawatcher.WatchedObjectEx;
import radixcore.modules.net.AbstractPacket;

public class PacketDataSync extends AbstractPacket<PacketDataSync>
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
		this.dataWatcherData = (Map) RadixNettyIO.readObject(byteBuf);
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(this.entityId);
		RadixNettyIO.writeObject(byteBuf, this.dataWatcherData);		
	}

	@Override
	public void processOnGameThread(PacketDataSync message, MessageContext context) 
	{
		PacketDataSync packet = (PacketDataSync)message;
		IWatchable entity = (IWatchable)this.getPlayerClient().worldObj.getEntityByID(packet.entityId);

		DataWatcherEx dataWatcherEx = entity.getDataWatcherEx();

		for (Object obj : packet.dataWatcherData.values())
		{
			WatchedObjectEx recvObject = (WatchedObjectEx)obj;
			WatchedObjectEx currentObject = dataWatcherEx.getWatchedObject(recvObject.getDataValueId());

			currentObject.setObject(recvObject.getObject());
		}
	}
}
