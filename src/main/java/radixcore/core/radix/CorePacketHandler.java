package radixcore.core.radix;

import net.minecraftforge.fml.relauncher.Side;
import radixcore.modules.net.AbstractPacketHandler;
import radixcore.packets.PacketDataSync;
import radixcore.packets.PacketDataSyncReq;
import radixcore.packets.PacketPostLogin;
import radixcore.packets.PacketTest;
import radixcore.packets.PacketWatchedUpdateC;
import radixcore.packets.PacketWatchedUpdateS;

public class CorePacketHandler extends AbstractPacketHandler
{
	public CorePacketHandler(String modId) 
	{
		super(modId);
	}

	@Override
	public void registerPackets() 
	{
		this.registerPacket(PacketWatchedUpdateC.class, Side.CLIENT);
		this.registerPacket(PacketWatchedUpdateS.class, Side.SERVER);
		this.registerPacket(PacketDataSyncReq.class, Side.SERVER);
		this.registerPacket(PacketDataSync.class, Side.CLIENT);
		this.registerPacket(PacketTest.class, Side.SERVER);
		this.registerPacket(PacketTest.class, Side.CLIENT);
		this.registerPacket(PacketPostLogin.class, Side.CLIENT);
	}
}
