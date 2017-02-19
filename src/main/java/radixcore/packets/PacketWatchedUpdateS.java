package radixcore.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.modules.RadixNettyIO;
import radixcore.modules.datawatcher.DataWatcherEx;
import radixcore.modules.datawatcher.IWatchable;
import radixcore.modules.net.AbstractPacket;

public class PacketWatchedUpdateS extends AbstractPacket<PacketWatchedUpdateS>
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
	public void processOnGameThread(PacketWatchedUpdateS packet, MessageContext context) 
	{
		EntityPlayer player = this.getPlayer(context);
		IWatchable watchable = null;

		watchable = (IWatchable)player.world.getEntityByID(packet.entityId);

		if (watchable != null)
		{
			DataWatcherEx dataWatcherEx = watchable.getDataWatcherEx();
			boolean flag = !DataWatcherEx.allowClientSideModification;

			if (flag)
			{
				DataWatcherEx.allowClientSideModification = true;
			}

			dataWatcherEx.updateObject(packet.watchedId, packet.watchedValue, true); //Server-side received info from client. Dispatch to all other clients.

			if (flag)
			{
				DataWatcherEx.allowClientSideModification = false;
			}
		}
	}
}
