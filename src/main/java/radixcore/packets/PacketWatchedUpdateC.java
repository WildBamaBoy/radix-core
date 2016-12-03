package radixcore.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.modules.RadixNettyIO;
import radixcore.modules.datawatcher.DataWatcherEx;
import radixcore.modules.datawatcher.IWatchable;
import radixcore.modules.net.AbstractPacket;

public class PacketWatchedUpdateC extends AbstractPacket<PacketWatchedUpdateC>
{
	private int entityId;
	private String modId;
	private int watchedId;
	private Object watchedValue;

	public PacketWatchedUpdateC()
	{
	}

	public PacketWatchedUpdateC(int entityId, int watchedId, Object watchedValue)
	{
		this.entityId = entityId;
		this.watchedId = watchedId;
		this.watchedValue = watchedValue;
	}

	public PacketWatchedUpdateC(String modId, int watchedId, Object watchedValue)
	{
		this.modId = modId;
		this.watchedId = watchedId;
		this.watchedValue = watchedValue;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		this.entityId = byteBuf.readInt();
		this.modId = (String) RadixNettyIO.readObject(byteBuf);
		this.watchedId = byteBuf.readInt();
		this.watchedValue = RadixNettyIO.readObject(byteBuf);
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(this.entityId);
		RadixNettyIO.writeObject(byteBuf, this.modId);
		byteBuf.writeInt(this.watchedId);
		RadixNettyIO.writeObject(byteBuf, this.watchedValue);
	}

	@Override
	public void processOnGameThread(PacketWatchedUpdateC packet, MessageContext context) 
	{
		IWatchable watchable = null;

		watchable = (IWatchable)this.getPlayer(context).worldObj.getEntityByID(packet.entityId);

		if (watchable != null)
		{
			DataWatcherEx dataWatcherEx = watchable.getDataWatcherEx();
			dataWatcherEx.updateObject(packet.watchedId, packet.watchedValue, false); //Do not dispatch client-side.
		}
	}
}
