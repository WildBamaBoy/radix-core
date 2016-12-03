package radixcore.modules.updates;

import java.net.URL;
import java.util.Scanner;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import radixcore.core.ModMetadataEx;
import radixcore.core.RadixCore;

/**
 * <b>NOTICE!</b>
 * You <b>must</b> specify your mod's Curse ID during initialization. See MCA's preInit method for implementation. 
 * 
 * <p>This update protocol will work for you assuming the all of the following is true.
 * 
 * <ul>
 * <li>Your mod version and Minecraft version are not equal. (e.g mod version 1.8, Minecraft 1.8)
 * <li>Your mod's file extension is .jar.
 * <li>Your mod's name on Curse contains your mod's version in code. Standard naming such as 
 * <code>([modid]-[mod version]-[mc version]-universal.jar) is recommended. </code>
 * </ul>
 * @author MCGamer20000
 * @author WildBamaBoy
 */
public class CurseUpdateProtocol implements IUpdateProtocol
{
	@Override
	public UpdateData getUpdateData(ModMetadataEx modData) 
	{
		UpdateData returnData;

		try 
		{
			URL url = new URL("http://widget.mcf.li/mc-mods/minecraft/" + modData.curseId + ".json");
			Scanner scanner = new Scanner(url.openStream());

			JsonObject file = new GsonBuilder().create().fromJson(scanner.nextLine(), JsonObject.class).get("download").getAsJsonObject();

			returnData = new UpdateData();
			returnData.minecraftVersion = file.get("version").getAsString();
			returnData.modVersion = file.get("name").getAsString();
			
			//Parse out everything but the version number.
			returnData.modVersion = returnData.modVersion.replace(returnData.minecraftVersion, "").replace(".jar", "").replaceAll("[^0-9.]", "");
			scanner.close();
			
			return returnData;
		}

		catch (Exception e) 
		{
			RadixCore.getLogger().error("Unable to check for updates for " + modData.modId + ".", e);
		}

		return null;
	}

	@Override
	public void cleanUp() 
	{

	}
}