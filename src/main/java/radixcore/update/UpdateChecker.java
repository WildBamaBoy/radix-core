package radixcore.update;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import radixcore.constant.Font;
import radixcore.core.ModMetadataEx;
import radixcore.core.RadixCore;

public final class UpdateChecker implements Runnable
{
	private final ModMetadataEx exData;
	private final ICommandSender commandSender;
	private boolean hasCheckedForUpdates;

	/**
	 * Constructor
	 * 
	 * @param mod 			The mod data of the mod checking for updates.
	 * @param commandSender The player checking for updates.
	 */
	public UpdateChecker(ModMetadataEx exData, ICommandSender commandSender)
	{
		this.exData = exData;
		this.commandSender = commandSender;
	}

	@Override
	public void run()
	{
		try
		{
			if (!hasCheckedForUpdates && exData.updateProtocolClass != null)
			{
				RadixCore.getLogger().info("Checking if updates available for " + exData.name + "...");

				IUpdateProtocol updateProtocol = exData.updateProtocolClass.newInstance();
				UpdateData updateData = updateProtocol.getUpdateData(exData);

				if (!(updateData.minecraftVersion + "-" + updateData.modVersion).equals(exData.version))
				{
					final String messageUpdateVersion = 
							Font.Color.DARKGREEN + exData.name + " " + updateData.modVersion + Font.Color.YELLOW + " for " + 
							Font.Color.DARKGREEN + "Minecraft " + updateData.minecraftVersion + Font.Color.YELLOW + " is available.";
					
					final String messageUpdateURL = 
							Font.Color.YELLOW + "Click " + Font.Color.BLUE + Font.Format.ITALIC + Font.Format.UNDERLINE + "here" + 
							Font.Format.RESET + Font.Color.YELLOW + " to download the update for " + exData.name + ".";

					final ITextComponent chatComponentUpdate = new TextComponentString(messageUpdateURL);
					chatComponentUpdate.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, exData.url));
					chatComponentUpdate.getStyle().setUnderlined(true);
					
					commandSender.sendMessage(new TextComponentString(messageUpdateVersion));
					commandSender.sendMessage(chatComponentUpdate);
				}

				updateProtocol.cleanUp();
			}
		}

		catch (final Exception e)
		{
			RadixCore.getLogger().error("Unexpected exception during update checking for " + exData.name + ". Error was: " + e.getMessage());
		}
		
		finally
		{
			hasCheckedForUpdates = true;
		}
	}
}
