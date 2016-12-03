package radixcore.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import radixcore.core.ModMetadataEx;
import radixcore.core.RadixCore;
import radixcore.modules.net.AbstractPacket;
import radixcore.modules.updates.RadixUpdateChecker;

public class PacketPostLogin extends AbstractPacket<PacketPostLogin>
{
	public PacketPostLogin()
	{
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{		
	}

	@Override
	public void processOnGameThread(PacketPostLogin message, MessageContext context) 
	{
		for (ModMetadataEx exData : RadixCore.getRegisteredMods())
		{
			if (RadixCore.allowUpdateChecking)
			{
				try
				{
					Thread T = new Thread(new RadixUpdateChecker(exData, getPlayer(context)));
					T.setName(exData.name + " Update Checker");
					T.start();
				}

				catch (Exception e)
				{
					RadixCore.getLogger().error("Unexpected exception while starting update checker for " + exData.name + ". Error was " + e.getMessage());
					continue;
				}
			}
		}
	}
}
