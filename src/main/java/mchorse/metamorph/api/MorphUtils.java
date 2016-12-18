package mchorse.metamorph.api;

import java.io.InputStream;
import java.lang.reflect.Type;
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
     */
    public static void loadMorphSettings(MorphManager manager, InputStream input)
    {
        Scanner scanner = new Scanner(input, "UTF-8");

        @SuppressWarnings("serial")
        Type type = new TypeToken<Map<String, MorphSettings>>()
        {}.getType();

        Map<String, MorphSettings> data = gson.fromJson(scanner.useDelimiter("\\A").next(), type);

        scanner.close();
        manager.settings.putAll(data);
    }
}