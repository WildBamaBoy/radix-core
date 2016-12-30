package radixcore.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.core.RadixCore;
import radixcore.data.DataWatcherEx;
import radixcore.data.IWatchable;
import radixcore.util.RadixExcept;

public class PacketDataSyncReq extends AbstractPacket implements IMessage, IMessageHandler<PacketDataSyncReq, IMessage>
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
	public IMessage onMessage(PacketDataSyncReq packet, MessageContext context)
	{
		RadixCore.getPacketHandler().addPacketForProcessing(context.side, packet, context);
		return null;
	}

	@Override
	public void processOnGameThread(IMessageHandler message, MessageContext context) 
	{
		PacketDataSyncReq packet = (PacketDataSyncReq)message;
		
		try
		{
			IWatchable watchable = (IWatchable) context.getServerHandler().playerEntity.getEntityWorld().getEntityByID(packet.entityId);

			if (watchable != null) //Can be null, assuming it's a client-side sync issue. Doesn't seem to affect anything.
			{
				DataWatcherEx dataWatcherEx = watchable.getDataWatcherEx();
				RadixCore.getPacketHandler().sendPacketToPlayer(new PacketDataSync(packet.entityId, dataWatcherEx), context.getServerHandler().playerEntity);
			}
		}

		catch (Throwable e)
		{
			RadixExcept.logErrorCatch(e, "Error sending sync data to client.");
		}
	}
}
