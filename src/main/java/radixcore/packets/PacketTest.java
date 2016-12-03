package radixcore.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.core.RadixCore;
import radixcore.modules.net.AbstractPacket;

public class PacketTest extends AbstractPacket<PacketTest>
{
	public enum Type
	{
		REQ(1),
		SYN(2),
		SYNACK(3),
		ACK(4);
		
		int id;
		
		Type(int id)
		{
			this.id = id;
		}
		
		static Type byId(int id)
		{
			for (Type type : values())
			{
				if (type.id == id)
				{
					return type;
				}
			}
			
			return null;
		}
	};
	
	private Type type;
	
	public PacketTest()
	{
		
	}
	
	public PacketTest(Type type)
	{
		this.type = type;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		type = Type.byId(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(type.id);
	}

	@Override
	public void processOnGameThread(PacketTest packet, MessageContext context) 
	{
		RadixCore.getLogger().info("Recieved packet: " + packet.type);
		
		switch (packet.type)
		{
		case REQ: packetHandler.sendPacketToServer(new PacketTest(Type.SYN)); break;
		case SYN: packetHandler.sendPacketToPlayer(new PacketTest(Type.SYNACK), getPlayer(context)); break;
		case SYNACK: packetHandler.sendPacketToServer(new PacketTest(Type.ACK)); break;
		case ACK: break;
		}
	}
}
