package radixcore.modules.lang;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import radixcore.core.RadixCore;
import radixcore.modules.RadixMath;

public class LanguageManager 
{
	private String modId;
	private AbstractLanguageParser parser;
	private Map<String, String> translationsMap;

	public LanguageManager(String providedModId)
	{
		this.modId = providedModId.toLowerCase();
		this.translationsMap = new HashMap<String, String>();

		boolean loadedLanguage = false;

		for (StackTraceElement element : new Throwable().getStackTrace())
		{
			if (element.getClassName().equals("net.minecraft.server.dedicated.DedicatedServer"))
			{
				loadLanguage("en_US");
				loadedLanguage = true;
			}
		}

		if (!loadedLanguage)
		{
			loadLanguage(getGameLanguageID());
		}
	}

	public LanguageManager(String modId, AbstractLanguageParser parser)
	{
		this(modId);
		this.parser = parser;
	}

	@SideOnly(Side.CLIENT)
	public String getGameLanguageID()
	{
		String languageID = "en_US";

		try
		{
			languageID = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		}

		catch (final Exception e)
		{
			RadixCore.getLogger().error("Unable to get current language code. Defaulting to English.");
		}

		return languageID;
	}

	public String getString(String id)
	{
		return getString(id, (Object) null);
	}

	public String getString(String id, Object... arguments)
	{
		//Check if the exact provided key exists in the translations map.
		if (translationsMap.containsKey(id))
		{
			//Parse it if a parser was provided.
			if (parser != null)
			{
				return parser.parsePhrase(translationsMap.get(id), arguments);
			}

			else
			{
				return translationsMap.get(id);
			}
		}

		else
		{
			//Build a list of keys that at least contain part of the provided key name.
			List<String> containingKeys = new ArrayList<String>();

			for (String key : translationsMap.keySet())
			{
				if (key.contains(id))
				{
					containingKeys.add(key);
				}
			}

			//Return a random potentially valid key if some were found.
			if (containingKeys.size() > 0)
			{
				String key = containingKeys.get(RadixMath.getNumberInRange(0, containingKeys.size() - 1));

				if (parser != null)
				{
					return parser.parsePhrase(translationsMap.get(key), arguments);
				}

				else
				{
					return translationsMap.get(key);
				}
			}

			else
			{
				RadixCore.getLogger().error("[" + modId + "] No mapping found for requested phrase ID: " + id);
				return id;
			}
		}
	}

	/**
	 * @return	The number of phrases containing the provided string in their ID.
	 */
	public int getNumberOfPhrasesMatchingID(String id)
	{
		List<String> containingKeys = new ArrayList<String>();

		for (String key : translationsMap.keySet())
		{
			if (key.contains(id))
			{
				containingKeys.add(key);
			}
		}

		return containingKeys.size();
	}

	public void loadLanguage(String languageId)
	{
		//Remove old translations.
		translationsMap.clear();

		//Handle all English locales.
		if (languageId.startsWith("en_") && !languageId.equals("en_US"))
		{
			loadLanguage("en_US");
		}

		//And Spanish locales.
		else if (languageId.startsWith("es_") && !languageId.equals("es_ES"))
		{
			loadLanguage("es_ES");
		}

		//All checks for locales have passed. Load the desired language.
		InputStream inStream = StringUtils.class.getResourceAsStream("/assets/" + modId + "/lang/" + languageId + ".lang");

		if (inStream == null) //When language is not found, default to English.
		{
			//Make sure we're not already English. Null stream on English is a problem.
			if (languageId.equals("en_US"))
			{
				throw new RuntimeException("Unable to load English language files. Loading cannot continue.");
			}

			else
			{
				RadixCore.getLogger().error("Cannot load language " + languageId + " for " + modId + ". Defaulting to English.");
				loadLanguage("en_US");
			}
		}

		else
		{
			try
			{
				List<String> lines = IOUtils.readLines(inStream, Charsets.UTF_8);
				int lineNumber = 0;

				for (String line : lines)
				{
					lineNumber++;

					if (!line.startsWith("#") && !line.isEmpty())
					{
						String[] split = line.split("\\=");
						String key = split[0];
						String value = split.length == 2 ? split[1].replace("\\", "") : "";

						if (key.isEmpty())
						{
							throw new IOException("Empty phrase key on line " + lineNumber);
						}

						if (value.isEmpty())
						{
							RadixCore.getLogger().warn("Empty phrase value on line " + lineNumber + ". Key value: " + key);
						}

						translationsMap.put(key, value);
					}
				}

				RadixCore.getLogger().info("Loaded language " + languageId + " for " + modId + ".");
			}

			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
