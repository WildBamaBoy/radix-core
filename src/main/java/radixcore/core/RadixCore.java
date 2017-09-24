package radixcore.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import radixcore.command.CommandRadixCore;
import radixcore.core.radix.CoreCrashWatcher;
import radixcore.core.radix.CorePacketHandler;
import radixcore.modules.updates.RDXUpdateProtocol;

@Mod(modid = RadixCore.ID, name = RadixCore.NAME, version = RadixCore.VERSION, acceptedMinecraftVersions = "[1.12,1.12.2]")
public class RadixCore 
{
	public static final String ID = "radixcore";
	public static final String NAME = "RadixCore";
	public static final String MINECRAFT_VERSION = "1.12.x";
	public static final String MOD_VERSION = "2.3.1";
	public static final String VERSION = MINECRAFT_VERSION + "-" + MOD_VERSION;
	
	@Instance(ID)
	private static RadixCore instance;
	private static Configuration config;
	private static Logger logger;
	private static String runningDirectory;
	private static CoreCrashWatcher crashWatcher;
	private static CorePacketHandler packetHandler;
	private static final List<ModMetadataEx> registeredMods = new ArrayList<ModMetadataEx>();

	public static boolean isTesting;
	public static boolean allowUpdateChecking;
	public static boolean allowCrashReporting;
	public static boolean allowStatisticsCollection;
	
    @EventHandler 
    public void preInit(FMLPreInitializationEvent event)
    {
    	instance = this;
    	logger = event.getModLog();
    	runningDirectory = System.getProperty("user.dir");
    	
    	config = new Configuration(event.getSuggestedConfigurationFile());
    	config.setCategoryComment("Privacy", "Settings relating to your privacy are located here.");
    	allowUpdateChecking = config.get("Privacy", "Allow update checking", true, "WARNING: Will disable for all RadixCore-based mods!").getBoolean();
    	allowCrashReporting = config.get("Privacy", "Allow crash reporting", true, "WARNING: Will disable for all RadixCore-based mods! Your Minecraft username, OS version, Java version, PC username, and installed mods may be shared with the mod author.").getBoolean();
    	allowStatisticsCollection = config.get("Privacy", "Allow statistics collection", true, "WARNING: Will disable for all RadixCore-based mods! This setting will also respect your 'Snooper' settings in Minecraft.").getBoolean();
    	config.save();
    	
    	crashWatcher = new CoreCrashWatcher();
    	packetHandler = new CorePacketHandler("RadixCore");
    	
		MinecraftForge.EVENT_BUS.register(new RadixEvents());

		ModMetadataEx exData = ModMetadataEx.getFromModMetadata(event.getModMetadata());
		exData.updateProtocol = new RDXUpdateProtocol();
		exData.packetHandler = packetHandler;
		RadixCore.registerMod(exData);
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new CommandRadixCore());
    }
    
    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    	crashWatcher.checkForCrashReports();
    }
    
    public static String getRunningDirectory()
    {
    	return runningDirectory;
    }
    
    public static RadixCore getInstance()
    {
    	return instance;
    }
    
    public static Logger getLogger()
    {
    	return logger;
    }
    
    public static CorePacketHandler getPacketHandler()
    {
    	return packetHandler;
    }
    
    public static void registerMod(ModMetadata modMetadata)
    {
    	registerMod(ModMetadataEx.getFromModMetadata(modMetadata));
    }
    
    public static void registerMod(ModMetadataEx modMetadataEx)
    {
    	registeredMods.add(modMetadataEx);
    }
    
    public static List<ModMetadataEx> getRegisteredMods()
    {
    	return Collections.unmodifiableList(registeredMods);
    }
    
    public static ModMetadataEx getModMetadataByID(String modID)
    {
    	for (ModMetadataEx data : registeredMods)
    	{
    		if (data.modId.equals(modID))
    		{
    			return data;
    		}
    	}
    	
    	return null;
    }
}
