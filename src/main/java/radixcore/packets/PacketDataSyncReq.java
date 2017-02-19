package radixcore.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.core.RadixCore;
import radixcore.modules.datawatcher.DataWatcherEx;
import radixcore.modules.datawatcher.IWatchable;
import radixcore.modules.net.AbstractPacket;

public class PacketDataSyncReq extends AbstractPacket<PacketDataSyncReq>
{
	private int entityId;

	public PacketDataSyncReq()
	{
	}

	public PacketDataSyncReq(int entityId)
	{
		this.entityId = entityId;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		this.entityId = byteBuf.readInt();
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(this.entityId);
	}

	@Override
	public void processOnGameThread(PacketDataSyncReq packet, MessageContext context) 
	{
		IWatchable watchable = (IWatchable) context.getServerHandler().playerEntity.world.getEntityByID(packet.entityId);

		if (watchable != null) //Can be null, assuming it's a client-side sync issue. Doesn't seem to affect anything.
		{
			DataWatcherEx dataWatcherEx = watchable.getDataWatcherEx();
			RadixCore.getPacketHandler().sendPacketToPlayer(new PacketDataSync(packet.entityId, dataWatcherEx), context.getServerHandler().playerEntity);
		}
	}
}
