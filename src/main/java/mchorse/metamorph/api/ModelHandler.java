package mchorse.metamorph.api;

import java.util.HashMap;
import java.util.Map;

/**
 * This class responsible for storing domain custom models and sending models to
 * players who are logged in.
 */
public class ModelHandler
{
    /**
     * Cached models, they're loaded from stuffs
     */
    public Map<String, Model> models = new HashMap<String, Model>();

    /**
     * Load default provided models into model map
     */
    public void loadModels()
    {
        /* Animals */
        this.load("Chicken");
        this.load("Cow");
        this.load("MushroomCow", "mooshroom");
        this.load("Ozelot", "ocelot");
        this.load("Pig");
        this.load("Rabbit");
        this.load("Sheep");
        this.load("Squid");
        this.load("Wolf");

        /* Neutral mobs */
        this.load("Villager");

        /* Hostile mobs */
        this.load("Creeper");
    }

    /**
     * Load a custom model with name and lowercase'd filename generated from 
     * name. 
     */
    private void load(String name)
    {
        this.load(name, name.toLowerCase());
    }

    /**
     * Load a custom model with name and filename
     */
    private void load(String name, String filename)
    {
        String path = "assets/metamorph/models/entity/";
        ClassLoader loader = this.getClass().getClassLoader();

        try
        {
            this.models.put(name, Model.parse(loader.getResourceAsStream(path + filename + ".json")));
        }
        catch (Exception e)
        {
            System.out.println("Failed to load a custom model by name '" + name + "'");

            e.printStackTrace();
        }
    }
}