package mchorse.metamorph.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.events.RegisterBlacklistEvent;
import mchorse.metamorph.api.events.RegisterRemapEvent;
import mchorse.metamorph.api.events.RegisterSettingsEvent;
import mchorse.metamorph.api.json.MorphSettingsAdapter;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Register handler 
 * 
 * This event handler is responsible for 
 */
public class RegisterHandler
{
    /**
     * GSON instance that is responsible for deserializing morph settings
     */
    private final static Gson GSON = new GsonBuilder().registerTypeAdapter(MorphSettings.class, new MorphSettingsAdapter()).create();

    /**
     * Register morph settings from default morphs configuration that 
     * comes with Metamorph and user configuration file 
     */
    @SubscribeEvent
    public void onSettingsReload(RegisterSettingsEvent event)
    {
        this.loadMorphSettings(event.settings, this.getClass().getClassLoader().getResourceAsStream("assets/metamorph/morphs.json"));
        this.loadMorphSettings(event.settings, Metamorph.proxy.morphs);
    }

    /**
     * Load morph settings into {@link MorphManager} with given {@link File} and 
     * with a try-catch which logs out an error in case of failure.
     */
    private void loadMorphSettings(Map<String, MorphSettings> settings, File config)
    {
        try
        {
            this.loadMorphSettings(settings, new FileInputStream(config));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Load morph settings from {@link InputStream}
     */
    private void loadMorphSettings(Map<String, MorphSettings> settings, InputStream input)
    {
        Scanner scanner = new Scanner(input, "UTF-8");

        @SuppressWarnings("serial")
        Type type = (new TypeToken<Map<String, MorphSettings>>() {}).getType();
        Map<String, MorphSettings> data = GSON.fromJson(scanner.useDelimiter("\\A").next(), type);

        scanner.close();

        for (Map.Entry<String, MorphSettings> entry : data.entrySet())
        {
            String key = entry.getKey();
            MorphSettings morphSettings = entry.getValue();

            if (settings.containsKey(key))
            {
                settings.get(key).copy(morphSettings);
            }
            else
            {
                settings.put(key, morphSettings);
            }
        }
    }

    /**
     * Registers blacklist
     */
    @SubscribeEvent
    public void onRegisterBlacklist(RegisterBlacklistEvent event)
    {
        event.blacklist.add("metamorph:morph");

        this.loadBlacklist(event.blacklist, Metamorph.proxy.blacklist);
    }

    /**
     * Load user provided blacklist using the safe way.
     */
    private void loadBlacklist(Set<String> set, File blacklist)
    {
        try
        {
            Scanner scanner = new Scanner(new FileInputStream(blacklist), "UTF-8");

            @SuppressWarnings("serial")
            Type type = (new TypeToken<List<String>>() {}).getType();
            List<String> data = GSON.fromJson(scanner.useDelimiter("\\A").next(), type);

            set.addAll(data);
            scanner.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Registers remapper
     */
    @SubscribeEvent
    public void onRegisterRemapper(RegisterRemapEvent event)
    {
        event.map.put("metamorph.Block", "block");

        this.loadMappings(event.map, Metamorph.proxy.remap);
    }

    /**
     * Load user provided morph ID mappings using the safe way.
     */
    private void loadMappings(Map<String, String> map, File remap)
    {
        try
        {
            Scanner scanner = new Scanner(new FileInputStream(remap), "UTF-8");

            @SuppressWarnings("serial")
            Type type = (new TypeToken<Map<String, String>>() {}).getType();
            Map<String, String> data = GSON.fromJson(scanner.useDelimiter("\\A").next(), type);

            map.putAll(data);
            scanner.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}