package radixcore.modules.updates;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import radixcore.core.ModMetadataEx;
import radixcore.core.RadixCore;

/**
 * An update protocol that communicates with our server software in order to
 * check for updates. Please don't use this. It won't work for you.
 */
public class RDXUpdateProtocol implements IUpdateProtocol
{
	private Socket connectSocket;
	
	@Override
	public UpdateData getUpdateData(ModMetadataEx modData) 
	{
		String minecraftVersion = "1.11.2";
		
		String url = "http://files.radix-shock.com/get-xml-property.php?modName=%modName%&mcVersion=%mcVersion%&xmlProperty=version";
		url = url.replace("%modName%", modData.modId).replace("%mcVersion%", minecraftVersion);
		
		try
		{
			UpdateData data = new UpdateData();
			String response = readStringFromURL(url);

			data.minecraftVersion = minecraftVersion;
			data.modVersion = response;

			return data;
		}
		
		catch (FileNotFoundException e)
		{
			RadixCore.getLogger().warn("Update server responded with 404 (not found). Update service may be down, try again later!");
			return null;
		}
		
		catch (Exception e)
		{
			RadixCore.getLogger().error("Failed to check for updates for: " + modData.name);
			RadixCore.getLogger().error(e);
			return null;
		}
	}

	@Override
	public void cleanUp() 
	{
	}
	
	private static String readStringFromURL(String urlString) throws IOException
	{
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		connection.connect();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String output = in.readLine();
		in.close();
		
		return output;
	}
}
