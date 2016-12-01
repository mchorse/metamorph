package mchorse.metamorph;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Metamorph mod
 * 
 * This mod provides functionality for survival morphing. To gain a morph 
 * you have to kill a mob. Once you killed it, you gain its morphing. Once you 
 * gained its morphing you can use special menu to select a mob into which to 
 * morph.
 * 
 * Except different shape, you gain also special abilities for specific mobs. 
 * In creative you can access all morphings.
 * 
 * Inspired by Morph and Shape Shifter Z mods (mostly due to the fact that 
 * they're outdated).
 */
@Mod(modid = Metamorph.MODID, name = Metamorph.MODNAME, version = Metamorph.VERSION, guiFactory = Metamorph.GUI_FACTORY)
public class Metamorph
{
    /* Metadata fields */

    public static final String MODID = "metamorph";
    public static final String MODNAME = "Metamorph";
    public static final String VERSION = "1.0.2";

    public static final String CLIENT_PROXY = "mchorse.metamorph.ClientProxy";
    public static final String SERVER_PROXY = "mchorse.metamorph.CommonProxy";

    public static final String GUI_FACTORY = "mchorse.metamorph.config.GuiFactory";

    /* Forge stuff classes */

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static CommonProxy proxy;

    @Mod.Instance
    public static Metamorph instance;

    /* Events */

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        LOGGER = event.getModLog();

        proxy.preLoad(event);
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.load();
    }

    /* Logging */

    public static boolean DEBUG = true;
    public static Logger LOGGER;

    /**
     * Log out the message  
     */
    public static void log(String message)
    {
        if (DEBUG)
        {
            LOGGER.log(Level.INFO, message);
        }
    }
}