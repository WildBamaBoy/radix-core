package radixcore.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import radixcore.network.RadixPacketHandler;
import radixcore.update.RDXUpdateProtocol;

@Mod(modid = RadixCore.ID, name = RadixCore.NAME, version = RadixCore.VERSION, dependencies="required-after:Forge@[12.18.0.2007,)", acceptedMinecraftVersions = "[1.10.2]")
public class RadixCore 
{
	protected static final String ID = "RadixCore";
	protected static final String NAME = "RadixCore";
	protected static final String VERSION = "1.10.2-2.1.3";

	@Instance(ID)
	private static RadixCore instance;
	private static Configuration config;
	private static Logger logger;
	private static String runningDirectory;
	private static RadixCrashWatcher crashWatcher;
	private static RadixPacketHandler packetHandler;
	protected static final List<ModMetadataEx> registeredMods = new ArrayList<ModMetadataEx>();

	public static boolean isTesting;
	public static boolean allowUpdateChecking;
	public static boolean allowCrashReporting;
	
    @EventHandler 
    public void preInit(FMLPreInitializationEvent event)
    {
    	instance = this;
    	logger = event.getModLog();
    	runningDirectory = System.getProperty("user.dir");
    	
    	config = new Configuration(event.getSuggestedConfigurationFile());
    	config.setCategoryComment("Privacy", "Settings relating to your privacy are located here.");
    	allowUpdateChecking = config.get("Privacy", "Allow update checking", true).getBoolean();
    	allowCrashReporting = config.get("Privacy", "Allow crash reporting", true).getBoolean();
    	
    	config.get("Privacy", "Allow crash reporting", true).setComment( 
    			"Mod crashes are sent to a remote server for debugging purposes. \n"
    			+ "Your Minecraft username, OS version, Java version, PC username, and installed mods may be shared with the mod author.");
    	
    	config.save();
    	
    	crashWatcher = new RadixCrashWatcher();
    	packetHandler = new RadixPacketHandler("RadixCore");
    	
		FMLCommonHandler.instance().bus().register(new RadixEvents());
		MinecraftForge.EVENT_BUS.register(new RadixEvents());

		ModMetadataEx exData = ModMetadataEx.getFromModMetadata(event.getModMetadata());
		exData.updateProtocolClass = RDXUpdateProtocol.class;
		RadixCore.registerMod(exData);
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
    
    public static RadixPacketHandler getPacketHandler()
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
    	return registeredMods;
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
