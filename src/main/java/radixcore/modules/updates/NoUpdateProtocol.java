package radixcore.modules.updates;

import radixcore.core.ModMetadataEx;

/**
 * Noop update protocol to use when you don't want to use updates.
 */
public class NoUpdateProtocol implements IUpdateProtocol
{
	@Override
	public UpdateData getUpdateData(ModMetadataEx exData) 
	{
		return null;
	}

	@Override
	public void cleanUp() 
	{
	}
}
