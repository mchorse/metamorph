package mchorse.metamorph.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mchorse.metamorph.api.json.MorphSettingsAdapter;

public class MorphUtils
{
    /**
     * GSON instance that is responsible for deserializing morph settings
     */
    private static Gson gson = new GsonBuilder().registerTypeAdapter(MorphSettings.class, new MorphSettingsAdapter()).create();

    /**
     * Load morph settings into {@link MorphManager}. 
     * 
     * I've got no idea how to handle different morph settings between client 
     * and a server.
     */
    public static void loadMorphSettings(MorphManager manager, InputStream input)
    {
        Scanner scanner = new Scanner(input, "UTF-8");

        @SuppressWarnings("serial")
        Type type = new TypeToken<Map<String, MorphSettings>>()
        {}.getType();

        Map<String, MorphSettings> data = gson.fromJson(scanner.useDelimiter("\\A").next(), type);

        scanner.close();

        for (Map.Entry<String, MorphSettings> entry : data.entrySet())
        {
            String key = entry.getKey();
            MorphSettings settings = entry.getValue();

            if (manager.settings.containsKey(key))
            {
                manager.settings.get(key).merge(settings);
            }
            else
            {
                manager.settings.put(key, settings);
            }
        }
    }

    /**
     * Load morph settings into {@link MorphManager} with given {@link File} and 
     * with a try-catch which logs out an error in case of failure.
     * 
     * You can use it freely.
     */
    public static void loadMorphSettings(MorphManager manager, File config)
    {
        try
        {
            loadMorphSettings(manager, new FileInputStream(config));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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
     * Load user provided blacklist using the safe way.
     */
    public static void loadBlacklist(MorphManager instance, File blacklist)
    {
        try
        {
            loadBlacklist(instance, new FileInputStream(blacklist));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Load user provided blacklist. Using this method, you're responsible for 
     * catching exceptions yourself.  
     */
    public static void loadBlacklist(MorphManager instance, FileInputStream input)
    {
        Scanner scanner = new Scanner(input, "UTF-8");

        @SuppressWarnings("serial")
        Type type = new TypeToken<List<String>>()
        {}.getType();
        List<String> data = gson.fromJson(scanner.useDelimiter("\\A").next(), type);

        instance.blacklist.addAll(data);
        scanner.close();
    }
}