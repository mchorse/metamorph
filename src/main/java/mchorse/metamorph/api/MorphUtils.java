package mchorse.metamorph.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import mchorse.metamorph.api.events.RegisterBlacklistEvent;
import mchorse.metamorph.api.events.RegisterSettingsEvent;
import net.minecraftforge.common.MinecraftForge;

public class MorphUtils
{
    /**
     * Generate an empty file
     */
    public static void generateFile(File config, String content)
    {
        config.getParentFile().mkdirs();

        try
        {
            PrintWriter writer = new PrintWriter(config);
            writer.print(content);
            writer.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reload blacklist using event
     */
    public static Set<String> reloadBlacklist()
    {
        RegisterBlacklistEvent event = new RegisterBlacklistEvent();
        MinecraftForge.EVENT_BUS.post(event);

        return event.blacklist;
    }

    /**
     * Reload morph settings using event 
     */
    public static Map<String, MorphSettings> reloadMorphSettings()
    {
        RegisterSettingsEvent event = new RegisterSettingsEvent();
        MinecraftForge.EVENT_BUS.post(event);

        return event.settings;
    }
}