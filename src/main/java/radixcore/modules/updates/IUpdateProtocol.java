package radixcore.modules.updates;

import radixcore.core.ModMetadataEx;

public interface IUpdateProtocol 
{
	UpdateData getUpdateData(ModMetadataEx modData);
	
	void cleanUp();
}
