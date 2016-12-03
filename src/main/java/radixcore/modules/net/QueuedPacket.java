package radixcore.modules.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * A simple helper object used to store a packet and other needed information in a processing queue.
 */
public class QueuedPacket 
{
	private final AbstractPacket packet;
	private final MessageContext context;
	private final IMessageHandler message;
	
	public QueuedPacket(AbstractPacket packet, IMessageHandler message, MessageContext context)
	{
		this.packet = packet;
		this.context = context;
		this.message = message;
	}
	
	public AbstractPacket getPacket()
	{
		return packet;
	}
	
	public MessageContext getContext()
	{
		return context;
	}
	
	public IMessageHandler getMessage()
	{
		return message;
	}
}
